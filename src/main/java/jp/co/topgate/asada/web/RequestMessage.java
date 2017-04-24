package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * リクエストメッセージクラス
 * HTTP/1.1対応
 *
 * @author asada
 */
public class RequestMessage {
    /**
     * リクエストラインのスペース
     */
    private static final String REQUEST_LINE_SPACE = " ";

    /**
     * リクエスト行の項目数
     */
    private static final int REQUEST_LINE_NUM_ITEMS = 3;

    /**
     * URIのクエリー前のクエスチョンマーク
     */
    private static final String URI_QUESTION_MARK = "\\?";

    /**
     * URIのクエリー内のアンパサンド
     */
    private static final String URI_QUERY_AMPERSAND = "&";

    /**
     * URIのクエリーの中のイコール
     */
    private static final String URI_QUERY_EQUAL = "=";

    /**
     * URIのクエリーの項目数
     */
    private static final int URI_QUERY_NUM_ITEMS = 2;

    /**
     * リヘッダーフィールドのコロン（その後のスペースは自由のため注意）
     */
    private static final String HEADER_FIELD_COLON = ":";

    /**
     * ヘッダーフィールドの項目数
     */
    private static final int HEADER_FIELD_NUM_ITEMS = 2;

    /**
     * メッセージボディのアンパサンド
     */
    private static final String MESSAGE_BODY_AMPERSAND = "&";

    /**
     * メッセージボディのイコール
     */
    private static final String MESSAGE_BODY_EQUAL = "=";

    /**
     * メッセージボディの項目数
     */
    private static final int MESSAGE_BODY_NUM_ITEMS = 2;

    private String method = null;
    private String uri = null;
    private Map<String, String> uriQuery = new HashMap<>();
    private String protocolVersion = null;
    private Map<String, String> headerFieldUri = new HashMap<>();
    private Map<String, String> messageBody = new HashMap<>();

    /**
     * コンストラクタ、リクエストメッセージのパースを行う
     *
     * @param is サーバーソケットのInputStream
     * @throws RequestParseException パースに失敗した場合に投げられる
     */
    public RequestMessage(InputStream is) throws RequestParseException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str = br.readLine();
            if (str == null) {
                throw new RequestParseException();
            }
            String[] requestLine = str.split(REQUEST_LINE_SPACE);
            if (requestLine.length != REQUEST_LINE_NUM_ITEMS) {
                throw new RequestParseException();
            }

            method = requestLine[0];
            uri = requestLine[1];
            if (uri.equals("/")) {
                uri = "/index.html";
            }
            protocolVersion = requestLine[2];

            while ((str = br.readLine()) != null && !str.equals("")) {
                String[] header = str.split(HEADER_FIELD_COLON);
                if (header.length == HEADER_FIELD_NUM_ITEMS) {
                    header[1] = header[1].trim();
                    headerFieldUri.put(header[0], header[1]);
                } else if (header.length > HEADER_FIELD_NUM_ITEMS) {
                    header[1] = header[1].trim();
                    headerFieldUri.put(header[0], header[1] + HEADER_FIELD_COLON + header[2]);
                } else {
                    throw new RequestParseException();
                }
            }

            if ("GET".equals(method)) {
                String[] s1 = uri.split(URI_QUESTION_MARK);
                uri = s1[0];
                if (s1.length > 1) {
                    String[] s2 = s1[1].split(URI_QUERY_AMPERSAND);
                    for (String aS2 : s2) {
                        String[] s3 = aS2.split(URI_QUERY_EQUAL);
                        if (s3.length == URI_QUERY_NUM_ITEMS) {
                            uriQuery.put(s3[0], s3[1]);
                        } else {
                            throw new RequestParseException();
                        }
                    }
                }
            } else if ("POST".equals(method)) {
                while ((str = br.readLine()) != null && !str.equals("")) {
                    String[] s1 = str.split(MESSAGE_BODY_AMPERSAND);
                    for (String aS1 : s1) {
                        String[] s2 = aS1.split(MESSAGE_BODY_EQUAL);
                        if (s2.length == MESSAGE_BODY_NUM_ITEMS) {
                            messageBody.put(s2[0], s2[1]);
                        } else {
                            throw new RequestParseException();
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RequestParseException();
        }
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
     * @param name 　探したいQuery名
     * @return Query値を返す。URIに含まれていなかった場合はNullを返す
     */
    public String findUriQuery(String name) {
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
        if (fieldName != null) {
            return headerFieldUri.get(fieldName);
        } else {
            return null;
        }
    }

    /**
     * メッセージボディに含まれていたQuery名を元にQuery値を返す
     *
     * @param key 探したいQuery名
     * @return Query値を返す。URIに含まれていなかった場合はNullを返す
     */
    public String findMessageBody(String key) {
        if (key != null) {
            return messageBody.get(key);
        } else {
            return null;
        }
    }
}
