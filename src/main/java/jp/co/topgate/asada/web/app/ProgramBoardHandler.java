package jp.co.topgate.asada.web.app;

import jp.co.topgate.asada.web.ContentType;
import jp.co.topgate.asada.web.RequestMessage;
import jp.co.topgate.asada.web.ResponseMessage;
import jp.co.topgate.asada.web.exception.HtmlInitializeException;
import jp.co.topgate.asada.web.exception.RequestParseException;
import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebAppの処理を行うハンドラークラス
 *
 * @author asada
 */
public class ProgramBoardHandler extends Handler {

    /**
     * メッセージボディのクエリーをクエリー毎に分割する
     */
    private static final String MESSAGE_BODY_EACH_QUERY_SEPARATOR = "&";

    /**
     * メッセージボディのイコール
     */
    private static final String MESSAGE_BODY_NAME_VALUE_SEPARATOR = "=";

    /**
     * メッセージボディの項目数
     */
    private static final int MESSAGE_BODY_NUM_ITEMS = 2;

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
     */
    @Override
    public StatusLine doRequestProcess() {
        String method = requestMessage.getMethod();
        String uri = requestMessage.getUri();
        String protocolVersion = requestMessage.getProtocolVersion();
        StatusLine sl = ProgramBoardHandler.getStatusLine(method, uri, protocolVersion);

        try {
            if ("POST".equals(requestMessage.getMethod())) {
                doPost(messageBodyParse(new String(requestMessage.getMessageBody())));

            } else if ("GET".equals(requestMessage.getMethod())) {
                String html = he.getHtml(EditHtmlList.INDEX_HTML);
                html = he.editIndexOrSearchHtml(EditHtmlList.INDEX_HTML, html, ModelController.getAllMessage());
                he.writeHtml(EditHtmlList.INDEX_HTML, html);
            }
        } catch (IOException e) {
            return StatusLine.INTERNAL_SERVER_ERROR;
        }
        return sl;
    }

    /**
     * レスポンスを返すときに呼び出すメソッド
     *
     * @param os SocketのOutputStream
     * @throws HtmlInitializeException {@link HtmlEditor#allInitialization()}を参照
     */
    @Override
    public void doResponseProcess(OutputStream os, StatusLine sl) throws HtmlInitializeException {
        ResponseMessage rm;

        if (sl.equals(StatusLine.OK)) {
            String path = Handler.getFilePath(UrlPattern.PROGRAM_BOARD, requestMessage.getUri());
            ContentType ct = new ContentType(path);
            rm = new ResponseMessage(os, sl, path);
            rm.addHeader("Content-Type", ct.getContentType());
            rm.addHeader("Content-Length", String.valueOf(new File(path).length()));

        } else {
            rm = new ResponseMessage(os, sl);
            rm.addHeader("Content-Type", "text/html; charset=UTF-8");
        }

        rm.returnResponse();

        he.allInitialization();
    }

    /**
     * リクエストメッセージのmethod,uri,protocolVersionから、レスポンスのステータスコードを決定するメソッド
     *
     * @param method          リクエストメッセージのメソッドを渡す
     * @param uri             URIを渡す
     * @param protocolVersion プロトコルバージョンを渡す
     * @return StatusLineを返す
     */
    public static StatusLine getStatusLine(String method, String uri, String protocolVersion) {
        if (!"HTTP/1.1".equals(protocolVersion)) {
            return StatusLine.HTTP_VERSION_NOT_SUPPORTED;

        } else if (!"GET".equals(method) && !"POST".equals(method)) {
            return StatusLine.NOT_IMPLEMENTED;

        } else {
            File file = new File(Handler.getFilePath(UrlPattern.PROGRAM_BOARD, uri));
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
     * @param messageBody
     * @throws IOException
     */
    void doPost(Map<String, String> messageBody) throws IOException {
        String param = messageBody.get("param");
        if (param != null && requestMessage.getUri().startsWith("/program/board/")) {
            Message message;

            String html;

            switch (param) {
                case "contribution":
                    String name = messageBody.get("name");
                    String title = messageBody.get("title");
                    String text = messageBody.get("text");
                    String password = messageBody.get("password");

                    name = replaceInputValue(name);
                    title = replaceInputValue(title);
                    text = replaceInputValue(text);
                    password = replaceInputValue(password);

                    ModelController.addMessage(name, title, text, password);

                    html = he.getHtml(EditHtmlList.INDEX_HTML);
                    html = he.editIndexOrSearchHtml(EditHtmlList.INDEX_HTML, html, ModelController.getAllMessage());
                    he.writeHtml(EditHtmlList.INDEX_HTML, html);
                    break;

                case "search":
                    requestMessage.setUri("/program/board/search.html");
                    int number = Integer.parseInt(messageBody.get("number"));
                    String nameToFind = ModelController.getName(number);
                    List<Message> list = ModelController.findSameNameMessage(nameToFind);

                    html = he.getHtml(EditHtmlList.SEARCH_HTML);
                    html = he.editIndexOrSearchHtml(EditHtmlList.SEARCH_HTML, html, list);
                    he.writeHtml(EditHtmlList.SEARCH_HTML, html);
                    break;

                case "delete1":
                    requestMessage.setUri("/program/board/delete.html");
                    message = ModelController.findMessage(Integer.parseInt(messageBody.get("number")));

                    html = he.getHtml(EditHtmlList.DELETE_HTML);
                    html = he.editDeleteHtml(html, message);
                    he.writeHtml(EditHtmlList.DELETE_HTML, html);

                    break;

                case "delete2":
                    int num = Integer.parseInt(messageBody.get("number"));
                    password = messageBody.get("password");

                    if (ModelController.deleteMessage(num, password)) {
                        requestMessage.setUri("/program/board/result.html");

                    } else {
                        requestMessage.setUri("/program/board/result.html");
                        message = ModelController.findMessage(Integer.parseInt(messageBody.get("number")));

                        html = he.getHtml(EditHtmlList.DELETE_HTML);
                        html = he.editDeleteHtml(html, message);
                        he.writeHtml(EditHtmlList.DELETE_HTML, html);
                    }
                    break;

                case "back":
                    requestMessage.setUri("/program/board/index.html");
                    html = he.getHtml(EditHtmlList.INDEX_HTML);
                    html = he.editIndexOrSearchHtml(EditHtmlList.INDEX_HTML, html, ModelController.getAllMessage());
                    he.writeHtml(EditHtmlList.INDEX_HTML, html);
                    break;

                default:
                    requestMessage.setUri("/program/board/index.html");
                    html = he.getHtml(EditHtmlList.INDEX_HTML);
                    html = he.editIndexOrSearchHtml(EditHtmlList.INDEX_HTML, html, ModelController.getAllMessage());
                    he.writeHtml(EditHtmlList.INDEX_HTML, html);
            }
        }
    }

    /**
     * セキュリティに問題のある入力値(<script></script>など)をHTMLの特殊文字に置き換えるメソッド
     *
     * @param rawStr 生の文字列
     * @return 置き換えた文字列
     */
    static String replaceInputValue(String rawStr) {
        String result = rawStr;
        for (String key : invalidChar.keySet()) {
            result = rawStr.replaceAll(key, invalidChar.get(key));
        }
        return result;
    }

    /**
     * メッセージボディをパースするメソッド
     *
     * @param messageBody パースしたい文字列
     * @return パースした結果をMapで返す
     * @throws RequestParseException リクエストになんらかの異常があった
     */
    static Map<String, String> messageBodyParse(String messageBody) throws RequestParseException {
        try {
            messageBody = URLDecoder.decode(messageBody, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            throw new RequestParseException("UTF-8でのデコードに失敗");
        }

        Map<String, String> result = new HashMap<>();
        String[] s1 = messageBody.split(MESSAGE_BODY_EACH_QUERY_SEPARATOR);
        for (String s : s1) {
            String[] s2 = s.split(MESSAGE_BODY_NAME_VALUE_SEPARATOR);
            if (s2.length == MESSAGE_BODY_NUM_ITEMS) {
                result.put(s2[0], s2[1]);
            } else {
                throw new RequestParseException("リクエストのメッセージボディが不正なものだった:" + s2[0]);
            }
        }
        return result;
    }
}
