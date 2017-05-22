package jp.co.topgate.asada.web.program.board;

import jp.co.topgate.asada.web.*;
import jp.co.topgate.asada.web.exception.RequestParseException;
import jp.co.topgate.asada.web.util.*;
import jp.co.topgate.asada.web.exception.DoPostException;
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
     * リクエストの処理を行うメソッド
     */
    @Override
    public void doRequestProcess() {
        String method = requestMessage.getMethod();
        String uri = requestMessage.getUri();
        String protocolVersion = requestMessage.getProtocolVersion();

        StatusLine statusLine = ProgramBoardHandler.getStatusLine(method, uri, protocolVersion);

        if (!StatusLine.OK.equals(statusLine)) {                                                        //OKでない場合はこのままリターン
            this.statusLine = statusLine;
            return;
        }
        try {
            if ("GET".equals(requestMessage.getMethod()) && uri.endsWith("/index.html")) {              //GETでなおかつindex.htmlを要求された場合
                doGet(htmlEditor);
                this.statusLine = statusLine;

            } else if ("POST".equals(requestMessage.getMethod())) {                                            //POSTの場合

                if ("application/x-www-form-urlencoded".equals(requestMessage.findHeaderByName("Content-Type"))) {
                    Map<String, String> messageBody = requestMessage.parseMessageBodyToMapString();
                    String param = messageBody.get("param");
                    String newUri = doPost(htmlEditor, Param.getParam(param), messageBody);
                    requestMessage.setUri(newUri);
                    this.statusLine = statusLine;
                } else {
                    this.statusLine = StatusLine.BAD_REQUEST;
                }

            } else {
                this.statusLine = statusLine;
            }

        } catch (RequestParseException | DoPostException e) {
            this.statusLine = StatusLine.BAD_REQUEST;

        } catch (IOException e) {
            this.statusLine = StatusLine.INTERNAL_SERVER_ERROR;
        }
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
     * GETの場合の処理
     * POSTでindex.htmlファイルに書き込む場合も呼ばれる
     *
     * @param htmlEditor HtmlEditorのオブジェクト
     * @throws IOException HTMLファイルに書き込み中に例外発生
     */
    static void doGet(HtmlEditor htmlEditor) throws IOException {
        String html = htmlEditor.editIndexOrSearchHtml(EditHtmlList.INDEX_HTML, ModelController.getAllMessage());
        htmlEditor.writeHtml(EditHtmlList.INDEX_HTML, html);
    }

    /**
     * POSTの場合の処理
     *
     * @param htmlEditor  HtmlEditorのオブジェクトを渡す
     * @param param       メッセージボディで送られてくるparamのEnum
     * @param messageBody リクエストのメッセージボディを渡す
     * @return レスポンスメッセージのボディの参照を変更
     * @throws IOException     HTMLファイルに書き込み中に例外発生
     * @throws DoPostException POST処理中に例外発生
     */
    static String doPost(HtmlEditor htmlEditor, Param param, Map<String, String> messageBody) throws IOException, DoPostException {
        if (param == null) {
            throw new DoPostException("POSTのリクエストメッセージのヘッダーフィールドにparamが含まれていませんでした。");
        }

        EditHtmlList editHtmlList;
        switch (param) {
            case WRITE: {
                String name = messageBody.get("name");
                String title = messageBody.get("title");
                String text = messageBody.get("text");
                String password = messageBody.get("password");

                if (name == null || title == null || text == null || password == null) {
                    throw new DoPostException("param:" + param + " name:" + name + " title:" + title + " text:" + text + " password:" + password + "のいずれかの項目に問題があります。");
                }

                name = InvalidChar.replace(name);
                title = InvalidChar.replace(title);
                text = InvalidChar.replace(text);
                password = InvalidChar.replace(password);

                text = HtmlEditor.changeLineFeedToBr(text);

                ModelController.addMessage(name, title, text, password);

                return writeIndex(htmlEditor);
            }

            case SEARCH: {
                String number = messageBody.get("number");
                if (number == null || !NumberUtils.isNumber(number)) {
                    throw new DoPostException("param:" + param + " number:" + number + " numberに問題があります。");
                }
                String name = ModelController.getName(Integer.parseInt(number));
                List<Message> messageList = ModelController.findMessageByName(name);

                if (messageList.size() > 0) {
                    editHtmlList = EditHtmlList.SEARCH_HTML;
                    htmlEditor.writeHtml(editHtmlList, htmlEditor.editIndexOrSearchHtml(editHtmlList, messageList));
                    return EditHtmlList.SEARCH_HTML.getUri();

                } else {
                    throw new DoPostException(param + "で 検索結果が 0 でした。");
                }
            }

            case DELETE_STEP_1: {
                String number = messageBody.get("number");
                if (number == null || !NumberUtils.isNumber(number)) {
                    throw new DoPostException("param:" + param + " number:" + number + " numberに問題があります。");
                }

                Message message = ModelController.findMessageByID(Integer.parseInt(number));
                if (message != null) {
                    editHtmlList = EditHtmlList.DELETE_HTML;
                    htmlEditor.writeHtml(editHtmlList, htmlEditor.editDeleteHtml(message));
                    return EditHtmlList.DELETE_HTML.getUri();

                } else {
                    throw new DoPostException(param + "で message が null でした。");
                }
            }

            case DELETE_STEP_2: {
                String number = messageBody.get("number");
                String password = messageBody.get("password");
                if (number == null || !NumberUtils.isNumber(number) || password == null) {
                    throw new DoPostException("param:" + param + " number:" + number + " password:" + password + " のどちらかに問題があります。");
                }

                if (ModelController.deleteMessage(Integer.parseInt(number), password)) {
                    return "/program/board/result.html";

                } else {
                    Message message = ModelController.findMessageByID(Integer.parseInt(messageBody.get("number")));

                    if (message != null) {
                        editHtmlList = EditHtmlList.DELETE_HTML;
                        htmlEditor.writeHtml(editHtmlList, htmlEditor.editDeleteHtml(message));
                        return EditHtmlList.DELETE_HTML.getUri();

                    } else {
                        throw new DoPostException(param + "で message が null でした。");
                    }
                }
            }

            case BACK:
                return writeIndex(htmlEditor);

            default:
                throw new DoPostException("param:" + param + " paramが想定されているもの以外が送られました。");
        }
    }

    /**
     * index.htmlを編集するメソッド
     *
     * @param htmlEditor HtmlEditorのオブジェクト
     * @return レスポンスメッセージのボディの参照を変更
     * @throws IOException HTMLファイルに書き込み中に例外発生
     */
    static String writeIndex(HtmlEditor htmlEditor) throws IOException {
        String html = htmlEditor.editIndexOrSearchHtml(EditHtmlList.INDEX_HTML, ModelController.getAllMessage());
        htmlEditor.writeHtml(EditHtmlList.INDEX_HTML, html);
        return EditHtmlList.INDEX_HTML.getUri();
    }

    /**
     * レスポンスの処理を行うメソッド
     *
     * @param outputStream SocketのOutputStreamを渡す
     * @throws HtmlInitializeException {@link HtmlEditor#resetAllFiles()}を参照
     */
    @Override
    public void doResponseProcess(OutputStream outputStream) throws HtmlInitializeException {
        ResponseMessage responseMessage = new ResponseMessage();
        String path = Handler.getFilePath(UrlPattern.PROGRAM_BOARD, requestMessage.getUri());

        if (statusLine.equals(StatusLine.OK)) {
            ContentType ct = new ContentType(path);
            responseMessage.addHeader("Content-Type", ct.getContentType());
            responseMessage.addHeader("Content-Length", String.valueOf(new File(path).length()));

        } else {
            responseMessage.addHeader("Content-Type", "text/html; charset=UTF-8");
        }

        responseMessage.returnResponse(outputStream, statusLine, path);

        htmlEditor.resetAllFiles();
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
 * POSTで送られてくるparamのEnum
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

        Param[] enumArray = Param.values();

        for (Param enumStr : enumArray) {
            if (str.equals(enumStr.name)) {
                return enumStr;
            }
        }
        return null;
    }
}
