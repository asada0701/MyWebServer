package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseRuntimeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
class RequestMessage {
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
    private HashMap<String, String> uriQuery = new HashMap<>();
    private String protocolVersion = null;
    private HashMap<String, String> headerFieldUri = new HashMap<>();
    private HashMap<String, String> messageBody = new HashMap<>();

    /**
     * コンストラクタ
     *
     * @param is サーバーソケットのリクエストメッセージ
     */
    RequestMessage(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            String str = br.readLine();
            if (str == null) {
                throw new RequestParseRuntimeException();
            }
            String[] requestLine = str.split(REQUEST_LINE_SPACE);
            if (requestLine.length != REQUEST_LINE_NUM_ITEMS) {
                throw new RequestParseRuntimeException();
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
                    throw new RequestParseRuntimeException();
                }
            }

            if ("POST".equals(getMethod())) {
                while ((str = br.readLine()).equals("")) {
                    String[] s1 = str.split(MESSAGE_BODY_AMPERSAND);
                    for (int i = 0; i < s1.length; i++) {
                        String[] s2 = s1[i].split(MESSAGE_BODY_EQUAL);
                        if (s2.length == MESSAGE_BODY_NUM_ITEMS) {
                            messageBody.put(s2[0], s2[1]);
                        } else {
                            throw new RequestParseRuntimeException();
                        }
                    }
                }
            } else if ("GET".equals(getMethod())) {
                String[] s1 = uri.split(URI_QUESTION_MARK);
                uri = s1[0];
                if (s1.length > 1) {
                    String[] s2 = s1[1].split(URI_QUERY_AMPERSAND);
                    for (int k = 0; k < s2.length; k++) {
                        String[] s3 = s2[k].split(URI_QUERY_EQUAL);
                        if (s3.length == URI_QUERY_NUM_ITEMS) {
                            uriQuery.put(s3[0], s3[1]);
                        } else {
                            throw new RequestParseRuntimeException();
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException();
        }
    }

    String getMethod() {
        return method;
    }

    String getUri() {
        return uri;
    }

    String findUriQuery(String name) {
        String result = null;
        if (name != null) {
            result = uriQuery.get(name);
        }
        return result;
    }

    String getProtocolVersion() {
        return protocolVersion;
    }

    String findHeaderByName(String fieldName) {
        String result = null;
        if (fieldName != null) {
            result = headerFieldUri.get(fieldName);
        }
        return result;
    }

    String findMessageBody(String key) {
        String result;
        if (key != null) {
            result = messageBody.get(key);
        } else {
            result = null;
        }
        return result;
    }

    void setMethod(String method) {
        this.method = method;
    }

    void setUri(String uri) {
        this.uri = uri;
    }

    void setUriQuery(String key, String value) {
        this.uriQuery.put(key, value);
    }

    void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    void setHeaderFieldUri(String key, String value) {
        this.headerFieldUri.put(key, value);
    }

    void setMessageBody(String key, String value) {
        this.messageBody.put(key, value);
    }
}
