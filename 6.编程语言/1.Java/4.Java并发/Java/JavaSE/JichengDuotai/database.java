package JavaSE.JichengDuotai;

import java.util.ArrayList;

public class database {
    ArrayList<Item> listItem = new ArrayList<Item>();
    public void add(Item item){//add方法，传入ltem类型
        listItem.add(item);//添加进入listltem容器中
    }
    public void list(){//list方法 负责遍历容器中所有数据
        for (Item item:
             listItem) {
            item.print();
        }
    }

    public static void main(String[] args) {
        database database = new database();//创建database对象
        database.add(new CD("起风了",3,false,"买辣椒也用券"));//添加ltem类型对象，添加ltem子类对象CD（匿名对象），CD构造器初始化，多态
        database.add(new CD("流量", 3, false,"半阳"));
        database.add(new dvd("一出好戏", 125, false,"黄渤"));
        database.list();
    }
}
