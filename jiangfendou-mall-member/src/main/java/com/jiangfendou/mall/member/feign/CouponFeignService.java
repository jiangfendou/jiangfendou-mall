package com.jiangfendou.mall.member.feign;

import com.jiangfendou.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 声明一个调用的接口
 * */
@FeignClient("jiangfendou-mall-coupon")
public interface CouponFeignService {

    @RequestMapping("/coupon/coupon/member/list")
    public R memberCoupons();
}
