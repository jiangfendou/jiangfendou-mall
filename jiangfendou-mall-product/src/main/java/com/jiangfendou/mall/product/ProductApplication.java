package com.jiangfendou.mall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 1、整合Mybatis-plus
 *      1）、导入依赖
 *       <dependency>
 *           <groupId>com.baomidou</groupId>
 *           <artifactId>mybatis-plus-boot-starter</artifactId>
 *           <version>3.2.0</version>
 *       </dependency>
 *
 * */
@SpringBootApplication
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

}
