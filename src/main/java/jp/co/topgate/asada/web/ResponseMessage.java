package jp.co.topgate.asada.web;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * レスポンスメッセージクラス
 *
 * @author asada
 */
public class ResponseMessage {
    /**
     * ヘッダーフィールドのコロン
     */
    private static final String HEADER_FIELD_COLON = ": ";
    /**
     * HTTPステータスコード:200
     */
    static final int OK = 200;
    /**
     * HTTPステータスコード:400
     */
    static final int BAD_REQUEST = 400;
    /**
     * HTTPステータスコード:404
     */
    static final int NOT_FOUND = 404;
    /**
     * HTTPステータスコード:500
     */
    static final int INTERNAL_SERVER_ERROR = 500;
    /**
     * HTTPステータスコード:501
     */
    static final int NOT_IMPLEMENTED = 501;
    /**
     * HTTPステータスコード:505
     */
    static final int HTTP_VERSION_NOT_SUPPORTED = 505;

    private String protocolVersion = null;
    private Map<Integer, String> reasonPhrase = new HashMap<>();
    private List<String> headerField = new ArrayList<>();

    /**
     * コンストラクタ
     * プロトコルバージョンの初期設定をする
     * リーズンフレーズを用意する
     */
    public ResponseMessage() {
        protocolVersion = "HTTP/1.1";
        reasonPhrase.put(OK, "OK");
        reasonPhrase.put(BAD_REQUEST, "Bad Request");
        reasonPhrase.put(NOT_FOUND, "Not Found");
        reasonPhrase.put(INTERNAL_SERVER_ERROR, "Internal Server Error");
        reasonPhrase.put(NOT_IMPLEMENTED, "Not Implemented");
        reasonPhrase.put(HTTP_VERSION_NOT_SUPPORTED, "HTTP Version Not Supported");
    }


    /**
     * プロトコルバージョンの設定をする
     */
    public void setProtocolVersion(String protocolVersion) {
        if (protocolVersion != null) {
            this.protocolVersion = protocolVersion;
        }
    }

    /**
     * ステータスコードとリーズンフレーズを追加する
     */
    public void addReasonPhrase(int statusCode, String reasonPhrase) {
        if (reasonPhrase != null) {
            this.reasonPhrase.put(statusCode, reasonPhrase);
        }
    }

    /**
     * ヘッダーフィールドにヘッダ名とヘッダ値を追加する
     */
    public void addHeader(String name, String value) {
        if (name != null && value != null) {
            headerField.add(name + HEADER_FIELD_COLON + value);
        }
    }

    /**
     * リソースファイルを送る時のメソッド
     */
    public void returnResponse(OutputStream os, int statusCode, ResourceFile rf) throws IOException {
        addHeader("Content-Type", rf.getContentType());
        InputStream in = null;
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(protocolVersion).append(" ").append(statusCode).append(" ").append(reasonPhrase.get(statusCode));
            builder.append("\n");
            for (String s : headerField) {
                builder.append(s).append("\n");
            }
            builder.append("\n");
            os.write(builder.toString().getBytes());
            in = new FileInputStream(rf);
            int num;
            while ((num = in.read()) != -1) {
                os.write(num);
            }
            os.flush();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * エラーメッセージを送る時のメソッド
     */
    public void returnErrorResponse(OutputStream os, int statusCode) throws IOException {
        this.addHeader("Content-Type", "text/html; charset=UTF-8");
        String stringMessageBody;

        switch (statusCode) {
            case BAD_REQUEST:
                stringMessageBody =
                        "<html><head><title>400 Bad Request</title></head>" +
                                "<body><h1>Bad Request</h1>" +
                                "<p>Your browser sent a request that this server could not understand.<br /></p></body></html>";
                break;

            case NOT_FOUND:
                stringMessageBody =
                        "<html><head><title>404 Not Found</title></head>" +
                                "<body><h1>Not Found</h1>" +
                                "<p>お探しのページは見つかりませんでした。</p></body></html>";
                break;

            case NOT_IMPLEMENTED:
                stringMessageBody =
                        "<html><head><title>501 Not Implemented</title></head>" +
                                "<body><h1>Not Implemented</h1>" +
                                "<p>そのファイルは開けません。</p></body></html>";
                break;

            case HTTP_VERSION_NOT_SUPPORTED:
                stringMessageBody =
                        "<html><head><title>505 HTTP Version Not Supported</title></head>" +
                                "<body><h1>HTTP Version Not Supported</h1></body></html>";
                break;

            default:
                stringMessageBody =
                        "<html><head><title>500 Internal Server Error</title></head>" +
                                "<body><h1>Internal Server Error</h1>" +
                                "<p>サーバー内部のエラーにより表示できません。ごめんなさい。</p></body></html>";
        }
        PrintWriter pw = new PrintWriter(os, true);
        StringBuilder builder = new StringBuilder();
        builder.append(protocolVersion).append(" ").append(statusCode).append(" ").append(reasonPhrase.get(statusCode)).append("\n");
        for (String s : headerField) {
            builder.append(s).append("\n");
        }
        builder.append("\n");
        builder.append(stringMessageBody);
        pw.println(builder.toString());
        pw.close();
    }

    String getProtocolVersion() {
        return this.protocolVersion;
    }

    String findReasonPhraseByStatusCode(int statusCode) {
        return reasonPhrase.get(statusCode);
    }

    List<String> getHeaderField() {
        return this.headerField;
    }
}
