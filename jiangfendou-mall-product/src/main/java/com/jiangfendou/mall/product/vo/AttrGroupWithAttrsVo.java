package com.jiangfendou.mall.product.vo;

import com.jiangfendou.mall.product.entity.AttrEntity;
import java.util.List;
import lombok.Data;

/**
 * @author jiangmh
 */
@Data
public class AttrGroupWithAttrsVo {

    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    /**
     * 完成路径
     */
    private Long[] catelogPath;

    private List<AttrEntity> attrs;
}
