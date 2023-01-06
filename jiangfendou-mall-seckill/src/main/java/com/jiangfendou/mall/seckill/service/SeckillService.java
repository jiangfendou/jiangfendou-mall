package com.jiangfendou.mall.seckill.service;

import com.jiangfendou.mall.seckill.to.SeckillSkuRedisTo;
import java.util.List;

public interface SeckillService {

    void uploadSeckillSkuLatest3Days();

    /**
     * 返回当前时间参与秒杀商品的信息
     * */
    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    SeckillSkuRedisTo getSkuSeckilInfo(Long skuId);

    String kill(String killId, String key, Integer num) throws InterruptedException;
}
