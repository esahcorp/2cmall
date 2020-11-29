package com.cambrian.mall.product;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author kuma 2020-11-29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedissonLockTest {

    @Autowired
    RedissonClient redisson;

    public String park() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        park.trySetPermits(3);
        park.acquire();
        return "ok";
    }

    public String go() {
        RSemaphore park = redisson.getSemaphore("park");
        park.trySetPermits(3);
        park.release();
        return "ok";
    }

    public String closeDoor() throws InterruptedException {
        RCountDownLatch door = redisson.getCountDownLatch("door");
        System.out.println(1);
        boolean b = door.trySetCount(3);
        System.out.println("2 " + b);
        door.await();
        System.out.println(3);
        return "The door has been closed.";
    }

    public String closeDoor(Integer id) {
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.countDown();
        return "Class " + id + " has been lock.";
    }

    @Test
    public void start() throws InterruptedException {
        Assert.assertTrue(true);
    }
}
