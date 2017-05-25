package jp.co.topgate.asada.web;

import java.io.InputStream;
import java.util.*;

/**
 * リクエストメッセージクラスHTTP/1.1に対応しています。
 * インスタンス化するのはRequestMessageParserのみが行う。{@link RequestMessageParser#parse(InputStream)}
 *
 * @author asada
 */
public class RequestMessage {
    private String method;
    private String uri;
    private Map<String, String> uriQuery = null;
    private String protocolVersion;
    private Map<String, String> headerField = null;
    private byte[] messageBody = null;

    /**
     * コンストラクタ
     *
     * @param method          リクエストメッセージのメソッドを渡す
     * @param uri             URIを渡す
     * @param protocolVersion プロトコルバージョンを渡す
     */
    RequestMessage(String method, String uri, String protocolVersion) {
        this.method = method;
        this.uri = uri;
        this.protocolVersion = protocolVersion;
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

    public String getProtocolVersion() {
        return protocolVersion;
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
    String findUriQuery(String name) {
        return uriQuery.getOrDefault(name, null);
    }

    void setHeaderField(Map<String, String> headerField) {
        this.headerField = headerField;
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

    public byte[] getMessageBody() {
        return messageBody;
    }
}
