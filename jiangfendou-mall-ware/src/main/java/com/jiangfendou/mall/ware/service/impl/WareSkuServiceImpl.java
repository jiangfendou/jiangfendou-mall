package com.jiangfendou.mall.ware.service.impl;

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


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity>
    implements WareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
            new Query<WareSkuEntity>().getPage(params),
            new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

}