package com.jiangfendou.mall.product.web;

import com.jiangfendou.mall.product.service.SkuInfoService;
import com.jiangfendou.mall.product.vo.SkuItemVo;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String skuIem(@PathVariable Long skuId, Model model) throws ExecutionException, InterruptedException {
        System.out.println("准备查询" + skuId + "详情");

        SkuItemVo vos = skuInfoService.skuItem(skuId);

        model.addAttribute("item", vos);
        return "item";
    }
}
