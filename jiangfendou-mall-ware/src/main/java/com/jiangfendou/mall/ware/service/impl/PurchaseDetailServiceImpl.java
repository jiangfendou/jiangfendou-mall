package com.jiangfendou.mall.ware.service.impl;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.Query;

import com.jiangfendou.mall.ware.dao.PurchaseDetailDao;
import com.jiangfendou.mall.ware.entity.PurchaseDetailEntity;
import com.jiangfendou.mall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity>
    implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> purchaseDetailEntityQueryWrapper = new QueryWrapper<>();
        String key = (String)params.get("key");
        if (StringUtils.isNotBlank(key)) {
            purchaseDetailEntityQueryWrapper.and(w -> {
                w.eq("purchase_id", key).or().eq("sku_id", key);
            });
        }
        String status = (String)params.get("status");
        if (StringUtils.isNotBlank(status)) {
            purchaseDetailEntityQueryWrapper.eq("status", status);
        }
        String wareId = (String)params.get("wareId");
        if (StringUtils.isNotBlank(wareId)) {
            purchaseDetailEntityQueryWrapper.eq("ware_id", wareId);
        }
        IPage<PurchaseDetailEntity> page = this.page(
            new Query<PurchaseDetailEntity>().getPage(params),
            purchaseDetailEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(Long id) {
        List<PurchaseDetailEntity> purchaseDetailEntities =
            this.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id));
        return purchaseDetailEntities;
    }

}