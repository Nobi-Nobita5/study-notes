package JavaSE.并发.P10_案例_售票;

public class MainClass {
    public static void main(String[] args) {
        Station station1 = new Station("站台1");
        Station station2 = new Station("站台2");
        Station station3 = new Station("站台3");
        station1.start();
        station2.start();
        station3.start();
    }
}

class Station extends Thread {
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
