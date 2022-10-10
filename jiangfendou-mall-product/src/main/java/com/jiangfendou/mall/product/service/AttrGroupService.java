package com.jiangfendou.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.mall.product.entity.AttrEntity;
import com.jiangfendou.mall.product.entity.AttrGroupEntity;

import com.jiangfendou.mall.product.vo.AttrGroupWithAttrsVo;
import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author jiangfendou
 * @email 49323245@qq.com
 * @date 2022-05-08 20:27:21
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params, Long cateLogId);

    AttrGroupEntity getAttrGroupDetail(Long attrGroupId) throws Exception;

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrs(Long catelogId);
}

