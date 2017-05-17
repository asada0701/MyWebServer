package jp.co.topgate.asada.web.app;

import jp.co.topgate.asada.web.ContentType;
import jp.co.topgate.asada.web.RequestMessage;
import jp.co.topgate.asada.web.ResponseMessage;
import jp.co.topgate.asada.web.exception.HtmlInitializeException;
import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;

import java.io.File;
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
     * リクエストメッセージ
     */
    private RequestMessage requestMessage;

    private HtmlEditor he;

    /**
     * コンストラクタ
     *
     * @param requestMessage リクエストメッセージのオブジェクト
     * @throws HtmlInitializeException {@link HtmlEditor#HtmlEditor()}を参照
     */
    ProgramBoardHandler(RequestMessage requestMessage) throws HtmlInitializeException {
        this.requestMessage = requestMessage;
        this.he = new HtmlEditor();
    }

    /**
     * 抽象メソッド、リクエストの処理を行うメソッド
     * TemplateMethodパターン
     */
    @Override
    public StatusLine requestComes() {
        String method = requestMessage.getMethod();
        String uri = requestMessage.getUri();
        String protocolVersion = requestMessage.getProtocolVersion();
        StatusLine sl = ProgramBoardHandler.getStatusLine(method, uri, protocolVersion);

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
     * リクエストメッセージのmethod,uri,protocolVersionから、レスポンスのステータスコードを決定するメソッド
     *
     * @param method          リクエストメッセージのメソッドを渡す
     * @param uri             URIを渡す
     * @param protocolVersion プロトコルバージョンを渡す
     * @return StatusLineを返す
     */
    static StatusLine getStatusLine(String method, String uri, String protocolVersion) {
        if (!"HTTP/1.1".equals(protocolVersion)) {
            return StatusLine.HTTP_VERSION_NOT_SUPPORTED;

        } else if (!"GET".equals(method) && !"POST".equals(method)) {
            return StatusLine.NOT_IMPLEMENTED;

        } else {
            File file = new File(Handler.getFilePath(uri));
            if (!file.exists() || !file.isFile()) {
                return StatusLine.NOT_FOUND;
            } else {
                return StatusLine.OK;
            }
        }
    }

    /**
     * POSTの場合の処理
     *
     * @param requestMessage リクエストメッセージクラスのオブジェクトを渡す
     * @throws IOException
     * @throws NullPointerException 引数がnull
     */
    void doPost(RequestMessage requestMessage) throws IOException, NullPointerException {
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
     * @throws NullPointerException 引数がnull
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
     * @throws NullPointerException    引数がnull
     * @throws HtmlInitializeException {@link HtmlEditor#allInitialization()}を参照
     */
    @Override
    public void returnResponse(OutputStream os, StatusLine sl) throws HtmlInitializeException {
        Objects.requireNonNull(os);
        Objects.requireNonNull(sl);

        ResponseMessage rm;

        if (sl.equals(StatusLine.OK)) {
            String path = Handler.getFilePath(requestMessage.getUri());
            rm = new ResponseMessage(os, sl, path);
            ContentType ct = new ContentType(path);
            rm.addHeader("Content-Type", ct.getContentType());

        } else {
            String path = "";
            rm = new ResponseMessage(os, sl, path);
            rm.addHeader("Content-Type", "text/html; charset=UTF-8");
        }

        rm.doResponse();

        he.allInitialization();
    }
}
