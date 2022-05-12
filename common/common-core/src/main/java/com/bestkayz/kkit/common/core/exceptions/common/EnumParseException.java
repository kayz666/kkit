package com.bestkayz.kkit.common.core.exceptions.common;

import com.bestkayz.kkit.common.core.base.IBaseError;
import com.bestkayz.kkit.common.core.exceptions.CommonException;

import static com.bestkayz.kkit.common.core.base.BaseError.ENUM_PARSE_EXCEPTION;

/**
 * @author: Kayz
 * @create: 2022-02-14
 **/
public class EnumParseException extends CommonException {
    public EnumParseException(String message) {
        super(message);
    }

    public EnumParseException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    protected IBaseError exception() {
        return ENUM_PARSE_EXCEPTION;
    }
}
