package com.jyb.miaosha.controller;

import com.jyb.miaosha.domain.MiaoshaUser;
import com.jyb.miaosha.redis.RedisService;
import com.jyb.miaosha.service.GoodsService;
import com.jyb.miaosha.service.MiaoUserService;
import com.jyb.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoUserService miaoUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

//    从浏览器中获取Cookie
    @RequestMapping("/to_list")
    public String toList(Model model, MiaoshaUser user) {
        model.addAttribute("user",user);
//        查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
//        做页面展示
        model.addAttribute("goodsList",goodsList);
        return "goods_list";
    }

    @RequestMapping("/to_detail/{goodsId}")
    public String toDetail(Model model, MiaoshaUser user,
                           @PathVariable("goodsId")long goodsId) {
        model.addAttribute("user", user);
        GoodsVo good=goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", good);
        //秒杀开始时间和结束时间
        long startAt=good.getStartDate().getTime();
        long endAt=good.getEndDate().getTime();
        //当前时间
        long now=System.currentTimeMillis();
        //设置秒杀状态
        int miaoshaStatus=0;
        int remainSeconds=0;
        if(now<startAt){ //秒杀还没开始，倒计时
            miaoshaStatus=0;
            remainSeconds= (int) ((startAt-now)/1000);
        }else if(now>endAt){ //秒杀结束
            miaoshaStatus=2;
            remainSeconds = -1;
        }else{ //秒杀进行中
            miaoshaStatus=1;
            remainSeconds=0;
        }
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        return "goods_detail";
    }








}
