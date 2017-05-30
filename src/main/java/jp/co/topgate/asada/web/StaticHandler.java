package jp.co.topgate.asada.web;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 静的なコンテンツの配信を行うハンドラー
 *
 * @author asada
 */
public class StaticHandler extends Handler {

    /**
     * HTTPリクエストのメソッド
     */
    private static final String METHOD = "GET";

    private RequestMessage requestMessage;
    private ResponseMessage responseMessage;

    /**
     * コンストラクタ
     *
     * @param requestMessage  リクエストメッセージのオブジェクト
     * @param responseMessage レスポンスメッセージのオブジェクト
     */
    StaticHandler(RequestMessage requestMessage, ResponseMessage responseMessage) {
        this.requestMessage = requestMessage;
        this.responseMessage = responseMessage;
    }

    /**
     * {@link Handler#handleRequest()}を参照
     */
    @Override
    public void handleRequest() {
        String method = requestMessage.getMethod();

        String rawUri = requestMessage.getUri();
        String uri = changeUriToWelcomePage(rawUri);
        Path filePath = Paths.get(Handler.FILE_PATH + uri);

        StatusLine statusLine = StaticHandler.decideStatusLine(method, filePath);
        sendResponse(responseMessage, statusLine, filePath);
    }

    /**
     * リクエストのURIが"/"で終わっている場合はwelcome pageを表示する
     *
     * @param uri URIを渡す
     * @return "/"で終わっている場合は{@link Main}のwelcome pageを連結して返す
     */
    static String changeUriToWelcomePage(String uri) {
        if (!uri.endsWith("/")) {
            return uri;
        }
        return uri + Main.WELCOME_PAGE_NAME;
    }

    /**
     * リクエストメッセージのmethod,uri,protocolVersionから、レスポンスのステータスコードを決定するメソッド
     * 1.プロトコルバージョンがHTTP/1.1以外の場合は505:HTTP Version Not Supported
     * 2.GET,POST以外のメソッドの場合は501:Not Implemented
     * 3.URIで指定されたファイルがリソースフォルダにない、もしくはディレクトリの場合は404:Not Found
     * 4.1,2,3でチェックして問題がなければ200:OK
     *
     * @param method   リクエストメッセージのメソッドを渡す
     * @param filePath リソースファイルのPathを渡す
     * @return レスポンスメッセージの状態行(StatusLine)を返す
     */
    static StatusLine decideStatusLine(String method, Path filePath) {
        if (!StaticHandler.METHOD.equals(method)) {
            return StatusLine.NOT_IMPLEMENTED;
        }
        File file = filePath.toFile();
        if (!file.exists() || !file.isFile()) {
            return StatusLine.NOT_FOUND;
        }
        return StatusLine.OK;
    }

    /**
     * バイナリデータと文字データをレスポンスメッセージボディに書き込み、送信するメソッド
     *
     * @param responseMessage レスポンスメッセージを渡す
     * @param statusLine      ステータスラインを渡す
     * @param filePath        リソースファイルのパス
     */
    static void sendResponse(ResponseMessage responseMessage, StatusLine statusLine, Path filePath) {

        if (statusLine.equals(StatusLine.OK)) {
            //レスポンスメッセージにヘッダーフィールドを追加
            String contentType = ContentType.getContentType(filePath.toString());
            responseMessage.addHeaderWithContentType(contentType);
            long contentLength = filePath.toFile().length();
            responseMessage.addHeaderWithContentLength(String.valueOf(contentLength));

            //メッセージボディを書き込む
            OutputStream outputStream = responseMessage.getOutputStream(statusLine);
            try (InputStream in = new FileInputStream(filePath.toFile())) {
                int num;
                while ((num = in.read()) != -1) {
                    outputStream.write(num);
                }
            } catch (IOException e) {

            }

        } else {
            responseMessage.addHeaderWithContentType(ContentType.getHtmlType());

            PrintWriter pw = responseMessage.getPrintWriter(statusLine);
            pw.write(ResponseMessage.getErrorMessageBody(statusLine));
            pw.flush();
        }
    }

    //テスト用
    RequestMessage getRequestMessage() {
        return requestMessage;
    }

    ResponseMessage getResponseMessage() {
        return responseMessage;
    }
}
