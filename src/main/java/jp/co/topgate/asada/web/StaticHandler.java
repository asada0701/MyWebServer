package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.FileForbiddenException;

import java.nio.file.Path;

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
        Path filePath;
        try {
            String uri = changeUriToWelcomePage(requestMessage.getUri());
            filePath = Handler.getFilePath(uri);

        } catch (FileForbiddenException e) {
            sendErrorResponse(responseMessage, StatusLine.FORBIDDEN);
            return;
        }

        if (filePath.toFile().exists()) {
            sendResponse(responseMessage, filePath);

        } else {
            sendErrorResponse(responseMessage, StatusLine.NOT_FOUND);
        }
    }

    /**
     * {@link Handler#checkMethod(String)}を参照
     */
    @Override
    public boolean checkMethod(String method) {
        return method.equals("GET");
    }

    //テスト用
    RequestMessage getRequestMessage() {
        return requestMessage;
    }

    ResponseMessage getResponseMessage() {
        return responseMessage;
    }
}
