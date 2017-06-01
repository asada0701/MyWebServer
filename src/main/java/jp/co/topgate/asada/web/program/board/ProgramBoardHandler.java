package jp.co.topgate.asada.web.program.board;

import jp.co.topgate.asada.web.*;
import jp.co.topgate.asada.web.exception.CsvRuntimeException;
import jp.co.topgate.asada.web.exception.IllegalRequestException;
import jp.co.topgate.asada.web.program.board.model.Message;
import jp.co.topgate.asada.web.program.board.model.ModelController;
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
        String method = requestMessage.getMethod();

        try {
            ModelController.setMessageList(CsvHelper.readMessage());

            if (method.equals("GET")) {
                doGet(requestMessage, responseMessage, issueTimeId());

            } else if (method.equals("POST")) {
                doPost(requestMessage, responseMessage, issueTimeId());

                CsvHelper.writeMessage(ModelController.getAllMessage());
            }
        } catch (IllegalRequestException e) {
            sendErrorResponse(responseMessage, StatusLine.BAD_REQUEST);

        } catch (NullPointerException e) {
            sendErrorResponse(responseMessage, StatusLine.INTERNAL_SERVER_ERROR);

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
    static void doGet(RequestMessage requestMessage, ResponseMessage responseMessage, String nowTimeID) {
        Path filePath = Handler.getFilePath(requestMessage.getUri());

        //index.htmlをGET要求された場合の処理
        if (filePath.equals(HtmlList.INDEX_HTML.getPath())) {
            String html = HtmlEditor.editIndexHtml(ModelController.getAllMessage(), nowTimeID);
            sendResponse(responseMessage, html);
            return;
        }

        //search.htmlをGETリクエストされた場合の処理
        if (filePath.equals(HtmlList.SEARCH_HTML.getPath())) {
            String param = requestMessage.findUriQuery("param");
            if (param == null) {
                String html = HtmlEditor.editIndexHtml(ModelController.getAllMessage(), nowTimeID);
                sendResponse(responseMessage, html);
                return;
            }

            //特定のユーザーの書き込んだ内容を表示する処理
            if (param.equals("search")) {
                String name = requestMessage.findUriQuery("name");
                if (name == null) {
                    throw new IllegalRequestException("param:" + param + " nameがnullです。");
                }
                List<Message> messageList = ModelController.findMessageByName(name);

                if (messageList.size() == 0) {
                    throw new IllegalRequestException(param + "で 検索結果が 0 でした。");
                }

                String html = HtmlEditor.editSearchHtml(messageList, nowTimeID);
                sendResponse(responseMessage, html);
                return;
            }
        }

        if (!filePath.toFile().exists()) {
            sendErrorResponse(responseMessage, StatusLine.NOT_FOUND);
            return;
        }

        Handler.sendResponse(responseMessage, filePath);
    }

    /**
     * POSTの場合の処理
     *
     * @param requestMessage  RequestMessageのオブジェクト
     * @param responseMessage ResponseMessageのオブジェクト
     * @throws IllegalRequestException リクエストメッセージに問題があった
     * @throws NullPointerException    引数がnullの場合
     */
    static void doPost(RequestMessage requestMessage, ResponseMessage responseMessage, String nowTimeID) throws IllegalRequestException, NullPointerException {

        //このメソッドの責務は重く、副作用が大きい(CSVファイルに書き込むデータを扱う)ため、特別にnullチェックを行う。
        if (requestMessage == null || responseMessage == null || nowTimeID == null) {
            throw new NullPointerException();
        }

        //リクエストのヘッダーフィールドにparamがあるか、メッセージボディは問題ないか確認。
        Map<String, String> messageBody = requestMessage.parseMessageBodyToMap();
        if (messageBody == null) {
            throw new IllegalRequestException("messageBodyがnullである。");
        }
        String param = messageBody.get("param");
        if (param == null) {
            throw new IllegalRequestException("paramがnullである。");
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

                    throw new IllegalRequestException("param:" + param + " name:" + unsafe_name + " title:" + unsafe_title + " text:"
                            + unsafe_text + " password:" + password + " timeID:" + timeId + "のいずれかがnullである。");
                }

                String safe_name = UnsafeChar.replace(unsafe_name);
                String safe_title = UnsafeChar.replace(unsafe_title);
                String safe_text = UnsafeChar.replace(unsafe_text);

                safe_text = HtmlEditor.changeLineFeedToBrTag(safe_text);

                if (!ModelController.isExist(timeId)) {
                    ModelController.addMessage(safe_name, safe_title, safe_text, password, timeId);
                }
                String html = HtmlEditor.editIndexHtml(ModelController.getAllMessage(), nowTimeID);
                sendResponse(responseMessage, html);
                return;
            }

            //ユーザーがindexのページで書き込んだ内容を削除する処理
            case "delete_step_1": {
                String number = messageBody.get("number");
                if (number == null || !NumberUtils.isNumber(number)) {
                    throw new IllegalRequestException("param:" + param + " number:" + number + " numberに問題があります。");
                }

                Message message = ModelController.findMessageByID(Integer.parseInt(number));
                if (message == null) {
                    throw new IllegalRequestException(param + "で message が null でした。");
                }

                String html = HtmlEditor.editDeleteHtml(message);
                sendResponse(responseMessage, html);
                return;
            }

            //step1でdeleteページを表示し、ユーザーが削除ボタンを押した時の処理
            case "delete_step_2": {
                String number = messageBody.get("number");
                String password = messageBody.get("password");

                if (number == null || !NumberUtils.isNumber(number) || password == null) {
                    throw new IllegalRequestException("param:" + param + " number:" + number + " password:" + password + " のどちらかに問題があります。");
                }

                if (ModelController.deleteMessage(Integer.parseInt(number), password)) {
                    //パスワードがあっていて、削除できた時の処理
                    sendResponse(responseMessage, HtmlEditor.getResultHtml());
                    return;

                } else {
                    //削除に失敗した時の処理
                    Message message = ModelController.findMessageByID(Integer.parseInt(number));

                    if (message != null) {
                        sendResponse(responseMessage, HtmlEditor.editDeleteHtml(message));
                        return;

                    } else {
                        throw new IllegalRequestException(param + "で message が null でした。");
                    }
                }
            }

            //ページに配置した戻るボタンを押した時の処理
            case "back": {
                String html = HtmlEditor.editIndexHtml(ModelController.getAllMessage(), nowTimeID);
                sendResponse(responseMessage, html);
                return;
            }

            default:
                throw new IllegalRequestException("param:" + param + " paramに想定されているもの以外が送られました。");
        }
    }

    /**
     * HTMLページに書き込むID（二重リクエスト防ぐためのもの）を発行するメソッド
     * TODO HTMLのソースコードを見られてもいいようにする。問題は、リクエストメッセージパースクラスのメソッド内で、URF-8でエンコードした時に例外が発生したこと。
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
