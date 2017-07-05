package com.github.wall2huang.configure;/**
 * Created by Administrator on 2017/7/4.
 */

import com.github.wall2huang.annotation.ZkValue;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

/**
 * author : Administrator
 **/
@Component
public class ZkProcessConfigure implements BeanPostProcessor
{
    public static CuratorFramework client;

    static
    {
        client = CuratorFrameworkFactory.newClient("192.168.0.129:2181", new ExponentialBackoffRetry(1000, 3));
        client.start();
    }

    public static void main(String[] args) throws Exception
    {
        String s = "/springBoot";



    }


    /**
     * @param o 初始化后的bean
     * @param s bean的名称
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException
    {
        ZkValue annotation = o.getClass().getDeclaredAnnotation(ZkValue.class);

        if (annotation != null)
        {
            Field[] fields = o.getClass().getFields();
            for (Field field : fields)
            {
                ZkValue declaredAnnotation = field.getDeclaredAnnotation(ZkValue.class);
                if (declaredAnnotation != null)
                {
                    String path = declaredAnnotation.path();
                    String value = declaredAnnotation.value();
                    try
                    {
                        String data = new String(client.getData().forPath(path), "UTF-8");
                        field.setAccessible(true);
                        field.set(o, data);
                        System.out.println(field.get(o));

                    } catch (KeeperException.NoNodeException e)
                    {
                        try
                        {
                            client.create()
                                    .creatingParentsIfNeeded()
                                    .withMode(CreateMode.PERSISTENT)
                                    .forPath(s, value.getBytes("UTF-8"));
                        } catch (Exception e1)
                        {
                            e.printStackTrace();
                        }

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        }

        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException
    {
        return o;
    }
}
