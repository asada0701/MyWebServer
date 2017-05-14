package jp.co.topgate.asada.web.app;

import com.google.common.base.Strings;
import jp.co.topgate.asada.web.RequestMessage;
import jp.co.topgate.asada.web.ResponseMessage;
import jp.co.topgate.asada.web.StaticHandler;
import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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
     * urlPatternを使用する上での注意点
     * /program/board/」と「/program/」は同じ扱いとなります。どちらのパスが優先されるかの保証はできません。
     * /program3/board/」と「/program/board/」は別扱いとなります。
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
    public static Handler getHandler(BufferedInputStream bis) throws RequestParseException {
        RequestMessage requestMessage = new RequestMessage(bis);

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
     * リクエストが来たときに呼び出すメソッド
     *
     * @param bis SocketのInputStreamをBufferedInputStreamにラップして渡す
     */
    public abstract void requestComes(BufferedInputStream bis) throws IOException;

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
