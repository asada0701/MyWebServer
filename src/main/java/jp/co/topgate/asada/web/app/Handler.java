package jp.co.topgate.asada.web.app;

import jp.co.topgate.asada.web.RequestMessage;
import jp.co.topgate.asada.web.StaticHandler;
import jp.co.topgate.asada.web.exception.HtmlInitializeException;
import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.InputStream;
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
    public static final String FILE_PATH = "./src/main/resources";

    /**
     * ハンドラーのファクトリーメソッド
     * RequestParseExceptionはServerクラスでキャッチして、バッドリクエストレスポンスを返す
     * 注意点
     * URLパターンとの比較の際にStringのstartsWithメソッドを使用しています。
     * /program/board/と/program/といった始まりが同じURLには注意してください。
     *
     * @param is ソケットの入力ストリーム
     * @return 今回の接続を担当するハンドラーのオブジェクト
     * @throws RequestParseException   {@link RequestMessage#RequestMessage(InputStream)}を参照
     * @throws HtmlInitializeException {@link HtmlEditor#HtmlEditor()}を参照
     */
    public static Handler getHandler(InputStream is) throws RequestParseException, HtmlInitializeException {
        RequestMessage requestMessage = new RequestMessage(is);
        String uri = requestMessage.getUri();

        Handler handler = new StaticHandler(requestMessage);                //デフォルトのハンドラー

        if (uri.startsWith(UrlPattern.PROGRAM_BOARD.getUrlPattern())) {
            handler = new ProgramBoardHandler(requestMessage);              //program board用ハンドラー
        }
        return handler;
    }

    /**
     * URIを元に、実際のファイルパスを返すメソッド
     */
    static String getFilePath(UrlPattern urlPattern, String uri) {
        return FILE_PATH + uri.replace(
                urlPattern.getUrlPattern(),
                urlPattern.getFilePath());
    }

    /**
     * リクエストの処理を行うメソッド
     *
     * @return レスポンスラインの状態行(StatusLine)を返す
     */
    public abstract StatusLine doRequestProcess();

    /**
     * レスポンスの処理を行うメソッド
     * レスポンスに追加したいヘッダがある場合は、このメソッド内で追加する
     *
     * @param os SocketのOutputStream
     * @param sl ステータスラインの列挙型
     */
    public abstract void doResponseProcess(OutputStream os, StatusLine sl);
}
