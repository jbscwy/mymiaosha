package com.jyb.miaosha.access;

import com.jyb.miaosha.domain.MiaoshaUser;

/**
 * 使用ThreadLocal存储
 * 每个线程中只有一份，数据与线程绑定，线程安全
 * @author jyb
 * @since 2021-05-28 22:34
 */
public class UserContext {

    //ThreadLocal是线程内部存储类，可以在指定线程中存储数据，数据存储后，只有指定线程可以得到存储数据
    private static ThreadLocal<MiaoshaUser> userHolder=new ThreadLocal<>();

    public static void setUser(MiaoshaUser user){
        userHolder.set(user);
//        remove();
    }

    public static MiaoshaUser getUser(){
        MiaoshaUser miaoshaUser = userHolder.get();
//        remove();
        return miaoshaUser;
    }

    public static void remove(){
        userHolder.remove();
    }

}
