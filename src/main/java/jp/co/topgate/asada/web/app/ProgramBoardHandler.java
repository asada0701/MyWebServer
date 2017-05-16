package jp.co.topgate.asada.web.app;

import jp.co.topgate.asada.web.RequestMessage;
import jp.co.topgate.asada.web.ResponseMessage;
import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * WebAppの処理を行うハンドラークラス
 *
 * @author asada
 */
public class ProgramBoardHandler extends Handler {

    private static int score = 1;

    /**
     * 不正な入力値を置き換える
     */
    private static Map<String, String> invalidChar = new HashMap<>();

    static {
        invalidChar.put("<", "&lt;");
        invalidChar.put(">", "&gt;");
        invalidChar.put("&", "&amp;");
        invalidChar.put("\"", "&quot;");
        invalidChar.put("\'", "&#39;");
    }

    /**
     * コンストラクタ
     *
     * @param requestMessage リクエストメッセージのオブジェクト
     */
    ProgramBoardHandler(RequestMessage requestMessage) {
        this.requestMessage = requestMessage;
    }

    @Override
    public StatusLine requestComes() {
        String method = requestMessage.getMethod();
        String uri = requestMessage.getUri();
        String protocolVersion = requestMessage.getProtocolVersion();
        StatusLine sl = StatusLine.getStatusLine(method, uri, protocolVersion);

        try {
            if ("POST".equals(requestMessage.getMethod())) {
                doPost(requestMessage);

            } else if ("GET".equals(requestMessage.getMethod())) {
                HtmlEditor.writeIndexHtml();
            }
        } catch (IOException e) {
            return StatusLine.INTERNAL_SERVER_ERROR;
        }

        return sl;
    }

    /**
     * POSTの場合の処理
     *
     * @param requestMessage リクエストメッセージクラスのオブジェクトを渡す
     * @throws IOException
     * @throws NullPointerException
     */
    private void doPost(RequestMessage requestMessage) throws IOException, NullPointerException {
        Objects.requireNonNull(requestMessage);

        String param = requestMessage.findMessageBody("param");
        if (param != null && requestMessage.getUri().startsWith("/program/board/")) {
            Message message;

            switch (param) {
                case "contribution":
                    String name = requestMessage.findMessageBody("name");
                    String title = requestMessage.findMessageBody("title");
                    String text = requestMessage.findMessageBody("text");
                    String password = requestMessage.findMessageBody("password");

                    name = replaceInputValue(name);
                    title = replaceInputValue(title);
                    text = replaceInputValue(text);

                    ModelController.addMessage(name, title, text, password);
                    HtmlEditor.writeIndexHtml();
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

    /**
     * セキュリティに問題のある入力値(<script></script>など)をHTMLの特殊文字に置き換えるメソッド
     *
     * @param str 生の文字列
     * @return 置き換えた文字列
     * @throws NullPointerException
     */
    static String replaceInputValue(String str) throws NullPointerException {
        Objects.requireNonNull(str);

        for (String key : invalidChar.keySet()) {
            str = str.replaceAll(key, invalidChar.get(key));
        }
        return str;
    }

    /**
     * レスポンスを返すときに呼び出すメソッド
     *
     * @param os SocketのOutputStream
     * @throws RuntimeException     データを保存しているCSVファイルに異常が見つかった場合に発生する
     * @throws NullPointerException
     */
    @Override
    public void returnResponse(OutputStream os, StatusLine sl) {
        Objects.requireNonNull(os);
        Objects.requireNonNull(sl);

        System.out.println(score + "回目");
        if (score == 3) {
            System.out.println("3回目:" + sl.getStatusCode());
        }
        score++;

        try {
            String path = "";
            if (requestMessage != null) {
                path = Handler.getFilePath(requestMessage.getUri());
            }
            new ResponseMessage(os, sl, path);

        } catch (IOException e) {
            /*
            ソケットにレスポンスを書き出す段階で、例外が出た。
            原因としては、ソケットが閉じてしまった場合などが考えられる。
            レスポンスを返せない例外なので、発生しても無視する。
             */
        }
    }
}
