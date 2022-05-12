package com.bestkayz.kkit.common.core.exceptions.common;

import com.bestkayz.kkit.common.core.base.IBaseError;
import com.bestkayz.kkit.common.core.exceptions.CommonException;

import static com.bestkayz.kkit.common.core.base.BaseError.NOT_FIND_OBJECT;

/**
 * @author: Kayz
 * @create: 2022-02-14
 **/
public class ObjectNotFindException extends CommonException {

    public ObjectNotFindException(String message) {
        super(message);
    }

    public ObjectNotFindException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    protected IBaseError exception() {
        return NOT_FIND_OBJECT;
    }
}
