package JavaSE.并发.P2_线程池;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/*
* 特点：固定长度的线程池【默认1】，支持定时的以及周期性的任务执行【通过设置延时时间实现】，
*       因为任务是周期性执行的，必须得前一个任务完成，才能开始后面的任务，所以任务的执行是串行的。
* */
public class Test4_NewScheduledThreadPool {
    public static void main(String[] args) {
        //定义线程池大小为3
        ScheduledExecutorService newScheduledThreadPool =
                Executors.newScheduledThreadPool(3);
        for (int i = 0; i < 5; i++) {
            int temp = i;
            newScheduledThreadPool.schedule(
                    //需要使用函数式接口（只有一个抽象方法的接口）时，就可以使用lambda表达式来代替匿名内部类的写法。
                    () -> System.out.println("线程" + Thread.currentThread().getName() + "任务编号i:" + temp),
                    2,
                    TimeUnit.SECONDS);//这里表示进行周期为2秒的执行。
        }
        newScheduledThreadPool.shutdown();
        }
}
