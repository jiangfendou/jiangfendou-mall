package com.jiangfendou.mall.order.feign;

import com.jiangfendou.mall.order.vo.OrderItemVo;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("jiangfendou-mall-cart")
public interface CartFeignService {

    /**
     * 查询当前用户购物车选中的商品项
     * @return
     */
    @GetMapping(value = "/currentUserCartItems")
    List<OrderItemVo> getCurrentCartItems();

}
