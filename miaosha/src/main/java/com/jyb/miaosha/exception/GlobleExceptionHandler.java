package com.jyb.miaosha.exception;


import com.jyb.miaosha.resuilt.CodeMsg;
import com.jyb.miaosha.resuilt.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
@ResponseBody
public class GlobleExceptionHandler {

    @ExceptionHandler(value=Exception.class)
    public Result<String> exceptionHandle(HttpServletRequest request,Exception e){
        if(e instanceof GlobleException){
          GlobleException ex=(GlobleException)e;
          return Result.error(ex.getCm());
        } else if(e instanceof BindException){
            BindException ex=(BindException)e;
            List<ObjectError> errors=ex.getAllErrors();
            ObjectError error=errors.get(0);
            String msg=error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fileArgs(msg));
        }else{
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
