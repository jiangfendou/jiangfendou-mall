package com.jiangfendou.mall.ware.service.impl;

import com.jiangfendou.common.to.SkuHasStockVo;
import com.jiangfendou.common.utils.R;
import com.jiangfendou.mall.ware.feign.ProductFeginService;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.Query;

import com.jiangfendou.mall.ware.dao.WareSkuDao;
import com.jiangfendou.mall.ware.entity.WareSkuEntity;
import com.jiangfendou.mall.ware.service.WareSkuService;
import org.springframework.util.CollectionUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity>
    implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private ProductFeginService productFeginService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wareSkuEntityQueryWrapper = new QueryWrapper<>();
        String skuId = (String)params.get("skuId");
        if (StringUtils.isNotBlank(skuId)) {
            wareSkuEntityQueryWrapper.eq("sku_id", skuId);

        }
        String wareId = (String)params.get("wareId");
        if (StringUtils.isNotBlank(wareId)) {
            wareSkuEntityQueryWrapper.eq("ware_id", wareId);

        }
        IPage<WareSkuEntity> page = this.page(
            new Query<WareSkuEntity>().getPage(params),
            wareSkuEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        List<WareSkuEntity> wareSkuEntities =
            wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId)
                .eq("ware_id", wareId));
        if (CollectionUtils.isEmpty(wareSkuEntities)) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            // TODO 远程查询sku的name
            try {
                R info = productFeginService.info(skuId);
                Map<String, Object> map = (Map<String, Object>)info.get("skuInfo");
                if (info.getCode() == 0) {
                    wareSkuEntity.setSkuName((String)map.get("skuName"));
                }
            } catch (Exception e) {
                log.warn("远程获取sku name失败");
            }

            wareSkuDao.insert(wareSkuEntity);
        } else {
            this.wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> skuHasStockVos = skuIds.stream().map(skuId -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            Long count = this.baseMapper.getSkuStock(skuId);
            skuHasStockVo.setSkuId(skuId);
            skuHasStockVo.setHasStock(count != null && count > 0);
            return skuHasStockVo;
        }).collect(Collectors.toList());

        return skuHasStockVos;
    }
}