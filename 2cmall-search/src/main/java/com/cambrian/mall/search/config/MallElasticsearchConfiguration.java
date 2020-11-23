package com.cambrian.mall.search.config;

import org.elasticsearch.client.RequestOptions;
import org.springframework.context.annotation.Configuration;

/**
 * @author kuma 2020-11-17
 */
@Configuration
public class MallElasticsearchConfiguration {

    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//		builder.addHeader("Authorization", "Bearer " + TOKEN);
//		builder.setHttpAsyncResponseConsumerFactory(
//				new HttpAsyncResponseConsumerFactory
//						.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();

    }
}
