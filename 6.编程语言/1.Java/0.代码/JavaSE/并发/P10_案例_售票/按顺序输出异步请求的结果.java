package JavaSE.并发.P10_案例_售票;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *问题：已知arr=[url1,url2,url3],fetch(url)可获取url的请求结果，请实现按顺序输出异步请求结果
 **/


/*
方法一：创建相应数量线程，依次启动，执行对应的异步任务，并使用join等待每个线程完成
 */
class 按顺序输出异步请求的结果 {
    public static void main(String[] args) throws InterruptedException {
        List<String> urls = List.of("url1", "url2", "url3");
        List<Thread> threads = new ArrayList<>();

        for (String url : urls) {
            Thread thread = new Thread(() -> {
                String response = fetch(url);
                System.out.println("Response for " + url + ": " + response);
            });
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
            thread.join(); // 等待每个线程完成
        }
    }

    // 模拟异步请求
    private static String fetch(String url) {
        // 执行异步请求，这里使用休眠来模拟请求耗时
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 返回请求结果
        return "Response from " + url;
    }
}
/*
方法二：
1.创建一个固定大小的线程池ExecutorService，并使用submit方法提交异步任务。
  每个异步任务返回一个Future对象。
2.我们使用future.get()方法按顺序获取每个异步任务的结果，并输出结果。
  TODO 请注意，future.get()是一个阻塞调用，它会等待异步任务完成并获取结果。
  因此，程序会按顺序输出结果。
3.最后，在使用完ExecutorService后调用shutdown()方法来关闭线程池。
 */
class Solve2{
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        List<String> urls = List.of("url1", "url2", "url3", "url4", "url5", "url6");
        ExecutorService executor = Executors.newFixedThreadPool(urls.size());

        for (String url : urls) {
            Future<String> future = executor.submit(() -> fetch(url));
            String response = future.get();
            System.out.println("Response: " + response);
        }


        executor.shutdown();
    }

    // 模拟异步请求
    private static String fetch(String url) {
        // 执行异步请求，这里使用休眠来模拟请求耗时
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 返回请求结果
        return "Response from " + url;
    }
}


