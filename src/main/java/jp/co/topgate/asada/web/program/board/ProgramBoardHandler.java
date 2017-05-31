package jp.co.topgate.asada.web.program.board;

import jp.co.topgate.asada.web.*;
import jp.co.topgate.asada.web.Handler;
import jp.co.topgate.asada.web.exception.CsvRuntimeException;
import jp.co.topgate.asada.web.exception.IllegalRequestException;
import jp.co.topgate.asada.web.program.board.model.Message;
import jp.co.topgate.asada.web.program.board.model.ModelController;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.*;
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
                doGet(requestMessage, responseMessage);

            } else if (method.equals("POST")) {
                doPost(requestMessage, responseMessage);

                CsvHelper.writeMessage(ModelController.getAllMessage());
            }
        } catch (IllegalRequestException e) {
            e.printStackTrace();
            sendErrorResponse(responseMessage, StatusLine.BAD_REQUEST);

        } catch (IOException e) {
            e.printStackTrace();
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
     * @throws IOException HTMLファイルに書き込み中に例外発生
     */
    static void doGet(RequestMessage requestMessage, ResponseMessage responseMessage) throws IOException {
        Path filePath = Handler.getFilePath(requestMessage.getUri());

        if (!Handler.checkFile(filePath.toFile())) {
            sendErrorResponse(responseMessage, StatusLine.NOT_FOUND);
            return;
        }

        //index.htmlをGET要求された場合は編集が入る
        if (filePath.equals(ProgramBoardHtmlList.INDEX_HTML.getPath())) {
            String html = HtmlEditor.editIndexOrSearchHtml(ProgramBoardHtmlList.INDEX_HTML, ModelController.getAllMessage(), issueTimeId());
            sendResponse(responseMessage, html);
            return;
        }

        if (filePath.equals(ProgramBoardHtmlList.SEARCH_HTML.getPath())) {
            //リクエストのヘッダーフィールドにparamがあるか、メッセージボディは問題ないか確認。
            String param = requestMessage.findUriQuery("param");
            if (param == null) {
                throw new IllegalRequestException("paramがnullである。");
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

                String html = HtmlEditor.editIndexOrSearchHtml(ProgramBoardHtmlList.SEARCH_HTML, messageList, issueTimeId());
                sendResponse(responseMessage, html);
                return;
            }
        }

        sendResponse(responseMessage, filePath.toFile());
    }

    /**
     * POSTの場合の処理
     *
     * @param requestMessage  RequestMessageのオブジェクト
     * @param responseMessage ResponseMessageのオブジェクト
     * @throws IOException             HTMLファイルに書き込み中に例外発生
     * @throws IllegalRequestException リクエストメッセージに問題があった
     */
    static void doPost(RequestMessage requestMessage, ResponseMessage responseMessage) throws IOException, IllegalRequestException {

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
                String html = HtmlEditor.editIndexOrSearchHtml(ProgramBoardHtmlList.INDEX_HTML, ModelController.getAllMessage(), issueTimeId());
                sendResponse(responseMessage, html);
                return;
            }

//            //特定のユーザーが書き込んだ内容だけを表示させる処理
//            case "search": {
//                String number = messageBody.get("number");
//                if (number == null || !NumberUtils.isNumber(number)) {
//                    throw new IllegalRequestException("param:" + param + " number:" + number + " numberに問題があります。");
//                }
//                String name = ModelController.getName(Integer.parseInt(number));
//                List<Message> messageList = ModelController.findMessageByName(name);
//
//                if (messageList.size() == 0) {
//                    throw new IllegalRequestException(param + "で 検索結果が 0 でした。");
//                }
//
//                String html = HtmlEditor.editIndexOrSearchHtml(ProgramBoardHtmlList.SEARCH_HTML, messageList, issueTimeId());
//                sendResponse(responseMessage, html);
//                return;
//            }

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
                    sendResponse(responseMessage, ProgramBoardHtmlList.RESULT_HTML.getPath().toFile());
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
                String html = HtmlEditor.editIndexOrSearchHtml(ProgramBoardHtmlList.INDEX_HTML, ModelController.getAllMessage(), issueTimeId());
                sendResponse(responseMessage, html);
                return;
            }

            default:
                throw new IllegalRequestException("param:" + param + " paramに想定されているもの以外が送られました。");
        }
    }

    /**
     * HTMLを編集した場合にレスポンスを送信するメソッド
     *
     * @param responseMessage レスポンスメッセージを渡す
     * @param html            編集したHTMLの文字列を渡す
     */
    static void sendResponse(ResponseMessage responseMessage, String html) {
        responseMessage.addHeaderWithContentType(ContentType.getHtmlType());
        responseMessage.addHeaderWithContentLength(String.valueOf(html.getBytes().length));

        PrintWriter printWriter = responseMessage.getPrintWriter(StatusLine.OK);
        for (String line : html.split("\n")) {
            printWriter.write(line + "\n");
        }
        printWriter.flush();
    }

    /**
     * バイナリデータやハンドラー内で編集しなかった場合のメソッド
     *
     * @param responseMessage レスポンスメッセージを渡す
     * @param file            レスポンスしたいリソースファイルを渡す
     */
    static void sendResponse(ResponseMessage responseMessage, File file) {
        responseMessage.addHeaderWithContentType(ContentType.getContentType(file.getPath()));
        responseMessage.addHeaderWithContentLength(String.valueOf(file.length()));

        OutputStream outputStream = responseMessage.getOutputStream(StatusLine.OK);
        try (InputStream inputStream = new FileInputStream(file)) {
            int tmp;
            while ((tmp = inputStream.read()) != -1) {
                outputStream.write(tmp);
            }
            outputStream.flush();

        } catch (IOException e) {

        }
    }

    /**
     * エラーレスポンスを送信するメソッド
     *
     * @param responseMessage レスポンスメッセージを渡す
     * @param statusLine      ステータスラインを渡す
     */
    static void sendErrorResponse(ResponseMessage responseMessage, StatusLine statusLine) {
        responseMessage.addHeaderWithContentType(ContentType.getHtmlType());

        PrintWriter printWriter = responseMessage.getPrintWriter(statusLine);
        printWriter.write(ResponseMessage.getErrorMessageBody(statusLine));
        printWriter.flush();
    }

    /**
     * HTMLページに書き込むID（二重リクエスト防ぐためのもの）を発行するメソッド
     * TODO HTMLのソースコードを見られてもいいようにする。問題は、リクエストメッセージパースクラスのメソッドで、URF-8でエンコードするので、例外が発生するものがある。
     *
     * @return エンコードされて発行する
     */
    static String issueTimeId() {
        LocalDateTime ldt = LocalDateTime.now();
        return "" + ldt.getYear() + ldt.getMonthValue() + ldt.getDayOfMonth() + ldt.getHour() +
                ldt.getMinute() + ldt.getSecond() + ldt.getNano();
    }
}
