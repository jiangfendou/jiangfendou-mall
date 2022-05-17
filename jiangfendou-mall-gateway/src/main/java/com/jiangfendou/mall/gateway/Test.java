package com.jiangfendou.mall.gateway;

import org.springframework.beans.factory.annotation.Value;

public class Test {

    @Value("${test.user}")
    private String user;
}
