package com.jyb.miaosha.controller;

import com.jyb.miaosha.domain.MiaoshaUser;
import com.jyb.miaosha.redis.GoodsKey;
import com.jyb.miaosha.redis.RedisService;
import com.jyb.miaosha.resuilt.Result;
import com.jyb.miaosha.service.GoodsService;
import com.jyb.miaosha.service.MiaoUserService;
import com.jyb.miaosha.vo.GoodsDetailVo;
import com.jyb.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

//    spring中的应用上下文，即spring容器
    @Autowired
    ApplicationContext applicationContext;

//    从浏览器中获取Cookie
    @RequestMapping(value="/to_list",produces = "text/html")
//   响应体
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response,Model model, MiaoshaUser user) {
        model.addAttribute("user",user);

        //1.取缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
//        查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
//        做页面展示
        model.addAttribute("goodsList",goodsList);
//        return "goods_list";

        //
        SpringWebContext ctx=new SpringWebContext(request,response,
                request.getServletContext(),request.getLocale(),model.asMap(), (org.springframework.context.ApplicationContext) applicationContext);
        //2.手动渲染，使用ThymeleafViewResolver渲染,参数中需要添加配置或容器，保存着信息
        html=thymeleafViewResolver.getTemplateEngine().process("goods_list",ctx);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsList,"",html);
        }
        return html;
    }

    @RequestMapping("/to_detail2/{goodsId}")
    @ResponseBody
    public String toDetail2(HttpServletRequest request, HttpServletResponse response,
                           Model model, MiaoshaUser user,
                           @PathVariable("goodsId")long goodsId) {
        model.addAttribute("user", user);

        //1.取缓存
        String html = redisService.get(GoodsKey.getGoodsList, ""+goodsId, String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }

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
        //        return "goods_detail";
        //
        SpringWebContext ctx=new SpringWebContext(request,response,
                request.getServletContext(),request.getLocale(),model.asMap(), (org.springframework.context.ApplicationContext) applicationContext);
        //2.手动渲染，使用ThymeleafViewResolver渲染,参数中需要添加配置或容器，保存着信息
        html=thymeleafViewResolver.getTemplateEngine().process("goods_detail",ctx);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);
        }
        return html;
    }


    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> toDetail(HttpServletRequest request,
                                          HttpServletResponse response,
                                          Model model, MiaoshaUser user,
                                          @PathVariable("goodsId")long goodsId) {

        GoodsVo good=goodsService.getGoodsVoByGoodsId(goodsId);
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
        GoodsDetailVo goodsDetailVo=new GoodsDetailVo();
        goodsDetailVo.setGoods(good);
        goodsDetailVo.setMiaoshaStatus(miaoshaStatus);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setUser(user);
        return Result.success(goodsDetailVo);
    }






}
