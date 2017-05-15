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
     * プロトコルバージョン
     */
    private String protocolVersion = "HTTP/1.1";

    /**
     * ヘッダーフィールド
     */
    private List<String> headerField = new ArrayList<>();

    /**
     * コンストラクタ
     * returnResponseメソッドを呼び出し、レスポンスメッセージを書き出す
     *
     * @param os       ソケットの出力ストリーム
     * @param sl       ステータスライン
     * @param filePath リソースファイルのパス
     */
    public ResponseMessage(OutputStream os, StatusLine sl, String filePath) throws IOException {
        if (os == null || filePath == null) {
            throw new IOException();
        }
        StringBuilder builder = new StringBuilder();

        builder.append(protocolVersion).append(" ").append(sl.getStatusCode()).append(" ").append(sl.getReasonPhrase());
        builder.append("\n");

        if (sl.equals(StatusLine.OK)) {
            try {
                ContentType ct = new ContentType(filePath);
                addHeader("Content-Type", ct.getContentType());

            } catch (IllegalArgumentException e) {
                addHeader("Content-Type", ContentType.defaultFileType);
            }

        } else {
            addHeader("Content-Type", "text/html; charset=UTF-8");
        }

        for (String s : headerField) {
            builder.append(s).append("\n");
        }
        builder.append("\n");

        os.write(builder.toString().getBytes());

        if (sl.equals(StatusLine.OK)) {
            try (InputStream in = new FileInputStream(new File(filePath))) {
                int num;
                while ((num = in.read()) != -1) {
                    os.write(num);
                }
                os.flush();
            }
        } else {
            os.write(getErrorMessageBody(sl).getBytes());
            os.flush();
        }
    }

    /**
     * エラーメッセージを保持しているメソッド
     *
     * @param sl ステータスライン
     * @return エラーの場合のレスポンスメッセージの内容
     */
    private String getErrorMessageBody(StatusLine sl) {
        switch (sl) {
            case BAD_REQUEST:
                return "<html><head><title>400 Bad Request</title></head>" +
                        "<body><h1>Bad Request</h1>" +
                        "<p>Your browser sent a request that this server could not understand.<br /></p></body></html>";

            case NOT_FOUND:
                return "<html><head><title>404 Not Found</title></head>" +
                        "<body><h1>Not Found</h1>" +
                        "<p>お探しのページは見つかりませんでした。</p></body></html>";

            case NOT_IMPLEMENTED:
                return "<html><head><title>501 Not Implemented</title></head>" +
                        "<body><h1>Not Implemented</h1>" +
                        "<p>Webサーバーでメソッドが実装されていません。</p></body></html>";

            case HTTP_VERSION_NOT_SUPPORTED:
                return "<html><head><title>505 HTTP Version Not Supported</title></head>" +
                        "<body><h1>HTTP Version Not Supported</h1></body></html>";

            default:
                return "<html><head><title>500 Internal Server Error</title></head>" +
                        "<body><h1>Internal Server Error</h1>" +
                        "<p>サーバー内部のエラーにより表示できません。ごめんなさい。</p></body></html>";
        }
    }

    /**
     * プロトコルバージョンの設定をするメソッド
     */
    void setProtocolVersion(String protocolVersion) {
        if (protocolVersion != null) {
            this.protocolVersion = protocolVersion;
        }
    }

    /**
     * ヘッダーフィールドにヘッダ名とヘッダ値を追加するメソッド
     */
    void addHeader(String name, String value) {
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
