package com.jiangfendou.mall.product.dao;

import com.jiangfendou.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 *
 * @author jiangfendou
 * @email 49323245@qq.com
 * @date 2022-05-08 20:27:20
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {

}
