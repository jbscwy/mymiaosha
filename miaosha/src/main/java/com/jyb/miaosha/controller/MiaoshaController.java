package com.jyb.miaosha.controller;

import com.jyb.miaosha.access.AccessLimit;
import com.jyb.miaosha.domain.MiaoshaOrder;
import com.jyb.miaosha.domain.MiaoshaUser;
import com.jyb.miaosha.domain.OrderInfo;
import com.jyb.miaosha.rabbitmq.MQSender;
import com.jyb.miaosha.rabbitmq.MiaoshaMessage;
import com.jyb.miaosha.redis.AccessKey;
import com.jyb.miaosha.redis.GoodsKey;
import com.jyb.miaosha.redis.MiaoshaKey;
import com.jyb.miaosha.redis.RedisService;
import com.jyb.miaosha.resuilt.CodeMsg;
import com.jyb.miaosha.resuilt.Result;
import com.jyb.miaosha.service.GoodsService;
import com.jyb.miaosha.service.MiaoUserService;
import com.jyb.miaosha.service.MiaoshaService;
import com.jyb.miaosha.service.OrderService;
import com.jyb.miaosha.util.MD5Util;
import com.jyb.miaosha.util.UUIDUtil;
import com.jyb.miaosha.vo.GoodsVo;
import com.rabbitmq.client.AMQP;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
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
    @RequestMapping(value="/{path}/do_miaosha",method= RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser user,
                                @RequestParam("goodsId")long goodsId,
                                @PathVariable("path")String path){
        model.addAttribute("user",user);
        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证path路径是否正确
        boolean check=miaoshaService.checkPath(user,goodsId,path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
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
    @AccessLimit(seconds=5,maxCount=10,needLogin=true)
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


    //添加验证码校验
    @AccessLimit(seconds=5,maxCount=5,needLogin=true)
    @RequestMapping(value="/path",method= RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request,MiaoshaUser user,
                                         @RequestParam("goodsId")long goodsId,
                                         @RequestParam(value="verifyCode",defaultValue = "0")int verifyCode) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        String requestURI = request.getRequestURI();
        String key=requestURI+"_"+user.getId();
        Integer count=redisService.get(AccessKey.access,key,Integer.class);
        if(count==null){
            redisService.set(AccessKey.access,key,1);
        } else if(count<5){
            redisService.incr(AccessKey.access,key);
        }else{
            return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
        }

        //检查验证码
        boolean check=miaoshaService.checkVerifyCode(user,goodsId,verifyCode);
        if(!check){
            return Result.error(CodeMsg.VERICODE_ERROR);
        }
        String path=miaoshaService.createMiaoshaPath(user,goodsId);
        return Result.success(path);
    }



    @RequestMapping(value="/verifyCode",method= RequestMethod.GET)
    @ResponseBody
    public Result<String> getVerifyCode(HttpServletResponse response, MiaoshaUser user,
                                        @RequestParam("goodsId")long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try{
            //生成验证码图片
            BufferedImage image=miaoshaService.createVerifyCode(user,goodsId);
            //直接将图片插入到httpServletResponse中，返回客户端
            OutputStream out=response.getOutputStream();
            ImageIO.write(image,"JPEG",out);
            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }

}
