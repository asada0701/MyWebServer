package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yusuke-pc on 2017/05/01.
 */
public class HandlerFactory {
    /**
     * リソースファイルのパス
     */
    private static final String FILE_PATH = "./src/main/resources";

    /**
     * URI,実際のパス
     */
    private static Map<String, String> urlPattern = new HashMap<>();

    static {
        urlPattern.put("/program/board/", "/2/");
    }

    public static Handler getHandler(BufferedInputStream bis) {
        Handler handler;
        RequestLine requestLine;
        try {
            requestLine = new RequestLine(bis);
            String uri = requestLine.getUri();

            //先に入れてしまう。
            handler = new StaticHandler();

            for (String s : urlPattern.keySet()) {
                if (uri.startsWith(s)) {
                    handler = new WebAppHandler();
                }
            }

            //リクエストラインに問題ない場合はハンドラーにリクエストラインを渡す。
            handler.setRequestLine(requestLine);

        } catch (RequestParseException e) {

            //リクエストラインのパースの失敗:400
            handler = new StaticHandler();

            //リクエストラインのデフォルトコンストラクタを使うべきではないゾ。
            requestLine = new RequestLine();

            //nullを避けられる。
            handler.setRequestLine(requestLine);
            handler.setStatusCode(ResponseMessage.BAD_REQUEST);
        }

        return handler;
    }

    public static String getUrlPattern(String key) {
        for (String s : urlPattern.keySet()) {
            if (key.startsWith(s)) {
                return urlPattern.get(s);
            }
        }
        return null;
    }

    public static String getFilePath(String uri) {
        for (String s : urlPattern.keySet()) {
            //URIが何から始まっているか確認
            if (uri.startsWith(s)) {
                //今回/program/board/の部分が/2/となるメソッド、こんなに長い意味あるのかな、、、てかヌルポが怖い、、、

                String path = urlPattern.get(s);

                String[] str = uri.split("/");
                StringBuilder builder = new StringBuilder();

                builder.append(str[3]);

                for (int i = 4; i < str.length; i++) {
                    builder.append("/").append(str[i]);
                }
                return FILE_PATH + path + builder.toString();
            }
        }
        return FILE_PATH + uri;
    }
}
