package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * リクエストメッセージクラス
 * HTTP/1.1対応
 * サーバーが受け取ったリクエストをパースして、必要な情報を返す
 *
 * @author asada
 */
public class RequestMessage {
    /**
     * ヘッダーフィールドのフィールド名とフィールド値を分割する（その後のスペースは自由のため注意）
     */
    private static final String HEADER_FIELD_NAME_VALUE_DIVISION = ":";

    /**
     * ヘッダーフィールドの項目数
     */
    private static final int HEADER_FIELD_NUM_ITEMS = 2;

    /**
     * メッセージボディのクエリーをクエリー毎に分割する
     */
    private static final String MESSAGE_BODY_EACH_QUERY_DIVISION = "&";

    /**
     * メッセージボディのイコール
     */
    private static final String MESSAGE_BODY_NAME_VALUE_DIVISION = "=";

    /**
     * メッセージボディの項目数
     */
    private static final int MESSAGE_BODY_NUM_ITEMS = 2;

    private Map<String, String> headerFieldUri = new HashMap<>();
    private Map<String, String> messageBody = new HashMap<>();

//    public RequestMessage(BufferedInputStream bis, RequestLine rl) throws RequestParseException {
//        if (bis == null) {
//            throw new RequestParseException("引数であるInputStreamがnullだった");
//        }
//        try {
//            System.out.println("リクエストメッセージのパース開始");
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));
//
//            int[] num = new int[1];
//            num[0] = br.read();
//
//
//
//            System.out.println("リクエストライン" + str);
//
//            if (str == null) {
//                throw new RequestParseException("BufferedReaderのreadLineメソッドの戻り値がnullだった");
//            }
//
//            while ((str = br.readLine()) != null && !str.equals("")) {
//                System.out.println("リクエストヘッダーフィールド" + str);
//
//                String[] header = str.split(HEADER_FIELD_NAME_VALUE_DIVISION);
//                if (header.length == HEADER_FIELD_NUM_ITEMS) {
//                    header[1] = header[1].trim();
//                    headerFieldUri.put(header[0], header[1]);
//                } else if (header.length > HEADER_FIELD_NUM_ITEMS) {
//                    header[1] = header[1].trim();
//                    headerFieldUri.put(header[0], header[1] + HEADER_FIELD_NAME_VALUE_DIVISION + header[2]);
//                } else {
//                    throw new RequestParseException("ヘッダーフィールドが不正なものだった:" + str);
//                }
//            }
//
//            System.out.println("リクエストヘッダーフィールドが読み終わった");
//
//            if ("POST".equals(rl.getMethod())) {
////                while ((str = br.readLine()) != null && !str.equals("")) {
////                    str = URLDecoder.decode(str, "UTF-8");
////                    System.out.println("リクエストメッセージボディ" + str);
////
////                    String[] s1 = str.split(MESSAGE_BODY_EACH_QUERY_DIVISION);
////                    for (String aS1 : s1) {
////                        String[] s2 = aS1.split(MESSAGE_BODY_NAME_VALUE_DIVISION);
////                        if (s2.length == MESSAGE_BODY_NUM_ITEMS) {
////                            messageBody.put(s2[0], s2[1]);
////                        } else {
////                            throw new RequestParseException("リクエストのメッセージボディが不正なものだった:" + str);
////                        }
////                    }
////                }
//                System.out.println("あ");
//                StringBuilder builder = new StringBuilder();
//                int num;
//                while ((num = br.read()) != -1) {
//                    builder.append(String.valueOf(num)).append("\n");
//                }
//                System.out.println(builder.toString());
//            }
//
//        } catch (IOException e) {
//            throw new RequestParseException("BufferedReaderで発生した例外:" + e.toString());
//
//        }
//    }

    /**
     * コンストラクタ、リクエストメッセージのパースを行う
     *
     * @param bis サーバーソケットのInputStream
     * @throws RequestParseException パースに失敗した場合に投げられる
     */
    public RequestMessage(BufferedInputStream bis, RequestLine rl) throws RequestParseException {
        if (bis == null) {
            throw new RequestParseException("引数であるInputStreamがnullだった");
        }
        try {
            System.out.println("リクエストメッセージのパース開始");

            BufferedReader br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));

            String str = br.readLine();

            System.out.println("リクエストライン" + str);

            if (str == null) {
                throw new RequestParseException("BufferedReaderのreadLineメソッドの戻り値がnullだった");
            }

            while ((str = br.readLine()) != null && !str.equals("")) {
                System.out.println("リクエストヘッダーフィールド" + str);

                String[] header = str.split(HEADER_FIELD_NAME_VALUE_DIVISION);
                if (header.length == HEADER_FIELD_NUM_ITEMS) {
                    header[1] = header[1].trim();
                    headerFieldUri.put(header[0], header[1]);
                } else if (header.length > HEADER_FIELD_NUM_ITEMS) {
                    header[1] = header[1].trim();
                    headerFieldUri.put(header[0], header[1] + HEADER_FIELD_NAME_VALUE_DIVISION + header[2]);
                } else {
                    throw new RequestParseException("ヘッダーフィールドが不正なものだった:" + str);
                }
            }

            System.out.println("リクエストヘッダーフィールドが読み終わった");



            if ("POST".equals(rl.getMethod())) {
//                while ((str = br.readLine()) != null && !str.equals("")) {
//                    str = URLDecoder.decode(str, "UTF-8");
//                    System.out.println("リクエストメッセージボディ" + str);
//
//                    String[] s1 = str.split(MESSAGE_BODY_EACH_QUERY_DIVISION);
//                    for (String aS1 : s1) {
//                        String[] s2 = aS1.split(MESSAGE_BODY_NAME_VALUE_DIVISION);
//                        if (s2.length == MESSAGE_BODY_NUM_ITEMS) {
//                            messageBody.put(s2[0], s2[1]);
//                        } else {
//                            throw new RequestParseException("リクエストのメッセージボディが不正なものだった:" + str);
//                        }
//                    }
//                }
                System.out.println("あ");
                StringBuilder builder = new StringBuilder();
                int num;
                while ((num = br.read()) != -1) {
                    builder.append(String.valueOf(num)).append("\n");
                }
                System.out.println(builder.toString());
            }

        } catch (IOException e) {
            throw new RequestParseException("BufferedReaderで発生した例外:" + e.toString());

        }
    }

    /**
     * ヘッダーフィールドのヘッダ名を元にヘッダ値を返す
     *
     * @param fieldName 探したいヘッダ名
     * @return ヘッダ値を返す。ヘッダーフィールドに含まれていなかった場合はNullを返す
     */
    public String findHeaderByName(String fieldName) {
        if (fieldName != null) {
            return headerFieldUri.get(fieldName);
        } else {
            return null;
        }
    }

    /**
     * メッセージボディに含まれていたQuery名を元にQuery値を返す
     *
     * @param key 探したいQuery名
     * @return Query値を返す。URIに含まれていなかった場合はNullを返す
     */
    public String findMessageBody(String key) {
        if (key != null) {
            return messageBody.get(key);
        } else {
            return null;
        }
    }
}
