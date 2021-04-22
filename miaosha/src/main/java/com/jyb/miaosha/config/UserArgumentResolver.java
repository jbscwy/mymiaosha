package com.jyb.miaosha.config;

import com.jyb.miaosha.domain.MiaoshaUser;
import com.jyb.miaosha.service.MiaoUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 从cookie中获取用户
 */
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    MiaoUserService miaoUserService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
//        获取参数类型
        Class<?> clazz=methodParameter.getParameterType();
//        判断是否为MiaoshaUser类
        return clazz== MiaoshaUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception {
//        获取httpservletRequest
        HttpServletRequest nativeRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
//        获取httpservletResponse
        HttpServletResponse nativeResponse = nativeWebRequest.getNativeResponse(HttpServletResponse.class);

//        从请求头中获取token
        String paramToken=nativeRequest.getParameter(MiaoUserService.COOKI_NAME_TOKEN);
//        从请求头中获取cookie
        String cookieToken=getCookieValue(nativeRequest,MiaoUserService.COOKI_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken) &&
        StringUtils.isEmpty(paramToken)){
            return null;
        }
        String token=StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return miaoUserService.getByToken(nativeResponse,token);
    }

//   遍历请求中所有的cookies，获取需要的cookie
    private String getCookieValue(HttpServletRequest nativeRequest, String cookieName) {
        //获取所有token
        Cookie[] cookies = nativeRequest.getCookies();
        //遍历cookie，获取想要的cookie
        for(Cookie cookie:cookies){
            if(cookie.getName().equals(cookieName)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
