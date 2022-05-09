package com.jiangfendou.mall.order.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.Query;

import com.jiangfendou.mall.order.dao.OrderReturnApplyDao;
import com.jiangfendou.mall.order.entity.OrderReturnApplyEntity;
import com.jiangfendou.mall.order.service.OrderReturnApplyService;


@Service("orderReturnApplyService")
public class OrderReturnApplyServiceImpl extends ServiceImpl<OrderReturnApplyDao, OrderReturnApplyEntity>
    implements OrderReturnApplyService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderReturnApplyEntity> page = this.page(
            new Query<OrderReturnApplyEntity>().getPage(params),
            new QueryWrapper<OrderReturnApplyEntity>()
        );

        return new PageUtils(page);
    }

}