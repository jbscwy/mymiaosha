package com.jyb.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


//使用service提供redis服务
@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;


    /**
     * 获取单个对象
     * @param prefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix prefix,String key,Class<T> clazz){
//        通过jedisPool，获取jedis对象
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
//            生成真正的key
            String realKey=prefix.getPrefix()+key;
            String s = jedis.get(realKey);
//            将String类型转化为Bean类型
            T t=stringToBean(s,clazz);
            return t;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置单个对象
     * @param prefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean set(KeyPrefix prefix,String key,T value){
//        通过jedisPool，获取jedis对象
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            String str=beanToString(value);
            if(str==null || str.length()<=0){
                return false;
            }
            //            生成真正的key
            String realKey=prefix.getPrefix()+key;
            int seconds = prefix.expireSeconds();
            if(seconds<=0){
                jedis.set(realKey,str);
            }else{
//                根据过期时间判断
                jedis.setex(realKey,seconds,str);
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 删除
     * @param prefix
     * @param key
     * @return
     */
    public boolean delete(KeyPrefix prefix,String key){
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            //生成真正的key
            String realKey=prefix.getPrefix()+key;
            Long del = jedis.del(realKey);
            return del>0;
        }finally {
            //操作结束，返回线程池
            returnToPool(jedis);
        }
    }



    /**
     * 判断键是否存在
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean exists(KeyPrefix prefix,String key){
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            //            生成真正的key
            String realKey=prefix.getPrefix()+key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }


    /**
     * 键值加1
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix prefix,String key){
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            //            生成真正的key
            String realKey=prefix.getPrefix()+key;
//            将键存储的数值加1
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }


    /**
     * 键值减1
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long decr(KeyPrefix prefix,String key){
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            //            生成真正的key
            String realKey=prefix.getPrefix()+key;
//            将键存储的数值加1
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }


    /**
     * 将任意类型转化为字符串
     * @param value
     * @param <T>
     * @return
     */
    private <T> String beanToString(T value) {
        if(value==null){
            return null;
        }
//        根据class判断是否为空
        Class<?> clazz=value.getClass();
        if(clazz==int.class || clazz==Integer.class){
            return ""+value;
        }else if(clazz==String.class){
            return (String)value;
        }else if (clazz==long.class || clazz==Long.class){
            return ""+value;
        }else {
            return JSON.toJSONString(value);
        }
    }

    /**
     * 将String转化为Bean
     * @param s
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> T stringToBean(String s,Class<T> clazz) {
        if(s==null || s.length()<=0 || clazz==null){
            return null;
        }
        if(clazz==int.class || clazz==Integer.class){
            return (T)Integer.valueOf(s);
        }else if(clazz==String.class){
            return (T) s;
        }else if (clazz==long.class || clazz==Long.class){
            return (T) Long.valueOf(s);
        }else {
//           将string转化为json，再映射成clazz类
            return JSON.toJavaObject(JSON.parseObject(s),clazz);
        }
    }


    private void returnToPool(Jedis jedis) {
        if(jedis!=null){
            //不会关闭，而是返回到连接池中
            jedis.close();
        }
    }

}
