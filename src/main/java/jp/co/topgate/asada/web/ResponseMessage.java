package jp.co.topgate.asada.web;

import com.google.common.base.Strings;
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
     * ステータスライン
     */
    private StatusLine sl;

    private OutputStream os;

    /**
     * リソースファイルの場所
     */
    private String filePath;

    /**
     * コンストラクタ
     */
    public ResponseMessage(OutputStream os, StatusLine sl, String filePath) {
        this(os, sl);
        this.filePath = filePath;
    }

    /**
     * コンストラクタ
     *
     * @param os ソケットの出力ストリーム
     * @param sl ステータスライン
     * @throws NullPointerException 引数がnull
     */
    public ResponseMessage(OutputStream os, StatusLine sl) {
        this.os = os;
        this.sl = sl;
    }

    /**
     * リソースファイルを使用したい場合に使用するメソッド
     * レスポンスメッセージを返したいタイミングで呼び出す
     *
     * @return trueの場合レスポンスに成功、falseの場合は失敗
     */
    public boolean returnResponse() {
        try {
            os.write(getResponseLine(protocolVersion, sl).getBytes());
            os.write(getHeader(headerField).getBytes());

            if (sl.equals(StatusLine.OK)) {
                if (Strings.isNullOrEmpty(filePath)) {
                    return false;
                }

                try (InputStream in = new FileInputStream(filePath)) {
                    int num;
                    while ((num = in.read()) != -1) {
                        os.write(num);
                    }
                }
            } else {
                os.write(getErrorMessageBody(sl).getBytes());
            }

            os.flush();
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    /**
     * JSONなど、リソースファイルを使わない場合に使用するメソッド
     * レスポンスメッセージを返したいタイミングで呼び出す
     *
     * @return trueの場合レスポンスに成功、falseの場合は失敗
     */
    public boolean returnResponse(byte[] target) {
        try {
            os.write(getResponseLine(protocolVersion, sl).getBytes());
            os.write(getHeader(headerField).getBytes());

            if (sl.equals(StatusLine.OK)) {
                os.write(target);
            } else {
                os.write(getErrorMessageBody(sl).getBytes());
            }

            os.flush();
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    /**
     * レスポンスラインを生成する
     *
     * @param protocolVersion プロトコルバージョンを渡す
     * @param sl              StatusLineを渡す
     * @return レスポンスラインの文字列が返される
     */
    @NotNull
    static String getResponseLine(String protocolVersion, StatusLine sl) {
        String[] str = {protocolVersion, String.valueOf(sl.getStatusCode()), sl.getReasonPhrase()};
        return String.join(" ", str) + "\n";
    }

    /**
     * ヘッダーを生成する
     *
     * @param list ヘッダーのリストを渡す
     * @return ヘッダーの文字列が返される
     */
    @NotNull
    static String getHeader(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            builder.append(s).append("\n");
        }
        builder.append("\n");
        return builder.toString();
    }

    /**
     * エラーメッセージを保持しているメソッド
     * 引数がnullの場合もHTTPステータスコード:500の文字列を返します
     *
     * @param sl ステータスライン
     * @return エラーの場合のレスポンスメッセージの内容
     */
    public static String getErrorMessageBody(StatusLine sl) {
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
    public void setProtocolVersion(String protocolVersion) {
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

    StatusLine getStatusLine() {
        return this.sl;
    }
}
