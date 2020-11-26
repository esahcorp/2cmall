package com.cambrian.mall.product;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

/**
 * @author kuma 2020-11-27
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringRedisTests {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void testSetAndGet() {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        String key = "testSetAndGet";
        String randomValue = UUID.randomUUID().toString();
        valueOperations.set(key, randomValue);
        Assert.assertEquals(randomValue, valueOperations.get(key));
    }
}
