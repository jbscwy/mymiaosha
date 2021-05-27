package com.jyb.miaosha.service;

import com.jyb.miaosha.dao.OrderDao;
import com.jyb.miaosha.domain.MiaoshaOrder;
import com.jyb.miaosha.domain.MiaoshaUser;
import com.jyb.miaosha.domain.OrderInfo;
import com.jyb.miaosha.redis.OrderKey;
import com.jyb.miaosha.redis.RedisService;
import com.jyb.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author jyb
 * @since 2021-04-23 21:47
 */
@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;
    @Autowired
    RedisService redisService;

    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long userId, long goodsId) {
//        return orderDao.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
//        不差数据库，先查缓存
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid,""+userId+"_"+goodsId,MiaoshaOrder.class);
    }

//    生成订单
    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo good) {
        OrderInfo orderInfo=new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(good.getId());
        orderInfo.setGoodsName(good.getGoodsName());
        orderInfo.setGoodsPrice(good.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        long orderId=orderDao.insert(orderInfo);
        MiaoshaOrder miaoshaOrder=new MiaoshaOrder();
        miaoshaOrder.setGoodsId(good.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);

        redisService.set(OrderKey.getMiaoshaOrderByUidGid,
                ""+user.getId()+"_"+good.getId(),miaoshaOrder);

        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {
        return  orderDao.getOrderById(orderId);

    }
}
