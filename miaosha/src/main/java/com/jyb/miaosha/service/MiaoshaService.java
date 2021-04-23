package com.jyb.miaosha.service;

import com.jyb.miaosha.dao.GoodsDao;
import com.jyb.miaosha.domain.Goods;
import com.jyb.miaosha.domain.MiaoshaUser;
import com.jyb.miaosha.domain.OrderInfo;
import com.jyb.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jyb
 * @since 2021-04-23 21:56
 */
@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;

//   添加原子操作
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo good) {
        //减库存、下订单、写入秒杀订单
        goodsService.reduceStock(good);
        //order_info、miaosha_order
        return orderService.createOrder(user,good);
    }
}
