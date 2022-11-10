package com.jiangfendou.mall.product.feign;

import com.jiangfendou.common.to.es.SkuEsModel;
import com.jiangfendou.common.utils.R;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author jiangmh
 */
@FeignClient("jiangfendou-mall-search")
public interface SearchFeignService {

    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
