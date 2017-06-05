package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.program.board.ProgramBoardHandler;

import java.io.*;
import java.nio.file.Path;

/**
 * ハンドラー抽象クラス
 *
 * @author asada
 */
public abstract class Handler {

    /**
     * リソースファイルのパス
     */
    private static final String FILE_PATH = "src/main/resources/";

    /**
     * sendResponseかsendErrorResponseメソッドがまだ呼ばれていない場合はfalse
     * すでにレスポンスが送信済みの場合はtrueになる
     */
    private boolean isSent = false;

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
     * 使用した人が例外は必ず気づける（nullよりも良い
     *
     * @return ファイルのパスを返す。resourcesフォルダ以外にアクセスした場合はnullを返す。
     */
    protected static Path getFilePath(String uri) {
        //使用した人が例外は必ず気づける（nullよりも良い
        Path normalized_filepath = unsafe_filePath.normalize();

        if (!safe_filepath.startsWith(FILE_PATH)) {
            return null;
        }
        return safe_filepath;
    }

    /**
     * リクエストのURIが"/"で終わっている場合はwelcome pageを表示したい時に、使用するメソッド
     * （使用例）
     * String uri = changeUriToWelcomePage(requestMessage.getUri());
     *
     * @param uri URIを渡す
     * @return "/"で終わっている場合は{@link Main}のwelcome pageを連結して返す
     */
    protected static String changeUriToWelcomePage(String uri) {
        if (!uri.endsWith("/")) {
            return uri;
        }
        return uri + Main.WELCOME_PAGE_NAME;
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
     * レスポンスを送信するメソッド
     * リソースファイルを送信する場合に使用する
     * すでに、このメソッドか引数が異なるsendResponse、sendErrorResponseメソッドを呼び出していた場合は
     * このメソッドの処理は実行されません。
     *
     * @param responseMessage レスポンスメッセージを渡す
     * @param filePath        レスポンスしたいリソースファイルを渡す
     */
    protected void sendResponse(ResponseMessage responseMessage, Path filePath) {
        if (isSent) {
            return;
        }
        isSent = true;

        responseMessage.addHeaderWithContentType(ContentType.getContentType(filePath));
        responseMessage.addHeaderWithContentLength(String.valueOf(filePath.toFile().length()));

        responseMessage.writeResponseLineAndHeader(StatusLine.OK);

        OutputStream outputStream = responseMessage.getOutputStream();
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
     * レスポンスを送信するメソッド
     * メッセージボディに書き込みた文字列がある場合に使用する
     * すでに、このメソッドか引数が異なるsendResponse、sendErrorResponseメソッドを呼び出していた場合は
     * このメソッドの処理は実行されません。
     *
     * @param responseMessage レスポンスメッセージを渡す
     * @param str             レスポンスしたい文字列を渡す
     */
    protected void sendResponse(ResponseMessage responseMessage, String str) {
        if (isSent) {
            return;
        }
        isSent = true;

        responseMessage.addHeaderWithContentType(ContentType.getHtmlType());
        responseMessage.addHeaderWithContentLength(String.valueOf(str.getBytes().length));

        responseMessage.writeResponseLineAndHeader(StatusLine.OK);

        PrintWriter printWriter = new PrintWriter(responseMessage.getOutputStream());
        printWriter.write(str);
        printWriter.flush();
    }

    /**
     * エラーレスポンスを送信するメソッド
     * すでに、このメソッドか引数が異なるsendResponse、sendErrorResponseメソッドを呼び出していた場合は
     * このメソッドの処理は実行されません。
     *
     * @param responseMessage レスポンスメッセージを渡す
     * @param statusLine      ステータスラインを渡す
     */
    protected void sendErrorResponse(ResponseMessage responseMessage, StatusLine statusLine) {
        if (isSent) {
            return;
        }
        isSent = true;

        responseMessage.addHeaderWithContentType(ContentType.getHtmlType());

        responseMessage.writeResponseLineAndHeader(statusLine);

        PrintWriter printWriter = new PrintWriter(responseMessage.getOutputStream());
        printWriter.write(ResponseMessage.getErrorMessageBody(statusLine));
        printWriter.flush();
    }

    /**
     * リダイレクトをするメソッド
     *
     * @param responseMessage レスポンスメッセージを渡す
     * @param uri             変移先のURIを渡す
     */
    protected void sendRedirect(ResponseMessage responseMessage, String uri) {
        responseMessage.addHeader("Location", uri);

        responseMessage.writeResponseLineAndHeader(StatusLine.See_Other);
    }
}
