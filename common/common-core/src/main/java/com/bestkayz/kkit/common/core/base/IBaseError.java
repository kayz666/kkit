package com.bestkayz.kkit.common.core.base;

import com.bestkayz.kkit.common.core.exceptions.common.EnumParseException;

/**
 * 错误枚举
 * @Author : kayz
 * @Date : 2020/1/22
 * @Version 1.0
 */
public interface IBaseError {

    /**
     * 返回code
     *
     * @return
     */
    String getCode();

    /**
     * 返回msg
     *
     * @return
     */
    String getMsg();

    static BaseError valueOfCode(Integer code){
        for (BaseError value : BaseError.values()) {
            if (value.getCode().equals(code)){
                return value;
            }
        }
        throw new EnumParseException("未能解析此异常代码:"+code);
    }

}
