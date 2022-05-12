package com.bestkayz.kkit.common.core.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.Instant;
import java.time.ZonedDateTime;

import static com.bestkayz.kkit.common.core.base.BaseError.SYSTEM_ERROR;


/**
 * 通用返回
 * @Author : kayz
 * @Date : 2020/1/22
 * @Version 1.0
 */
@ApiModel(description = "通用返回结构体")
@Data
public class Result <T>{

    public static final String SUCCESSFUL_CODE = "0";
    public static final String SUCCESSFUL_MSG = "success";

    @ApiModelProperty(value = "结果代码", required = true)
    private String code;

    @ApiModelProperty(value = "结果信息")
    private String msg;

    @ApiModelProperty(value = "结果生成时间戳")
    private Instant time = ZonedDateTime.now().toInstant();

    @ApiModelProperty(value = "处理结果数据")
    private T data;

    @ApiModelProperty(value = "错误详情")
    private String details;

    public Result() {
        this(SYSTEM_ERROR.getCode(),SYSTEM_ERROR.getMsg(),null,"");
    }

    public Result(String code, String msg, T data,String details) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.details = details;
    }

    public boolean succeed(){
        return SUCCESSFUL_CODE.equals(this.code);
    }

    public boolean succeedAndNotNull(){
        return this.succeed() && this.data != null;
    }

    public static Result success() {
        return success(null);
    }

    public static Result success(Object data) {
        return Result.success(null,data);
    }

    public static Result success(String details, Object data) {
        return new Result<>(SUCCESSFUL_CODE, SUCCESSFUL_MSG, data,details);
    }

    public static Result fail(){
        return Result.fail(SYSTEM_ERROR);
    }

    public static Result fail(IBaseError iBaseError) {
        return Result.fail(iBaseError, iBaseError.getMsg());
    }

    public static Result fail(String details) {
        return Result.fail(SYSTEM_ERROR, details);
    }

    public static Result fail(String details,Object data) {
        return Result.fail(SYSTEM_ERROR,details,data);
    }

    public static Result fail(IBaseError iBaseError, String details) {
        return Result.fail(iBaseError, details,null);
    }

    public static Result fail(IBaseError iBaseError, String details, Object data) {
        return new Result(iBaseError.getCode(),iBaseError.getMsg(),data,details);
    }

    public static Result fail(String code, String msg, Object data,String details){
        return new Result(code,msg,data,details);
    }

}
