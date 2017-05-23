package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.HtmlInitializeException;
import jp.co.topgate.asada.web.program.board.HtmlEditor;
import jp.co.topgate.asada.web.program.board.ProgramBoardHandler;

import java.io.OutputStream;

/**
 * ハンドラー抽象クラス
 *
 * @author asada
 */
public abstract class Handler {
    /**
     * リソースファイルのパス
     */
    static final String FILE_PATH = "./src/main/resources";

    /**
     * ハンドラーのファクトリーメソッド
     * 注意点
     * URLパターンとの比較の際にStringのstartsWithメソッドを使用しています。
     * /program/board/と/program/といった始まりが同じURLには注意してください。
     *
     * @param requestMessage リクエストメッセージのオブジェクトを渡す
     * @return 今回の接続を担当するハンドラーのオブジェクトを返す
     * @throws HtmlInitializeException {@link HtmlEditor#HtmlEditor()}を参照
     */
    static Handler getHandler(RequestMessage requestMessage) throws HtmlInitializeException {
        String uri = requestMessage.getUri();

        Handler handler = new StaticHandler(requestMessage);

        if (uri.startsWith(UrlPattern.PROGRAM_BOARD.getUrlPattern())) {
            handler = new ProgramBoardHandler(requestMessage);
        }
        return handler;
    }

    /**
     * URIを元に、実際のファイルパスを返すメソッド
     */
    public static String getFilePath(UrlPattern urlPattern, String uri) {
        return FILE_PATH + uri.replace(
                urlPattern.getUrlPattern(),
                urlPattern.getFilePath());
    }

    /**
     * リクエストを適切に処理し、ResponseMessageのオブジェクトを生成し、OutputStreamにレスポンスを書き込む
     *
     * @param outputStream SocketのOutputStream
     */
    public abstract void handleRequest(OutputStream outputStream);
}
