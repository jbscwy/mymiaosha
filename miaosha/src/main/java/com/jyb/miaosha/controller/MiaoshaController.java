package com.jyb.miaosha.controller;

import com.jyb.miaosha.domain.MiaoshaOrder;
import com.jyb.miaosha.domain.MiaoshaUser;
import com.jyb.miaosha.domain.OrderInfo;
import com.jyb.miaosha.rabbitmq.MQSender;
import com.jyb.miaosha.rabbitmq.MiaoshaMessage;
import com.jyb.miaosha.redis.GoodsKey;
import com.jyb.miaosha.redis.RedisService;
import com.jyb.miaosha.resuilt.CodeMsg;
import com.jyb.miaosha.resuilt.Result;
import com.jyb.miaosha.service.GoodsService;
import com.jyb.miaosha.service.MiaoUserService;
import com.jyb.miaosha.service.MiaoshaService;
import com.jyb.miaosha.service.OrderService;
import com.jyb.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jyb
 * @since 2021-04-23 21:39
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

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

    @Autowired
    MQSender sender;


//    使用hashmap做标记
    private Map<Long,Boolean> localOverMap=new HashMap<>();


    /**
     * 系统初始化
     * 在系统启动时将商品加载到缓存中
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if(goodsVoList==null){
            return;
        }
        for(GoodsVo goodsVo:goodsVoList){
            redisService.set(GoodsKey.getMiaoshaGoodsStock,
                    ""+goodsVo.getId(),goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(),false);

        }
    }

    /**
     * Get与Post的区别：
     * Get：幂等性
     * Post：不是幂等的
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value="/do_miaosha",method= RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model, MiaoshaUser user,
                                  @RequestParam("goodsId")long goodsId){
        model.addAttribute("user",user);
        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //内存标记减少redis访问
        boolean over=localOverMap.get(goodsId);
        //如果标记为成功
        if(over){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }


        //在redis中减少库存
        Long decr = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);

        if(decr<0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀过了
        MiaoshaOrder order= orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order!=null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //如果没有秒杀过，进入消息队列
        MiaoshaMessage miaoshaMessage=new MiaoshaMessage();
        miaoshaMessage.setUser(user);
        miaoshaMessage.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(miaoshaMessage);

        return Result.success(0); //表示排队中

//        //判断库存
//        GoodsVo good= goodsService.getGoodsVoByGoodsId(goodsId);
//        int stock=good.getStockCount();
//        if(stock<=0){
//            return Result.error(CodeMsg.MIAO_SHA_OVER);
//        }
//        //判断是否已经秒杀过了
//        MiaoshaOrder order= orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
//        if(order!=null){
//            return Result.error(CodeMsg.REPEATE_MIAOSHA);
//        }
//        //减库存、下订单、写入秒杀订单
//        OrderInfo orderInfo=miaoshaService.miaosha(user,good);
//        return Result.success(orderInfo);
    }


    /**
     * orderId:成功
     * -1：秒杀失败
     * 0：排队中
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value="/result",method= RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoshaUser user,
                                @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //判断用户是否秒杀到该商品
        long result= miaoshaService.getMiaoshaResult(user.getId(),goodsId);
        return Result.success(result);
    }



}
