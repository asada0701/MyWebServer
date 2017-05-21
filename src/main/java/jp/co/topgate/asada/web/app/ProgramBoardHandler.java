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
import java.util.List;
import java.util.Map;

import static jp.co.topgate.asada.web.app.InvalidChar.replaceInputValue;

/**
 * WebAppの処理を行うハンドラークラス
 *
 * @author asada
 */
public class ProgramBoardHandler extends Handler {

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
            if ("GET".equals(requestMessage.getMethod()) && uri.endsWith("/index.html")) {
                writeIndex(he);

            } else if ("POST".equals(requestMessage.getMethod())) {
                Map<String, String> messageBody = requestMessage.getMessageBody().parseToStringMap();
                String param = messageBody.get("param");
                if (param != null) {
                    doPost(he, messageBody, Param.getEnum(param));
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
        ResponseMessage rm = new ResponseMessage();
        String path = Handler.getFilePath(UrlPattern.PROGRAM_BOARD, requestMessage.getUri());

        if (sl.equals(StatusLine.OK)) {
            ContentType ct = new ContentType(path);
            rm.addHeader("Content-Type", ct.getContentType());
            rm.addHeader("Content-Length", String.valueOf(new File(path).length()));

        } else {
            rm.addHeader("Content-Type", "text/html; charset=UTF-8");
        }

        try {
            rm.returnResponse(os, sl, path);
        } catch (IOException e) {

        }

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
     * @param messageBody リクエストのメッセージボディを渡す
     * @param param       メッセージボディで送られてくるparamのEnum
     * @throws IOException HTMLファイルに書き込み中に例外発生
     */
    void doPost(HtmlEditor he, Map<String, String> messageBody, Param param) throws IOException {
        EditHtmlList ehl;
        String html = null;
        switch (param) {
            case SEARCH: {
                String numberStr = messageBody.get("number");
                if (numberStr == null) {
                    writeIndex(he);
                    return;
                }
                ehl = EditHtmlList.SEARCH_HTML;

                requestMessage.setUri("/program/board/search.html");
                int number = Integer.parseInt(numberStr);
                String nameToFind = ModelController.getName(number);
                List<Message> list = ModelController.findSameNameMessage(nameToFind);

                if (list.size() > 0) {
                    html = he.getHtml(ehl);
                    html = he.editIndexOrSearchHtml(ehl, html, list);

                } else {
                    writeIndex(he);
                    return;
                }
                break;
            }

            case DELETE1: {
                ehl = EditHtmlList.DELETE_HTML;

                requestMessage.setUri("/program/board/delete.html");
                Message message = ModelController.findMessage(Integer.parseInt(messageBody.get("number")));

                if (message != null) {
                    html = he.getHtml(ehl);
                    html = he.editDeleteHtml(html, message);
                } else {
                    writeIndex(he);
                    return;
                }
                break;
            }

            case DELETE2: {
                ehl = EditHtmlList.DELETE_HTML;

                int number = Integer.parseInt(messageBody.get("number"));
                String password = messageBody.get("password");

                if (ModelController.deleteMessage(number, password)) {
                    requestMessage.setUri("/program/board/result.html");

                } else {
                    requestMessage.setUri("/program/board/delete.html");
                    Message message = ModelController.findMessage(Integer.parseInt(messageBody.get("number")));

                    if (message != null) {
                        html = he.getHtml(ehl);
                        html = he.editDeleteHtml(html, message);
                    } else {
                        writeIndex(he);
                        return;
                    }
                }
                break;
            }

            case CONTRIBUTION: {
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

            case BACK:

            default:
                writeIndex(he);
                return;
        }
        he.writeHtml(ehl, html);
    }

    void writeIndex(HtmlEditor he) throws IOException {
        requestMessage.setUri("/program/board/index.html");
        String html = he.getHtml(EditHtmlList.INDEX_HTML);
        html = he.editIndexOrSearchHtml(EditHtmlList.INDEX_HTML, html, ModelController.getAllMessage());
        he.writeHtml(EditHtmlList.INDEX_HTML, html);
    }

    /**
     * リクエストメッセージのmethod,uri,protocolVersionから、レスポンスのステータスコードを決定するメソッド
     * 1.プロトコルバージョンがHTTP/1.1以外の場合は500:HTTP Version Not Supported
     * 2.GET,POST以外のメソッドの場合は501:Not Implemented
     * 3.URIで指定されたファイルがリソースフォルダにない、もしくはディレクトリの場合は404:Not Found
     * 4.1,2,3でチェックして問題がなければ200:OK
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

enum Param {
    CONTRIBUTION("contribution"),
    SEARCH("search"),
    DELETE1("delete1"),
    DELETE2("delete2"),
    BACK("back");

    // メンバ変数の定義
    // このメンバ変数は必須です。
    private String name;

    /**
     * このメソッドも必須です。
     *
     * @return enum型のvalueを返します。
     */
    public String getName() {
        return name;
    }

    // コンストラクタの実装
    Param(String name) {
        this.name = name;
    }

    // メソッドのオーバーライド
    public String toString() {
        return name;
    }


    /**
     * このメソッドは、文字列を元に、enumを返します。
     *
     * @param str 文字列（例）search
     * @return Enum（例）Param.SEARCH
     */
    public static Param getEnum(String str) {
        Param[] enumArray = Param.values();

        for (Param enumStr : enumArray) {
            if (str.equals(enumStr.name)) {
                return enumStr;
            }
        }
        return null;
    }
}
