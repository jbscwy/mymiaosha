package com.jyb.miaosha.validator;

import com.jyb.miaosha.util.ValidatorUtil;
import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 实现定义验证给定约束的逻辑的接口
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {
    /**
     * 初始化
     * @param isMobile
     */
    private boolean required=false;

    /**
     * 初始化
     * @param isMobile
     */
    @Override
    public void initialize(IsMobile isMobile) {
        required=isMobile.required();
    }

    /**
     * 校验手机格式
     * @param s
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(required){
            return ValidatorUtil.isMobile(s);
        }else{
            if(StringUtils.isEmpty(s)){
                return true;
            }else{
                return ValidatorUtil.isMobile(s);
            }
        }
    }
}
