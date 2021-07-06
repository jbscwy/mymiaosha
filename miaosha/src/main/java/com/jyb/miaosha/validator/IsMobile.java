package com.jyb.miaosha.validator;


@java.lang.annotation.Documented
//这里对手机格式进行验证
@javax.validation.Constraint(validatedBy = {IsMobileValidator.class})
@java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.CONSTRUCTOR, java.lang.annotation.ElementType.PARAMETER})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface IsMobile {

    boolean required() default true;

    java.lang.String message() default "手机号格式有误";

    java.lang.Class<?>[] groups() default {};

    java.lang.Class<? extends javax.validation.Payload>[] payload() default {};
}
