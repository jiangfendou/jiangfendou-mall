/**
  * Copyright 2022 bejson.com 
  */
package com.jiangfendou.mall.product.vo;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * Auto-generated: 2022-10-10 9:17:32
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class SpuSaveVo {

    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private int publishStatus;
    private List<String> decript;
    private List<String> images;
    private Bounds bounds;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;
}