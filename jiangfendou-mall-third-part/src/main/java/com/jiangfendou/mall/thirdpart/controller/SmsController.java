package com.jiangfendou.mall.thirdpart.controller;

import com.jiangfendou.common.utils.R;
import com.jiangfendou.mall.thirdpart.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @RequestMapping("/sendcode")
    public R sendSms(@RequestParam("phone") String phone, @RequestParam("code") String code){
        smsService.sendSms(phone, code);
        return R.ok();
    }
}
