package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.program.board.ProgramBoardHandler;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    private static final String PROGRAM_BOARD_URI = "/program/board";

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
        if (uri.startsWith(PROGRAM_BOARD_URI)) {
            return new ProgramBoardHandler(requestMessage, responseMessage);
        }
        return new StaticHandler(requestMessage, responseMessage);
    }

    /**
     * URIを元に、実際のファイルパスを返すメソッド
     */
    public static Path getFilePath(String uri) {
        return Paths.get(FILE_PATH, uri);
    }

    /**
     * リクエストを適切に処理し、ResponseMessageのオブジェクトを生成してServerクラスに返す
     * レスポンスメッセージを実際に書き込むのはServerが行う。
     */
    public abstract void handleRequest();

    /**
     * リクエストのメソッドが担当するハンドラーが処理できるものか判定する。デフォルトでは、GETとPOSTは対応することになる。
     * falseの場合はServerクラス内で実装されていないメソッドである旨をレスポンスする。
     *
     * @param method リクエストメッセージのメソッドを渡す
     * @return trueの場合はハンドラーが処理できるメソッドである。falseの場合はハンドラーが処理しないメソッドである
     */
    public boolean checkMethod(String method) {
        return method.equals("GET") || method.equals("POST");
    }

    /**
     * バイナリデータやハンドラー内で編集しなかった場合のメソッド
     *
     * @param responseMessage レスポンスメッセージを渡す
     * @param filePath        レスポンスしたいリソースファイルを渡す
     */
    protected static void sendResponse(ResponseMessage responseMessage, Path filePath) {
        responseMessage.addHeaderWithContentType(ContentType.getContentType(filePath));
        responseMessage.addHeaderWithContentLength(String.valueOf(filePath.toFile().length()));

        OutputStream outputStream = responseMessage.getOutputStream(StatusLine.OK);
        try (InputStream inputStream = new FileInputStream(filePath.toFile())) {
            int tmp;
            while ((tmp = inputStream.read()) != -1) {
                outputStream.write(tmp);
            }
            outputStream.flush();

        } catch (IOException e) {

        }
    }

    /**
     * エラーレスポンスを送信するメソッド
     *
     * @param responseMessage レスポンスメッセージを渡す
     * @param statusLine      ステータスラインを渡す
     */
    protected static void sendErrorResponse(ResponseMessage responseMessage, StatusLine statusLine) {
        responseMessage.addHeaderWithContentType(ContentType.getHtmlType());

        PrintWriter printWriter = responseMessage.getPrintWriter(statusLine);
        printWriter.write(ResponseMessage.getErrorMessageBody(statusLine));
        printWriter.flush();
    }

    /**
     * HTMLを編集した場合にレスポンスを送信するメソッド
     *
     * @param responseMessage レスポンスメッセージを渡す
     * @param html            編集したHTMLの文字列を渡す
     */
    protected static void sendResponse(ResponseMessage responseMessage, String html) {
        responseMessage.addHeaderWithContentType(ContentType.getHtmlType());
        responseMessage.addHeaderWithContentLength(String.valueOf(html.getBytes().length));

        PrintWriter printWriter = responseMessage.getPrintWriter(StatusLine.OK);
        printWriter.write(html);
        printWriter.flush();
    }
}
