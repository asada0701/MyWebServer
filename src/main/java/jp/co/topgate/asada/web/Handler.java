package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.program.board.ProgramBoardHandler;

import java.io.File;

/**
 * ハンドラー抽象クラス
 *
 * @author asada
 */
public abstract class Handler {

    /**
     * リソースファイルのパス
     */
    private static final String FILE_PATH = "./src/main/resources/";

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
        if (uri.startsWith("/program/board/")) {
            return new ProgramBoardHandler(requestMessage, responseMessage);
        }
        return new StaticHandler(requestMessage, responseMessage);
    }

    /**
     * URIを元に、実際のファイルパスを返すメソッド
     */
    public static String getFilePath(String uri) {
        return FILE_PATH + uri;
    }

    /**
     * ファイルが存在するか、ディレクトリではないかを判定するメソッド
     *
     * @param file ファイルを渡す
     * @return trueの場合はファイルが存在し、ディレクトリではない
     */
    public static boolean checkFile(File file) {
        return file.exists() && file.isFile();
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
    public boolean checkMethod(String method) {
        return method.equals("GET") || method.equals("POST");
    }
}
