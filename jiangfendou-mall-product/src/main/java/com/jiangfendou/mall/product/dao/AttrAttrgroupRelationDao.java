package com.jiangfendou.mall.product.dao;

import com.jiangfendou.mall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangfendou.mall.product.vo.AttrGroupVo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 属性&属性分组关联
 *
 * @author jiangfendou
 * @email 49323245@qq.com
 * @date 2022-05-08 20:27:21
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteBatchRelation(@Param("attrGroupVos") List<AttrGroupVo> attrGroupVos);
}
