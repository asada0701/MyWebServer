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
     * URIと実際のパスのハッシュマップ
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

    /**
     * URIを元に実際のファイルパスを返すメソッド
     *
     * @param uri リクエストラインクラスのURI
     * @retur リクエストされたファイルのパス
     */
    public static String getFilePath(String uri) {
        if (uri == null) {
            return null;
        }
        String[] s = uri.split("/");
        int slashNum = s.length;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < slashNum - 1; i++) {
            builder.append(s[i]).append("/");
        }
        String str = builder.toString();
        if (urlPattern.containsKey(str)) {
            return FILE_PATH + urlPattern.get(str) + s[slashNum - 1];
        }
        return FILE_PATH + uri;
    }
}
