package jp.co.topgate.asada.web;

import com.google.common.base.Strings;
import jp.co.topgate.asada.web.exception.RequestParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * リクエストメッセージクラス
 * HTTP/1.1対応
 * サーバーが受け取ったリクエストをパースして、必要な情報を返す
 *
 * @author asada
 */
public class RequestMessage {
    /**
     * リクエストラインを分割する
     */
    private static final String REQUEST_LINE_SEPARATOR = " ";

    /**
     * リクエスト行の項目数
     */
    private static final int REQUEST_LINE_NUM_ITEMS = 3;

    /**
     * URIのクエリーをクエリー毎に分割する
     */
    private static final String URI_EACH_QUERY_SEPARATOR = "&";

    /**
     * クエリー名とクエリー値を分割する
     */
    private static final String URI_QUERY_NAME_VALUE_SEPARATOR = "=";

    /**
     * URIのクエリーの項目数
     */
    private static final int URI_QUERY_NUM_ITEMS = 2;

    private String method = null;
    private String uri = null;
    private Map<String, String> uriQuery = new HashMap<>();
    private String protocolVersion = null;

    /**
     * ヘッダーフィールドのフィールド名とフィールド値を分割する（その後のスペースは自由のため注意）
     */
    private static final String HEADER_FIELD_NAME_VALUE_SEPARATOR = ":";

    /**
     * ヘッダーフィールドの項目数
     */
    private static final int HEADER_FIELD_NUM_ITEMS = 2;

    /**
     * メッセージボディのクエリーをクエリー毎に分割する
     */
    private static final String MESSAGE_BODY_EACH_QUERY_SEPARATOR = "&";

    /**
     * メッセージボディのイコール
     */
    private static final String MESSAGE_BODY_NAME_VALUE_SEPARATOR = "=";

    /**
     * メッセージボディの項目数
     */
    private static final int MESSAGE_BODY_NUM_ITEMS = 2;

    private Map<String, String> headerFieldUri = new HashMap<>();
    private Map<String, String> messageBody = new HashMap<>();

    /**
     * コンストラクタ、リクエストメッセージのパースを行う
     *
     * @param bis サーバーソケットのInputStream
     * @throws RequestParseException パースに失敗した場合に投げられる
     */
    public RequestMessage(@NotNull BufferedInputStream bis) throws RequestParseException {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));
            String str = br.readLine();
            if (Strings.isNullOrEmpty(str)) {
                throw new RequestParseException("BufferedReaderの中身がnullもしくは空である");
            }
            String[] requestLine = str.split(REQUEST_LINE_SEPARATOR);
            if (requestLine.length != REQUEST_LINE_NUM_ITEMS) {
                throw new RequestParseException("リクエストラインが不正なものだった:" + str);
            }

            method = requestLine[0];

            String[] s = uriSeparator(requestLine[1]);
            uri = s[0];
            if (s[1] != null) {
                uriQuery = uriQueryParse(s[1]);
            }

            protocolVersion = requestLine[2];

            if (uri.endsWith("/")) {
                uri = uri + "index.html";
            }

        } catch (IOException e) {
            throw new RequestParseException("BufferedReaderで発生した例外:" + e.toString());

        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));
            String str = br.readLine();
            if (str == null) {
                throw new RequestParseException("BufferedReaderのreadLineメソッドの戻り値がnullだった");
            }

            while (!Strings.isNullOrEmpty(str = br.readLine())) {

                String[] header = str.split(HEADER_FIELD_NAME_VALUE_SEPARATOR);
                if (header.length == HEADER_FIELD_NUM_ITEMS) {
                    header[1] = header[1].trim();
                    headerFieldUri.put(header[0], header[1]);
                } else if (header.length > HEADER_FIELD_NUM_ITEMS) {
                    header[1] = header[1].trim();
                    headerFieldUri.put(header[0], header[1] + HEADER_FIELD_NAME_VALUE_SEPARATOR + header[2]);
                } else {
                    throw new RequestParseException("ヘッダーフィールドが不正なものだった:" + str);
                }
            }

            if ("POST".equals(method)) {
                messageBodyParse(br);
            }
        } catch (IOException e) {
            throw new RequestParseException("BufferedReaderで発生した例外:" + e.toString());

        }
    }

    /**
     * メッセージボディをパースするメソッド
     *
     * @param br コンストラクタで作成したBufferedReaderのオブジェクト
     * @throws IOException           BufferedReaderから発生した例外
     * @throws RequestParseException リクエストになんらかの異常があった
     */
    private void messageBodyParse(BufferedReader br) throws IOException, RequestParseException {
        if (br == null) {
            throw new RequestParseException("引数BufferedReaderがnullだった");
        }
        String contentLengthS = findHeaderByName("Content-Length");
        if (contentLengthS != null) {
            String str = null;
            int contentLength = Integer.parseInt(contentLengthS);
            if (0 < contentLength) {
                char[] c = new char[contentLength];
                int i = br.read(c);
                str = new String(c);
            }
            if (str == null) {
                throw new RequestParseException("POSTなのにメッセージボディが空だった");
            }
            str = URLDecoder.decode(str, "UTF-8");
            String[] s1 = str.split(MESSAGE_BODY_EACH_QUERY_SEPARATOR);
            for (String aS1 : s1) {
                String[] s2 = aS1.split(MESSAGE_BODY_NAME_VALUE_SEPARATOR);
                if (s2.length == MESSAGE_BODY_NUM_ITEMS) {
                    messageBody.put(s2[0], s2[1]);
                } else {
                    throw new RequestParseException("リクエストのメッセージボディが不正なものだった:" + str);
                }
            }
        } else {
            throw new RequestParseException("ヘッダーフィールドにContent-Lengthの項目が存在しなかった");
        }
    }

    /**
     * ヘッダーフィールドのヘッダ名を元にヘッダ値を返す
     *
     * @param fieldName 探したいヘッダ名
     * @return ヘッダ値を返す。ヘッダーフィールドに含まれていなかった場合はNullを返す
     */
    public String findHeaderByName(String fieldName) {
        return headerFieldUri.get(fieldName);
    }

    /**
     * メッセージボディに含まれていたQuery名を元にQuery値を返す
     *
     * @param key 探したいQuery名
     * @return Query値を返す。URIに含まれていなかった場合はNullを返す
     */
    public String findMessageBody(String key) {
        return messageBody.get(key);
    }

    /**
     * URIをパスとクエリーに分割するメソッド
     *
     * @param rawUri 分割したいURIを渡す
     * @return Stringの配列を返す
     * @throws RequestParseException リクエストのURIが正しい形式に沿っていない場合発生する
     */
    static String[] uriSeparator(String rawUri) throws RequestParseException {
        String[] str = new String[URI_QUERY_NUM_ITEMS];
        try {
            URI uri = new URI(rawUri);
            str[0] = uri.getPath();
            str[1] = uri.getQuery();

            return str;
        } catch (URISyntaxException e) {
            throw new RequestParseException(e.getMessage());
        }
    }

    /**
     * URIのクエリーのパースを行うメソッド
     *
     * @throws RequestParseException クエリーに問題があった場合発生する
     */
    static Map<String, String> uriQueryParse(String strUri) throws RequestParseException {
        Map<String, String> uriQuery = new HashMap<>();
        String[] s = strUri.split(URI_EACH_QUERY_SEPARATOR);
        for (String s2 : s) {
            String[] s3 = s2.split(URI_QUERY_NAME_VALUE_SEPARATOR);
            if (s3.length == 2) {
                uriQuery.put(s3[0], s3[1]);
            } else {
                throw new RequestParseException("URIのクエリーが不正なものだった");
            }
        }
        return uriQuery;
    }

    /**
     * リクエストメッセージのメソッドを返す
     *
     * @return HTTPメソッドを返す
     */
    public String getMethod() {
        return method;
    }

    /**
     * リクエストメッセージのURIを返す
     *
     * @return URIを返す
     */
    public String getUri() {
        return uri;
    }

    /**
     * リクエストメッセージURIに含まれていたQuery名を元にQuery値を返す
     *
     * @param name 探したいQuery名
     * @return Query値を返す。URIに含まれていなかった場合はNullを返す
     */
    String findUriQuery(@Nullable String name) {
        if (name != null) {
            return uriQuery.get(name);
        } else {
            return null;
        }
    }

    /**
     * リクエストメッセージのプロトコルバージョンを返す
     *
     * @return プロトコルバージョンを返す
     */
    String getProtocolVersion() {
        return protocolVersion;
    }

    void setUri(String uri) {
        this.uri = uri;
    }
}
