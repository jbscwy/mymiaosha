package com.jyb.miaosha.redis;

public interface KeyPrefix {
//    到期时间
    public int expireSeconds();
//    获取前缀
    public String getPrefix();
}
