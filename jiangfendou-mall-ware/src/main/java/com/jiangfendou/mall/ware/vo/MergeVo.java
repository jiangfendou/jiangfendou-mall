package com.jiangfendou.mall.ware.vo;

import java.util.List;
import lombok.Data;

/**
 * @author jiangmh
 */
@Data
public class MergeVo {

    private Long purchaseId;

    private List<Long> items;
}
