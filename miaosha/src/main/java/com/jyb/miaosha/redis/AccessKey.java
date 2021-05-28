package com.jyb.miaosha.redis;

/**
 * @author jyb
 * @since 2021-05-28 21:13
 */
public class AccessKey extends BasePrefix{

    public AccessKey(String prefix) {
        super(prefix);
    }

    public AccessKey(int expireSeconds,String prefix){
        super(expireSeconds,prefix);
    }

    //设置秒杀过期时间为60秒
    public static AccessKey access=new AccessKey(5,"access");

    public static AccessKey withExpire(int expireSeconds){
        return new AccessKey(expireSeconds,"access");
    }


}
