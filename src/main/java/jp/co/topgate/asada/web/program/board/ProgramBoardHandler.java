package jp.co.topgate.asada.web.program.board;

import jp.co.topgate.asada.web.*;
import jp.co.topgate.asada.web.Handler;
import jp.co.topgate.asada.web.UrlPattern;
import jp.co.topgate.asada.web.exception.RequestParseException;
import jp.co.topgate.asada.web.util.*;
import jp.co.topgate.asada.web.exception.IllegalRequestException;
import jp.co.topgate.asada.web.exception.HtmlInitializeException;
import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ProgramBoardの配信を行うハンドラークラス
 *
 * @author asada
 */
public class ProgramBoardHandler extends Handler {

    /**
     * HTTPリクエストのプロトコルバージョン
     */
    static final String PROTOCOL_VERSION = "HTTP/1.1";

    /**
     * HTTPリクエストのコンテンツタイプ
     */
    static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

    /**
     * HTTPリクエストのメソッド
     */
    private static List<String> method = new ArrayList<>();

    static {
        method.add("GET");
        method.add("POST");
    }

    /**
     * 引数targetがmethodのリストに含まれているか判定する
     *
     * @param target ターゲット文字列
     * @return trueの場合は含まれている、falseの場合は含まれない
     */
    static boolean matchMethod(String target) {
        for (String s : method) {
            if (s.equals(target)) {
                return true;
            }
        }
        return false;
    }

    private RequestMessage requestMessage;

    private HtmlEditor htmlEditor;

    /**
     * コンストラクタ
     *
     * @param requestMessage リクエストメッセージのオブジェクト
     * @throws HtmlInitializeException {@link HtmlEditor#HtmlEditor()}を参照
     */
    public ProgramBoardHandler(RequestMessage requestMessage) throws HtmlInitializeException {
        this.requestMessage = requestMessage;
        this.htmlEditor = new HtmlEditor();
    }

    /**
     * {@link Handler#handleRequest(OutputStream)}を参照
     *
     * @param outputStream SocketのoutputStream
     * @throws HtmlInitializeException {@link HtmlEditor#resetAllFiles()}を参照
     */
    @Override
    public void handleRequest(OutputStream outputStream) throws HtmlInitializeException {
        String method = requestMessage.getMethod();
        String uri = requestMessage.getUri();
        String protocolVersion = requestMessage.getProtocolVersion();

        StatusLine statusLine = ProgramBoardHandler.decideStatusLine(method, uri, protocolVersion);
        ResponseMessage responseMessage = new ResponseMessage();

        if (!statusLine.equals(StatusLine.OK)) {
            responseMessage.addHeaderWithContentType(ContentType.errorResponseContentType);
            responseMessage.writeResponse(outputStream, statusLine, "");
        }

        try {
            if ("GET".equals(requestMessage.getMethod()) && uri.endsWith(Main.WELCOME_PAGE_NAME)) {              //GETでなおかつindex.htmlを要求された場合
                doGet(htmlEditor);

            } else if ("POST".equals(requestMessage.getMethod())) {                                            //POSTの場合

                if (ProgramBoardHandler.CONTENT_TYPE.equals(requestMessage.findHeaderByName("Content-Type"))) {
                    Map<String, String> messageBody = requestMessage.getMessageBodyToMapString();
                    String param = messageBody.get("param");
                    ProgramBoardHtmlList programBoardHtmlList = doPost(htmlEditor, Param.getParam(param), messageBody);
                    String newUri = programBoardHtmlList.getUri();
                    requestMessage.setUri(newUri);
                } else {
                    statusLine = StatusLine.BAD_REQUEST;
                }
            }

        } catch (RequestParseException | IllegalRequestException e) {
            statusLine = StatusLine.BAD_REQUEST;

        } catch (IOException e) {
            statusLine = StatusLine.INTERNAL_SERVER_ERROR;
        }

        String path = Handler.getFilePath(UrlPattern.PROGRAM_BOARD, requestMessage.getUri());

        if (statusLine.equals(StatusLine.OK)) {
            ContentType contentType = new ContentType(path);
            responseMessage.addHeaderWithContentType(contentType.getContentType());
            responseMessage.addHeader("Content-Length", String.valueOf(new File(path).length()));

        } else {
            responseMessage.addHeaderWithContentType(ContentType.errorResponseContentType);
        }

        responseMessage.writeResponse(outputStream, statusLine, path);

        htmlEditor.resetAllFiles();
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
    static StatusLine decideStatusLine(String method, String uri, String protocolVersion) {
        if (!ProgramBoardHandler.PROTOCOL_VERSION.equals(protocolVersion)) {
            return StatusLine.HTTP_VERSION_NOT_SUPPORTED;
        }

        if (!ProgramBoardHandler.matchMethod(method)) {
            return StatusLine.NOT_IMPLEMENTED;
        }

        if ("GET".equals(method)) {
            String path = Handler.getFilePath(UrlPattern.PROGRAM_BOARD, uri);
            File file = new File(path);
            if (!file.exists() || !file.isFile()) {
                return StatusLine.NOT_FOUND;
            }

        } else if ("POST".equals(method)) {
            if (!ProgramBoardHtmlList.INDEX_HTML.getUri().equals(uri)) {
                return StatusLine.BAD_REQUEST;
            }
        }

        return StatusLine.OK;
    }

    /**
     * GETの場合の処理
     * POSTでindex.htmlファイルに書き込む場合も呼ばれる
     *
     * @param htmlEditor HtmlEditorのオブジェクト
     * @throws IOException HTMLファイルに書き込み中に例外発生
     */
    static void doGet(HtmlEditor htmlEditor) throws IOException {
        String html = htmlEditor.editIndexOrSearchHtml(ProgramBoardHtmlList.INDEX_HTML, ModelController.getAllMessage());
        htmlEditor.writeHtml(ProgramBoardHtmlList.INDEX_HTML, html);
    }

    /**
     * POSTの場合の処理
     *
     * @param htmlEditor  HtmlEditorのオブジェクトを渡す
     * @param param       メッセージボディで送られてくるparamのEnum
     * @param messageBody リクエストのメッセージボディを渡す
     * @return レスポンスメッセージのボディの参照を変更
     * @throws IOException             HTMLファイルに書き込み中に例外発生
     * @throws IllegalRequestException リクエストメッセージに問題があった
     */
    static ProgramBoardHtmlList doPost(HtmlEditor htmlEditor, Param param, Map<String, String> messageBody) throws IOException, IllegalRequestException {
        if (htmlEditor == null) {
            throw new IllegalRequestException("doPostメソッドの引数HtmlEditorがnullでした。");
        }
        if (param == null) {
            throw new IllegalRequestException("POSTのリクエストメッセージのヘッダーフィールドにparamが含まれていませんでした。");
        }

        ProgramBoardHtmlList programBoardHtmlList;
        switch (param) {
            case WRITE: {
                String name = messageBody.get("name");
                String title = messageBody.get("title");
                String text = messageBody.get("text");
                String password = messageBody.get("password");

                if (name == null || title == null || text == null || password == null) {
                    throw new IllegalRequestException("param:" + param + " name:" + name + " title:" + title + " text:" + text + " password:" + password + "のいずれかの項目に問題があります。");
                }

                name = InvalidChar.replace(name);
                title = InvalidChar.replace(title);
                text = InvalidChar.replace(text);
                password = InvalidChar.replace(password);

                text = HtmlEditor.changeLineFeedToBrTag(text);

                ModelController.addMessage(name, title, text, password);

                return writeIndex(htmlEditor);
            }

            case SEARCH: {
                String number = messageBody.get("number");
                if (number == null || !NumberUtils.isNumber(number)) {
                    throw new IllegalRequestException("param:" + param + " number:" + number + " numberに問題があります。");
                }
                String name = ModelController.getName(Integer.parseInt(number));
                List<Message> messageList = ModelController.findMessageByName(name);

                if (messageList.size() > 0) {
                    programBoardHtmlList = ProgramBoardHtmlList.SEARCH_HTML;
                    htmlEditor.writeHtml(programBoardHtmlList, htmlEditor.editIndexOrSearchHtml(programBoardHtmlList, messageList));
                    return ProgramBoardHtmlList.SEARCH_HTML;

                } else {
                    throw new IllegalRequestException(param + "で 検索結果が 0 でした。");
                }
            }

            case DELETE_STEP_1: {
                String number = messageBody.get("number");
                if (number == null || !NumberUtils.isNumber(number)) {
                    throw new IllegalRequestException("param:" + param + " number:" + number + " numberに問題があります。");
                }

                Message message = ModelController.findMessageByID(Integer.parseInt(number));
                if (message != null) {
                    programBoardHtmlList = ProgramBoardHtmlList.DELETE_HTML;
                    htmlEditor.writeHtml(programBoardHtmlList, htmlEditor.editDeleteHtml(message));
                    return ProgramBoardHtmlList.DELETE_HTML;

                } else {
                    throw new IllegalRequestException(param + "で message が null でした。");
                }
            }

            case DELETE_STEP_2: {
                String number = messageBody.get("number");
                String password = messageBody.get("password");
                if (number == null || !NumberUtils.isNumber(number) || password == null) {
                    throw new IllegalRequestException("param:" + param + " number:" + number + " password:" + password + " のどちらかに問題があります。");
                }

                if (ModelController.deleteMessage(Integer.parseInt(number), password)) {
                    return ProgramBoardHtmlList.RESULT_HTML;

                } else {
                    Message message = ModelController.findMessageByID(Integer.parseInt(messageBody.get("number")));

                    if (message != null) {
                        programBoardHtmlList = ProgramBoardHtmlList.DELETE_HTML;
                        htmlEditor.writeHtml(programBoardHtmlList, htmlEditor.editDeleteHtml(message));
                        return ProgramBoardHtmlList.DELETE_HTML;

                    } else {
                        throw new IllegalRequestException(param + "で message が null でした。");
                    }
                }
            }

            case BACK:
                return writeIndex(htmlEditor);

            default:
                throw new IllegalRequestException("param:" + param + " paramが想定されているもの以外が送られました。");
        }
    }

    /**
     * index.htmlを編集するメソッド
     *
     * @param htmlEditor HtmlEditorのオブジェクト
     * @return レスポンスメッセージのボディの参照を変更
     * @throws IOException HTMLファイルに書き込み中に例外発生
     */
    static ProgramBoardHtmlList writeIndex(HtmlEditor htmlEditor) throws IOException {
        String html = htmlEditor.editIndexOrSearchHtml(ProgramBoardHtmlList.INDEX_HTML, ModelController.getAllMessage());
        htmlEditor.writeHtml(ProgramBoardHtmlList.INDEX_HTML, html);
        return ProgramBoardHtmlList.INDEX_HTML;
    }

    //テスト用

    RequestMessage getRequestMessage() {
        return requestMessage;
    }

    HtmlEditor getHtmlEditor() {
        return htmlEditor;
    }
}

/**
 * POSTで送られてくるparam
 *
 * @author asada
 */
enum Param {

    /**
     * index.htmlでメッセージを投稿する場合
     */
    WRITE("write"),

    /**
     * index.htmlで投稿者の名前で検索する場合
     */
    SEARCH("search"),

    /**
     * index.htmlで投稿したメッセージを削除する場合
     */
    DELETE_STEP_1("delete_step_1"),

    /**
     * delete.htmlでパスワードの確認する場合
     */
    DELETE_STEP_2("delete_step_2"),

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
    public static Param getParam(String str) {
        if (str == null) {
            return null;
        }

        Param[] array = Param.values();

        for (Param param : array) {
            if (str.equals(param.name)) {
                return param;
            }
        }
        return null;
    }
}

