package com.jyb.miaosha.redis;

/**
 * @author jyb
 * @since 2021-05-27 19:47
 */
public class MiaoshaKey extends BasePrefix{
    public MiaoshaKey(String prefix) {
        super(prefix);
    }


    public static MiaoshaKey isGoodsOver=new MiaoshaKey("go");

}
