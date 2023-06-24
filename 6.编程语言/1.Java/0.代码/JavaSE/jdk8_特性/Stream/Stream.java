package JavaSE.jdk8_特性.Stream;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Stream {
    //map方法
    @Test
    public void mapTest() {
        List<Double> cost = Arrays.asList(10.0, 20.0,30.0);
        cost.stream().map(x -> x + x*0.05).forEach(x -> System.out.println(x));
    }
    //reduce方法
    @Test
    public void mapReduceTest() {
        List<Double> cost = Arrays.asList(10.0, 20.0,30.0);
        double allCost = cost.stream().map(x -> x+x*0.05).reduce((sum,x) -> sum + x).get();
        System.out.println(allCost);
    }
    //filter方法
    //Stream 流提供了一个 collect() 方法，可以收集流中的数据到【集合】或者【数组】中去。
    @Test
    public void filterTest() {
        List<Double> cost = Arrays.asList(10.0, 20.0,30.0,40.0);
        List<Double> filteredCost = cost.stream().filter(x -> x > 25.0).collect(Collectors.toList());
        filteredCost.forEach(x -> System.out.println(x));

    }
}
