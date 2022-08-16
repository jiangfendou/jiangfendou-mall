package com.jiangfendou.mall.thirdpart;

//import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
//@MapperScan("com.jiangfendou.mall.product.dao")
@SpringBootApplication
public class ThirdPartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThirdPartApplication.class, args);
    }
}
