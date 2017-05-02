package jp.co.topgate.asada.web;

/**
 * Created by yusuke-pc on 2017/05/02.
 */
public class Test2 {

    public static void main(String[] args) throws Exception {
        int[] num = new int[1];
        num[0] = 110;
        byte[] b = new byte[1];
        b[0] = (byte) num[0];
        System.out.println(new String(b, "UTF-8"));
//        try {
//            int[] num = {110, 97, 109, 101, 61, 97, 115, 97, 100, 97, 38, 109, 97, 105, 108, 61};
//            byte[] b = new byte[num.length];
//            for (int i = 0; i < num.length; i++) {
//                b[i] = (byte) num[i];
//            }
//            System.out.println(new String(b, "UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }
}
