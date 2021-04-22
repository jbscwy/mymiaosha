package com.jyb.miaosha.service;

import com.jyb.miaosha.dao.MiaoshaUserDao;
import com.jyb.miaosha.domain.MiaoshaUser;
import com.jyb.miaosha.exception.GlobleException;
import com.jyb.miaosha.redis.MiaoshaUserKey;
import com.jyb.miaosha.redis.RedisService;
import com.jyb.miaosha.resuilt.CodeMsg;
import com.jyb.miaosha.util.MD5Util;
import com.jyb.miaosha.util.UUIDUtil;
import com.jyb.miaosha.vo.LoginVo;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoUserService {

    public static final String COOKI_NAME_TOKEN="token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;


    public MiaoshaUser getById(long id){
        return miaoshaUserDao.getById(id);
    }

    public Boolean login(HttpServletResponse response, LoginVo loginVo) {
        if(loginVo==null){
//         return CodeMsg.SERVER_ERROR;
//            往外抛异常
            throw new GlobleException(CodeMsg.SERVER_ERROR);
        }
        String mobile=loginVo.getMobile();
        String formPass=loginVo.getPassword();
//判断手机号是否存在
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if(user==null){
            throw new GlobleException(CodeMsg.MOBILE_NOT_EXIST);
        }
//验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if(calcPass.equals(dbPass)){
//            密码错误
            throw new GlobleException(CodeMsg.PASSWORD_ERROR);
        }
        addCookie(response,user);
        return true;
    }

    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        //延长有效期
        if(user!=null) {
            addCookie(response, user);
        }
        return user;
    }

    /**
     * 添加cookie
     * @param response
     * @param user
     */
    private void addCookie(HttpServletResponse response, MiaoshaUser user){
        //        生成cookie
        String token= UUIDUtil.uuid();
//        将用户信息token添加到redis缓存中
        redisService.set(MiaoshaUserKey.token,token,user);
//       设置Cookie
        Cookie cookie=new Cookie(COOKI_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
//        将cookie添加到response类中
        cookie.setPath("/");
        response.addCookie(cookie);
    }


}
