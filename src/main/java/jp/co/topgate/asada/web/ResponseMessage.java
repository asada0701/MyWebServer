package jp.co.topgate.asada.web;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

/**
 * レスポンスメッセージクラス
 *
 * @author asada
 */
public class ResponseMessage {

    /**
     * リクエストラインを分割する
     */
    private static final String REQUEST_LINE_SEPARATOR = " ";

    /**
     * ヘッダーフィールドの名前と値を分割する
     */
    private static final String HEADER_FIELD_NAME_VALUE_SEPARATOR = ": ";

    /**
     * プロトコルバージョン
     */
    private String protocolVersion = "HTTP/1.1";

    /**
     * ヘッダーフィールド
     */
    private List<String> headerField = new ArrayList<>();

    /**
     * リソースファイルを使用したい場合に使用するメソッド
     * レスポンスメッセージを返したいタイミングで呼び出す
     *
     * @param outputStream ソケットの出力ストリーム
     * @param statusLine   ステータスライン
     * @param filePath     リソースファイルのパスを渡す
     *                     （例）./src/main/resources/index.html
     */
    public void returnResponse(OutputStream outputStream, StatusLine statusLine, String filePath) {
        try {
            outputStream.write(getResponseLine(protocolVersion, statusLine).getBytes());
            outputStream.write(getHeader(headerField).getBytes());

            if (statusLine.equals(StatusLine.OK)) {
                try (InputStream in = new FileInputStream(filePath)) {
                    int num;
                    while ((num = in.read()) != -1) {
                        outputStream.write(num);
                    }
                }
            } else {
                outputStream.write(getErrorMessageBody(statusLine).getBytes());
            }
            outputStream.flush();
        } catch (IOException e) {

        }
    }

    /**
     * JSONなど、リソースファイルを使わない場合に使用するメソッド
     * レスポンスメッセージを返したいタイミングで呼び出す
     *
     * @param outputStream ソケットの出力ストリーム
     * @param statusLine   ステータスライン
     * @param target       byteの配列でレスポンスのメッセージボディを渡す
     */
    void returnResponse(OutputStream outputStream, StatusLine statusLine, byte[] target) {
        try {
            outputStream.write(getResponseLine(protocolVersion, statusLine).getBytes());
            outputStream.write(getHeader(headerField).getBytes());

            if (statusLine.equals(StatusLine.OK)) {
                outputStream.write(target);
            } else {
                outputStream.write(getErrorMessageBody(statusLine).getBytes());
            }
            outputStream.flush();
        } catch (IOException e) {

        }
    }

    /**
     * レスポンスラインを生成する
     *
     * @param protocolVersion プロトコルバージョンを渡す
     * @param statusLine      StatusLineを渡す
     * @return レスポンスラインの文字列が返される
     */
    @NotNull
    static String getResponseLine(String protocolVersion, StatusLine statusLine) {
        String[] str = {protocolVersion, String.valueOf(statusLine.getStatusCode()), statusLine.getReasonPhrase()};
        return String.join(REQUEST_LINE_SEPARATOR, str) + "\n";
    }

    /**
     * ヘッダーを生成する
     *
     * @param headerField ヘッダーのリストを渡す
     * @return ヘッダーフィールドの文字列が返される
     */
    @NotNull
    static String getHeader(List<String> headerField) {
        StringBuilder builder = new StringBuilder();
        for (String s : headerField) {
            builder.append(s).append("\n");
        }
        builder.append("\n");
        return builder.toString();
    }

    /**
     * エラーメッセージを保持しているメソッド
     * 引数がnullの場合もHTTPステータスコード:500の文字列を返します
     *
     * @param statusLine ステータスライン
     * @return エラーの場合のレスポンスメッセージの内容
     */
    static String getErrorMessageBody(StatusLine statusLine) {
        if (statusLine == null) {
            return "<html><head><title>500 Internal Server Error</title></head>" +
                    "<body><h1>Internal Server Error</h1>" +
                    "<p>サーバー内部のエラーにより表示できません。ごめんなさい。</p></body></html>";
        }
        switch (statusLine) {
            case BAD_REQUEST:
                return "<html><head><title>400 Bad Request</title></head>" +
                        "<body><h1>Bad Request</h1>" +
                        "<p>Your browser sent a request that this server could not understand.<br /></p></body></html>";

            case NOT_FOUND:
                return "<html><head><title>404 Not Found</title></head>" +
                        "<body><h1>Not Found</h1>" +
                        "<p>お探しのページは見つかりませんでした。</p></body></html>";

            case INTERNAL_SERVER_ERROR:
                return "<html><head><title>500 Internal Server Error</title></head>" +
                        "<body><h1>Internal Server Error</h1>" +
                        "<p>サーバー内部のエラーにより表示できません。ごめんなさい。</p></body></html>";

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
     *
     * @param protocolVersion プロトコルバージョン
     */
    void setProtocolVersion(String protocolVersion) {
        if (protocolVersion != null) {
            this.protocolVersion = protocolVersion;
        }
    }

    /**
     * ヘッダーフィールドのListに追加するメソッド
     *
     * @param name  ヘッダ名
     * @param value ヘッダ値
     */
    public void addHeader(String name, String value) {
        if (name != null && value != null) {
            headerField.add(name + HEADER_FIELD_NAME_VALUE_SEPARATOR + value);
        }
    }

    //テスト用
    String getProtocolVersion() {
        return this.protocolVersion;
    }

    List<String> getHeaderField() {
        return this.headerField;
    }
}
