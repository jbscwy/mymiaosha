package com.jyb.miaosha.service;

import com.jyb.miaosha.dao.GoodsDao;
import com.jyb.miaosha.domain.Goods;
import com.jyb.miaosha.domain.MiaoshaGoods;
import com.jyb.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jyb
 * @since 2021-04-22 20:16
 */
@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }


    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVo(goodsId);
    }

    public boolean reduceStock(GoodsVo good) {
        MiaoshaGoods g=new MiaoshaGoods();
        g.setGoodsId(good.getId());
        int ret=goodsDao.reduceStock(g);
        return ret>0;
    }
}
