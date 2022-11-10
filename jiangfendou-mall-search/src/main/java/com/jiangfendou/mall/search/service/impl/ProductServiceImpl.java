package com.jiangfendou.mall.search.service.impl;

import com.alibaba.fastjson2.JSON;
import com.jiangfendou.common.to.es.SkuEsModel;
import com.jiangfendou.mall.search.config.ElasticSearchConfig;
import com.jiangfendou.mall.search.constant.EsConstant;
import com.jiangfendou.mall.search.service.ProductService;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        // 数据保存ES
        // 数据保存索引，product，建立好映射关系
        // es中保存数据
        BulkRequest bulkRequest = new BulkRequest();
        skuEsModels.forEach(item -> {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(String.valueOf(item.getSkuId()));
            indexRequest.source(JSON.toJSONString(item), XContentType.JSON);
            bulkRequest.add(indexRequest);
        });
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
        // TODO 如果批量错误
        List<Integer> ids = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getItemId();
        }).collect(Collectors.toList());
        log.info("商品上架完成：{}", ids);
        return bulk.hasFailures();
    }
}
