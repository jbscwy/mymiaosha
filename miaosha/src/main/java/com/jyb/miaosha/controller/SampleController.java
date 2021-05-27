package com.jyb.miaosha.controller;

import com.jyb.miaosha.domain.User;
import com.jyb.miaosha.rabbitmq.MQSender;
import com.jyb.miaosha.redis.RedisService;
import com.jyb.miaosha.redis.UserKey;
import com.jyb.miaosha.resuilt.Result;
import com.jyb.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

//    @RequestMapping("/mq")
//    @ResponseBody
//    public Result<String> mq(Model model){
//        sender.send("hello,imooc");
//        return Result.success("Hello,world");
//    }
//
//    @RequestMapping("/mq/topic")
//    @ResponseBody
//    public Result<String> topic(){
//        sender.sendTopic("hello,topic");
//        return Result.success("Hello,topic");
//    }
//
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public Result<String> fanout(){
//        sender.sendFanout("hello,fanout");
//        return Result.success("Hello,fanout");
//    }
//
//    @RequestMapping("/mq/headers")
//    @ResponseBody
//    public Result<String> headers(){
//        sender.sendHeaders("hello,headers");
//        return Result.success("Hello,headers");
//    }


    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name","jyb");
        return "hello";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet() {
        User user = userService.getById(1);
        return Result.success(user);
    }


//    测试事务
    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx() {
        userService.tx();
        return Result.success(true);
    }


    //实现redisGet方法
    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
//        String s = redisService.get("2", String.class);
        User user= redisService.get(UserKey.getById,""+1, User.class);
        return Result.success(user);
    }


    //实现redisSet方法
    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
        User user=new User();
        user.setId(1);
        user.setName("11111");
        boolean set = redisService.set(UserKey.getById, "" + 1, user);
        return Result.success(set);
    }






}
