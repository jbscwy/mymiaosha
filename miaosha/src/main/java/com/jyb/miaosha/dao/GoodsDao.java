package com.jyb.miaosha.dao;

import com.jyb.miaosha.domain.Goods;
import com.jyb.miaosha.domain.MiaoshaGoods;
import com.jyb.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {

//    private Double miaoshaPrice;
//    private Integer stockCount;
//    private Date startDate;
//    private Date endDate;
    @Select("select g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date" +
            " from miaosha_goods mg left join goods g on mg.goods_id=g.id")
    public List<GoodsVo> listGoodsVo();


    @Select("select g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date" +
            " from miaosha_goods mg left join goods g on mg.goods_id=g.id"+
            " where g.id=#{id}")
    public GoodsVo getGoodsVo(@Param("id")long id);


//    为了防止超卖，在减少库存前需要判断库存是否大于0
    @Update("update miaosha_goods set stock_count=stock_count-1 where goods_id=#{goodsId} and stock_count>0")
    void reduceStock(MiaoshaGoods g);
}
