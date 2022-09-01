package com.jiangfendou.mall.product.service.impl;

import com.jiangfendou.mall.product.service.CategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.Query;

import com.jiangfendou.mall.product.dao.AttrGroupDao;
import com.jiangfendou.mall.product.entity.AttrGroupEntity;
import com.jiangfendou.mall.product.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long cateLogId) {
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
        String key = String.valueOf(params.get("key") == null ? "" : params.get("key"));
        if (cateLogId != 0) {
            queryWrapper.eq("catelog_id", cateLogId);
        }
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and((obj) -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);

            });
        }
        IPage<AttrGroupEntity> page = this.page(
            new Query<AttrGroupEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public AttrGroupEntity getAttrGroupDetail(Long attrGroupId) throws Exception {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        if (attrGroup == null) {
            throw new Exception();
        }
        attrGroup.setCatelogPath(categoryService.getCateLogPath(attrGroup.getCatelogId()));
        return attrGroup;
    }

}