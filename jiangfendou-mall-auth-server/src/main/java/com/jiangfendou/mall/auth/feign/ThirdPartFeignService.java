package com.jiangfendou.mall.auth.feign;

import com.jiangfendou.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("jiangfendou-mall-third-part")
public interface ThirdPartFeignService {

    @RequestMapping("/sms/sendcode")
    public R sendSms(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
