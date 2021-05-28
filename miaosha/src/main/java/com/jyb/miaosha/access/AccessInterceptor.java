package com.jyb.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.jyb.miaosha.domain.MiaoshaUser;
import com.jyb.miaosha.redis.AccessKey;
import com.jyb.miaosha.redis.RedisService;
import com.jyb.miaosha.resuilt.CodeMsg;
import com.jyb.miaosha.resuilt.Result;
import com.jyb.miaosha.service.MiaoUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.thymeleaf.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * @author jyb
 * @since 2021-05-28 22:11
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    MiaoUserService miaoUserService;
    @Autowired
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            MiaoshaUser user = getUser(request, response);
            //将用户放到ThreadLocal中，
            UserContext.setUser(user);
            HandlerMethod hm=(HandlerMethod)handler;
            //获取注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit==null){
                return true;
            }
            int countLimit = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            int seconds = accessLimit.seconds();

            String key=request.getRequestURI();
            if(needLogin){
                if(user==null){
                    render(response,CodeMsg.SESSION_ERROR);
                    return false;
                }
                key+="_"+user.getId();
            }else{

            }
            AccessKey accessKey = AccessKey.withExpire(seconds);
            Integer count=redisService.get(accessKey,key,Integer.class);
            if(count==null){
                redisService.set(accessKey,key,1);
            } else if(count<countLimit){
                redisService.incr(accessKey,key);
            }else{
                render(response,CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return super.preHandle(request, response, handler);
    }

    private void render(HttpServletResponse response, CodeMsg codeMsg) throws Exception{
        //这里需要设置输出格式，不然会出现乱码
        response.setContentType("application/json;charset=UTF-8");
        //发送给客户端的数据
        OutputStream outputStream = response.getOutputStream();
        //将对象转化为string
        String str = JSON.toJSONString(Result.error(codeMsg));
        outputStream.write(str.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }

    //直接从cookie中获取用户
    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response){
        //        从请求头中获取token
        String paramToken=request.getParameter(MiaoUserService.COOKI_NAME_TOKEN);
        //        从请求头中获取cookie
        String cookieToken=getCookieValue(request,MiaoUserService.COOKI_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken) &&
                StringUtils.isEmpty(paramToken)){
            return null;
        }
        //从http请求中获取token
        String token=StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return miaoUserService.getByToken(response,token);
    }

    //   遍历请求中所有的cookies，获取需要的cookie
    private String getCookieValue(HttpServletRequest nativeRequest, String cookieName) {
        //获取所有token
        Cookie[] cookies = nativeRequest.getCookies();
        if(cookies==null || cookies.length<=0){
            return null;
        }
        //遍历cookie，获取想要的cookie
        for(Cookie cookie:cookies){
            if(cookie.getName().equals(cookieName)){
                return cookie.getValue();
            }
        }
        return null;
    }

}
