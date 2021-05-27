package com.jyb.miaosha.rabbitmq;

import com.jyb.miaosha.domain.MiaoshaUser;

/**
 * @author jyb
 * @since 2021-05-27 18:52
 */
public class MiaoshaMessage {
    private MiaoshaUser user;
    private long goodsId;

    public MiaoshaUser getUser() {
        return user;
    }

    public void setUser(MiaoshaUser user) {
        this.user = user;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
