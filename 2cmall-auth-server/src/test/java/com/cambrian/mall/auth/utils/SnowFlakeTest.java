package com.cambrian.mall.auth.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 雪花算法基本测试
 *
 * @author kuma
 */
public class SnowFlakeTest {

    @Test
    public void smock() {
        System.out.println(System.currentTimeMillis());
        long startTime = System.nanoTime();
        SnowFlake snowFlake = new SnowFlake(2L, 3L);
        for (int i = 0; i < 500000; i++) {
            long id = snowFlake.nextId();
            System.out.println(id);
        }
        System.out.println((System.nanoTime() - startTime) / (1000 * 1000) + "ms");
        assertTrue(true);
    }

    @Test
    public void generate_300000_id_should_never_duplicate() {
        // given
        SnowFlake snowFlake = new SnowFlake(1L, 5L);
        // when
        int maxSize = 300 * 1000;
        List<Long> source = new ArrayList<>();
        for (int i = 0; i < maxSize; i++) {
            long id = snowFlake.nextId();
            source.add(id);
        }
        // then
        Set<Long> set = new HashSet<>(source);
        assertEquals("300,000 id 不重复", source.size(), set.size());
    }
}