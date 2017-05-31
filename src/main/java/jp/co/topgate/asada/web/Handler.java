package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.program.board.ProgramBoardHandler;

/**
 * ハンドラー抽象クラス
 *
 * @author asada
 */
public abstract class Handler {

    /**
     * リソースファイルのパス
     */
    static final String FILE_PATH = "./src/main/resources/static";

    /**
     * ハンドラーのファクトリーメソッド
     * 注意点
     * URLパターンとの比較の際にStringのstartsWithメソッドを使用しています
     * /program/board/と/program/といった始まりが同じURLには注意してください
     *
     * @param requestMessage リクエストメッセージのオブジェクトを渡す
     * @return 今回の接続を担当するハンドラーのオブジェクトを返す
     */
    static Handler getHandler(RequestMessage requestMessage, ResponseMessage responseMessage) {
        String uri = requestMessage.getUri();
        if (uri.startsWith(UrlPattern.PROGRAM_BOARD.getUrlPattern())) {
            return new ProgramBoardHandler(requestMessage, responseMessage);
        }
        return new StaticHandler(requestMessage, responseMessage);
    }

    /**
     * URIを元に、実際のファイルパスを返すメソッド
     */
    public static String getFilePath(UrlPattern urlPattern, String uri) {
        return "./src/main/resources" + uri.replace(urlPattern.getUrlPattern(), urlPattern.getFilePath());
    }

    /**
     * リクエストを適切に処理し、ResponseMessageのオブジェクトを生成してServerクラスに返す
     * レスポンスメッセージを実際に書き込むのはServerが行う
     */
    public abstract void handleRequest();

    /**
     * リクエストのメソッドが担当するハンドラーが処理できるものか判定する
     * falseの場合はサーバーが実装されていないメソッドである旨をレスポンスする
     *
     * @param method リクエストメッセージのメソッドを渡す
     * @return trueの場合はハンドラーが処理できるメソッドである。falseの場合はハンドラーが処理しないメソッドである
     */
    public abstract boolean checkMethod(String method);
}
