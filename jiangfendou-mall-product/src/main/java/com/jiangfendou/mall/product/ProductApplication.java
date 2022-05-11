package com.jiangfendou.mall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1、整合Mybatis-plus
 * 1）、导入依赖
 * <dependency>
 * <groupId>com.baomidou</groupId>
 * <artifactId>mybatis-plus-boot-starter</artifactId>
 * <version>3.2.0</version>
 * </dependency>
 * 2）、配置
 * 1、配置数据源
 * 1）、导入数据库的驱动
 * 2）、在application.yml文件中配置相关信息
 * 2、配置Mybatis-Plus
 * 1）、使用@MapperScan
 * 2）、告诉mybatis-plus，sql映射文件的位置
 */
@EnableDiscoveryClient
@MapperScan("com.jiangfendou.mall.product.dao")
@SpringBootApplication
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

}
