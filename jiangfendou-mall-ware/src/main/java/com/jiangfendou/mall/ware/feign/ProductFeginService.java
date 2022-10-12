package com.jiangfendou.mall.ware.feign;

import com.jiangfendou.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("jiangfendou-mall-product")
public interface ProductFeginService {

    /**
     * 1、让所有请求通过网关
     *  @FeignClient("jiangfendou-mall-gateway")
     *  api/product/skuinfo/info/{skuId}
     * 2、让后台直接指定服务
     *  @FeignClient("jiangfendou-mall-product")
     *  product/skuinfo/info/{skuId}
     * */

    @RequestMapping("product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
