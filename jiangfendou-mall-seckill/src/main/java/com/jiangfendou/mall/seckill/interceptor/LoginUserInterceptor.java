package com.jiangfendou.mall.seckill.interceptor;

import com.jiangfendou.common.constant.AuthServerConstant;
import com.jiangfendou.common.vo.MemberResponseVo;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberResponseVo> loginUser = new InheritableThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/kill", request.getRequestURI());
        if (match ) {
            MemberResponseVo attribute = (MemberResponseVo)request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
            if (attribute != null) {
                loginUser.set(attribute);
                return true;
            } else {
                // 没登录就去登录
                request.getSession().setAttribute("msg", "请先登录");
                response.sendRedirect("http://auth.jiangfendou.com/login.html");
                return false;
            }
        }
        return true;
    }
}
