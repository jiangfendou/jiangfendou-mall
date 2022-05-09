package com.jiangfendou.mall.coupon.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.Query;

import com.jiangfendou.mall.coupon.dao.HomeSubjectDao;
import com.jiangfendou.mall.coupon.entity.HomeSubjectEntity;
import com.jiangfendou.mall.coupon.service.HomeSubjectService;


@Service("homeSubjectService")
public class HomeSubjectServiceImpl extends ServiceImpl<HomeSubjectDao, HomeSubjectEntity>
    implements HomeSubjectService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HomeSubjectEntity> page = this.page(
            new Query<HomeSubjectEntity>().getPage(params),
            new QueryWrapper<HomeSubjectEntity>()
        );

        return new PageUtils(page);
    }

}