package com.jyb.miaosha.vo;

import com.jyb.miaosha.domain.MiaoshaUser;

/**
 * @author jyb
 * @since 2021-05-04 20:16
 */
public class GoodsDetailVo {
    private GoodsVo goods;
    private int miaoshaStatus=0;
    private int remainSeconds=0;
    private MiaoshaUser user;

    public MiaoshaUser getUser() {
        return user;
    }

    public void setUser(MiaoshaUser user) {
        this.user = user;
    }

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }

    public void setMiaoshaStatus(int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }
}
