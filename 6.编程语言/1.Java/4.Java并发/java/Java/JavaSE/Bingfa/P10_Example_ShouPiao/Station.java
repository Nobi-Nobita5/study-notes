package Java.JavaSE.Bingfa.P10_Example_ShouPiao;

public class Station extends Thread {
    static int tick = 20;
    static Object ob = "aa";
    public Station (String name){
        super(name);
    }

    @Override
    public void run() {
        while (tick>0){
            synchronized (ob) {
                if (tick > 0) {
                    System.out.println(getName() + "卖出了第" + tick + "张票");
                    tick--;
                } else {
                    System.out.println("票卖完了");
                }
            }
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
