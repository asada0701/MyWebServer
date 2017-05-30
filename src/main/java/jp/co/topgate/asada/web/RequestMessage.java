package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setUriQuery(Map<String, String> uriQuery) {
        this.uriQuery = uriQuery;
    }

    public void setHeaderField(Map<String, String> headerField) {
        this.headerField = headerField;
    }

    public void setMessageBody(byte[] messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * リクエストメッセージURIに含まれていたQuery名を元にQuery値を返す
     *
     * @param name 探したいQuery名
     * @return Query値を返す。URIに含まれていなかった場合はNullを返す
     */
    public String findUriQuery(String name) {
        return uriQuery.getOrDefault(name, null);
    }

    /**
     * ヘッダーフィールドのヘッダ名を元にヘッダ値を返す
     *
     * @param fieldName 探したいヘッダ名
     * @return ヘッダ値を返す。ヘッダーフィールドに含まれていなかった場合はNullを返す
     */
    public String findHeaderByName(String fieldName) {
        return headerField.get(fieldName);
    }

    /**
     * メッセージボディをパースするメソッド
     * 注意点
     * コンテンツタイプがapplication/x-www-form-urlencodedではない場合(nullの場合も)は、nullを返す
     *
     * @return パースした結果をMapで返す
     * @throws RequestParseException パースした結果不正なリクエストだった
     */
    public Map<String, String> parseMessageBodyToMap() throws RequestParseException {
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
