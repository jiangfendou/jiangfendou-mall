package com.jiangfendou.mall.search.vo;

import java.util.List;
import lombok.Data;

@Data
public class SearchParamVo {

    private String keyword;

    /** private */
    private Long catalog3Id;

    /**
     * 排序条件
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hotScore_asc/desc
     * */
    private String sort;

    /**
     * 过滤条件
     * hasStock(是否有货)、skuPrice区间、brandId、catalog3Id、attrs
     * hasStock=0/1
     * skuPrice=1~500
     *
     * hasStock(是否有货) -> 0  无库存 1、有库存
     * */

    private Integer  hasStock;

    private String  skuPrice;

    private List<Long> brandId;

    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 原生的所有查询条件
     */
    private String _queryString;



}
