package com.jiangfendou.mall.thirdpart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ThirdPartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThirdPartApplication.class, args);
    }
}
