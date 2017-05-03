package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * ハンドラファクトリークラス
 *
 * @author asada
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

    /**
     * ハンドラーのファクトリーメソッド
     *
     * @param bis ソケットの入力ストリーム
     * @return 今回の接続を担当するハンドラーのオブジェクト
     */
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
        for (String s : urlPattern.keySet()) {
            String[] s1 = s.split("/");
            String[] s2 = uri.split("/");
            int i1 = s1.length;
            int i2 = s2.length;

            StringBuilder builder = new StringBuilder();
            builder.append(FILE_PATH).append(urlPattern.get(s));

            for (int i = i1; i < i2; i++) {
                if (i == i1) {
                    builder.append(s2[i]);
                } else {
                    builder.append("/").append(s2[i]);
                }
            }
            return builder.toString();
        }
        return FILE_PATH + uri;
    }
}
