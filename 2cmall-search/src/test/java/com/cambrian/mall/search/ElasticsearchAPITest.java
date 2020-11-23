package com.cambrian.mall.search;

import com.alibaba.fastjson.JSON;
import com.cambrian.mall.search.config.MallElasticsearchConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author kuma 2020-11-17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticsearchAPITest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void indexDoc() throws IOException {
        IndexRequest request = new IndexRequest("users");
        User user = new User("zhangsan", 20, "M");
        request.source(JSON.toJSONString(user), XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(request, MallElasticsearchConfiguration.COMMON_OPTIONS);
        assertNotNull(response.getResult());
        assertEquals(DocWriteResponse.Result.CREATED, response.getResult());
    }

    @Data
    @AllArgsConstructor
    class User {
        String name;
        Integer age;
        String gender;
    }

    @Test
    public void searchDoc() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank_account");

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery("address", "mill"));
        TermsAggregationBuilder ageTerms = AggregationBuilders.terms("ageTerms").field("age").size(10);
        builder.aggregation(ageTerms);
        AvgAggregationBuilder ageAvg = AggregationBuilders.avg("ageAvg").field("age");
        builder.aggregation(ageAvg);
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        builder.aggregation(balanceAvg);
        builder.from(0);
        builder.size(5);
        searchRequest.source(builder);

        SearchResponse response = restHighLevelClient.search(searchRequest, MallElasticsearchConfiguration.COMMON_OPTIONS);

        assertNotNull(response);

        SearchHit[] searchHits = response.getHits().getHits();
        Arrays.stream(searchHits).map(hit -> {
            String sourceAsString = hit.getSourceAsString();
            return JSON.parseObject(sourceAsString, Account.class);
        }).forEach(System.out::println);

        Aggregations aggregations = response.getAggregations();
        Terms ageTerms1 = aggregations.get("ageTerms");
        for (Terms.Bucket bucket : ageTerms1.getBuckets()) {
            System.out.println(bucket.getKey() + ":" + bucket.getDocCount());
        }

        Avg ageAvg1 = aggregations.get("ageAvg");
        System.out.println("平均年龄" + ageAvg1.getValue());

        Avg balanceAvg1 = aggregations.get("balanceAvg");
        System.out.println("平均薪资" + balanceAvg1.getValue());
    }
}
