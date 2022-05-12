package com.bestkayz.kkit.common.core.exceptions;

import com.bestkayz.kkit.common.core.base.BaseException;

/**
 * @author: Kayz
 * @create: 2022-02-14
 **/
public abstract class CommonException extends BaseException {

    public CommonException(String message) {
        super(message);
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
    }

}
