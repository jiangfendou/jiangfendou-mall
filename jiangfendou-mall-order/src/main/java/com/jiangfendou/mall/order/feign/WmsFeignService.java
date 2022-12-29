package com.jiangfendou.mall.order.feign;

import com.jiangfendou.common.utils.R;
import com.jiangfendou.mall.order.vo.WareSkuLockVo;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("jiangfendou-mall-ware")
public interface WmsFeignService {

    /**
     * 查询sku是否有库存
     * @return
     */
    @PostMapping(value = "/ware/waresku/has-stock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);


    /**
     * 查询运费和收货地址信息
     * @param addrId
     * @return
     */
    @GetMapping(value = "/ware/wareinfo/fare")
    R getFare(@RequestParam("addrId") Long addrId);


    /**
     * 锁定库存
     * @param vo
     * @return
     */
    @PostMapping(value = "/ware/waresku/lock/order")
    R orderLockStock(@RequestBody WareSkuLockVo vo);


    @GetMapping(value = "/ware/waresku/test")
    R test();
}
