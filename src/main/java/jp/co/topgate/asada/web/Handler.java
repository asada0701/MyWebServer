package jp.co.topgate.asada.web;

import java.io.*;

/**
 * ハンドラー抽象クラス
 *
 * @author asada
 */
public abstract class Handler {

    /**
     * HTTPレスポンスメッセージのステータスコード
     */
    int statusCode;

    /**
     * リクエストライン
     */
    RequestLine requestLine;

    /**
     * リクエストが来たときに呼び出すメソッド
     *
     * @param bis SocketのInputStreamをBufferedInputStreamにラップして渡す
     */
    public void requestComes(BufferedInputStream bis) {
        if (requestLine != null) {
            String method = requestLine.getMethod();        //サーバーをスタートする前にアクセスすると、ここでヌルポする
            String uri = requestLine.getUri();
            String protocolVersion = requestLine.getProtocolVersion();

            if (!"HTTP/1.1".equals(protocolVersion)) {
                statusCode = ResponseMessage.HTTP_VERSION_NOT_SUPPORTED;

            } else if (!"GET".equals(method) && !"POST".equals(method)) {
                statusCode = ResponseMessage.NOT_IMPLEMENTED;

            } else {
                File file = new File(HandlerFactory.getFilePath(uri));
                if (!file.exists() || !file.isFile()) {
                    statusCode = ResponseMessage.NOT_FOUND;
                } else {
                    statusCode = ResponseMessage.OK;
                }
            }
        }
    }

    /**
     * 抽象メソッド、レスポンスを返すときに呼び出すメソッド
     *
     * @param os SocketのOutputStream
     */
    public abstract void returnResponse(OutputStream os);

    /**
     * ステータスコードをセットできる
     *
     * @param statusCode HTTPレスポンスのステータスコード
     */
    void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * リクエストラインをセットできる
     *
     * @param requestLine requestLineクラスのオブジェクト
     */
    void setRequestLine(RequestLine requestLine) {
        this.requestLine = requestLine;
    }
}
