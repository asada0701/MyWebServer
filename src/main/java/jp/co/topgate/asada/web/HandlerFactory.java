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
class HandlerFactory {
    /**
     * リソースファイルのパス
     */
    private static final String FILE_PATH = "./src/main/resources";

    /**
     * URIとファイルパスのハッシュマップ
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
    static Handler getHandler(BufferedInputStream bis) {
        Handler handler;
        RequestLine requestLine;
        try {
            requestLine = new RequestLine(bis);
            String uri = requestLine.getUri();

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

            //ハンドラーにリクエストラインを渡せないのでnullになるので注意
        }

        return handler;
    }

    /**
     * URIを元にファイルパスを返すメソッド
     * （例）/program/board/css/style.css
     * を渡すと
     * ./src/main/resources/2/css/style.css
     * が返ってくる
     *
     * @param uri リクエストラインクラスのURI
     * @retur リクエストされたファイルのパス
     */
    static String getFilePath(String uri) {
        if (uri == null) {
            return null;
        }

        for (String s : urlPattern.keySet()) {
            String[] s1 = s.split("/");
            String[] s2 = uri.split("/");
            int i1 = s1.length;
            int i2 = s2.length;

            boolean isMatch = true;
            if (i2 >= i1) {
                for (int i = 0; i < i1; i++) {
                    if (!s1[i].equals(s2[i])) {
                        isMatch = false;
                    }
                }
            } else {
                continue;
            }

            if (isMatch) {
                StringBuffer buffer = new StringBuffer();
                buffer.append(FILE_PATH).append(urlPattern.get(s));

                for (int i = i1; i < i2; i++) {
                    if (i == i1) {
                        buffer.append(s2[i]);
                    } else {
                        buffer.append("/").append(s2[i]);
                    }
                }
                return buffer.toString();
            }
        }

        return FILE_PATH + uri;
    }
}
