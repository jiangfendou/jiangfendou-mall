package com.jiangfendou.mall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 本地事务失效问题
 * 同一个对像内事务方法互调失效，原因 绕过了代理对象，事务时使用代理对象来控制的
 * 解决：
 *  使用代理对象来调用事务的方法
 *  1）、引入aop-starter；spring-boot-starter-aop；引入了 aspectj
 *  2）、@EnableAspectJAutoProxy；开启aspectj动态代理，之后所有的动态代理都是aspectj创建的（即使没有接口也可以创建代理对象）
 *      对外暴漏代理对象
 *  3）、代码
 *      OrderServiceImpl order = （OrderServiceImpl）AopContext。currentProxy();
 *      order.a();
 *      order.b();
 *      这样被调用的方法关于事务的配置就可以生效了
 * */
@EnableRedisHttpSession
@EnableRabbit
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan("com.jiangfendou.mall.order.dao")
@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}
