package com.jiangfendou.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.mall.product.entity.SpuInfoEntity;

import com.jiangfendou.mall.product.vo.SpuSaveVo;
import java.util.Map;

/**
 * spu信息
 *
 * @author jiangfendou
 * @email 49323245@qq.com
 * @date 2022-05-08 20:27:20
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo spuSaveVo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);
}

