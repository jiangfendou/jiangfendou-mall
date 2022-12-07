package com.jiangfendou.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jiangfendou.common.to.es.SkuEsModel;
import com.jiangfendou.common.utils.R;
import com.jiangfendou.mall.search.config.ElasticSearchConfig;
import com.jiangfendou.mall.search.constant.EsConstant;
import com.jiangfendou.mall.search.feign.ProductFeignService;
import com.jiangfendou.mall.search.service.MallSearchService;
import com.jiangfendou.mall.search.vo.AttrResponseVo;
import com.jiangfendou.mall.search.vo.SearchParamVo;
import com.jiangfendou.mall.search.vo.SearchResult;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParamVo searchParamVo) {
        // 1、动态构建出查询需要的DSL语句

        SearchResult searchResult = null;

        // 1、准备检索请求
        SearchRequest searchRequest = buildSearchRequest(searchParamVo);

        try {
            // 2、执行检索请求SearchResponse search =
            SearchResponse search = restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);

            // 分析响应数据，封装成我们需要的格式
            searchResult = buildSearchResult(search, searchParamVo);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return searchResult;
    }

    /**
     * 准备检索请求
     * */
    private SearchRequest buildSearchRequest(SearchParamVo searchParamVo) {
        // 构建DSL语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 构建bool-query
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 构建must
        if (StringUtils.isNotBlank(searchParamVo.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", searchParamVo.getKeyword()));
        }

        // bool--filter 按照三级分类查询
        if (searchParamVo.getCatalog3Id() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", searchParamVo.getCatalog3Id()));

        }

        // bool--filter 按照brand id查询
        if (!CollectionUtils.isEmpty(searchParamVo.getBrandId())) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", searchParamVo.getBrandId()));

        }

        // ------------------- 按照所有的属性查询 -------------

        // 按照属性查询
        if(!CollectionUtils.isEmpty(searchParamVo.getAttrs())) {
             searchParamVo.getAttrs().forEach(item -> {
                 BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                 // attrs = 1_5寸:8寸
                 String[] s = item.split("_");
                 String attrId = s[0];
                 String[] attrValue = s[1].split(":");
                 nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                 nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValue));
                 NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
                 boolQueryBuilder.filter(nestedQueryBuilder);
             });
        }

        // 按照是否有库存查询
        if (!Objects.isNull(searchParamVo.getHasStock())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", searchParamVo.getHasStock() == 1));
        }

        // 按照价格区间
        if (StringUtils.isNotBlank(searchParamVo.getSkuPrice())) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            String[] split = searchParamVo.getSkuPrice().split("_");
            if (split.length == 2) {
                rangeQueryBuilder.gte(split[0]).lte(split[1]);
            } else if (split.length == 1) {
                if (searchParamVo.getSkuPrice().startsWith("_")) {
                    rangeQueryBuilder.lte(split[0]);
                }
                if (searchParamVo.getSkuPrice().endsWith("_")) {
                    rangeQueryBuilder.gte(split[0]);
                }
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        searchSourceBuilder.query(boolQueryBuilder);

        /**
         * 排序，分页，高亮
         */

        //排序
        //形式为sort=hotScore_asc/desc
        if(!StringUtils.isEmpty(searchParamVo.getSort())){
            String sort = searchParamVo.getSort();
            String[] sortFileds = sort.split("_");

            SortOrder sortOrder = "asc".equalsIgnoreCase(sortFileds[1]) ? SortOrder.ASC : SortOrder.DESC;

            searchSourceBuilder.sort(sortFileds[0], sortOrder);
        }

        //分页
        searchSourceBuilder.from((searchParamVo.getPageNum() - 1) * EsConstant.PRODUCT_PAGE_SIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);

        //高亮
        if(!StringUtils.isEmpty(searchParamVo.getKeyword())){

            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");

            searchSourceBuilder.highlighter(highlightBuilder);
        }

        /**
         * 聚合分析
         */
        //1. 按照品牌进行聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg");
        brandAgg.field("brandId").size(50);


        //1.1 品牌的子聚合-品牌名聚合
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg")
            .field("brandName").size(1));
        //1.2 品牌的子聚合-品牌图片聚合
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg")
            .field("brandImg").size(1));

        searchSourceBuilder.aggregation(brandAgg);

        //2. 按照分类信息进行聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg");
        catalogAgg.field("catalogId").size(20);

        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));

        searchSourceBuilder.aggregation(catalogAgg);

        //2. 按照属性信息进行聚合
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_agg", "attrs");
        //2.1 按照属性ID进行聚合
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        attrAgg.subAggregation(attrIdAgg);
        //2.1.1 在每个属性ID下，按照属性名进行聚合
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        //2.1.1 在每个属性ID下，按照属性值进行聚合
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        searchSourceBuilder.aggregation(attrAgg);

        log.info("构建的DSL语句 {}", searchSourceBuilder.toString());

        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
    }

    /**
     * 构建结果数据
     * */
    private SearchResult buildSearchResult(SearchResponse search, SearchParamVo searchParamVo) {

        SearchResult searchResult = new SearchResult();
        List<SkuEsModel> esModels = new ArrayList<>();
        SearchHits hits = search.getHits();
        // 遍历所有商品信息
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                // getSourceAsString() 方法将hit转换成字符串
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);

                // 判断是否按关键字检索，若是就显示高亮，否则不显示
                if (!StringUtils.isEmpty(searchParamVo.getKeyword())) {
                    // 拿到高亮信息显示标题
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String skuTitleValue = skuTitle.getFragments()[0].string();
                    esModel.setSkuTitle(skuTitleValue);
                }
                esModels.add(esModel);
            }
        }
        // 返回所有查询到的商品
        searchResult.setProducts(esModels);

        //2、当前商品涉及到的所有属性信息
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        //获取属性信息的聚合
        ParsedNested attrsAgg = search.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            //1、得到属性的id
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);

            //2、得到属性的名字
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);

            //3、得到属性的所有值
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attr_value_agg");
            List<String> attrValues = attrValueAgg.getBuckets().stream().map(item -> item.getKeyAsString()).collect(
                Collectors.toList());
            attrVo.setAttrValue(attrValues);

            attrVos.add(attrVo);
        }
        // 当前商品所涉及到的属性信息
        searchResult.setAttrVos(attrVos);

        //3、当前商品涉及到的所有品牌信息
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        //获取到品牌的聚合
        ParsedLongTerms brandAgg = search.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();

            //1、得到品牌的id
            long brandId = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brandId);

            //2、得到品牌的名字
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);

            //3、得到品牌的图片
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);

            brandVos.add(brandVo);
        }
        // 当前查询到的结果，所涉及到的所有品牌
        searchResult.setBrandVos(brandVos);

        //4、当前商品涉及到的所有分类信息
        //获取到分类的聚合
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catalogAgg = search.getAggregations().get("catalog_agg");
        for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            //得到分类id
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));

            //得到分类名
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
            catalogVos.add(catalogVo);
        }

        // 当前查询到的结果，所涉及到的所有分类
        searchResult.setCatalogVos(catalogVos);

        //===============以上可以从聚合信息中获取====================//

        //5、分页信息-页码
        searchResult.setPageNum(searchParamVo.getPageNum());
        //5、1分页信息、总记录数
        long total = hits.getTotalHits().value;
        searchResult.setTotal(total);

        //5、2分页信息-总页码-计算
        int totalPages = (int)total % EsConstant.PRODUCT_PAGE_SIZE == 0 ?
            (int)total / EsConstant.PRODUCT_PAGE_SIZE : ((int)total / EsConstant.PRODUCT_PAGE_SIZE + 1);
        searchResult.setTotalPages(totalPages);

        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        searchResult.setPageNavs(pageNavs);

        //6、构建面包屑导航
        if (searchParamVo.getAttrs() != null && searchParamVo.getAttrs().size() > 0) {
            List<SearchResult.NavVo> collect = searchParamVo.getAttrs().stream().map(attr -> {
                //1、分析每一个attrs传过来的参数值
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    AttrResponseVo data = r.getData("attr", new TypeReference<AttrResponseVo>() {
                    });
                    navVo.setNavName(data.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }

                //2、取消了这个面包屑以后，我们要跳转到哪个地方，将请求的地址url里面的当前置空
                //拿到所有的查询条件，去掉当前
                String encode = null;
                try {
                    encode = URLEncoder.encode(attr, "UTF-8");
                    encode.replace("+", "%20");  //浏览器对空格的编码和Java不一样，差异化处理
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String replace = searchParamVo.get_queryString().replace("&attrs=" + attr, "");
                navVo.setLink("http://search.jiangfendou.com/list.html?" + replace);

                return navVo;
            }).collect(Collectors.toList());

            searchResult.setNavs(collect);
        }
        return searchResult;
    }
}
