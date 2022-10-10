package com.jiangfendou.mall.product.service.impl;

import com.jiangfendou.common.constant.ProductConstant;
import com.jiangfendou.mall.product.dao.AttrAttrgroupRelationDao;
import com.jiangfendou.mall.product.dao.AttrGroupDao;
import com.jiangfendou.mall.product.dao.CategoryDao;
import com.jiangfendou.mall.product.entity.AttrAttrgroupRelationEntity;
import com.jiangfendou.mall.product.entity.AttrGroupEntity;
import com.jiangfendou.mall.product.entity.CategoryEntity;
import com.jiangfendou.mall.product.service.CategoryService;
import com.jiangfendou.mall.product.vo.AttrGroupVo;
import com.jiangfendou.mall.product.vo.AttrResponseVo;
import com.jiangfendou.mall.product.vo.AttrVo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

import com.jiangfendou.mall.product.dao.AttrDao;
import com.jiangfendou.mall.product.entity.AttrEntity;
import com.jiangfendou.mall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryService categoryService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
            new Query<AttrEntity>().getPage(params),
            new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAttr(AttrVo attrVo) {
        // save attr
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.save(attrEntity);

        if (attrVo.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() &&
            attrVo.getAttrGroupId() != null) {
            // 保存关联关系
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
    }

    @Override
    public PageUtils searchAttrs(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<AttrEntity>()
            .eq("attr_type", "base".equalsIgnoreCase(attrType) ?
                ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if (catelogId != 0) {
            attrEntityQueryWrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            attrEntityQueryWrapper.and(wrapper -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(
            new Query<AttrEntity>().getPage(params),
            attrEntityQueryWrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrResponseVo> attrResponseVoList = records.stream().map(attrEntity -> {
            AttrResponseVo attrResponseVo = new AttrResponseVo();
            BeanUtils.copyProperties(attrEntity, attrResponseVo);
            if ("base".equalsIgnoreCase(attrType)) {
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity =
                    attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_id", attrEntity.getAttrId()));
                if (attrAttrgroupRelationEntity != null && attrAttrgroupRelationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                    if (attrGroupEntity != null) {
                        attrResponseVo.setGroupName(attrGroupEntity.getAttrGroupName());
                    }
                }
            }
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrResponseVo.setCatelogName(categoryEntity.getName());

            }
            return attrResponseVo;
        }).collect(Collectors.toList());
        pageUtils.setList(attrResponseVoList);
        return pageUtils;
    }

    @Override
    public AttrResponseVo getAttrInfo(Long attrId) throws Exception {
        AttrResponseVo attrResponseVo = new AttrResponseVo();
        AttrEntity attrEntity = this.getById(attrId);
        if (attrEntity == null) {
            throw new Exception();
        }
        BeanUtils.copyProperties(attrEntity, attrResponseVo);
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        AttrGroupEntity attrGroupEntity = new AttrGroupEntity();
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
             attrAttrgroupRelationEntity =
                attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", attrId));
            if (attrAttrgroupRelationEntity == null) {
                throw new Exception();
            }
            attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
            if (attrGroupEntity == null) {
                throw new Exception();
            }
            attrResponseVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
            attrResponseVo.setGroupName(attrGroupEntity.getAttrGroupName());
        }

        Long[] cateLogPath = categoryService.getCateLogPath(attrGroupEntity.getCatelogId());
        CategoryEntity categoryEntity = categoryDao.selectById(attrGroupEntity.getCatelogId());
        if (categoryEntity == null) {
            throw new Exception();
        }
        attrResponseVo.setCatelogPath(cateLogPath);
        attrResponseVo.setCatelogName(categoryEntity.getName());
        return attrResponseVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            Integer count = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .eq("attr_id", attr.getAttrId()));
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
            if (count > 0) {

                attrAttrgroupRelationDao.update(attrAttrgroupRelationEntity,
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            } else {
                attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
            }
        }
    }

    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> list =
            attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .eq("attr_group_id", attrgroupId));
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<Long> attrIds = list.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        Collection<AttrEntity> attrEntities = this.listByIds(attrIds);
        return (List<AttrEntity>)attrEntities;
    }

    @Override
    public void deleteRelation(AttrGroupVo[] attrGroupVo) {
        List<AttrGroupVo> attrGroupVos = Arrays.asList(attrGroupVo);
        attrAttrgroupRelationDao.deleteBatchRelation(attrGroupVos);
    }

    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrGroupId) {
        // 1、当前分组只能关联自己所属的分类的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
        Long cateLogId = attrGroupEntity.getCatelogId();
        // 2、当前分组只能关联别的分组没有应用的属性
        // 2-1、当前分类下的其他分组
        List<AttrGroupEntity> group =
            attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cateLogId));
        List<Long> attrGroupList = group.stream().map(item -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());

        // 2-2、这些分组关联的属性
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities =
            attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .in("attr_group_id", attrGroupList));
        List<Long> attrIds = attrAttrgroupRelationEntities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        // 2-3、从当前分类的所有属性中移除这些属性
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<AttrEntity>()
            .eq("catelog_id", cateLogId)
            .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (!CollectionUtils.isEmpty(attrIds)) {
            attrEntityQueryWrapper.notIn("attr_id", attrIds);
        }

        String key = (String)params.get("key");
        if (StringUtils.isNotBlank(key)) {
            attrEntityQueryWrapper.and(w -> {
               w.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), attrEntityQueryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }
}