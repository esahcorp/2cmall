package com.cambrian.mall.product;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * @author kuma 2020-12-08
 */
public class ThreadPoolTests {

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 20,
            60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(50),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardPolicy());

    @After
    public void setDown() {
        threadPoolExecutor.shutdown();
    }

    @Test
    public void executorThreadPool() throws InterruptedException {
//        ExecutorService executorService = Executors.newWorkStealingPool();
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
//        ExecutorService executorService = Executors.newFixedThreadPool(2);
//        ExecutorService executorService = Executors.newCachedThreadPool();
        System.out.println("Main thread start ...");

        CountDownLatch latch = new CountDownLatch(50);

        for (int i = 0; i < 50; i++) {
            threadPoolExecutor.execute(() -> {
                System.out.println("current thread: " + Thread.currentThread().getName());
                latch.countDown();
            });
        }
        latch.await();
        System.out.println("Main thread end ...");
        Assert.assertTrue(true);
    }

    @Test
    public void completableFutureSupplyAsync() throws ExecutionException, InterruptedException {
        System.out.println("Test start ...");
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("Current thread: " + Thread.currentThread().getName());
            return 10 / 2;
        }, threadPoolExecutor);
        Integer i = future.get();
        System.out.println("Result: " + i);
        System.out.println("Test end ...");
        Assert.assertTrue(true);
    }

    @Test
    public void completableFutureRunAsync() throws ExecutionException, InterruptedException {
        System.out.println("Test start ...");
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("Current thread: " + Thread.currentThread().getName());
            int i = 10 / 2;
            System.out.println("Result: " + i);
        }, threadPoolExecutor);
        future.get();
        System.out.println("Test end ...");
        Assert.assertTrue(true);
    }

    @Test
    public void completableFutureWhenComplete() throws ExecutionException, InterruptedException {
        System.out.println("Test start ...");
        CompletableFuture<Integer> future = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println("Current thread: " + Thread.currentThread().getName());
                    int i = 10 / 0;
                    System.out.println("Result: " + i);
                    return i;
                }, threadPoolExecutor)
                .exceptionally(e -> 10)
                .whenComplete((result, e) -> System.out.println("r: " + result + ", e: " + e))
                ;
        Integer i = future.get();
        System.out.println("Test end ..." + i);
        Assert.assertTrue(true);
    }

    @Test
    public void completableFutureHandle() throws ExecutionException, InterruptedException {
        System.out.println("Test start ...");
        CompletableFuture<Integer> future = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println("Current thread: " + Thread.currentThread().getName());
                    int i = 10 / 0;
                    System.out.println("Result: " + i);
                    return i;
                }, threadPoolExecutor)
                .handle((i, e) -> {
                    if (i != null) {
                        return i * 2;
                    }
                    if (e != null) {
                        return -1;
                    }
                    return 0;
                })
                .exceptionally(e -> -10);
        Integer i = future.get();
        System.out.println("Test end ..." + i);
        Assert.assertTrue(true);
    }

    @Test
    public void completableFutureThen() throws ExecutionException, InterruptedException {
        System.out.println("Test start ...");
        CompletableFuture<Void> future = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println("Current thread: " + Thread.currentThread().getName());
                    int i = 10 / 2;
                    System.out.println("1 Result: " + i);
                    return i;
                }, threadPoolExecutor)
                .thenApplyAsync(i -> {
                    System.out.println("apply thread: " + Thread.currentThread().getName());
                    return i * 2;
                }, threadPoolExecutor)
                .thenAcceptAsync(i -> {
                    System.out.println("accept thread: " + Thread.currentThread().getName());
                    System.out.println("Final result: " + i);
                }, threadPoolExecutor)
                .thenRunAsync(() -> System.out.println("run thread: " + Thread.currentThread().getName()), threadPoolExecutor);
        future.get();
        System.out.println("Test end ...");
        Assert.assertTrue(true);
    }
}
