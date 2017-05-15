package jp.co.topgate.asada.web.app;

import com.google.common.base.Strings;
import jp.co.topgate.asada.web.RequestMessage;
import jp.co.topgate.asada.web.StaticHandler;
import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
     * urlPatternを使用する上での注意点
     * /program/board/」と「/program/」は同じ扱いとなります。どちらのパスが優先されるかの保証はできません。
     * /program3/board/」と「/program/board/」は別扱いとなります。
     */
    private static Map<String, String> urlPattern = new HashMap<>();

    static {
        urlPattern.put("/program/board/", "/2/");
    }

    protected RequestMessage requestMessage;

    /**
     * ハンドラーのファクトリーメソッド
     *
     * @param is ソケットの入力ストリーム
     * @return 今回の接続を担当するハンドラーのオブジェクト
     * @throws RequestParseException {@link RequestMessage}を参照
     * @throws NullPointerException  引数がnull
     */
    public static Handler getHandler(InputStream is) throws RequestParseException, NullPointerException {
        Objects.requireNonNull(is);

        RequestMessage requestMessage = new RequestMessage(is);
        Handler handler = new StaticHandler(requestMessage);
        String uri = requestMessage.getUri();
        for (String s : urlPattern.keySet()) {
            if (uri.startsWith(s)) {
                handler = new ProgramBoardHandler(requestMessage);
            }
        }
        return handler;
    }

    /**
     * 抽象メソッド、リクエストの処理を行うメソッド
     */
    public abstract StatusLine requestComes();

    /**
     * 抽象メソッド、レスポンスを返すときに呼び出すメソッド
     *
     * @param os SocketのOutputStream
     */
    public abstract void returnResponse(OutputStream os, StatusLine sl);

    /**
     * URIを元にファイルパスを返すメソッド
     * （例）/program/board/css/style.css
     * を渡すと
     * ./src/main/resources/2/css/style.css
     * が返ってくる
     *
     * @param uri リクエストラインクラスのURI
     * @return リクエストされたファイルのパス
     */
    public static String getFilePath(String uri) {
        if (Strings.isNullOrEmpty(uri)) {
            return FILE_PATH + "/";
        }

        for (String s : urlPattern.keySet()) {
            String[] uriRegistered = s.split("/");
            String[] actualUri = uri.split("/");

            boolean isMatch = true;
            if (actualUri.length >= uriRegistered.length) {
                for (int i = 0; i < uriRegistered.length; i++) {
                    if (!uriRegistered[i].equals(actualUri[i])) {
                        isMatch = false;
                    }
                }
            } else {
                continue;
            }

            if (isMatch) {
                StringBuilder builder = new StringBuilder();
                builder.append(FILE_PATH).append(urlPattern.get(s)).append(actualUri[uriRegistered.length]);

                for (int i = uriRegistered.length + 1; i < actualUri.length; i++) {
                    builder.append("/").append(actualUri[i]);
                }
                return builder.toString();
            }
        }
        return FILE_PATH + uri;
    }
}
