package com.mcxiaoke.appmanager.task;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 扩展的ThreadPoolExecutor，添加一些实用方法
 * Project: com.douban.radio
 * User: com.mcxiaoke
 * Date: 13-5-9
 * Time: 下午1:33
 */
public class ThreadPool extends ThreadPoolExecutor {
    private static final RejectedExecutionHandler sDefaultHandler =
            new AbortPolicy();

    private ArrayList<Future<?>> mFutures = new ArrayList<Future<?>>();


    public static ThreadPool newSingleThreadPool(String name) {
        return new ThreadPool(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new CountThreadFactory(name));
    }

    public static ThreadPool newSingleThreadPool(ThreadFactory threadFactory) {
        return new ThreadPool(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                threadFactory);
    }

    public static ThreadPool newCachedThreadPool() {
        return new ThreadPool(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    public static ThreadPool newCachedThreadPool(ThreadFactory threadFactory) {
        return new ThreadPool(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                threadFactory);
    }

    public static ThreadPool newFixedThreadPool(int nThreads) {
        return new ThreadPool(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    public static ThreadPool newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
        return new ThreadPool(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                threadFactory);
    }

    public ThreadPool(int corePoolSize,
                      int maximumPoolSize,
                      long keepAliveTime,
                      TimeUnit unit,
                      BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                Executors.defaultThreadFactory(), sDefaultHandler);
    }

    public ThreadPool(int corePoolSize,
                      int maximumPoolSize,
                      long keepAliveTime,
                      TimeUnit unit,
                      BlockingQueue<Runnable> workQueue,
                      ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                threadFactory, sDefaultHandler);
    }

    public ThreadPool(int corePoolSize,
                      int maximumPoolSize,
                      long keepAliveTime,
                      TimeUnit unit,
                      BlockingQueue<Runnable> workQueue,
                      RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                Executors.defaultThreadFactory(), handler);
    }

    public ThreadPool(int corePoolSize,
                      int maximumPoolSize,
                      long keepAliveTime,
                      TimeUnit unit,
                      BlockingQueue<Runnable> workQueue,
                      ThreadFactory threadFactory,
                      RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        Future<T> future = super.submit(task);
        mFutures.add(future);
        return future;
    }

    @Override
    public Future<?> submit(Runnable task) {
        Future<?> future = super.submit(task);
        mFutures.add(future);
        return future;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        Future<T> future = super
                .submit(task, result);
        mFutures.add(future);
        return future;
    }

    @Override
    protected void finalize() {
        super.finalize();
        cancelAllPendingTasks();
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    /**
     * ThreadFactory with name and count reference
     */
    public static class CountThreadFactory implements ThreadFactory {
        private int count;
        private String name;

        public CountThreadFactory(String name) {
            this.name = (name == null ? "Android" : name);

        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(name + "-thread #" + count++);
            Log.v("CountThreadFactory", "newThread() thread=" + thread.getName());
            return thread;
        }
    }


    /**
     * 增加的方法：取消所有队列中等待的任务
     */
    public void cancelAllPendingTasks() {
        for (Future<?> future : mFutures) {
            future.cancel(false);
        }
        mFutures.clear();
    }

}
