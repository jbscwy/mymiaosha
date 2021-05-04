package com.jyb.miaosha.controller;


import com.jyb.miaosha.resuilt.CodeMsg;
import com.jyb.miaosha.resuilt.Result;
import com.jyb.miaosha.service.MiaoUserService;
import com.jyb.miaosha.service.UserService;
import com.jyb.miaosha.util.ValidatorUtil;
import com.jyb.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    MiaoUserService miaoUserService;

    private static Logger log= LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }


    @RequestMapping("/do_login")
//    将接收到的json数据绑定为指定对象，在这里自动将前端获取的data数据封装成LoginVo对象
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        //参数校验:密码、手机号
//        String passInput=loginVo.getPassword();
//        String mobile=loginVo.getMobile();
//        if(StringUtils.isEmpty(passInput)){
//            return Result.error(CodeMsg.PASSWORD_EMPTY);
//        }
//        if(StringUtils.isEmpty(mobile)){
//            return Result.error(CodeMsg.MOBILEPHONE_EMPTY);
//        }
//        if(!ValidatorUtil.isMobile(mobile)){
//            return Result.error(CodeMsg.PHONENUMBERFORMAT_ERROR);
//        }
        //登录
        String token = miaoUserService.login(response, loginVo);

        return Result.success(token);
    }





}
