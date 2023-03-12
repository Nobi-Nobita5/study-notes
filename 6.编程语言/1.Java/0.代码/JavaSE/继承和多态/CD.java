package JavaSE.继承和多态;

public class CD extends Item{//子类CD继承父类ltem
    private String artist;//艺术家
    public CD(String title, int palytime, boolean borrow, String artist) {//构造函数初始化
        super(title, palytime, borrow);//super关键字必须放在第一行，调用父类的构造函数，将标题，播放时间，是否外借传入
        this.artist = artist;
    }
    public void print() {//print方法重写父类的print
        System.out.print("CD ");
        super.print();//super调用父类的print方法
        System.out.print(" 艺术家："+artist);//输出子类独有的属性
        System.out.println();
    }
}
