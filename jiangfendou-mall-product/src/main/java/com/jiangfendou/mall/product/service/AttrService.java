package com.jiangfendou.mall.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.mall.product.entity.AttrEntity;

import com.jiangfendou.mall.product.vo.AttrGroupVo;
import com.jiangfendou.mall.product.vo.AttrResponseVo;
import com.jiangfendou.mall.product.vo.AttrVo;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 商品属性
 *
 * @author jiangfendou
 * @email 49323245@qq.com
 * @date 2022-05-08 20:27:21
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attrVo);

    PageUtils searchAttrs(Map<String, Object> params, Long catelogId, String attrType);

    AttrResponseVo getAttrInfo(Long attrId) throws Exception;

    void updateAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupVo[] attrGroupVo);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrGroupId);
}

