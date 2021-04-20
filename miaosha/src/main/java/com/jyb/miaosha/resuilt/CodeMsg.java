package com.jyb.miaosha.resuilt;

//状态码信息
public class CodeMsg {
    private int code;
    private String msg;

    //通用异常
    public static CodeMsg SUCCESS=new CodeMsg(0,"success");
    public static CodeMsg SERVER_ERROR=new CodeMsg(500100,"服务器异常");
    public static CodeMsg BIND_ERROR=new CodeMsg(500101,"参数校验异常：%s");

    //登录异常
    public static CodeMsg PASSWORD_EMPTY=new CodeMsg(500211,"密码不能为空");
    public static CodeMsg MOBILEPHONE_EMPTY=new CodeMsg(500212,"手机号不能为空");
    public static CodeMsg  PHONENUMBERFORMAT_ERROR=new CodeMsg(500213,"手机号格式错误");
    public static CodeMsg  MOBILE_NOT_EXIST=new CodeMsg(500214,"手机号不存在");
    public static CodeMsg  PASSWORD_ERROR=new CodeMsg(500215,"密码错误");

    //构造器
    public CodeMsg(int code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public CodeMsg fileArgs(Object... args){
        int code=this.code;
        //将原始的msg拼接到新的msg上
        String message=String.format(this.msg,args);
        return new CodeMsg(code,message);
    }


    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
