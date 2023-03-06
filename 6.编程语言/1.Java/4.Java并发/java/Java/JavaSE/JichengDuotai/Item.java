package Java.JavaSE.JichengDuotai;

public class Item {
    private String title;//标题
    private int palytime;//播放时间
    private boolean borrow;//是佛外借

    public Item(String title, int palytime, boolean borrow) {//构造函数初始化变量
        this.title = title;
        this.palytime = palytime;
        this.borrow = borrow;
    }

    public void print(){//print方法 输出数据
        System.out.println("标题：" + title + "时间：" + palytime);
    }
}
