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
                        //メッセージを投稿する

                        message = new Message();
                        message.setName(requestMessage.findMessageBody("name"));
                        message.setTitle(requestMessage.findMessageBody("title"));
                        message.setText(requestMessage.findMessageBody("text"));
                        message.setPassword(requestMessage.findMessageBody("pw"));
                        ModelController.addMessage(message);

                        he.contribution(message);
                        break;

                    case "search":
                        //投稿した人で絞り込む
                        //メッセージリストからメッセージオブジェクトを特定して、ユーザーオブジェクトの特定をする

                        he.search(ModelController.findMessage(Integer.parseInt(requestMessage.findMessageBody("number"))));
                        break;

                    case "delete1":
                        //投稿した文の削除をする
                        //メッセージリストからメッセージオブジェクトを特定する。
                        //delete.htmlにメッセージを書いて渡す。

                        String number2 = requestMessage.findMessageBody("number");
                        he.delete1();
                        requestLine.setUri("/program/board/delete.html");
                        break;

                    case "delete2":
                        //投稿した文の削除をする
                        //メッセージリストからメッセージオブジェクトを特定する。ユーザーオブジェクトの特定をする
                        //パスワードが一致した場合、削除する

                        Message message2 = new Message();
                        message2.setMessageID(Integer.parseInt(requestMessage.findMessageBody("number")));

                        he.delete2(message2);
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
