package com.bestkayz.kkit.common.core.base;

import lombok.Getter;

/**
 * 基础错误枚举
 * @Author : kayz
 * @Date : 2020/1/22
 * @Version 1.0
 */
@Getter
public enum BaseError implements IBaseError {

    SYSTEM_ERROR("-1","系统内部错误"),
    SYSTEM_BUSY("1","系统繁忙"),
    SYSTEM_MISSING_PARAMETER("2","缺少参数"),
    SYSTEM_PARAMETER_ERROR("3","参数错误"),
    NOT_FIND_OBJECT("4", "未找到对象"),
    ENUM_PARSE_EXCEPTION("5","枚举解析异常"), //EnumParseException
    ;

    private String code;

    private String msg;

    BaseError(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


}
