package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;
import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;

import java.io.*;
import java.util.ArrayList;

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
                    //POSTの時のみHTMLを編集する
                    try {
                        editHtml(new RequestMessage(bis, requestLine));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (RequestParseException e) {
            statusCode = ResponseMessage.BAD_REQUEST;
        }
    }

    /**
     * HTMLを編集するメソッド
     *
     * @param requestMessage
     * @throws IOException
     */
    private void editHtml(RequestMessage requestMessage) throws IOException {
        if (requestLine.getUri().startsWith("/program/board/")) {
            String param = requestMessage.findMessageBody("param");
            if (param != null) {
                Message message;
                HtmlEditor he = new HtmlEditor(requestLine);

                switch (param) {
                    case "contribution":
                        String name = requestMessage.findMessageBody("name");
                        String title = requestMessage.findMessageBody("title");
                        String text = requestMessage.findMessageBody("text");
                        String password = requestMessage.findMessageBody("password");

                        message = ModelController.addMessage(name, title, text, password);

                        he.contribution(message);
                        break;

                    case "search":
                        //投稿した人で絞り込む
                        //メッセージリストからメッセージオブジェクトを特定して、ユーザーオブジェクトの特定をする
                        requestLine.setUri("/program/board/search.html");

                        ArrayList<Message> al = ModelController.findMessageByID(Integer.parseInt(requestMessage.findMessageBody("number")));
                        he.search(al);
                        break;

                    case "delete1":
                        //投稿した文の削除をする
                        //メッセージリストからメッセージオブジェクトを特定する。
                        //delete.htmlにメッセージを書いて渡す。

                        requestLine.setUri("/program/board/delete.html");
                        message = ModelController.findMessage(Integer.parseInt(requestMessage.findMessageBody("number")));
                        he.delete1(message);
                        break;

                    case "delete2":
                        //投稿した文の削除をする
                        //メッセージリストからメッセージオブジェクトを特定する。ユーザーオブジェクトの特定をする
                        //パスワードが一致した場合、削除する

                        message = ModelController.findMessage(Integer.parseInt(requestMessage.findMessageBody("number")));

                        if (message != null) {
                            requestLine.setUri("/program/board/delete.html");
                            he.delete2(message);
                            ModelController.deleteMessage(message);
                            requestLine.setUri("/program/board/result.html");
                        } else {
                            requestLine.setUri("/program/board/delete.html");
                            System.out.println("パスワードが異なる場合の処理");
                        }
                        break;

                    case "back":
                        requestLine.setUri("/program/board/index.html");
                        break;

                    default:
                        requestLine.setUri("/program/board/index.html");
                }
            }
        }
    }
}
