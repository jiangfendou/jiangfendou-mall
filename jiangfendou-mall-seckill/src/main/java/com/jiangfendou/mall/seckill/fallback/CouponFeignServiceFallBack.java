package com.jiangfendou.mall.seckill.fallback;

import com.jiangfendou.common.exception.BaseCodeEnum;
import com.jiangfendou.common.utils.R;
import com.jiangfendou.mall.seckill.feign.CouponFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author jiangmh
 *
 * 慢调用比例的熔断时机：
 *      在统计时长内，请求数大于5个，如若大于指定比例阈值的请求数的响应时间都大于最大RT，
 *      那么会熔断该服务，熔断时间为设置的熔断时长
 */
@Slf4j
@Component
public class CouponFeignServiceFallBack implements CouponFeignService {
    @Override
    public R getLates3DaySession() {
        log.info("熔断方法调用。。。。。。。。。。。");
        return R.error(BaseCodeEnum.UN_KNOW_EXCEPTION.getCode(),
            BaseCodeEnum.UN_KNOW_EXCEPTION.getMsg());
    }
}
