package com.jyb.miaosha.controller;

import com.jyb.miaosha.domain.MiaoshaUser;
import com.jyb.miaosha.redis.RedisService;
import com.jyb.miaosha.resuilt.Result;
import com.jyb.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author jyb
 * @since 2021-04-24 21:38
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;

    //
    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(MiaoshaUser miaoshaUser){
        return Result.success(miaoshaUser);
    }

}
