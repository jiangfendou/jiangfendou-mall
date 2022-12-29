package com.jiangfendou.mall.ware.controller;

import static com.jiangfendou.common.exception.BaseCodeEnum.NO_STOCK_EXCEPTION;


import com.jiangfendou.common.exception.NoStockException;
import com.jiangfendou.common.to.SkuHasStockVo;
import com.jiangfendou.mall.ware.vo.WareSkuLockVo;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.jiangfendou.mall.ware.entity.WareSkuEntity;
import com.jiangfendou.mall.ware.service.WareSkuService;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.R;


/**
 * 商品库存
 *
 * @author jiangfendou
 * @email 49323245@qq.com
 * @date 2022-05-10 11:29:39
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    @GetMapping(value = "/test")
    public R test(){
        wareSkuService.test();
        return R.ok();
    }

    /**
     * 锁定库存
     * @param vo
     *
     * 库存解锁的场景
     *      1）、下订单成功，订单过期没有支付被系统自动取消或者被用户手动取消，都要解锁库存
     *      2）、下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚。之前锁定的库存就要自动解锁
     *      3）、
     *
     * @return
     */
    @PostMapping(value = "/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo vo) {

        try {
            boolean lockStock = wareSkuService.orderLockStock(vo);
            return R.ok().setData(lockStock);
        } catch (NoStockException e) {
            return R.error(NO_STOCK_EXCEPTION.getCode(), NO_STOCK_EXCEPTION.getMsg());
        }
    }

    /**
     * 查询sku是否有库存
     */
    @PostMapping("/has-stock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds) {
       List<SkuHasStockVo> vos = wareSkuService.getSkuHasStock(skuIds);
       return R.ok().setData(vos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
            public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
            public R info(@PathVariable("id") Long id) {
            WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
            public R save(@RequestBody WareSkuEntity wareSku) {
            wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
            public R update(@RequestBody WareSkuEntity wareSku) {
            wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
            public R delete(@RequestBody Long[] ids) {
            wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
