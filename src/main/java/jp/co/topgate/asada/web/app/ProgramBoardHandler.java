package jp.co.topgate.asada.web.app;

import jp.co.topgate.asada.web.ContentType;
import jp.co.topgate.asada.web.RequestMessage;
import jp.co.topgate.asada.web.ResponseMessage;
import jp.co.topgate.asada.web.exception.HtmlInitializeException;
import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * ProgramBoardの配信を行うハンドラークラス
 *
 * @author asada
 */
public class ProgramBoardHandler extends Handler {

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
     * リクエストの処理を行うメソッド
     *
     * @return レスポンスメッセージの状態行(StatusLine)を返す
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
                    requestMessage.setUri(doPost(he, Param.getEnum(param), messageBody));
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
                if (!EditHtmlList.INDEX_HTML.getUri().equals(uri)) {
                    return StatusLine.BAD_REQUEST;
                }
            }
        }
        return StatusLine.OK;
    }

    /**
     * POSTの場合の処理
     *
     * @param he          HtmlEditorのオブジェクトを渡す
     * @param param       メッセージボディで送られてくるparamのEnum
     * @param messageBody リクエストのメッセージボディを渡す
     * @return レスポンスメッセージのボディの参照を変更
     * @throws IOException HTMLファイルに書き込み中に例外発生
     */
    static String doPost(HtmlEditor he, Param param, Map<String, String> messageBody) throws IOException {
        EditHtmlList ehl;
        switch (param) {
            case SEARCH: {
                String number = messageBody.get("number");
                if (number == null || !NumberUtils.isNumber(number)) {
                    return writeIndex(he);
                }
                String name = ModelController.getName(Integer.parseInt(number));
                List<Message> messageList = ModelController.findSameNameMessage(name);

                if (messageList.size() > 0) {
                    ehl = EditHtmlList.SEARCH_HTML;
                    he.writeHtml(ehl, he.editIndexOrSearchHtml(ehl, messageList));
                    return EditHtmlList.SEARCH_HTML.getUri();

                } else {
                    return writeIndex(he);
                }
            }

            case DELETE1: {
                String number = messageBody.get("number");
                if (number == null || !NumberUtils.isNumber(number)) {
                    return writeIndex(he);
                }

                Message message = ModelController.findMessage(Integer.parseInt(number));
                if (message != null) {
                    ehl = EditHtmlList.DELETE_HTML;
                    he.writeHtml(ehl, he.editDeleteHtml(message));
                    return EditHtmlList.DELETE_HTML.getUri();

                } else {
                    return writeIndex(he);
                }
            }

            case DELETE2: {
                String number = messageBody.get("number");
                String password = messageBody.get("password");
                if (number == null || !NumberUtils.isNumber(number) || password == null) {
                    return writeIndex(he);
                }

                if (ModelController.deleteMessage(Integer.parseInt(number), password)) {
                    return "/program/board/result.html";

                } else {
                    Message message = ModelController.findMessage(Integer.parseInt(messageBody.get("number")));

                    if (message != null) {
                        ehl = EditHtmlList.DELETE_HTML;
                        he.writeHtml(ehl, he.editDeleteHtml(message));
                        return EditHtmlList.DELETE_HTML.getUri();

                    } else {
                        return writeIndex(he);
                    }
                }
            }

            case CONTRIBUTION: {
                String name = messageBody.get("name");
                String title = messageBody.get("title");
                String text = messageBody.get("text");
                String password = messageBody.get("password");

                if (name == null || title == null || text == null || password == null) {
                    return writeIndex(he);
                }

                name = InvalidChar.replace(name);
                title = InvalidChar.replace(title);
                text = InvalidChar.replace(text);
                password = InvalidChar.replace(password);

                text = HtmlEditor.changeLineFeedToBr(text);

                ModelController.addMessage(name, title, text, password);
            }
            case BACK:
            default:
                return writeIndex(he);
        }
    }

    /**
     * index.htmlファイルを編集する操作をまとめたメソッド
     *
     * @param he HtmlEditorのオブジェクト
     * @return レスポンスメッセージのボディの参照を変更
     * @throws IOException HTMLファイルに書き込み中に例外発生
     */
    static String writeIndex(HtmlEditor he) throws IOException {
        String html = he.editIndexOrSearchHtml(EditHtmlList.INDEX_HTML, ModelController.getAllMessage());
        he.writeHtml(EditHtmlList.INDEX_HTML, html);
        return EditHtmlList.INDEX_HTML.getUri();
    }

    /**
     * レスポンスの処理を行うメソッド
     *
     * @param os SocketのOutputStreamを渡す
     * @param sl ステータスラインの列挙型を渡す
     * @throws HtmlInitializeException {@link HtmlEditor#allInitialization()}を参照
     */
    @Override
    public void doResponseProcess(OutputStream os, StatusLine sl) throws HtmlInitializeException {
        ResponseMessage rm = new ResponseMessage();
        String path = Handler.getFilePath(UrlPattern.PROGRAM_BOARD, requestMessage.getUri());

        if (sl.equals(StatusLine.OK)) {
            ContentType ct = new ContentType(path);
            rm.addHeader("Content-Type", ct.getContentType());
            rm.addHeader("Content-Length", String.valueOf(new File(path).length()));

        } else {
            rm.addHeader("Content-Type", "text/html; charset=UTF-8");
        }

        rm.returnResponse(os, sl, path);

        he.allInitialization();
    }

    //テスト用

    RequestMessage getRequestMessage() {
        return requestMessage;
    }

    HtmlEditor getHtmlEditor() {
        return he;
    }
}

/**
 * POSTで送られてくるparamのEnum
 */
enum Param {

    /**
     * index.htmlでメッセージを投稿する場合
     */
    CONTRIBUTION("contribution"),

    /**
     * index.htmlで投稿者の名前で検索する場合
     */
    SEARCH("search"),

    /**
     * index.htmlで投稿したメッセージを削除する場合
     */
    DELETE1("delete1"),

    /**
     * delete.htmlでパスワードの確認する場合
     */
    DELETE2("delete2"),

    /**
     * index.htmlのページに戻る
     */
    BACK("back");

    private String name;

    public String getName() {
        return name;
    }

    Param(String name) {
        this.name = name;
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
