package com.cambrian.mall.search;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchApplicationTests {

	@Autowired
	RestHighLevelClient restHighLevelClient;

	@Autowired
	RequestOptions requestOptions;

	@Test
	public void contextLoads() {
		assertNotNull(restHighLevelClient);
		assertNotNull(requestOptions);
	}

}
