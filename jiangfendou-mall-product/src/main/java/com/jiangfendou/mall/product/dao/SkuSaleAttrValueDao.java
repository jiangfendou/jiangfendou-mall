package com.jiangfendou.mall.product.dao;

import com.jiangfendou.mall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangfendou.mall.product.vo.SkuItemSaleAttrVo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * sku销售属性&值
 *
 * @author jiangfendou
 * @email 49323245@qq.com
 * @date 2022-05-08 20:27:19
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId);

    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
}
