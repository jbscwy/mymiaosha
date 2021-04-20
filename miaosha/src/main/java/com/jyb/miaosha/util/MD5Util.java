package com.jyb.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    /**
     * 将字符串进行md5操作
     * @param src
     * @return
     */
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    /**
     * 将用户输入的字符串转换
     * @param inputPass
     * @return
     */
    public static final String salt="1a2b3c4d";
    public static String inputPassFormPass(String inputPass){
        String str=""+salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    public static String formPassToDBPass(String formPass,String salt){
        String str=""+salt.charAt(0)+salt.charAt(2)+formPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDbPass(String input,String saltDB){
        String formPass=inputPassFormPass(input);
        String dbPass=formPassToDBPass(formPass,saltDB);
        return dbPass;
    }



    public static void main(String[] args) {
        System.out.println(inputPassFormPass("123456"));//d3b1294a61a07da9b49b6e22b2cbd7f9
//        System.out.println(formPassToDBPass(inputPassFormPass("123456"),"1a2b3c4d"));
        System.out.println(inputPassToDbPass("123456","1a2b3c4d"));
    }
}

