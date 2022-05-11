package com.jiangfendou.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;


import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.mall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author jiangfendou
 * @email 49323245@qq.com
 * @date 2022-05-10 11:29:39
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

