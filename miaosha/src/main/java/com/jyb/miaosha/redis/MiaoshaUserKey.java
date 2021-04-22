package com.jyb.miaosha.redis;

public class MiaoshaUserKey extends BasePrefix{

    public static final int TOKEN_EXPIRE=3600*24*2;
    public MiaoshaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds,prefix);
    }
    public static MiaoshaUserKey token=new MiaoshaUserKey(TOKEN_EXPIRE,"t");
//    public static MiaoshaUserKey getByName=new MiaoshaUserKey("name");
}
