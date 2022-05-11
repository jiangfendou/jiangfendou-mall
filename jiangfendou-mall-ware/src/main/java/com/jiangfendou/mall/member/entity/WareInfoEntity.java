package com.jiangfendou.mall.member.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

    import java.io.Serializable;
import lombok.Data;

/**
 * 仓库信息
 *
 * @author jiangfendou
 * @email 49323245@qq.com
 * @date 2022-05-10 11:29:39
 */
@Data
@TableName("wms_ware_info")
public class WareInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

            /**
         * id
         */
                @TableId
            private Long id;
            /**
         * 仓库名
         */
            private String name;
            /**
         * 仓库地址
         */
            private String address;
            /**
         * 区域编码
         */
            private String areacode;
    
}
