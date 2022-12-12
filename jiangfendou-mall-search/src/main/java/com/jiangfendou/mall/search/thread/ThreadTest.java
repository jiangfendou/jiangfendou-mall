package com.jiangfendou.mall.search.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadTest {

    // 创建线程池, 当前系统中只有一两个， 每一个 异步任务，提交给线程池  让他自己去执行 就行
    public static ExecutorService service = Executors.newFixedThreadPool(10);


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main......start.....");
//        CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//        }, service);

//        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, service).whenComplete((result, exception) -> {
//            // 虽然能得到异常信息，但是没有办法修改返回数据
//            System.out.println("异步任务成功了。。。。。。。。。。。。。。结果是：" + result + "异常是：" + exception);
//        }).exceptionally(throwable -> {
//            // 感知异常，同时返回默认值
//            return 10;
//        });

//        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, service).handle((res, throwable) -> {
//            if (res != null) {
//                return res * 2;
//            }
//
//            if (throwable != null) {
//                return 0;
//            }
//            return 0;
//        });

        /**
         * 线程串行化
         * 1、thenRunAsync()  不能获取到上一步的执行结果
         * 2、thenAcceptAsyn() 能获取到上一步的执行结果但是没有返回值
         * 3、thenApplyAsyn() 能获取到上一步的执行结果 并且具有返回值
         * */
//        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, service).thenApplyAsync((res) -> {
//            System.out.println("任务2启动了~~~~" + res);
//            return "任务2";
//        }, service);


        /**
         * 两个都完成 才执行第三个任务
         *
         * */
//        CompletableFuture<Integer> integerCompletableFuture01 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务1线程：" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("任务1结束");
//            return i;
//        }, service);
//
//        CompletableFuture<Integer> stringCompletableFuture02 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务2线程：" + Thread.currentThread().getId());
//            try {
//                Thread.sleep(3000);
//                System.out.println("任务2结束");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return 1000000000;
//        }, service);

        // runAfterBothAsync 两个任务执行完才执行，不能感知前两个线程的执行结果的
//        integerCompletableFuture01.runAfterBothAsync(stringCompletableFuture02, () -> {
//            System.out.println("任务3开始");
//        }, service);

        // thenAcceptBothAsync  两个任务执行完才执行，能感知前两个线程的执行结果的
//        integerCompletableFuture01.thenAcceptBothAsync(stringCompletableFuture02, (f1, f2) -> {
//            System.out.println("任务3开始,任务1的结果:" + f1 + "------------" + "任务2的结果：" + f2);
//        }, service);

        // thenAcceptBothAsync  两个任务执行完才执行，能感知前两个线程的执行结果的,并且自己也可以返回结果
//        CompletableFuture<String> stringCompletableFuture =
//            integerCompletableFuture01.thenCombineAsync(stringCompletableFuture02, (f1, f2) -> {
//                System.out.println("任务3开始,任务1的结果:" + f1 + "------------" + "任务2的结果：" + f2);
//                return f1 + ":" + f2;
//            }, service);
//
//        System.out.println("任务三的返回结果：" + stringCompletableFuture.get());


        /**
         * 完成一个 就执行的方法
         * */

        // 不感知结果和自己业务的返回值
//        integerCompletableFuture01.runAfterEitherAsync(stringCompletableFuture02, () -> {
//            System.out.println("任务3开始");
//        }, service);

        // 可以感知之前的返回结果， 没有自己业务的返回值
//        integerCompletableFuture01.acceptEitherAsync(stringCompletableFuture02, (res) -> {
//            System.out.println("任务3开始" + res);
//        }, service);

        // 可以感知之前线程的返回结果， 有自己业务的返回值
//        CompletableFuture<Integer> integerCompletableFuture =
//            integerCompletableFuture01.applyToEitherAsync(stringCompletableFuture02, (res) -> {
//                System.out.println("任务3开始" + res);
//                return 200000;
//            }, service);

        /**
         * 多任务组合
         * */
        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的图片信息");
            return "hello.jpg";
        }, service);

        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的属性信息");
            return "黑色+256G";
        }, service);

        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(50000);
                System.out.println("查询商品的介绍信息");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "华为";
        }, service);

        //垃圾写法
//        futureImg.get();
//        futureAttr.get();
//        futureDesc.get();

        // allOf  需要都执完
        // 高端写法
//        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(futureImg, futureAttr, futureDesc);
//        // 等待所有结果完成
//        voidCompletableFuture.get();

        CompletableFuture<Object> objectCompletableFuture = CompletableFuture.anyOf(futureImg, futureAttr, futureDesc);

        objectCompletableFuture.get();

        System.out.println("main......end.....one:" + futureImg.get() + "two:" + futureAttr.get() + "three" + futureDesc.get());
    }

    public void thread(String[] args) throws ExecutionException, InterruptedException {

//        System.out.println("main......start.....");
//        Thread thread = new Thread01();
//        thread.start();
//        System.out.println("main......end.....");

//        System.out.println("main......start.....");
//        Runable01 runable01 = new Runable01();
//        new Thread(runable01).start();
//        System.out.println("main......end.....");

//         FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
//         new Thread(futureTask).start();
//         System.out.println(futureTask.get());

        service.execute(new Runable01());

        /**
         * 以上 三个创建线程的区别
         * 1 2 不能得到返回值，三可以得到返回值
         * 1 2 3都不能控制资源
         * 4 可以控制资源，性能稳定
         * */

        /**
         * 创建线程池
         * 1、Executors
         * 2、new ThreadPoolExecutor()
         *      参数
         *      int corePoolSize  核心线程数【一直存在除非设置 线程超时】--创建好以后就准备就绪的线程数量，就等待来接受异步任务来执行
         *      int maximumPoolSize 最大线程数量--控制资源
         *      long keepAliveTime 存活时间 -- 如果当前的线程数量 > core数量 （释放空闲的线程（maximumPoolSize - corePoolSize），只要线程的空闲时间大于指定的keepAliveTime）
         *      TimeUnit unit 时间单位
         *      BlockingQueue<Runnable> workQueue 阻塞队列 -- 如果任务有很多，就会将多的任务放在队列里面，只要有线程空闲就会去队列里面取出新的任务
         *      ThreadFactory threadFactory 线程的创建工厂
         *      RejectedExecutionHandler handler 如果队列满了，按照我们自定的拒绝策略来拒绝执行任务
         *
         * 运行流程：
         * 1、线程池创建，准备好 core 数量的核心线程，准备接受任务
         * 2、新的任务进来，用 core 准备好的空闲线程执行。
         * (1) 、core 满了，就将再进来的任务放入阻塞队列中。空闲的 core 就会自己去阻塞队
         * 列获取任务执行
         * (2) 、阻塞队列满了，就直接开新线程执行，最大只能开到 max 指定的数量
         * (3) 、max 都执行好了。Max-core 数量空闲的线程会在 keepAliveTime 指定的时间后自
         * 动销毁。最终保持到 core 大小
         * (4) 、如果线程数开到了 max 的数量，还有新任务进来，就会使用 reject 指定的拒绝策
         * 略进行处理
         * 3、所有的线程创建都是由指定的 factory 创建的。
         *
         * */
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
            200,
            10,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(100000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    }

    public static class Thread01 extends Thread {

        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }

    public static class Runable01 implements Runnable {

        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }

    public static class Callable01 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }
    }
}
