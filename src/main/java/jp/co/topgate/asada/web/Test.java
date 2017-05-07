package jp.co.topgate.asada.web;

import java.util.ArrayList;

/**
 * Created by yusukenakashima0701 on 2017/05/06.
 */
public class Test {
    public static void main(String[] args) {
        ArrayList<String> al = new ArrayList<>();
        al.add("1");
        al.add("2");
        al.add("3");

        for (String s : al) {
            System.out.println(s);
        }

        System.out.println();
        System.out.println(al.get(al.size() - 1));

        System.out.println();
        for (int i = al.size() - 1; i > -1; i--) {
            System.out.println(al.get(i));
        }
    }

}
