/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.jiangfendou.mall.admin.modules.oss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiangfendou.mall.admin.common.utils.PageUtils;
import com.jiangfendou.mall.admin.modules.oss.entity.SysOssEntity;

import java.util.Map;

/**
 * 文件上传
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysOssService extends IService<SysOssEntity> {

    PageUtils queryPage(Map<String, Object> params);
}
