package jp.co.topgate.asada.web;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by yusuke-pc on 2017/05/03.
 */
public class Test {
    public static void main(String[] args) {
        LocalDateTime ldt = LocalDateTime.now();
        String s2 = String.valueOf(ldt.getYear()) + "-" + ldt.getMonthValue() + "-" + ldt.getDayOfMonth() +
                " " + ldt.getHour() + ":" + ldt.getMinute();
        System.out.println(s2);
    }
}
