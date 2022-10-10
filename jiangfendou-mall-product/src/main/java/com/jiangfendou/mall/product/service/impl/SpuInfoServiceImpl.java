package com.jiangfendou.mall.product.service.impl;

import com.jiangfendou.mall.product.vo.SpuSaveVo;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.Query;

import com.jiangfendou.mall.product.dao.SpuInfoDao;
import com.jiangfendou.mall.product.entity.SpuInfoEntity;
import com.jiangfendou.mall.product.service.SpuInfoService;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
            new Query<SpuInfoEntity>().getPage(params),
            new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        // 1、保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        this.saveBaseSpuInfo(spuInfoEntity);
        // 2、保存spu的描述图片 pms_spu_info_desc
        // 3、保存spu的图片集 pms_spu_images
        // 4、保存spu的规格参数 pms_product_attr_value
        // 5、保存spu的积分信息 sms_spu_bounds
        // 5、保存当前spu对应的所有sku信息
        // 5-1、sku的基本信息 pms_sku_info
        // 5-2、sku的图片信息 pms_sku_images
        // 5-3、sku的销售属性信息 pms_sku_sale_attr_value
        // 5-4、sku的优惠、满减等信息 sms -> sms_sku_ladder\sms_sku_full_reduction\sms_member_peice
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

}