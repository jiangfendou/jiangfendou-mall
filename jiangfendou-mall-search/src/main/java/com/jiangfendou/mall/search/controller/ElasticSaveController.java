package com.jiangfendou.mall.search.controller;

import com.jiangfendou.common.exception.BaseCodeEnum;
import com.jiangfendou.common.to.es.SkuEsModel;
import com.jiangfendou.common.utils.R;
import com.jiangfendou.mall.search.service.ProductService;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {

    @Autowired
    private ProductService productService;

    /**
     * 上架商品
     * */
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
        boolean hasSuccess = false;
        try {
            hasSuccess = productService.productStatusUp(skuEsModels);
        } catch (Exception e) {
            log.error(BaseCodeEnum.UN_KNOW_EXCEPTION.getMsg() + "error message:{}", e);
            return R.error(BaseCodeEnum.PRODUCT_UP_ERROR.getCode(), BaseCodeEnum.UN_KNOW_EXCEPTION.getMsg());
        }
        if (!hasSuccess) {
            return R.ok();
        }
        return R.error(BaseCodeEnum.PRODUCT_UP_ERROR.getCode(), BaseCodeEnum.UN_KNOW_EXCEPTION.getMsg());
    }
}
