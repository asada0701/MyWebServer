package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;
import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;

import java.io.*;

/**
 * WebAppの処理を行うハンドラークラス
 *
 * @author asada
 */
public class WebAppHandler extends Handler {

    /**
     * リクエストが来たときに呼び出すメソッド
     *
     * @param bis SocketのInputStreamをBufferedInputStreamにラップして渡す
     */
    @Override
    public void requestComes(BufferedInputStream bis) {
        super.requestComes(bis);

        try {
            if (statusCode == ResponseMessage.OK) {


                if ("POST".equals(requestLine.getMethod())) {
                    //リクエストメッセージのパース
                    doPost(new RequestMessage(bis, requestLine));

                } else if ("GET".equals(requestLine.getMethod())) {
                    //index.htmlのGETの場合はデータを入れる
                    HtmlEditor.contribution();
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
        if (param != null && requestLine.getUri().startsWith("/program/board/")) {
            Message message;

            switch (param) {
                case "contribution":
                    String name = requestMessage.findMessageBody("name");
                    String title = requestMessage.findMessageBody("title");
                    String text = requestMessage.findMessageBody("text");
                    String password = requestMessage.findMessageBody("password");

                    ModelController.addMessage(name, title, text, password);

                    HtmlEditor.contribution();
                    break;

                case "search":
                    //投稿した人で絞り込む
                    //メッセージリストからメッセージオブジェクトを特定して、ユーザーオブジェクトの特定をする
                    requestLine.setUri("/program/board/search.html");
                    HtmlEditor.search(Integer.parseInt(requestMessage.findMessageBody("number")));
                    break;

                case "delete1":
                    //投稿した文の削除をする
                    //メッセージリストからメッセージオブジェクトを特定する。
                    //delete.htmlにメッセージを書いて渡す。
                    requestLine.setUri("/program/board/delete.html");
                    message = ModelController.findMessage(Integer.parseInt(requestMessage.findMessageBody("number")));
                    HtmlEditor.delete(message);
                    break;

                case "delete2":
                    //投稿した文の削除をする
                    //メッセージリストからメッセージオブジェクトを特定する。ユーザーオブジェクトの特定をする
                    //パスワードが一致した場合、削除する

                    int num = Integer.parseInt(requestMessage.findMessageBody("number"));
                    password = requestMessage.findMessageBody("password");

                    if (ModelController.deleteMessage(num, password)) {
                        //削除成功
                        requestLine.setUri("/program/board/result.html");

                    } else {
                        //削除失敗
                        requestLine.setUri("/program/board/result.html");
                        message = ModelController.findMessage(Integer.parseInt(requestMessage.findMessageBody("number")));
                        HtmlEditor.delete(message);
                    }
                    break;

                case "back":
                    requestLine.setUri("/program/board/index.html");
                    HtmlEditor.contribution();
                    break;

                default:
                    requestLine.setUri("/program/board/index.html");
                    HtmlEditor.contribution();
            }
        }
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
            if (requestLine != null) {
                path = Handler.getFilePath(requestLine.getUri());
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
