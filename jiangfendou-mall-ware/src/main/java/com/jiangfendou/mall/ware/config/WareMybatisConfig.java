package com.jiangfendou.mall.ware.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan("com.jiangfendou.mall.ware.dao")
public class WareMybatisConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        // 设置请求的页面大于最大页后操作，true调回到首页，false 继续请求 默认 false
        // paginationInterceptor.setOverflow(false);
        // 设置最大但也限制数量，默认500条， -1不受限制
        // paginationInterceptor.setLimit(500);
        return new PaginationInterceptor();

    }
}
