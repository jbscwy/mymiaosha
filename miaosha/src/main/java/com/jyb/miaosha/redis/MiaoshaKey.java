package com.jyb.miaosha.redis;

/**
 * @author jyb
 * @since 2021-05-27 19:47
 */
public class MiaoshaKey extends BasePrefix{
    public MiaoshaKey(String prefix) {
        super(prefix);
    }

    public MiaoshaKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }


    public static MiaoshaKey isGoodsOver=new MiaoshaKey("go");
    //设置秒杀过期时间为60秒
    public static MiaoshaKey getMiaoshaPath=new MiaoshaKey(60,"mp");
    //设置验证码过期时间
    public static MiaoshaKey verify_code=new MiaoshaKey(300,"vc");

}
