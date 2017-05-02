package jp.co.topgate.asada.web;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * レスポンスメッセージクラス
 * サーバーのレスポンスを作成する
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
     * returnResponseメソッドを呼び出し、レスポンスメッセージを書き出す
     *
     * @param os         ソケットの出力ストリーム
     * @param statusCode レスポンスメッセージのステータスコード
     * @param rf         リソースファイルのオブジェクト
     */
    public ResponseMessage(OutputStream os, int statusCode) throws IOException {
        if (os == null) {
            throw new IOException();
        }

        protocolVersion = "HTTP/1.1";

        reasonPhrase.put(OK, "OK");
        reasonPhrase.put(BAD_REQUEST, "Bad Request");
        reasonPhrase.put(NOT_FOUND, "Not Found");
        reasonPhrase.put(INTERNAL_SERVER_ERROR, "Internal Server Error");
        reasonPhrase.put(NOT_IMPLEMENTED, "Not Implemented");
        reasonPhrase.put(HTTP_VERSION_NOT_SUPPORTED, "HTTP Version Not Supported");

        returnResponse(os, statusCode, rf);
    }

    /**
     * レスポンスメッセージを出力ストリームに書き出すメソッド
     *
     * @param os         ソケットの出力ストリーム
     * @param statusCode レスポンスメッセージのステータスコード
     * @param rf         リソースファイルのオブジェクト
     */
    private void returnResponse(OutputStream os, int statusCode) throws IOException {
        if (os == null) {
            throw new IOException();
        }

        if (statusCode == OK) {
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
        } else {
            this.addHeader("Content-Type", "text/html; charset=UTF-8");
            String stringMessageBody = getErrorMessageBody(statusCode);

            StringBuilder builder = new StringBuilder();
            builder.append(protocolVersion).append(" ").append(statusCode).append(" ").append(reasonPhrase.get(statusCode)).append("\n");
            for (String s : headerField) {
                builder.append(s).append("\n");
            }
            builder.append("\n");
            builder.append(stringMessageBody);
            os.write(builder.toString().getBytes());
            os.flush();
        }
    }


    /**
     * エラーメッセージを保持しているメソッド
     *
     * @param statusCode
     * @return エラーの場合のレスポンスメッセージの内容
     */
    private String getErrorMessageBody(int statusCode) {
        String s;
        switch (statusCode) {
            case BAD_REQUEST:
                s = "<html><head><title>400 Bad Request</title></head>" +
                        "<body><h1>Bad Request</h1>" +
                        "<p>Your browser sent a request that this server could not understand.<br /></p></body></html>";
                break;

            case NOT_FOUND:
                s = "<html><head><title>404 Not Found</title></head>" +
                        "<body><h1>Not Found</h1>" +
                        "<p>お探しのページは見つかりませんでした。</p></body></html>";
                break;

            case NOT_IMPLEMENTED:
                s = "<html><head><title>501 Not Implemented</title></head>" +
                        "<body><h1>Not Implemented</h1>" +
                        "<p>Webサーバーでメソッドが実装されていません。</p></body></html>";
                break;

            case HTTP_VERSION_NOT_SUPPORTED:
                s = "<html><head><title>505 HTTP Version Not Supported</title></head>" +
                        "<body><h1>HTTP Version Not Supported</h1></body></html>";
                break;

            default:
                s = "<html><head><title>500 Internal Server Error</title></head>" +
                        "<body><h1>Internal Server Error</h1>" +
                        "<p>サーバー内部のエラーにより表示できません。ごめんなさい。</p></body></html>";
        }
        return s;
    }

    /**
     * プロトコルバージョンの設定をするメソッド
     */
    public void setProtocolVersion(String protocolVersion) {
        if (protocolVersion != null) {
            this.protocolVersion = protocolVersion;
        }
    }

    /**
     * ヘッダーフィールドにヘッダ名とヘッダ値を追加するメソッド
     */
    public void addHeader(String name, String value) {
        if (name != null && value != null) {
            headerField.add(name + HEADER_FIELD_COLON + value);
        }
    }

    String getProtocolVersion() {
        return this.protocolVersion;
    }

    List<String> getHeaderField() {
        return this.headerField;
    }
}
