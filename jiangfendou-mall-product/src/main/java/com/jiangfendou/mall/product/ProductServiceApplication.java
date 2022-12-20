package com.jiangfendou.mall.product;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

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
 *
 * 2、逻辑删除
 * 1）、配置全局的逻辑删除规则（省略）
 * 2）、配置逻辑删除的组件Bean（省略）
 * 3）、给Bean加上逻辑删除注解@tableLogic
 *
 * 缓存：
 * 1、读模式
 *  缓存穿透：查询一个null数据。                            解决方案：缓存null。
 *  缓存击穿：大量并发同时进来正好查询一个过期的数据。           解决方案：加锁  sync = true
 *  缓存雪崩：大量key同时过期。                             解决方案：加随机时间，加上过期时间就可以了
 * 2、写模式 （缓存于数据库一致）
 *  1）、读写加锁
 *  2）、引入canal，感知到mysql更新去更新数据库
 *  3）、读多写多，直接去查询数据区就行
 * 总结：
 *  常规数据：（读多写少，及时性，一致性要求不高的数据），完全可以使用spring-cache；写模式（只要缓存有过期时间就足够了）
 *  特殊数据：特殊设计
 * 原理：
 *  CacheManager(RedisCacheManager) -> Cache(RedisCache) -> Cache 负责缓存读写
 */
// 开启redis缓存session
@EnableRedisHttpSession
// 开启openfeign的注解
@EnableFeignClients(basePackages = "com.jiangfendou.mall.product.feign")
// 开启注册中心的注解
@EnableDiscoveryClient
@MapperScan("com.jiangfendou.mall.product.dao")
@SpringBootApplication
// 开启缓存的注解
@EnableCaching
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
