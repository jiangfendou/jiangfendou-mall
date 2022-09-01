package com.jiangfendou.mall.thirdpart.controller;

import com.jiangfendou.common.utils.R;
import com.jiangfendou.mall.thirdpart.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OssController {

    @Autowired
    private OssService ossService;

    @RequestMapping("oss/policy")
    public R policy(){
        return R.ok().put("data", ossService.policy());
    }
}
