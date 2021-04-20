package com.jyb.miaosha.util;

import org.codehaus.groovy.util.StringUtil;
import org.thymeleaf.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 判断手机号格式类
 */
public class ValidatorUtil {
    //1位后面跟着10位数
    public static final Pattern mobile_pattern=Pattern.compile("1\\d{10}");

    public static boolean isMobile(String src){
//        手机号为空
        if(StringUtils.isEmpty(src)){
            return false;
        }
        //格式校验
        Matcher m=mobile_pattern.matcher(src);
        return m.matches();
    }

    public static void main(String[] args) {
        System.out.println(isMobile("12312341234"));
    }
}
