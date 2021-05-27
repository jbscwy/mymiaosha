package com.jyb.miaosha.redis;

import com.jyb.miaosha.service.GoodsService;

/**
 * @author jyb
 * @since 2021-05-04 16:17
 */
public class GoodsKey extends BasePrefix{

    public GoodsKey(String prefix) {
        super(prefix);
    }

    //设置有效期
    public GoodsKey(int expireSeconds,String prefix){
        super(expireSeconds,prefix);
    }

    public static GoodsKey getGoodsList=new GoodsKey(60,"gl");
    public static GoodsKey getGoodsDetail=new GoodsKey(60,"gd");
    public static GoodsKey getMiaoshaGoodsStock=new GoodsKey(0,"gs");
}
