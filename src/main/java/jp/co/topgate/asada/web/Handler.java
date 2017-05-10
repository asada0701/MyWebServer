package jp.co.topgate.asada.web;

import com.google.common.base.Strings;
import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * ハンドラー抽象クラス
 *
 * @author asada
 */
public abstract class Handler {
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
     * HTTPレスポンスメッセージのステータスコード
     */
    protected int statusCode;

    /**
     * リクエストライン
     */
    protected RequestLine requestLine;

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
            handler.requestLine = requestLine;

        } catch (RequestParseException e) {
            //リクエストラインのパースの失敗:400
            handler = new StaticHandler();
            handler.statusCode = ResponseMessage.BAD_REQUEST;

            //ハンドラーにリクエストラインを渡せないのでnullになるので注意
        }

        return handler;
    }

    /**
     * リクエストが来たときに呼び出すメソッド
     *
     * @param bis SocketのInputStreamをBufferedInputStreamにラップして渡す
     */
    public void requestComes(BufferedInputStream bis) {
        if (requestLine != null) {
            String method = requestLine.getMethod();        //サーバーをスタートする前にアクセスすると、ここでヌルポする
            String uri = requestLine.getUri();
            String protocolVersion = requestLine.getProtocolVersion();

            if (!"HTTP/1.1".equals(protocolVersion)) {
                statusCode = ResponseMessage.HTTP_VERSION_NOT_SUPPORTED;

            } else if (!"GET".equals(method) && !"POST".equals(method)) {
                statusCode = ResponseMessage.NOT_IMPLEMENTED;

            } else {
                File file = new File(Handler.getFilePath(uri));
                if (!file.exists() || !file.isFile()) {
                    statusCode = ResponseMessage.NOT_FOUND;
                } else {
                    statusCode = ResponseMessage.OK;
                }
            }
        }
    }

    /**
     * 抽象メソッド、レスポンスを返すときに呼び出すメソッド
     *
     * @param os SocketのOutputStream
     */
    public abstract void returnResponse(OutputStream os);


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
        if (Strings.isNullOrEmpty(uri)) {
            uri = "/";
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
                buffer.append(FILE_PATH).append(urlPattern.get(s)).append(s2[i1]);

                for (int i = i1 + 1; i < i2; i++) {
                    buffer.append("/").append(s2[i]);
                }
                return buffer.toString();
            }
        }
        return FILE_PATH + uri;
    }

    /**
     * テスト用メソッド
     */
    void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * テスト用メソッド
     */
    void setRequestLine(RequestLine requestLine) {
        this.requestLine = requestLine;
    }
}
