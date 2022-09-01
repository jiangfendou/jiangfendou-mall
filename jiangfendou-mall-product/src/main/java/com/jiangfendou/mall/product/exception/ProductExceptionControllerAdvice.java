package com.jiangfendou.mall.product.exception;

import com.jiangfendou.common.exception.BaseCodeEnum;
import com.jiangfendou.common.utils.R;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.jiangfendou.mall.product.controller")
public class ProductExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        log.error("数据校验出现问题 = {}, 异常类型 = {}", e.getMessage(), e.getClass());
        Map<String, String> map = new HashMap<>();
        BindingResult bindingResult = e.getBindingResult();
        // 获取校验的错误
        bindingResult.getFieldErrors().forEach( item -> {
            map.put(item.getField(), item.getDefaultMessage());
        });
        return R.error(BaseCodeEnum.VALID_EXCEPTION.getCode(), BaseCodeEnum.VALID_EXCEPTION.getMsg())
            .put("data", map);
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable e) {
        System.out.println("error message:" + e);
        return R.error(BaseCodeEnum.UN_KNOW_EXCEPTION.getCode(), BaseCodeEnum.UN_KNOW_EXCEPTION.getMsg());
    }
}
