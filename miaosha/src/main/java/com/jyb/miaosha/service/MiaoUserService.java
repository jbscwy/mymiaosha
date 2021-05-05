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
        //1.取缓存,通过id获取对象
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        if(miaoshaUser!=null){
            return miaoshaUser;
        }
        //2.若缓存中没有，从数据库中取
        MiaoshaUser user = miaoshaUserDao.getById(id);
        if(user!=null){
            //3.添加缓存
            redisService.set(MiaoshaUserKey.getById, "" + id, user);
        }
        return user;
    }


    //https://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
    //修改用户密码
    public boolean updatePassword(String token,long id,String passwordNew){
        //取user
        MiaoshaUser user=getById(id);
        if(user==null){
            throw new GlobleException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库，创建秒杀用户
        MiaoshaUser toBeUpdate=new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(passwordNew,user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        //处理缓存，需要更新与id相关的所有缓存
        redisService.delete(MiaoshaUserKey.getById,""+id);
        //这里不能直接删除token，若直接删除将无法登录，需要进行更新
        user.setPassword(toBeUpdate.getPassword()); // 更新用户密码
        redisService.set(MiaoshaUserKey.token,token,user);  // 这里token需要在前面的方法中传入
        return true;
    }




    public String login(HttpServletResponse response, LoginVo loginVo) {
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
//        String calcPass = MD5Util.inputPassToDbPass(formPass, saltDB);

        if(!calcPass.equals(dbPass)){
//            密码错误
            throw new GlobleException(CodeMsg.PASSWORD_ERROR);
        }
        //生成token
        String token = UUIDUtil.uuid();
        addCookie(response,token,user);
        return token;
    }

    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        //延长有效期
        if(user!=null) {
            addCookie(response,token,user);
        }
        return user;
    }

    /**
     * 添加cookie
     * @param response
     * @param user
     */
    private void addCookie(HttpServletResponse response,String token,MiaoshaUser user){
        //        生成cookie
//        String token= UUIDUtil.uuid();
//        不需要每次都生成token，之后只需更新就行

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
