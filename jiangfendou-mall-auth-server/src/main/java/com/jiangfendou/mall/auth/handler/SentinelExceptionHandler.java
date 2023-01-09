package com.jiangfendou.mall.auth.handler;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.fastjson.JSON;
import com.jiangfendou.common.exception.BaseCodeEnum;
import com.jiangfendou.common.utils.R;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiangmh
 */
@Configuration
public class SentinelExceptionHandler implements BlockExceptionHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       BlockException e) throws Exception {
//        String msg = "未知异常";
//        int status = 429;
//
//        if (e instanceof FlowException) {
//            msg = "请求被限流了";
//        } else if (e instanceof ParamFlowException) {
//            msg = "请求被热点参数限流";
//        } else if (e instanceof DegradeException) {
//            msg = "请求被降级了";
//        } else if (e instanceof AuthorityException) {
//            msg = "没有权限访问";
//            status = 401;
//        }
        R error = new R();
        if (e instanceof FlowException) {
            error = R.error(BaseCodeEnum.TO_MANY_REQUEST.getCode(), BaseCodeEnum.TO_MANY_REQUEST.getMsg());
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(JSON.toJSONString(error));
    }
}