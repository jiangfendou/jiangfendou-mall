package com.jiangfendou.mall.product.exception;

import com.jiangfendou.common.exception.BaseCodeEnum;
import com.jiangfendou.common.utils.R;
import java.util.HashMap;
import java.util.Map;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProductExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return R.error(BaseCodeEnum.VALID_EXCEPTION.getCode(), BaseCodeEnum.VALID_EXCEPTION.getMsg())
            .put("data", errorMap);
    }

    @ExceptionHandler(value = Exception.class)
    public R handException(Exception e) {
        e.printStackTrace();
        return R.error(BaseCodeEnum.UN_KNOW_EXCEPTION.getCode(), BaseCodeEnum.UN_KNOW_EXCEPTION.getMsg());
    }
}
