package jp.co.topgate.asada.web.program.board;

import jp.co.topgate.asada.web.*;
import jp.co.topgate.asada.web.exception.CsvRuntimeException;
import jp.co.topgate.asada.web.exception.IllegalRequestException;
import jp.co.topgate.asada.web.program.board.model.Message;
import jp.co.topgate.asada.web.program.board.model.MessageController;
import org.apache.commons.lang3.math.NumberUtils;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * ProgramBoardの配信を行うハンドラークラス
 *
 * @author asada
 */
public class ProgramBoardHandler extends Handler {

    private RequestMessage requestMessage;
    private ResponseMessage responseMessage;

    /**
     * コンストラクタ
     *
     * @param requestMessage  リクエストメッセージのオブジェクト
     * @param responseMessage レスポンスメッセージのオブジェクト
     */
    public ProgramBoardHandler(RequestMessage requestMessage, ResponseMessage responseMessage) {
        this.requestMessage = requestMessage;
        this.responseMessage = responseMessage;
    }

    /**
     * {@link Handler#handleRequest()}を参照
     */
    @Override
    public void handleRequest() {
        try {
            String method = requestMessage.getMethod();

            if (method.equals("GET")) {
                doGet(requestMessage, responseMessage, issueTimeId());

            } else if (method.equals("POST")) {
                doPost(requestMessage, responseMessage, issueTimeId());
            }

        } catch (CsvRuntimeException e) {
            sendErrorResponse(responseMessage, StatusLine.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * GETの場合の処理
     *
     * @param requestMessage  RequestMessageのオブジェクト
     * @param responseMessage ResponseMessageのオブジェクト
     */
    void doGet(RequestMessage requestMessage, ResponseMessage responseMessage, String nowTimeID) throws CsvRuntimeException {
        String uri = changeUriToWelcomePage(requestMessage.getUri());
        Path filePath = getFilePath(uri);

        //ファイルパスが不正なものの場合はnullが返ってくる
        if (filePath == null) {
            sendErrorResponse(responseMessage, StatusLine.FORBIDDEN);
            return;
        }

        //index.htmlをGETリクエストされた場合の処理
        if (filePath.equals(HtmlList.INDEX_HTML.getPath())) {
            String html = HtmlEditor.editIndexHtml(MessageController.getAllMessage(), nowTimeID);
            sendResponse(responseMessage, html);
            return;
        }

        //search.htmlをGETリクエストされた場合の処理
        if (filePath.equals(HtmlList.SEARCH_HTML.getPath())) {
            String param = requestMessage.findUriQuery("param");
            doSearch(param, nowTimeID);
            return;
        }

        if (!filePath.toFile().exists()) {
            sendErrorResponse(responseMessage, StatusLine.NOT_FOUND);
            return;
        }

        sendResponse(responseMessage, filePath);
    }

    /**
     * GETでSearchにリクエストがきた場合の処理
     *
     * @param param     URIクエリーのparamを渡す
     * @param nowTimeID ユニークなID。二重リクエスト対策。
     * @throws CsvRuntimeException
     */
    void doSearch(String param, String nowTimeID) throws CsvRuntimeException {
        if (param == null) {
            //TODO リダイレクトの一貫性を考える
            //sendRedirect(responseMessage, "/program/board/");
            sendErrorResponse(responseMessage, StatusLine.BAD_REQUEST);
            return;
        }

        //特定のユーザーの書き込んだ内容を表示する処理
        if (param.equals("search")) {
            String name = requestMessage.findUriQuery("name");
            List<Message> messageList = MessageController.findMessageByName(name);

            if (name == null) {
                sendErrorResponse(responseMessage, StatusLine.BAD_REQUEST);
            }
            String html = HtmlEditor.editSearchHtml(messageList, nowTimeID);
            sendResponse(responseMessage, html);

        } else {
            //paramが予想していないものの場合
            sendErrorResponse(responseMessage, StatusLine.BAD_REQUEST);
        }
    }

    /**
     * POSTの場合の処理
     *
     * @param requestMessage  RequestMessageのオブジェクト
     * @param responseMessage ResponseMessageのオブジェクト
     */
    void doPost(RequestMessage requestMessage, ResponseMessage responseMessage, String nowTimeID) throws CsvRuntimeException {

        if (requestMessage == null || responseMessage == null || nowTimeID == null) {
            sendErrorResponse(responseMessage, StatusLine.INTERNAL_SERVER_ERROR);
            return;
        }

        //リクエストのヘッダーフィールドにparamがあるか、メッセージボディは問題ないか確認。
        Map<String, String> messageBody = requestMessage.parseMessageBodyToMap();
        if (messageBody == null) {
            sendErrorResponse(responseMessage, StatusLine.BAD_REQUEST);
            return;
        }
        String param = messageBody.get("param");
        if (param == null) {
            sendErrorResponse(responseMessage, StatusLine.BAD_REQUEST);
            return;
        }

        switch (param) {

            //ユーザーがメッセージを投稿する処理
            case "write": {
                String unsafe_name = messageBody.get("name");
                String unsafe_title = messageBody.get("title");
                String unsafe_text = messageBody.get("text");
                String password = messageBody.get("password");
                String timeId = messageBody.get("timeID");

                if (unsafe_name == null || unsafe_title == null || unsafe_text == null || password == null || timeId == null) {
                    sendErrorResponse(responseMessage, StatusLine.BAD_REQUEST);
                    return;
                }

                String safe_name = UnsafeChar.replace(unsafe_name);
                String safe_title = UnsafeChar.replace(unsafe_title);
                String safe_text = UnsafeChar.replace(unsafe_text);

                safe_text = HtmlEditor.changeLineFeedToBrTag(safe_text);

                if (!MessageController.isExist(timeId)) {
                    MessageController.addMessage(safe_name, safe_title, safe_text, password, timeId);

                } else {
                    //同一timeIDでPOSTした時のレスポンス
                    sendErrorResponse(responseMessage, StatusLine.BAD_REQUEST);
                }

                sendResponse(responseMessage, HtmlEditor.getIndexResultHtml());
                return;
            }

            //ユーザーがindexのページで書き込んだ内容を削除する処理
            case "deleteStep1": {
                String number = messageBody.get("number");
                if (number == null || !NumberUtils.isNumber(number)) {
                    sendErrorResponse(responseMessage, StatusLine.BAD_REQUEST);
                    return;
                }

                Message message = MessageController.findMessageByID(Integer.parseInt(number));
                if (message == null) {
                    sendErrorResponse(responseMessage, StatusLine.BAD_REQUEST);
                    return;
                }

                String html = HtmlEditor.editDeleteHtml(message);
                sendResponse(responseMessage, html);
                return;
            }

            //step1でdeleteページを表示し、ユーザーが削除ボタンを押した時の処理
            case "deleteStep2": {
                String number = messageBody.get("number");
                String password = messageBody.get("password");

                if (number == null || !NumberUtils.isNumber(number) || password == null) {
                    throw new IllegalRequestException("param:" + param + " number:" + number + " password:" + password + " のどちらかに問題があります。");
                }

                if (MessageController.deleteMessage(Integer.parseInt(number), password)) {
                    //パスワードがあっていて、削除できた時の処理
                    sendResponse(responseMessage, HtmlEditor.getDeleteResultHtml());
                    return;

                } else {
                    //削除に失敗した時の処理
                    Message message = MessageController.findMessageByID(Integer.parseInt(number));

                    if (message != null) {
                        String errorMessage = "パスワードが違います。";
                        sendResponse(responseMessage, HtmlEditor.editDeleteHtml(message, errorMessage));
                        return;

                    } else {
                        sendErrorResponse(responseMessage, StatusLine.BAD_REQUEST);
                        return;
                    }
                }
            }

            default:
                sendErrorResponse(responseMessage, StatusLine.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * HTMLページに書き込むID（二重リクエスト防ぐためのもの）を発行するメソッド
     * TODO HTMLのソースコードを見られてもいいようにする。問題は、Base64を使い、エンコードすると、リクエストメッセージパースクラスのメソッド内で、URF-8でエンコードした時に例外が発生したこと。
     *
     * @return エンコードされて発行する
     */
    private static String issueTimeId() {
        LocalDateTime ldt = LocalDateTime.now();
        return "" + ldt.getYear() + ldt.getMonthValue() + ldt.getDayOfMonth() + ldt.getHour() +
                ldt.getMinute() + ldt.getSecond() + ldt.getNano();
    }

    //テスト用
    RequestMessage getRequestMessage() {
        return requestMessage;
    }

    ResponseMessage getResponseMessage() {
        return responseMessage;
    }
}
