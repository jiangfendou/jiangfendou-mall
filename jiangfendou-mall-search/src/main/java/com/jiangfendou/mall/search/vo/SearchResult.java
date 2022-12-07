package com.jiangfendou.mall.search.vo;

import com.jiangfendou.common.to.es.SkuEsModel;
import java.util.List;
import lombok.Data;

@Data
public class SearchResult {

    /** 商品信息 */
    private List<SkuEsModel> products;

    /** 当前页码 */
    private Integer pageNum;

    /** 总记录数 */
    private Long total;

    /** 总页码 */
    private Integer totalPages;

    /** 当前查询到的结果，所涉及到的所有品牌 */
    private List<BrandVo> brandVos;

    /** 当前查询到的结果，所涉及到的所有属性 */
    private List<AttrVo> attrVos;

    /** 当前查询到的结果，所涉及到的所有分类 */
    private List<CatalogVo> catalogVos;

    /** 面包屑导航数据 */
    private List<NavVo> navs;

    private List<Integer> pageNavs;

    // ------------------------- 以上事返给页面的所有信息 ------------------

    @Data
    public static class BrandVo {

        private Long brandId;

        private String brandName;

        private String brandImg;
    }

    @Data
    public static class AttrVo {

        private Long attrId;

        private String attrName;

        private List<String> attrValue;
    }

    @Data
    public static class CatalogVo {

        private Long catalogId;

        private String catalogName;

    }

    @Data
    public static class NavVo {
        private String navName;
        private String navValue;
        private String link;
    }

}


