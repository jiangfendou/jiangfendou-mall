package com.jiangfendou.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.mall.product.entity.AttrAttrgroupRelationEntity;

import com.jiangfendou.mall.product.vo.AttrGroupVo;
import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author jiangfendou
 * @email 49323245@qq.com
 * @date 2022-05-08 20:27:21
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBatch(List<AttrGroupVo> vos);
}

