package com.github.wall2huang.annotation;/**
 * Created by Administrator on 2017/7/4.
 */

import org.springframework.web.bind.annotation.RestController;

/**
 * author : Administrator
 **/
@RestController
@ZkValue()
public class Controller
{
    @ZkValue(path = "/springBoot/test", value = "哈哈哈哈")
    private String zkValue;



}
