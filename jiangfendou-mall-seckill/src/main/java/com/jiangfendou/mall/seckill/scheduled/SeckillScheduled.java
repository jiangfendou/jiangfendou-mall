package com.jiangfendou.mall.seckill.scheduled;

import com.jiangfendou.mall.seckill.service.SeckillService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 秒杀商品的定时上架
 * 每天晚上三点，上架最近三天需要秒杀的商品
 *
 * */
@Slf4j
@Component
@Service
public class SeckillScheduled {

    @Autowired
    SeckillService seckillService;

    @Autowired
    RedissonClient redissonClient;

    /** 秒杀商品上架功能的锁 */
    private static final String UPLOAD_LOCK = "seckill:upload:lock";

    @Scheduled(cron = "*/1 * * * * ?")
    public void uploadSeckillSkuLatest3Days() {
        // 分布式锁
        RLock lock = redissonClient.getLock(UPLOAD_LOCK);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            seckillService.uploadSeckillSkuLatest3Days();
        } finally {
            lock.unlock();
        }
    }
}
