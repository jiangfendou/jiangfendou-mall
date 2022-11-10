package com.jiangfendou.common.exception;

/**
 * @author jiangmh
 */
public enum BaseCodeEnum {

    UN_KNOW_EXCEPTION(1000, "系统异常"),

    VALID_EXCEPTION(1001, "参数校验失败"),

    PRODUCT_UP_ERROR(1002, "商品上架异常");

    private int code;

    private String msg;

    BaseCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
