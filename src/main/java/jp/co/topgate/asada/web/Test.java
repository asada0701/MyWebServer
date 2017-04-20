package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseRuntimeException;

import java.util.HashMap;

/**
 * Created by yusuke-pc on 2017/04/20.
 */
public class Test {
    private static final String MESSAGE_BODY_AMPERSAND = "&";
    private static final String MESSAGE_BODY_EQUAL = "=";
    private static final int MESSAGE_BODY_NUM_ITEMS = 2;

    public static void main(String[] args){
        HashMap<String, String> messageBody = new HashMap<>();
        String str = "name=asada&like=cat";
        System.out.println(str);
        String[] s1 = str.split(MESSAGE_BODY_AMPERSAND);
        for (String aS1 : s1) {
            String[] s2 = aS1.split(MESSAGE_BODY_EQUAL);
            if (s2.length == MESSAGE_BODY_NUM_ITEMS) {
                messageBody.put(s2[0], s2[1]);
            } else {
                throw new RequestParseRuntimeException();
            }
        }
        for(String key : messageBody.keySet()){
            System.out.println(key + ":" + messageBody.get(key));
        }
    }
}
