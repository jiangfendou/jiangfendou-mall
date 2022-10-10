package com.jiangfendou.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jiangfendou.mall.product.dao.BrandDao;
import com.jiangfendou.mall.product.dao.CategoryDao;
import com.jiangfendou.mall.product.entity.BrandEntity;
import com.jiangfendou.mall.product.entity.CategoryEntity;
import com.jiangfendou.mall.product.service.BrandService;
import com.jiangfendou.mall.product.vo.BrandResponseVo;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.Query;

import com.jiangfendou.mall.product.dao.CategoryBrandRelationDao;
import com.jiangfendou.mall.product.entity.CategoryBrandRelationEntity;
import com.jiangfendou.mall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity>
    implements CategoryBrandRelationService {

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryBrandRelationDao categoryBrandRelationDao;

    @Autowired
    BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
            new Query<CategoryBrandRelationEntity>().getPage(params),
            new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveCategoryBrandRelation(CategoryBrandRelationEntity categoryBrandRelation) throws Exception {
        BrandEntity brandEntity = brandDao.selectById(categoryBrandRelation.getBrandId());
        if (brandEntity == null) {
            throw new Exception();
        }
        CategoryEntity categoryEntity = categoryDao.selectById(categoryBrandRelation.getCatelogId());
        if (categoryEntity == null) {
            throw new Exception();
        }
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        this.save(categoryBrandRelation);
    }

    @Override
    public void updateBrandName(Long brandId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setBrandId(brandId);
        categoryBrandRelationEntity.setBrandName(name);
        this.update(categoryBrandRelationEntity,
            new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
    }

    @Override
    public void updateCategory(Long catId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setCatelogId(catId);
        categoryBrandRelationEntity.setCatelogName(name);
        this.update(categoryBrandRelationEntity,
            new UpdateWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
    }

    @Override
    public List<BrandEntity> searchRelationBrandsList(Long catId) {
        List<CategoryBrandRelationEntity> categoryBrandRelationEntities =
            categoryBrandRelationDao.selectList(new QueryWrapper<CategoryBrandRelationEntity>()
                .eq("catelog_id", catId));
        List<BrandEntity> brandEntities = categoryBrandRelationEntities.stream().map(item -> {
            BrandEntity brandEntity = brandService.getById(item.getBrandId());
            return brandEntity;
        }).collect(Collectors.toList());
        return brandEntities;
    }

}