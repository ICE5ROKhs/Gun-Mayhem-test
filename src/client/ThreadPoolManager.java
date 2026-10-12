package client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {
    private static final int CORE_POOL_SIZE = 5; // 核心线程数
    private static final int MAX_POOL_SIZE = 10; // 最大线程数
    private static final long KEEP_ALIVE_TIME = 60L; // 空闲线程存活时间

    private static final ThreadPoolManager INSTANCE = new ThreadPoolManager();
    private final ExecutorService executor;

    private ThreadPoolManager() {
        // 创建线程池
        executor = Executors.newFixedThreadPool(CORE_POOL_SIZE);
        //executor = Executors.newCachedThreadPool(); // 如果需要可伸缩的线程池
    }

    public static ThreadPoolManager getInstance() {
        return INSTANCE;
    }

    public void execute(Runnable task) {
        executor.execute(task);
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

