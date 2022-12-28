package com.jiangfendou.mall.order.feign;

import com.jiangfendou.mall.order.vo.PayVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient("gulimall-third-party")
public interface ThridFeignService {

//    @GetMapping(value = "/pay",consumes = "application/json")
//    String pay(@RequestBody PayVo vo) throws AlipayApiException;

}
