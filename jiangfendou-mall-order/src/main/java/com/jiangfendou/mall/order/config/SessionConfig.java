package com.jiangfendou.mall.order.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;


/**
 * 自定义浏览器cooking的domain和cooking的name
 * @author jiangmh
 */
@Configuration
@ConditionalOnMissingBean(CookieSerializer.class)
public class SessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {

        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("JIANGSESSION");
        serializer.setDomainName("jiangfendou.com");
        return serializer;
    }


    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

}
