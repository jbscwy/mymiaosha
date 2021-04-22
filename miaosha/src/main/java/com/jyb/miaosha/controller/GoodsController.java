package com.jyb.miaosha.controller;

import com.jyb.miaosha.domain.MiaoshaUser;
import com.jyb.miaosha.service.MiaoUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoUserService miaoUserService;

//    从浏览器中获取Cookie
    @RequestMapping("/to_list")
    public String toList(Model model, MiaoshaUser user) {
        model.addAttribute("user",user);
        return "goods_list";
    }

}
