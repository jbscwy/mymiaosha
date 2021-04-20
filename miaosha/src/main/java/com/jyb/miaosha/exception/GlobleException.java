package com.jyb.miaosha.exception;

import com.jyb.miaosha.resuilt.CodeMsg;

/**
 * 接收往外抛出的异常
 */
public class GlobleException extends RuntimeException {

    private static final long serialVersionUID=1L;

    private CodeMsg cm;

    public GlobleException(CodeMsg cm){
        super(cm.toString());
        this.cm=cm;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public CodeMsg getCm() {
        return cm;
    }

    public void setCm(CodeMsg cm) {
        this.cm = cm;
    }
}
