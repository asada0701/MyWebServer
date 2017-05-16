package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.app.StatusLine;
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
     * @throws IOException          出力ストリームに書き出し中に例外発生
     * @throws NullPointerException 引数がnull
     */
    public ResponseMessage(OutputStream os, StatusLine sl, String filePath) throws IOException, NullPointerException {
        Objects.requireNonNull(os);
        Objects.requireNonNull(sl);
        Objects.requireNonNull(filePath);

        if (sl.equals(StatusLine.OK)) {
            ContentType ct = new ContentType(filePath);
            addHeader("Content-Type", ct.getContentType());

            os.write(getResponseLine(protocolVersion, sl).getBytes());
            os.write(getHeader(headerField).getBytes());

            try (InputStream in = new FileInputStream(new File(filePath))) {
                int num;
                while ((num = in.read()) != -1) {
                    os.write(num);
                }
                os.flush();
            }
        } else {
            addHeader("Content-Type", "text/html; charset=UTF-8");

            os.write(getResponseLine(protocolVersion, sl).getBytes());
            os.write(getHeader(headerField).getBytes());

            os.write(getErrorMessageBody(sl).getBytes());
            os.flush();
        }
    }

    /**
     * レスポンスラインを生成する
     *
     * @param protocolVersion プロトコルバージョンを渡す
     * @param sl              StatusLineを渡す
     * @return レスポンスラインの文字列が返される
     * @throws NullPointerException 引数がnull
     */
    @NotNull
    static String getResponseLine(String protocolVersion, StatusLine sl) throws NullPointerException {
        Objects.requireNonNull(protocolVersion);
        Objects.requireNonNull(sl);

        String[] str = {protocolVersion, String.valueOf(sl.getStatusCode()), sl.getReasonPhrase()};
        return String.join(" ", str) + "\n";
    }

    /**
     * ヘッダーを生成する
     *
     * @param list ヘッダーのリストを渡す
     * @return ヘッダーの文字列が返される
     * @throws NullPointerException 引数がnull
     */
    @NotNull
    static String getHeader(List<String> list) throws NullPointerException {
        Objects.requireNonNull(list);

        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            builder.append(s).append("\n");
        }
        builder.append("\n");
        return builder.toString();
    }

    /**
     * エラーメッセージを保持しているメソッド
     *
     * @param sl ステータスライン
     * @return エラーの場合のレスポンスメッセージの内容
     */
    static String getErrorMessageBody(StatusLine sl) {
        if (sl == null) {
            return "<html><head><title>500 Internal Server Error</title></head>" +
                    "<body><h1>Internal Server Error</h1>" +
                    "<p>サーバー内部のエラーにより表示できません。ごめんなさい。</p></body></html>";
        }
        switch (sl) {
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
     * ヘッダーフィールドにヘッダ名とヘッダ値を追加するメソッド
     *
     * @param name  ヘッダ名
     * @param value ヘッダ値
     */
    void addHeader(String name, String value) {
        if (name != null && value != null) {
            headerField.add(name + HEADER_FIELD_COLON + value);
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
