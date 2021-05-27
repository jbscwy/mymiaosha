package com.jyb.miaosha.service;

import com.jyb.miaosha.dao.GoodsDao;
import com.jyb.miaosha.domain.Goods;
import com.jyb.miaosha.domain.MiaoshaOrder;
import com.jyb.miaosha.domain.MiaoshaUser;
import com.jyb.miaosha.domain.OrderInfo;
import com.jyb.miaosha.redis.MiaoshaKey;
import com.jyb.miaosha.redis.RedisService;
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
    @Autowired
    RedisService redisService;

//   添加原子操作
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo good) {
        //减库存、下订单、写入秒杀订单
        boolean b = goodsService.reduceStock(good);
        //只有在减库存成功后才生成订单
        if(b){
            //order_info、miaosha_order
            return orderService.createOrder(user,good);
        }else{
            //如果减库存失败，做一个标记，表示商品已秒杀完
            setGoodsOver(good.getId());
            return null;
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver,""+goodsId,true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver,""+goodsId);
    }


    public long getMiaoshaResult(Long id, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(id, goodsId);
        if(order!=null){ //秒杀成功
            return order.getOrderId();
        }else{
            //判断商品是否已卖完
           boolean isOver=getGoodsOver(goodsId);
           if(isOver){
               return -1;
           }else{
               //如果没有卖完，轮询
               return 0;
           }
        }
    }


}
