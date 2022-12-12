package com.jiangfendou.mall.thirdpart.service.impl;

import com.jiangfendou.mall.thirdpart.config.SmsComponent;
import com.jiangfendou.mall.thirdpart.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private SmsComponent smsComponent;


    @Override
    public void sendSms(String phone, String code) {
        smsComponent.sendSmsCode(phone, code);
    }
}
