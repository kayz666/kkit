package com.bestkayz.kkit.hiksdk.exceptions;

import com.bestkayz.kkit.common.core.base.BaseException;
import com.bestkayz.kkit.common.core.base.IBaseError;
import lombok.Getter;

import static com.bestkayz.kkit.hiksdk.exceptions.HikSdkError.HIK_SDK_ERROR;

/**
 * @author: Kayz
 * @create: 2022-02-14
 **/
@Getter
public class HikSDKException extends BaseException {

    // SDK报出的错误代码
    private Integer sdkErrCode;

    public HikSDKException(String message) {
        super(message);
    }

    public HikSDKException(String message, Throwable cause) {
        super(message, cause);
    }

    public HikSDKException(Integer sdkErrCode,String message) {
        super(message);
        this.sdkErrCode = sdkErrCode;
    }

    @Override
    protected IBaseError exception() {
        return HIK_SDK_ERROR;
    }
}
