package jp.co.topgate.asada.web;

import java.util.function.IntBinaryOperator;

/**
 * Created by yusuke-pc on 2017/04/28.
 */
public class Test {
    public static void main(String[] args) {
        IntBinaryOperator func = Test::sub;
        int a = func.applyAsInt(5, 3);
        System.out.println("5 - 3は" + a);
    }

    public static int sub(int a, int b) {
        return a - b;
    }
}
