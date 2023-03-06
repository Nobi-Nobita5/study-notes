package Java.JavaSE.JichengDuotai;

public class dvd extends Item {//子类DVD继承父类ltem
    private String director;//导演
    public dvd(String title, int palytime, boolean borrow, String director) {//构造函数初始化
        super(title, palytime, borrow);//super关键字必须放在第一行，调用父类的构造函数，将标题，播放时间，是否外借传入
        this.director = director;
    }
    public void print() {//print方法重写父类的print
        System.out.print("DVD ");
        super.print();//super调用父类的print方法
        System.out.print(" 导演："+director);//输出子类独有的属性
        System.out.println();
    }
}
