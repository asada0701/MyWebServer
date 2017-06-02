package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * リクエストメッセージクラスHTTP/1.1に対応しています。
 * インスタンス化するのはRequestMessageParserのみが行う。{@link RequestMessageParser#parse(InputStream)}
 *
 * @author asada
 */
public class RequestMessage {
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

    private String method = null;
    private String uri = null;
    private Map<String, String> uriQuery = null;
    private Map<String, String> headerField = null;
    private byte[] messageBody = null;

    /**
     * コンストラクタ
     * リクエストメッセージパーサークラスから呼び出される。
     * 注意点
     * uriQuery、headerField、messageBodyはnullが入ってくることがあり、その場合は各find,getメソッドはnullを返すことになる。
     *
     * @param method      リクエストのメソッド
     * @param uri         URI
     * @param uriQuery    URIのクエリー
     * @param headerField ヘッダーフィールド
     * @param messageBody メッセージボディ
     */
    public RequestMessage(String method, String uri, Map<String, String> uriQuery, Map<String, String> headerField, byte[] messageBody) {
        this.method = method;
        this.uri = uri;
        this.uriQuery = uriQuery;
        this.headerField = headerField;
        this.messageBody = messageBody;
    }

    /**
     * コンストラクタ
     * ヘッダーフィールドが空の場合はこちらを使用する
     *
     * @param method   リクエストのメソッド
     * @param uri      URI
     * @param uriQuery URIのクエリー
     */
    public RequestMessage(String method, String uri, Map<String, String> uriQuery) {
        this(method, uri, uriQuery, null, null);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    /**
     * リクエストメッセージURIに含まれていたQuery名を元にQuery値を返す
     *
     * @param name 探したいQuery名
     * @return Query値を返す。URIに含まれていなかった場合はNullを返す
     */
    public String findUriQuery(String name) {
        if (uriQuery == null) {
            return null;
        }
        return uriQuery.get(name);
    }

    /**
     * ヘッダーフィールドのヘッダ名を元にヘッダ値を返す
     * 注意点
     * 送られてきたリクエストにヘッダーフィールドが含まれていなかった場合はnullを返す。
     *
     * @param fieldName 探したいヘッダ名
     * @return ヘッダ値を返す。ヘッダーフィールドに含まれていなかった場合はnullを返す
     */
    public String findHeaderByName(String fieldName) {
        if (headerField == null) {
            return null;
        }
        return headerField.get(fieldName);
    }

    /**
     * メッセージボディをパースするメソッド
     * 注意点
     * リクエストにヘッダーフィールドかメッセージボディが含まれていない場合はnullを返す
     * コンテンツタイプがapplication/x-www-form-urlencodedではない場合もnullを返す
     *
     * @return パースした結果をMapで返す
     * @throws RequestParseException パースした結果不正なリクエストだった
     */
    public Map<String, String> parseMessageBodyToMap() throws RequestParseException {
        if (headerField == null || messageBody == null) {
            return null;
        }
        String contentType = headerField.get("Content-Type");
        if (contentType == null || !contentType.equals("application/x-www-form-urlencoded")) {
            return null;
        }

        String sMessageBody = new String(messageBody);
        try {
            sMessageBody = URLDecoder.decode(sMessageBody, Main.CHARACTER_ENCODING_SCHEME);

        } catch (UnsupportedEncodingException e) {
            throw new RequestParseException(Main.CHARACTER_ENCODING_SCHEME + "でのデコードに失敗");
        }

        Map<String, String> result = new HashMap<>();
        String[] s1 = sMessageBody.split(MESSAGE_BODY_EACH_QUERY_SEPARATOR);
        for (String s : s1) {
            String[] s2 = s.split(MESSAGE_BODY_NAME_VALUE_SEPARATOR);
            if (s2.length == MESSAGE_BODY_NUM_ITEMS) {
                result.put(s2[0], s2[1]);
            } else {
                throw new RequestParseException("リクエストのメッセージボディが不正なものだった:" + s2[0]);
            }
        }
        return result;
    }
}
