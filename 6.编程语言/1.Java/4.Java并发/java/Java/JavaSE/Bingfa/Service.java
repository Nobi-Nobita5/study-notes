package Java.JavaSE.Bingfa;
/*
在这个例子中就是sleepThread线程拿到了service对象的同步锁，进入后休眠，但没有释放机锁，
那么waitThread线程是不能执行这个service对象的其他同步代码块的，也就就是不能进入这一段代码
 */
public class Service {

    public void mSleep(){
        synchronized(this){
            try{
                System.out.println(" Sleep 。当前时间："+System.currentTimeMillis());
                Thread.sleep(3*1000);
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
    }

    public void mWait(){
        synchronized(this){
            System.out.println(" Wait 。结束时间："+System.currentTimeMillis());
            //this.wait();是object类的方法，sleep() 和 wait() 的区别就是 调用sleep方法的线程不会释放对象锁，而调用wait() 方法会释放对象锁
        }
    }

}
