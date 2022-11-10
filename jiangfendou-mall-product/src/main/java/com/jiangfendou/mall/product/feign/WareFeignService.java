package com.jiangfendou.mall.product.feign;

import com.jiangfendou.common.utils.R;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("jiangfendou-mall-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/has-stock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}
