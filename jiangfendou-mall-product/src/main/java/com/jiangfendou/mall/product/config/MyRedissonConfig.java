package com.jiangfendou.mall.product.config;

import java.io.IOException;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyRedissonConfig.
 * @author jiangmh
 */
@Configuration
public class MyRedissonConfig {


    /**
     * 1、锁的自动续期，如果业务超长，运行期间自动给锁续上30秒，不用担心业务时间长，锁自动过期。
     * 2、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认在30秒后自动删除。
     * */
    @Bean
    public RedissonClient redissonClient() throws IOException {
        Config config = new Config();
//        config.useClusterServers()
//            .addNodeAddress("123.56.239.210:")
        config.useSingleServer()
            .setAddress("redis://123.56.239.210:6379")
            .setPassword("redis");
        return Redisson.create(config);
    }
}
