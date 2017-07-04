package com.github.wall2huang.annotation;/**
 * Created by Administrator on 2017/7/4.
 */

import org.springframework.web.bind.annotation.RestController;

/**
 * author : Administrator
 **/
@RestController
@ZkValue(path = "/zoo", value = "haha")
public class Controller
{


}
