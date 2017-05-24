package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.program.board.HtmlEditor;
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

    private StatusLine statusLine;

    private String filePath = null;

    private byte[] target = null;

    private HtmlEditor htmlEditor = null;

    /**
     * リソースファイルを使用し、使用したリソースファイルの初期化を行いたい場合に使用するコンストラクタ
     * writeメソッド内でレスポンスメッセージを出力ストリームに書き込んだ後に、初期化が実行される。
     *
     * @param statusLine レスポンスメッセージのステータスライン（状態行）
     * @param filePath   リソースファイルのパスを渡す
     *                   （例）./src/main/resources/index.html
     * @param htmlEditor HtmlEditorのオブジェクトを渡す
     */
    public ResponseMessage(StatusLine statusLine, String filePath, HtmlEditor htmlEditor) {
        this(statusLine, filePath);
        this.htmlEditor = htmlEditor;
    }

    /**
     * JSONなど、リソースファイルを使わない場合に使用するコンストラクタ
     *
     * @param statusLine レスポンスメッセージのステータスライン（状態行）
     * @param target     byteの配列でレスポンスメッセージのメッセージボディを渡す
     */
    public ResponseMessage(StatusLine statusLine, byte[] target) {
        this(statusLine);
        this.target = target;
    }

    /**
     * リソースファイルを使用したい場合に使用するコンストラクタ
     *
     * @param statusLine レスポンスメッセージのステータスライン（状態行）
     * @param filePath   リソースファイルのパスを渡す
     *                   （例）./src/main/resources/index.html
     */
    public ResponseMessage(StatusLine statusLine, String filePath) {
        this(statusLine);
        this.filePath = filePath;
    }

    /**
     * エラーメッセージを送りたい場合など、メッセージボディの内容が必要ない場合に使用するコンストラクタ
     *
     * @param statusLine レスポンスメッセージのステータスライン（状態行）
     */
    public ResponseMessage(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    /**
     * 引数で渡された出力ストリームにレスポンスメッセージを書き出す
     *
     * @param outputStream ソケットの出力ストリーム
     */
    public void write(OutputStream outputStream) {
        try {
            outputStream.write(createResponseLine(protocolVersion, statusLine).getBytes());
            outputStream.write(createHeader(headerField).getBytes());

            if (statusLine.equals(StatusLine.OK) && filePath != null) {
                try (InputStream in = new FileInputStream(filePath)) {
                    int num;
                    while ((num = in.read()) != -1) {
                        outputStream.write(num);
                    }
                }

            } else if (statusLine.equals(StatusLine.OK) && target != null) {
                outputStream.write(target);

            } else {
                outputStream.write(getErrorMessageBody(statusLine).getBytes());
            }

            outputStream.flush();
        } catch (IOException e) {

        } finally {
            if (htmlEditor != null) {
                htmlEditor.resetAllFiles();
            }
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
    static String createResponseLine(String protocolVersion, StatusLine statusLine) {
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
    static String createHeader(List<String> headerField) {
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

    /**
     * ヘッダーフィールドにコンテンツタイプを追加するメソッド
     *
     * @param value コンテンツタイプの値
     */
    public void addHeaderWithContentType(String value) {
        if (value != null) {
            headerField.add("Content-Type" + HEADER_FIELD_NAME_VALUE_SEPARATOR + value);
        }
    }

    //テスト用

    public String getProtocolVersion() {
        return this.protocolVersion;
    }

    public List<String> getHeaderField() {
        return this.headerField;
    }

    public StatusLine getStatusLine() {
        return this.statusLine;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public byte[] getTarget() {
        return this.target;
    }

    public HtmlEditor getHtmlEditor() {
        return this.htmlEditor;
    }
}
