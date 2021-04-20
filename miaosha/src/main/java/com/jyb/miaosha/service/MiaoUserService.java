package com.jyb.miaosha.service;

import com.jyb.miaosha.dao.MiaoshaUserDao;
import com.jyb.miaosha.domain.MiaoshaUser;
import com.jyb.miaosha.exception.GlobleException;
import com.jyb.miaosha.resuilt.CodeMsg;
import com.jyb.miaosha.util.MD5Util;
import com.jyb.miaosha.vo.LoginVo;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoUserService {

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    public MiaoshaUser getById(long id){
        return miaoshaUserDao.getById(id);
    }

    public Boolean login(LoginVo loginVo) {
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
        return true;
    }
}
