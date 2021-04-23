package com.jyb.miaosha.controller;

import com.jyb.miaosha.domain.MiaoshaOrder;
import com.jyb.miaosha.domain.MiaoshaUser;
import com.jyb.miaosha.domain.OrderInfo;
import com.jyb.miaosha.redis.RedisService;
import com.jyb.miaosha.resuilt.CodeMsg;
import com.jyb.miaosha.service.GoodsService;
import com.jyb.miaosha.service.MiaoUserService;
import com.jyb.miaosha.service.MiaoshaService;
import com.jyb.miaosha.service.OrderService;
import com.jyb.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author jyb
 * @since 2021-04-23 21:39
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

    @Autowired
    MiaoUserService miaoUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;


    @RequestMapping("/do_miaosha")
    public String list(Model model, MiaoshaUser user,
                       @RequestParam("goodsId")long goodsId){
        model.addAttribute("user",user);
        if(user==null){
            return "login";
        }
        //判断库存
        GoodsVo good= goodsService.getGoodsVoByGoodsId(goodsId);
        int stock=good.getStockCount();
        if(stock<=0){
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //判断是否已经秒杀过了
        MiaoshaOrder order= orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order!=null){
            model.addAttribute("errmsg",CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        //减库存、下订单、写入秒杀订单
        OrderInfo orderInfo=miaoshaService.miaosha(user,good);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",good);
        return "order_detail";
    }

}
