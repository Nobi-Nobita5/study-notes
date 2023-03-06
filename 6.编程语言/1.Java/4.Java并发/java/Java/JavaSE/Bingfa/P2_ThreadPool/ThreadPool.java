package Java.JavaSE.Bingfa.P2_ThreadPool;

import java.util.concurrent.*;

public class ThreadPool
{
    private static int POOL_NUM = 10;

    public static void main(String[] args)
    {
        ExecutorService executorService = new ThreadPoolExecutor(
                5,
                5,
                1l,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
        for(int i = 0; i<POOL_NUM; i++)
        {
            RunnableThread thread = new RunnableThread();
            executorService.execute(thread);
        }
    }
}

class RunnableThread implements Runnable
{
    private int THREAD_NUM = 10;
    public void run()
    {
        for(int i = 0; i<THREAD_NUM; i++)
        {
            System.out.println("线程" + Thread.currentThread() + " " + i);
        }
    }
}
