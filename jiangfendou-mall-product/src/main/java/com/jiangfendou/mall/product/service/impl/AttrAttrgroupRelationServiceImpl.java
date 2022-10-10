package com.jiangfendou.mall.product.service.impl;

import com.jiangfendou.mall.product.vo.AttrGroupVo;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.Query;

import com.jiangfendou.mall.product.dao.AttrAttrgroupRelationDao;
import com.jiangfendou.mall.product.entity.AttrAttrgroupRelationEntity;
import com.jiangfendou.mall.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity>
    implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
            new Query<AttrAttrgroupRelationEntity>().getPage(params),
            new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBatch(List<AttrGroupVo> vos) {
        List<AttrAttrgroupRelationEntity> collect = vos.stream().map(item -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(item.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(item.getAttrGroupId());
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

}