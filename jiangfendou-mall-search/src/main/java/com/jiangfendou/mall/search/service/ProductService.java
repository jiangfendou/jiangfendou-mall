package com.jiangfendou.mall.search.service;

import com.jiangfendou.common.to.es.SkuEsModel;
import java.io.IOException;
import java.util.List;

public interface ProductService {

    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
