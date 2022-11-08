import com.alibaba.fastjson.JSON;
import com.jiangfendou.mall.search.config.ElasticSearchConfig;
import com.jiangfendou.mall.search.entiy.Account;
import java.io.IOException;
import java.util.Map;
import javax.naming.directory.SearchResult;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ElasticSearchConfig.class)
class SearchApplicationTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    void contextLoads() {

    }

    /**
     * insert data to es
     *
     * 保存或者更新
     * */
    @Test
    void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
//        indexRequest.source("userName", "jiangfendou", "age", 30, "gender", "男");

        User user = new User();
        user.setUserName("jiangfendou");
        user.setAge(30);
        user.setGender("男");
        String userJson = JSON.toJSONString(user);
        indexRequest.source(userJson, XContentType.JSON);
        IndexResponse index = restHighLevelClient.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);

        // 响应的数据
        System.out.println(index);
    }

    @Test
    void searchData() throws IOException {
        // 1、创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        // 指定索引
        searchRequest.indices("bank");
        // 指定DSL，检索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 构造检索条件
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));

        // 按照你领的值分布聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);

        // 计算平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAvg);

        searchRequest.source(searchSourceBuilder);

        // 2、执行检索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);

        // 3、分析结果
        // 获取所有查到的数据
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit: searchHits) {
            String sourceAsString = hit.getSourceAsString();
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println(account);
        }
        // 分析信息
        Aggregations aggregations = searchResponse.getAggregations();
        Terms ageAgg1 = aggregations.get("ageAgg");
        ageAgg1.getBuckets().forEach(item -> {
            String keyAsString = item.getKeyAsString();
            System.out.println("年龄：" + keyAsString);
        });

        Avg balanceAgg1 = aggregations.get("balanceAvg");
        System.out.println("平均薪资：" + balanceAgg1.getValue());
    }

    @Data
    private static class User {
        private String userName;

        private String gender;

        private Integer age;
    }
}
