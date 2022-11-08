package com.jiangfendou.mall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ElasticSearchConfig.
 * @author jiangmh
 */
@Configuration
public class ElasticSearchConfig {

    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer" + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//            new HttpAsyncResponseConsumerFactory
//                .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024)
//        );
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient esRestClient() {

        RestClientBuilder builder = RestClient.builder(
            new HttpHost("124.223.77.220", 9200, "http"));
        //        RestHighLevelClient client = new RestHighLevelClient(
//            RestClient.builder(
//                new HttpHost("124.223.77.220", 9200, "http")
//            )
//        );
        return new RestHighLevelClient(builder);
    }
}
