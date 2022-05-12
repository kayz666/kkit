package com.bestkayz.kkit.hiksdk.exceptions;

import com.bestkayz.kkit.common.core.base.IBaseError;
import lombok.Getter;

/**
 * @author: Kayz
 * @create: 2022-02-14
 **/
@Getter
public enum HikSdkError implements IBaseError {

    HIK_SDK_ERROR("HIK_01","海康SDK 01异常");
    ;

    private String code;

    private String msg;

    HikSdkError(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
