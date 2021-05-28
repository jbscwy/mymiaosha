package com.jyb.miaosha.service;

import com.jyb.miaosha.dao.GoodsDao;
import com.jyb.miaosha.domain.Goods;
import com.jyb.miaosha.domain.MiaoshaOrder;
import com.jyb.miaosha.domain.MiaoshaUser;
import com.jyb.miaosha.domain.OrderInfo;
import com.jyb.miaosha.redis.MiaoshaKey;
import com.jyb.miaosha.redis.RedisService;
import com.jyb.miaosha.util.MD5Util;
import com.jyb.miaosha.util.UUIDUtil;
import com.jyb.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

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

    public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
        if(user==null || path==null){
            return false;
        }
        //从redis中获取缓存
        String pathOld=redisService.get(MiaoshaKey.getMiaoshaPath,""+user.getId()+"_"+goodsId,String.class);
        return pathOld.equals(path);
    }

    public String createMiaoshaPath(MiaoshaUser user, long goodsId) {
        if(user==null || goodsId<=0){
            return null;
        }
        //使用MD5加密
        String str= MD5Util.md5(UUIDUtil.uuid()+"123456");
        //保存到redis中,设置过期时间
        redisService.set(MiaoshaKey.getMiaoshaPath,""+user.getId()+"_"+goodsId,str);
        return str;
    }

    public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
        if(user==null || goodsId<=0){
            return null;
        }
        //生成验证码
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = setVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //根据输入的字符串，生成结果
        int rnd = calc(verifyCode);
        //把验证码存到redis中
        redisService.set(MiaoshaKey.verify_code, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    private int calc(String verifyCode) {
        try{
            //获取javaScript引擎
            ScriptEngineManager manager=new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            //根据字符串生成结果
            return (Integer) engine.eval(verifyCode);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }

    private static char[] ops=new char[]{'+','-','*'};

    private String setVerifyCode(Random random) {
        int num1=random.nextInt(10);
        int num2=random.nextInt(10);
        int num3=random.nextInt(10);
        char op1=ops[random.nextInt(3)];
        char op2=ops[random.nextInt(3)];
        String exp= ""+num1+op1+num2+op2+num3;
        return exp;

    }

    public boolean checkVerifyCode(MiaoshaUser user, long goodsId,int verifyCode) {
        if(user==null || goodsId<=0){
            return false;
        }
        //从redis中获取缓存
        Integer verifyOld=redisService.get(MiaoshaKey.verify_code,user.getId()+","+goodsId,Integer.class);
        if(verifyOld==null || verifyOld-verifyCode!=0){
            return false;
        }
        //删除redis中验证码
        redisService.delete(MiaoshaKey.verify_code,user.getId()+","+goodsId);
        return true;
    }
}
