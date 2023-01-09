package com.jiangfendou.common.exception;

/**
 * @author jiangmh
 */
public enum BaseCodeEnum {

    UN_KNOW_EXCEPTION(1000, "系统异常"),

    VALID_EXCEPTION(1001, "参数校验失败"),

    PRODUCT_UP_ERROR(1002, "商品上架异常"),

    VALID_SMS_CODE_EXCEPTION(1003, "验证码获取频率太高稍后再试"),

    PRODUCT_UP_EXCEPTION(1004,"商品上架异常"),

    USER_EXIST_EXCEPTION(1005,"存在相同的用户"),

    PHONE_EXIST_EXCEPTION(1006,"存在相同的手机号"),

    NO_STOCK_EXCEPTION(1007,"商品库存不足"),

    LOGINACCT_PASSWORD_EXCEPTION(1008,"账号或密码错误"),

    TO_MANY_REQUEST(10002,"请求流量过大，请稍后再试");

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
