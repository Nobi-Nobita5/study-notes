package Java.JavaSE.Bingfa;

public class Test{
    public static void main(String[] args){

        Service mService = new Service();

        Thread sleepThread = new Thread(new SleepThread(mService));
        Thread waitThread = new Thread(new WaitThread(mService));
        sleepThread.start();
        waitThread.start();

    }
}
