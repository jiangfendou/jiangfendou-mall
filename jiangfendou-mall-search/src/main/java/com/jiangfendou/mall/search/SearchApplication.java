package com.jiangfendou.mall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * SearchApplication.
 * @author jiangmh
 *
 * @EnableFeignClients 开启远程调用
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class  SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class);
    }
}
