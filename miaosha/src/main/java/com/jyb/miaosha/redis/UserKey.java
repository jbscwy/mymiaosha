package com.jyb.miaosha.redis;

public class UserKey extends BasePrefix {

    public UserKey(String prefix){
        //默认0代表永不过期
        super(prefix);
    }

    public static UserKey getById=new UserKey("id");
    public static UserKey getByName=new UserKey("name");



}
