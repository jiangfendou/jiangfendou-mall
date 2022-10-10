package com.jiangfendou.mall.product.controller;

import com.jiangfendou.mall.product.service.AttrAttrgroupRelationService;
import com.jiangfendou.mall.product.service.AttrService;
import com.jiangfendou.mall.product.vo.AttrGroupVo;
import com.jiangfendou.mall.product.vo.AttrGroupWithAttrsVo;
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

import com.jiangfendou.mall.product.entity.AttrGroupEntity;
import com.jiangfendou.mall.product.service.AttrGroupService;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.R;


/**
 * 属性分组
 *
 * @author jiangfendou
 * @email 49323245@qq.com
 * @date 2022-05-08 20:27:21
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId) {
        return R.ok().put("data", attrService.getRelationAttr(attrgroupId));
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{cateLogId}")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("cateLogId") Long cateLogId) {
        PageUtils page = attrGroupService.queryPage(params, cateLogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) throws Exception {
        return R.ok().put("attrGroup", attrGroupService.getAttrGroupDetail(attrGroupId));
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupVo[] attrGroupVo) {
        attrService.deleteRelation(attrGroupVo);
        return R.ok();
    }

    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@RequestParam Map<String, Object> params,
                            @PathVariable("attrgroupId") Long attrGroupId) {
        PageUtils page = attrService.getNoRelationAttr(params, attrGroupId);
        return R.ok().put("page", page);
    }

    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupVo> vos) {
        attrAttrgroupRelationService.saveBatch(vos);
        return R.ok();
    }

    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId) {
        List<AttrGroupWithAttrsVo> attrGroupWithAttrsVos = attrGroupService.getAttrGroupWithAttrs(catelogId);
        return R.ok().put("data", attrGroupWithAttrsVos);
    }
}
