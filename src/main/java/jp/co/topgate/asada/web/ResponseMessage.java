package jp.co.topgate.asada.web;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * レスポンスメッセージクラス
 * （使用例）
 * handleRequest(RequestMessage request, ResponseMessage response){
 *      response.setHeader("hoge", "hogehoge");
 *      response.writeResponseLienAndHeader(StatusLine.OK);
 *      OutputStream os = response.getOutputStream();
 * }
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
     * writeResponseLineメソッドがまだ呼ばれていない場合はfalse
     * すでに一度呼ばれている場合はtrueになる
     */
    private boolean isUsed = false;

    private List<String> headerField = new ArrayList<>();

    private OutputStream outputStream;

    /**
     * コンストラクタ
     *
     * @param outputStream レスポンスを書き込みたいoutputStreamのオブジェクトを渡す
     */
    public ResponseMessage(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * リクエストラインとヘッダーフィールドを、インスタンスがもつ出力ストリームに書き込むメソッド
     * すでに、このメソッドか引数が異なるwriteResponseLineAndHeaderメソッドを呼び出していた場合は
     * このメソッドの処理は実行されません。
     *
     * @param statusCode   ステータスコードを渡す
     * @param reasonPhrase ステータスコードのテキスト記述
     */
    void writeResponseLineAndHeader(int statusCode, String reasonPhrase) {
        if (isUsed) {
            return;
        }
        isUsed = true;

        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write(createResponseLine(statusCode, reasonPhrase));
        printWriter.write(createHeader(headerField));
        printWriter.flush();
    }

    /**
     * リクエストラインとヘッダーフィールドを、インスタンスがもつ出力ストリームに書き込むメソッド
     * すでに、このメソッドか引数が異なるwriteResponseLineAndHeaderメソッドを呼び出していた場合は、
     * このメソッドの処理は実行されません。
     *
     * @param statusLine ステータスラインを渡す
     */
    void writeResponseLineAndHeader(StatusLine statusLine) {
        if (isUsed) {
            return;
        }
        isUsed = true;

        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write(createResponseLine(statusLine.getStatusCode(), statusLine.getReasonPhrase()));
        printWriter.write(createHeader(headerField));
        printWriter.flush();
    }

    /**
     * ソケットの出力ストリームを返すメソッド
     *
     * @return 出力ストリームを返す
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * ステータスコードとステータスコードのテキスト記述を渡すとレスポンスラインを生成する
     *
     * @param statusCode   ステータスコードを渡す
     * @param reasonPhrase ステータスコードのテキスト記述
     * @return レスポンスラインの文字列が返される
     */
    @NotNull
    static String createResponseLine(int statusCode, String reasonPhrase) {
        String[] str = {Main.PROTOCOL_VERSION, String.valueOf(statusCode), reasonPhrase};
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
        for (String str : headerField) {
            builder.append(str).append("\n");
        }
        builder.append("\n");
        return builder.toString();
    }

    /**
     * エラーメッセージを保持しているメソッド
     * 引数がnullの場合もHTTPステータスコード:500の文字列を返します
     *
     * @param statusLine ステータスライン{@code Nullable}
     * @return エラーの場合のレスポンスメッセージの内容{@code NotNull}
     */
    @NotNull
    @Contract(pure = true)
    public static String getErrorMessageBody(StatusLine statusLine) {
        if (statusLine == null) {
            return "<html><head><title>500 Internal Server Error</title></head>" +
                    "<body><h1>Internal Server Error</h1>" +
                    "<p>サーバー内部のエラーにより表示できません。ごめんなさい。</p></body></html>";
        }
        switch (statusLine) {
            case BAD_REQUEST:
                return "<html><head><title>400 Bad request</title></head>" +
                        "<body><h1>Bad request</h1>" +
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
     * ヘッダーフィールドに追加するメソッド
     *
     * @param name  ヘッダ名
     * @param value ヘッダ値
     */
    public void addHeader(String name, String value) {
        headerField.add(name + HEADER_FIELD_NAME_VALUE_SEPARATOR + value);
    }

    /**
     * ヘッダーフィールドにコンテンツタイプを追加するメソッド
     *
     * @param value コンテンツタイプの値
     */
    public void addHeaderWithContentType(String value) {
        headerField.add("Content-Type" + HEADER_FIELD_NAME_VALUE_SEPARATOR + value);
    }

    /**
     * ヘッダーフィールドにコンテンツレングスを追加するメソッド
     *
     * @param value コンテンツレングスの値
     */
    public void addHeaderWithContentLength(String value) {
        headerField.add("Content-Length" + HEADER_FIELD_NAME_VALUE_SEPARATOR + value);
    }
}
