package com.jyb.miaosha.vo;

import com.jyb.miaosha.domain.OrderInfo;

/**
 * @author jyb
 * @since 2021-05-05 18:42
 */
public class OrderDetailVo {
    private GoodsVo goods;
    private OrderInfo order;

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public OrderInfo getOrder() {
        return order;
    }

    public void setOrder(OrderInfo order) {
        this.order = order;
    }
}
