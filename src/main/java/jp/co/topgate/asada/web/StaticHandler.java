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
        Path filePath = Handler.getFilePath(requestMessage.getUri());

        StatusLine statusLine;
        if (Handler.checkFile(filePath.toFile())) {
            statusLine = StatusLine.OK;
        } else {
            statusLine = StatusLine.NOT_FOUND;
        }
        sendResponse(statusLine, filePath);
    }

    /**
     * {@link Handler#checkMethod(String)}を参照
     */
    @Override
    public boolean checkMethod(String method) {
        return method.equals("GET");
    }

    /**
     * バイナリデータと文字データをレスポンスメッセージボディに書き込み、送信するメソッド
     *
     * @param statusLine ステータスラインを渡す
     * @param filePath   リソースファイルのパス
     */
    void sendResponse(StatusLine statusLine, Path filePath) {

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
