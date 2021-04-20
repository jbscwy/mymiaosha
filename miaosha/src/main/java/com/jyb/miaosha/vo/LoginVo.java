package com.jyb.miaosha.vo;

import com.jyb.miaosha.validator.IsMobile;
import org.hibernate.validator.constraints.Length;
import org.jetbrains.annotations.NotNull;

/**
 * 登录参数
 */
public class LoginVo {
    @NotNull //不能为空
    @IsMobile  //自定义注解
    private String mobile;
    @NotNull
    @Length(min=32) //限制长度
    private String password;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginVo{" +
                "mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
