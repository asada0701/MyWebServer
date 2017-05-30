package jp.co.topgate.asada.web.program.board;

import jp.co.topgate.asada.web.*;
import jp.co.topgate.asada.web.Handler;
import jp.co.topgate.asada.web.UrlPattern;
import jp.co.topgate.asada.web.exception.RequestParseException;
import jp.co.topgate.asada.web.util.*;
import jp.co.topgate.asada.web.exception.IllegalRequestException;
import jp.co.topgate.asada.web.program.board.model.Message;
import jp.co.topgate.asada.web.program.board.model.ModelController;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * ProgramBoardの配信を行うハンドラークラス
 * TODO 色々と無駄がある。
 *
 * @author asada
 */
public class ProgramBoardHandler extends Handler {
    /**
     * HTTPリクエストのメソッド
     */
    private static List<String> method = new ArrayList<>();

    static {
        method.add("GET");
        method.add("POST");
    }

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
     * returnしていても、finallyは必ず通るので、finallyの中でhtmlの初期化を行う。
     */
    @Override
    public void handleRequest() {
        String method = requestMessage.getMethod();
        String uri = requestMessage.getUri();

        if (!ProgramBoardHandler.matchMethod(method)) {
            sendErrorResponse(responseMessage, StatusLine.NOT_IMPLEMENTED);
            return;
        }

        if (method.equals("GET")) {
            String targetPath = Handler.getFilePath(UrlPattern.PROGRAM_BOARD, uri);

            if (targetPath.endsWith("/") || targetPath.endsWith(Main.WELCOME_PAGE_NAME)) {
                //ウェルカムページをレスポンスする
                try {
                    doGet(responseMessage);
                } catch (IOException e) {
                    sendErrorResponse(responseMessage, StatusLine.INTERNAL_SERVER_ERROR);
                }
                return;
            }

            File targetFile = new File(targetPath);
            if (!targetFile.exists() || !targetFile.isFile()) {
                sendErrorResponse(responseMessage, StatusLine.NOT_FOUND);
                return;
            }
            responseMessage.addHeaderWithContentType(ContentType.getContentType(targetPath));
            responseMessage.addHeaderWithContentLength(String.valueOf(targetFile.length()));
            sendResponse(responseMessage, targetFile);
            return;
        }

        try {
            if (method.equals("POST")) {
                Map<String, String> messageBody = requestMessage.parseMessageBodyToMap();
                if (messageBody == null) {
                    sendErrorResponse(responseMessage, StatusLine.BAD_REQUEST);
                }
                doPost(responseMessage, Param.getParam(messageBody.get("param")), messageBody);
            }

        } catch (RequestParseException | IllegalRequestException e) {
            sendErrorResponse(responseMessage, StatusLine.BAD_REQUEST);

        } catch (IOException e) {
            sendErrorResponse(responseMessage, StatusLine.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GETの場合の処理
     * POSTでindex.htmlファイルに書き込む場合も呼ばれる
     *
     * @param responseMessage ResponseMessageのオブジェクト
     */
    static void doGet(ResponseMessage responseMessage) throws IOException {
        String html = HtmlEditor.editIndexOrSearchHtml(ProgramBoardHtmlList.INDEX_HTML, ModelController.getAllMessage(), issueTimeId());
        sendResponse(responseMessage, html);
    }

    /**
     * POSTの場合の処理
     *
     * @param param       メッセージボディで送られてくるparamのEnum
     * @param messageBody リクエストのメッセージボディを渡す
     * @throws IOException             HTMLファイルに書き込み中に例外発生
     * @throws IllegalRequestException リクエストメッセージに問題があった
     */
    static void doPost(ResponseMessage responseMessage, Param param, Map<String, String> messageBody) throws IOException, IllegalRequestException {
        if (param == null) {
            throw new IllegalRequestException("POSTのリクエストメッセージのヘッダーフィールドにparamが含まれていませんでした。");
        }

        ProgramBoardHtmlList programBoardHtmlList;
        switch (param) {
            case WRITE: {
                String unsafe_name = messageBody.get("name");
                String unsafe_title = messageBody.get("title");
                String unsafe_text = messageBody.get("text");
                String unsafe_password = messageBody.get("password");
                String timeIdOfRequest = messageBody.get("timeID");

                if (unsafe_name == null || unsafe_title == null || unsafe_text == null || unsafe_password == null || timeIdOfRequest == null) {
                    throw new IllegalRequestException("param:" + param + " name:" + unsafe_name + " title:" + unsafe_title + " text:"
                            + unsafe_text + " password:" + unsafe_password + " timeID:" + timeIdOfRequest + "のいずれかの項目に問題があります。");
                }

                String safe_name = UnsafeChar.replace(unsafe_name);
                String safe_title = UnsafeChar.replace(unsafe_title);
                String safe_text = UnsafeChar.replace(unsafe_text);
                String safe_password = UnsafeChar.replace(unsafe_password);

                safe_text = HtmlEditor.changeLineFeedToBrTag(safe_text);

                if (ModelController.isExist(timeIdOfRequest)) {
                    writeIndex(responseMessage);
                    return;
                }

                ModelController.addMessage(safe_name, safe_title, safe_text, safe_password, timeIdOfRequest);
                writeIndex(responseMessage);
                return;
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
                    sendResponse(responseMessage, HtmlEditor.editIndexOrSearchHtml(programBoardHtmlList, messageList, issueTimeId()));
                    return;

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
                    sendResponse(responseMessage, HtmlEditor.editDeleteHtml(message));
                    return;

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
                    sendResponse(responseMessage, new File(ProgramBoardHtmlList.RESULT_HTML.getPath()));
                    return;

                } else {
                    Message message = ModelController.findMessageByID(Integer.parseInt(messageBody.get("number")));

                    if (message != null) {
                        sendResponse(responseMessage, HtmlEditor.editDeleteHtml(message));
                        return;

                    } else {
                        throw new IllegalRequestException(param + "で message が null でした。");
                    }
                }
            }

            case BACK:
                writeIndex(responseMessage);

            default:
                throw new IllegalRequestException("param:" + param + " paramに想定されているもの以外が送られました。");
        }
    }

    /**
     * index.htmlを編集するメソッド
     *
     * @throws IOException HTMLファイルに書き込み中に例外発生
     */
    static void writeIndex(ResponseMessage responseMessage) throws IOException {
        String html = HtmlEditor.editIndexOrSearchHtml(ProgramBoardHtmlList.INDEX_HTML, ModelController.getAllMessage(), issueTimeId());
        sendResponse(responseMessage, html);
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

    /**
     * 編集したHTMLをレスポンスで返すメソッド
     *
     * @param responseMessage
     * @param html
     */
    static void sendResponse(ResponseMessage responseMessage, String html) {
        responseMessage.addHeaderWithContentType("text/html; charset=UTF-8");
        responseMessage.addHeaderWithContentLength(String.valueOf(html.getBytes().length));

        PrintWriter printWriter = responseMessage.getPrintWriter(StatusLine.OK);
        for (String line : html.split("\n")) {
            printWriter.write(line + "\n");
        }
        printWriter.flush();
    }

    /**
     * バイナリデータをレスポンスで返すメソッド
     */
    static void sendResponse(ResponseMessage responseMessage, File file) {
        OutputStream outputStream = responseMessage.getOutputStream(StatusLine.OK);
        try (InputStream in = new FileInputStream(file)) {
            int num;
            while ((num = in.read()) != -1) {
                outputStream.write(num);
            }
        } catch (IOException e) {

        }
    }

    /**
     * エラー場合のレスポンスメッセージを書き込むメソッド
     *
     * @param responseMessage レスポンスメッセージを渡す
     * @param statusLine      ステータスラインを渡す
     */
    static void sendErrorResponse(ResponseMessage responseMessage, StatusLine statusLine) {
        responseMessage.addHeaderWithContentType(ContentType.ERROR_RESPONSE);

        PrintWriter pw = responseMessage.getPrintWriter(statusLine);
        pw.write(ResponseMessage.getErrorMessageBody(statusLine));
        pw.flush();
    }

    /**
     * HTMLページに書き込むID（二重リクエスト防ぐためのもの）を発行するメソッド
     *
     * @return エンコードされて発行する
     */
    static String issueTimeId() {
        LocalDateTime ldt = LocalDateTime.now();
        String timeID = "" + ldt.getYear() + ldt.getMonthValue() + ldt.getDayOfMonth() + ldt.getHour() +
                ldt.getMinute() + ldt.getSecond() + ldt.getNano();
        return Base64.getEncoder().encodeToString(timeID.getBytes());
    }
}


