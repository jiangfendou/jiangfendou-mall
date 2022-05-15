package com.jiangfendou.mall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1、如何使用Nacos作为配置中心统一管理配置
 * 1）、引入依赖
 *         <dependency>
 *             <groupId>com.alibaba.cloud</groupId>
 *             <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
 *         </dependency>
 *         <dependency>
 *             <groupId>org.springframework.cloud</groupId>
 *             <artifactId>spring-cloud-starter-bootstrap</artifactId>
 *             <version>3.0.2</version>
 *         </dependency>
 * 2)、创建一个bootstrap.yml
 *      spring:
 *          application:
 *              name: jiangfendou-mall-coupon
 *          cloud:
 *              nacos:
 *                  config:
 *                      server-addr: 124.223.77.220:8848
 *                  # 配置文件后缀名为yaml
 *                     file-extension: yaml
 * 3)、配置中心添加数据集（data id）生成规则是 spring.application.name: jiangfendou-mall-coupon.yaml
 * 4)、jiangfendou-mall-coupon.yaml 添加配置
 * 5）、动态获取配置
 *  @RefreshScope 动态获取并刷新配置
 *  @Value 获取配置的值
 *  优先使用配置中心的配置
 * 2、细节
 * 1）、命名空间：配置隔离
 *  默认的命名空间是public；默认新增的配置都在public空间
 *  1、开发；测试；生产； -- 通过命名空间来做环境隔离
 *      注意：在bootstrap.yaml配置上，需要使用那个命名空间下的配置，
 *      namespace: c30fe47e-6b09-4a84-8620-190dbf48459c
 *  2、每一个微服务之间互相隔离配置，每一个微服务之间都创建自己的命名空间，只加载自己的命名空间下的所有配置
 * 2）、配置集：所有配置的集合
 * 3）、配置集id：类似于文件名  Data Id （类似于文件名）
 * 4）、配置分组：
 *  默认所有的配置集都属于：DEFAULT_GROUP
 *  自定义分组
 *
 * 每一个微服务创建自己的命名空间，使用配置分组区分环境，dev，test，prod
 *
 * 3、同时加载多个配置集
 * 1）、微服务任何配置信息，任何配置文件都可以放在配置中心
 * 2）、只需要在bootstrap.yml说明加载配置中心哪些配置文件
 * 3）、@Value、 @ConfigurationProperties....
 * 以前Springboot任何方法从配置文件中获取值，都可以使用
 *  配置中心有的优先配置中心的配置。
 * */
@EnableDiscoveryClient
@MapperScan("com.jiangfendou.mall.coupon.dao")
@SpringBootApplication
public class CouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponApplication.class, args);
    }

}
