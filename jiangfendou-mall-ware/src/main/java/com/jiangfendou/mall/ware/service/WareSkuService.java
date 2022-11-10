package com.jiangfendou.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;


import com.jiangfendou.common.to.SkuHasStockVo;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.mall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author jiangfendou
 * @email 49323245@qq.com
 * @date 2022-05-10 11:29:39
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);
}

