package com.jiangfendou.mall.product.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catelog2Vo {

    private String catalogId;

    private List<Catelog3Vo> catalog3List;

    private String id;

    private String name;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Catelog3Vo {

        private String catalog2Id;

        private String id;

        private String name;
    }

}
