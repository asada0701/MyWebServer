package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

/**
 * リクエストメッセージクラス
 * HTTP/1.1対応
 * サーバーが受け取ったリクエストをパースして、必要な情報を返す
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

    private String method;
    private String uri;
    private Map<String, String> uriQuery = new HashMap<>();
    private String protocolVersion;
    private Map<String, String> headerField = new HashMap<>();
    private byte[] messageBody;

    /**
     * コンストラクタ
     *
     * @param method          リクエストメッセージのメソッドを渡す
     * @param uri             URIを渡す
     * @param protocolVersion プロトコルバージョンを渡す
     * @param headerField     ヘッダーフィールドを渡す
     */
    protected RequestMessage(String method, String uri, String protocolVersion, Map<String, String> headerField) {
        this.method = method;
        this.uri = uri;
        this.protocolVersion = protocolVersion;
        this.headerField = headerField;
    }

    public String getMethod() {
        return method;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    void setUriQuery(Map<String, String> uriQuery) {
        this.uriQuery = uriQuery;
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

    public String getProtocolVersion() {
        return protocolVersion;
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

    void setMessageBody(byte[] messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * メッセージボディをパースするメソッド
     *
     * @return パースした結果をMapで返す
     * @throws RequestParseException パースした結果不正なリクエストだった
     */
    public Map<String, String> parseMessageBodyToMapString() throws RequestParseException {
        String messageBody = new String(this.messageBody);
        try {
            messageBody = URLDecoder.decode(messageBody, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            throw new RequestParseException("UTF-8でのデコードに失敗");
        }

        Map<String, String> result = new HashMap<>();
        String[] s1 = messageBody.split(MESSAGE_BODY_EACH_QUERY_SEPARATOR);
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
