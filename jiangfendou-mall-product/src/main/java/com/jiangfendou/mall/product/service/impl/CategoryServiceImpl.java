package com.jiangfendou.mall.product.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.Query;

import com.jiangfendou.mall.product.dao.CategoryDao;
import com.jiangfendou.mall.product.entity.CategoryEntity;
import com.jiangfendou.mall.product.service.CategoryService;
import org.springframework.util.CollectionUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
            new Query<CategoryEntity>().getPage(params),
            new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listTree() {
        // 1、查出所有分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        // 2、组装成父子的树形结构
        if (CollectionUtils.isEmpty(categoryEntities)) {
            return new ArrayList<>();
        }
        // 2-1、找到所有的1级分类
        List<CategoryEntity> levelFirstMenus = categoryEntities.stream().filter(categoryEntity ->
            categoryEntity.getParentCid() == 0
        ).map(menu-> {
            menu.setChildren(getChildren(menu, categoryEntities));
            return menu;
        }).sorted((menuStart, menuEnd) -> {
            return (menuStart.getSort() == null ? 0 : menuStart.getSort()) -
                (menuEnd.getSort() == null ? 0 : menuEnd.getSort());
        }).collect(Collectors.toList());
        return levelFirstMenus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO 检查当前删除的菜单，是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 递归查找所有菜单的子菜单
     */
    private List<CategoryEntity> getChildren(CategoryEntity categoryEntity, List<CategoryEntity> categoryEntities) {
        List<CategoryEntity> children = categoryEntities.stream().filter(category -> {
            return Objects.equals(category.getParentCid(), categoryEntity.getCatId());
        }).map(categoryCopy -> {
            // 找到子菜单
            categoryCopy.setChildren(getChildren(categoryCopy, categoryEntities));
            return categoryCopy;
            // 菜单的排序
        }).sorted((menuStart, menuEnd) -> {
            return (menuStart.getSort() == null ? 0 : menuStart.getSort()) -
                (menuEnd.getSort() == null ? 0 : menuEnd.getSort());
        }).collect(Collectors.toList());
        return children;
    }
}