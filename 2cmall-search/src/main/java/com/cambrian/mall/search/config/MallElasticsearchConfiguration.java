package com.cambrian.mall.search.config;

import org.elasticsearch.client.RequestOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kuma 2020-11-17
 */
@Configuration
public class MallElasticsearchConfiguration {

    @Bean
    public RequestOptions commonOptions() {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//		builder.addHeader("Authorization", "Bearer " + TOKEN);
//		builder.setHttpAsyncResponseConsumerFactory(
//				new HttpAsyncResponseConsumerFactory
//						.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        return builder.build();
    }

//    @Bean
//    public RestHighLevelClient restHighLevelClient() {
//        return new RestHighLevelClient(
//                RestClient.builder(
//                        new HttpHost("localhost", 9200, "http")));
//    }
}
