package com.github.wall2huang.configure;/**
 * Created by Administrator on 2017/7/4.
 */

import com.github.wall2huang.annotation.ZkValue;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

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
            Field[] fields = o.getClass().getDeclaredFields();
            for (final Field field : fields)
            {
                ZkValue declaredAnnotation = field.getDeclaredAnnotation(ZkValue.class);
                if (declaredAnnotation != null)
                {
                    String path = declaredAnnotation.path();
                    String value = declaredAnnotation.value();
                    try
                    {
                        if( client.checkExists().forPath(path) == null)
                        {
                            client.create()
                                    .creatingParentsIfNeeded()
                                    .withMode(CreateMode.PERSISTENT)
                                    .forPath(path, value.getBytes("UTF-8"));
                        }
                        else
                        {
                            String data = new String(client.getData()
                                    .forPath(path), "UTF-8");
                            field.setAccessible(true);
                            field.set(o, data);
                        }

                        //监听本节点的变化
                        final NodeCache nodeCache = new NodeCache(client, path);
                        nodeCache.getListenable()
                                .addListener(() ->
                                {
                                    String currentData = new String(nodeCache.getCurrentData().getData(), "UTF-8");
                                    field.setAccessible(true);
                                    field.set(o, currentData);
                                });
                        nodeCache.start();
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
