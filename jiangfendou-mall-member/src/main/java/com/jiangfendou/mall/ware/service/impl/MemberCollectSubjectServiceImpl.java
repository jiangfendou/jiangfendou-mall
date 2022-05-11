package com.jiangfendou.mall.ware.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.Query;

import com.jiangfendou.mall.ware.dao.MemberCollectSubjectDao;
import com.jiangfendou.mall.ware.entity.MemberCollectSubjectEntity;
import com.jiangfendou.mall.ware.service.MemberCollectSubjectService;


@Service("memberCollectSubjectService")
public class MemberCollectSubjectServiceImpl extends ServiceImpl<MemberCollectSubjectDao, MemberCollectSubjectEntity>
    implements MemberCollectSubjectService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberCollectSubjectEntity> page = this.page(
            new Query<MemberCollectSubjectEntity>().getPage(params),
            new QueryWrapper<MemberCollectSubjectEntity>()
        );

        return new PageUtils(page);
    }

}