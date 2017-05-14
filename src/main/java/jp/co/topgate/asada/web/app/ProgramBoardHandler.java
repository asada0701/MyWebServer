package jp.co.topgate.asada.web.app;

import jp.co.topgate.asada.web.RequestMessage;
import jp.co.topgate.asada.web.ResponseMessage;
import jp.co.topgate.asada.web.exception.RequestParseException;
import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * WebAppの処理を行うハンドラークラス
 *
 * @author asada
 */
public class ProgramBoardHandler extends Handler {

    private int statusCode;

    private RequestMessage requestMessage;

    public ProgramBoardHandler(RequestMessage requestMessage) {
        this.requestMessage = requestMessage;
    }

    /**
     * リクエストが来たときに呼び出すメソッド
     *
     * @param bis SocketのInputStreamをBufferedInputStreamにラップして渡す
     */
    @Override
    public void requestComes(BufferedInputStream bis) throws IOException {

        try {
            if (statusCode == ResponseMessage.OK) {
                if ("POST".equals(requestMessage.getMethod())) {
                    bis.reset();
                    doPost(requestMessage);

                } else if ("GET".equals(requestMessage.getMethod())) {
                    HtmlEditor.writeIndexHtml();
                }
            }
        } catch (RequestParseException e) {
            statusCode = ResponseMessage.BAD_REQUEST;

        } catch (IOException e) {
            statusCode = ResponseMessage.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * POSTの場合の処理
     *
     * @param requestMessage リクエストメッセージクラスのオブジェクトを渡す
     */
    private void doPost(RequestMessage requestMessage) throws IOException {
        String param = requestMessage.findMessageBody("param");
        if (param != null && requestMessage.getUri().startsWith("/program/board/")) {
            Message message;

            switch (param) {
                case "contribution":
                    String name = requestMessage.findMessageBody("name");
                    String title = requestMessage.findMessageBody("title");
                    String text = requestMessage.findMessageBody("text");
                    String password = requestMessage.findMessageBody("password");

                    if (messageCheck(name, title, text)) {
                        ModelController.addMessage(name, title, text, password);

                        HtmlEditor.writeIndexHtml();
                    }
                    break;

                case "search":
                    requestMessage.setUri("/program/board/search.html");
                    int number = Integer.parseInt(requestMessage.findMessageBody("number"));
                    String nameToFind = ModelController.getName(number);
                    HtmlEditor.writeSearchHtml(nameToFind);
                    break;

                case "delete1":
                    requestMessage.setUri("/program/board/delete.html");
                    message = ModelController.findMessage(Integer.parseInt(requestMessage.findMessageBody("number")));
                    HtmlEditor.writeDeleteHtml(message);
                    break;

                case "delete2":
                    int num = Integer.parseInt(requestMessage.findMessageBody("number"));
                    password = requestMessage.findMessageBody("password");

                    if (ModelController.deleteMessage(num, password)) {
                        requestMessage.setUri("/program/board/result.html");

                    } else {
                        requestMessage.setUri("/program/board/result.html");
                        message = ModelController.findMessage(Integer.parseInt(requestMessage.findMessageBody("number")));
                        HtmlEditor.writeDeleteHtml(message);
                    }
                    break;

                case "back":
                    requestMessage.setUri("/program/board/index.html");
                    HtmlEditor.writeIndexHtml();
                    break;

                default:
                    requestMessage.setUri("/program/board/index.html");
                    HtmlEditor.writeIndexHtml();
            }
        }
    }

    private static boolean messageCheck(String name, String title, String text) {
        if ("<script>".equals(name) || "<script>".equals(title)) {
            return false;
        }
        return true;
    }

    /**
     * レスポンスを返すときに呼び出すメソッド
     *
     * @param os SocketのOutputStream
     * @throws RuntimeException データを保存しているCSVファイルに異常が見つかった場合に発生する
     */
    @Override
    public void returnResponse(OutputStream os) throws RuntimeException {
        try {
            String path = "";
            if (requestMessage != null) {
                path = Handler.getFilePath(requestMessage.getUri());
            }
            new ResponseMessage(os, statusCode, path);

        } catch (IOException e) {
            /*
            ソケットにレスポンスを書き出す段階で、例外が出た。
            原因としては、ソケットが閉じてしまった場合などが考えられる。
            レスポンスを返せない例外なので、発生しても無視する。
             */
        }
    }
}
