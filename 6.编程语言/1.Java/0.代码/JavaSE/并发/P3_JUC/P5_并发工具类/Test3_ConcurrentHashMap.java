package JavaSE.并发.P3_JUC.P5_并发工具类;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
/*
* 在这个例子中，我们首先创建了一个ConcurrentHashMap对象，并向其中添加了一些元素。
* 然后，我们创建了四个线程，其中两个线程（t1和t2）用于遍历ConcurrentHashMap，另外两个线程（t3和t4）用于更新ConcurrentHashMap。
* 可以看到，无论是遍历还是更新操作，都可以在多线程环境下安全地进行，而不会出现线程安全问题。
* */
public class Test3_ConcurrentHashMap {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        // 在多线程环境下遍历ConcurrentHashMap
        Thread t1 = new Thread(() -> {
            for (String key : map.keySet()) {
                System.out.println(Thread.currentThread().getName() + " : " + key + " - " + map.get(key));
            }
        });
        Thread t2 = new Thread(() -> {
            for (String key : map.keySet()) {
                System.out.println(Thread.currentThread().getName() + " : " + key + " - " + map.get(key));
            }
        });
        t1.start();
        t2.start();

        // 在多线程环境下更新ConcurrentHashMap
        Thread t3 = new Thread(() -> {
            map.put("a", 10);
            map.putIfAbsent("d", 4);
        });
        Thread t4 = new Thread(() -> {
            map.replace("b", 20);
            map.remove("c");
        });
        t3.start();
        t4.start();
    }
}
