package com.bestkayz.kkit.common.core.base;


import lombok.Getter;

import java.util.List;

/**
 * @Author : kayz
 * @Date : 2020/1/22
 * @Version 1.0
 */
@Getter
public abstract class BaseException extends RuntimeException{

    private final IBaseError iBaseError;

    public BaseException(String message) {
        super(message);
        this.iBaseError = exception();
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.iBaseError = exception();
    }

    protected abstract IBaseError exception();

    public String toShortExceptionMsg(){

        return toShortExceptionMsg(3);
    }

    public String toShortExceptionMsg(int line){

        return this.getMessage();
    }
}
