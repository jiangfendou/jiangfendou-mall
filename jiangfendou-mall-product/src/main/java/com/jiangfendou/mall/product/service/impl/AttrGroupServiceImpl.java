package com.jiangfendou.mall.product.service.impl;

import com.jiangfendou.mall.product.entity.AttrEntity;
import com.jiangfendou.mall.product.service.AttrService;
import com.jiangfendou.mall.product.service.CategoryService;
import com.jiangfendou.mall.product.vo.AttrGroupWithAttrsVo;
import com.jiangfendou.mall.product.vo.SpuItemAttrGroupVo;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    private AttrService attrService;


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

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrs(Long catelogId) {
        // 查询分组信息
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>()
            .eq("catelog_id", catelogId));
        // 查询所有属性
        List<AttrGroupWithAttrsVo> attrGroupWithAttrsVos = attrGroupEntities.stream().map(item -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(item, attrGroupWithAttrsVo);
            List<AttrEntity> attrs = attrService.getRelationAttr(attrGroupWithAttrsVo.getAttrGroupId());
            attrGroupWithAttrsVo.setAttrs(attrs);
            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());

        return attrGroupWithAttrsVos;
    }

    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        // 查出当前sup对应的所有属性的分组信息 以及当前分组下的所有属性对应的值
        AttrGroupDao baseMapper = this.getBaseMapper();
        List<SpuItemAttrGroupVo> vos = baseMapper.getAttrGroupWithAttrsBySpuId(spuId,catalogId);

        return vos;
    }

}