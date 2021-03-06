package com.jyb.miaosha.dao;

import com.jyb.miaosha.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MiaoshaUserDao {

    @Select("select * from miaosha_user where id=#{id}")
    public MiaoshaUser getById(@Param("id") long id);


//    传入类
    @Update("update miaosha_user set password=#{password} where id=#{id}")
    public void update(MiaoshaUser toBeUpdate);
}
