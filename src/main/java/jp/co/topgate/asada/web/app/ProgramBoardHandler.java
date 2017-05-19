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

        if (!StatusLine.OK.equals(sl)) {
            return sl;
        }
        try {
            if ("GET".equals(requestMessage.getMethod())) {
                if (uri.endsWith("/index.html")) {
                    String html = he.getHtml(EditHtmlList.INDEX_HTML);
                    html = he.editIndexOrSearchHtml(EditHtmlList.INDEX_HTML, html, ModelController.getAllMessage());
                    he.writeHtml(EditHtmlList.INDEX_HTML, html);
                }

            } else if ("POST".equals(requestMessage.getMethod())) {
                Map<String, String> messageBody = messageBodyParse(new String(requestMessage.getMessageBody()));
                String param = messageBody.get("param");
                if (param != null) {
                    doPost(he, messageBody);
                } else {
                    return StatusLine.BAD_REQUEST;
                }
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
     */
    @Override
    public void doResponseProcess(OutputStream os, StatusLine sl) {
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

        try {
            he.allInitialization();

        } catch (HtmlInitializeException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * POSTの場合の処理
     *
     * @param he          HtmlEditorのオブジェクトを渡す
     * @param messageBody リクエストのメッセージボディをパースして渡す
     * @throws IOException HTMLファイルに書き込み中に例外発生
     */
    void doPost(HtmlEditor he, Map<String, String> messageBody) throws IOException {
        EditHtmlList ehl;
        String html = null;
        String param = messageBody.get("param");
        switch (param) {
            case "search": {
                ehl = EditHtmlList.SEARCH_HTML;

                requestMessage.setUri("/program/board/search.html");
                int number = Integer.parseInt(messageBody.get("number"));
                String nameToFind = ModelController.getName(number);
                List<Message> list = ModelController.findSameNameMessage(nameToFind);

                html = he.getHtml(ehl);
                html = he.editIndexOrSearchHtml(ehl, html, list);
                break;
            }

            case "delete1": {
                ehl = EditHtmlList.DELETE_HTML;

                requestMessage.setUri("/program/board/delete.html");
                Message message = ModelController.findMessage(Integer.parseInt(messageBody.get("number")));

                html = he.getHtml(ehl);
                html = he.editDeleteHtml(html, message);
                break;
            }

            case "delete2": {
                ehl = EditHtmlList.DELETE_HTML;

                int number = Integer.parseInt(messageBody.get("number"));
                String password = messageBody.get("password");

                if (ModelController.deleteMessage(number, password)) {
                    requestMessage.setUri("/program/board/result.html");

                } else {
                    requestMessage.setUri("/program/board/result.html");
                    Message message = ModelController.findMessage(Integer.parseInt(messageBody.get("number")));

                    html = he.getHtml(ehl);
                    html = he.editDeleteHtml(html, message);
                }
                break;
            }

            case "contribution": {
                String name = messageBody.get("name");
                String title = messageBody.get("title");
                String text = messageBody.get("text");
                String password = messageBody.get("password");

                name = replaceInputValue(name);
                title = replaceInputValue(title);
                text = replaceInputValue(text);
                password = replaceInputValue(password);

                ModelController.addMessage(name, title, text, password);
            }

            case "back":

            default:
                ehl = EditHtmlList.INDEX_HTML;

                requestMessage.setUri("/program/board/index.html");
                html = he.getHtml(ehl);
                html = he.editIndexOrSearchHtml(ehl, html, ModelController.getAllMessage());
        }
        he.writeHtml(ehl, html);
    }

    /**
     * セキュリティに問題のある入力値(<script></script>など)をHTMLの特殊文字に置き換えるメソッド
     *
     * @param rawStr 生の文字列
     * @return 置き換えた文字列
     */
    static String replaceInputValue(String rawStr) {
        rawStr = rawStr.replaceAll("&", "&amp;");
        for (String key : invalidChar.keySet()) {
            rawStr = rawStr.replaceAll(key, invalidChar.get(key));
        }
        return rawStr;
    }

    /**
     * メッセージボディをパースするメソッド
     *
     * @param messageBody パースしたい文字列
     * @return パースした結果をMapで返す
     * @throws RequestParseException パースした結果不正なリクエストだった
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

    /**
     * リクエストメッセージのmethod,uri,protocolVersionから、レスポンスのステータスコードを決定するメソッド
     * 1.プロトコルバージョンがHTTP/1.1以外の場合は500
     * 2.GET,POST以外のメソッドの場合は501
     * 3.URIで指定されたファイルがリソースフォルダにない、もしくはディレクトリの場合は404
     * 4.1,2,3でチェックして問題がなければ200
     *
     * @param method          リクエストメッセージのメソッドを渡す
     * @param uri             URIを渡す
     * @param protocolVersion プロトコルバージョンを渡す
     * @return レスポンスラインの状態行(StatusLine)を返す
     */
    static StatusLine getStatusLine(String method, String uri, String protocolVersion) {
        if (!"HTTP/1.1".equals(protocolVersion)) {
            return StatusLine.HTTP_VERSION_NOT_SUPPORTED;

        } else if (!"GET".equals(method) && !"POST".equals(method)) {
            return StatusLine.NOT_IMPLEMENTED;

        } else {
            if ("GET".equals(method)) {
                String path = Handler.getFilePath(UrlPattern.PROGRAM_BOARD, uri);
                File file = new File(path);
                if (!file.exists() || !file.isFile()) {
                    return StatusLine.NOT_FOUND;
                }

            } else if ("POST".equals(method)) {
                if (!"/program/board/index.html".equals(uri)) {
                    return StatusLine.BAD_REQUEST;
                }
            }
        }
        return StatusLine.OK;
    }

    //テスト用

    RequestMessage getRequestMessage() {
        return requestMessage;
    }

    HtmlEditor getHtmlEditor() {
        return he;
    }
}
