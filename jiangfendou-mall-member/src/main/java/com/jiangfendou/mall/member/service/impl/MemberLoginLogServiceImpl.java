package com.jiangfendou.mall.member.service.impl;

import com.jiangfendou.mall.member.dao.MemberLoginLogDao;
import com.jiangfendou.mall.member.entity.MemberLoginLogEntity;
import com.jiangfendou.mall.member.service.MemberLoginLogService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.Query;


@Service("memberLoginLogService")
public class MemberLoginLogServiceImpl extends ServiceImpl<MemberLoginLogDao, MemberLoginLogEntity>
    implements MemberLoginLogService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberLoginLogEntity> page = this.page(
            new Query<MemberLoginLogEntity>().getPage(params),
            new QueryWrapper<MemberLoginLogEntity>()
        );

        return new PageUtils(page);
    }

}