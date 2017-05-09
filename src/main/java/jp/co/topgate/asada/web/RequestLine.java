package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * リクエストラインクラス
 *
 * @author asada
 */
class RequestLine {
    /**
     * リクエストラインを分割する
     */
    private static final String REQUEST_LINE_SEPARATOR = " ";

    /**
     * リクエスト行の項目数
     */
    private static final int REQUEST_LINE_NUM_ITEMS = 3;

    /**
     * URIとクエリーを分割する
     */
    private static final String URI_QUERY_SEPARATOR = "\\?";

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
     * コンストラクタ、リクエストメッセージのパースを行う
     *
     * @param bis サーバーソケットのInputStream
     * @throws RequestParseException パースに失敗した場合に投げられる
     */
    RequestLine(BufferedInputStream bis) throws RequestParseException {
        if (bis == null) {
            throw new RequestParseException("引数がnullだった");
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));
            String str = br.readLine();
            if (str == null) {
                throw new RequestParseException("BufferedReaderのreadLineメソッドの戻り値がnullだった");
            }
            String[] requestLine = str.split(REQUEST_LINE_SEPARATOR);
            if (requestLine.length != REQUEST_LINE_NUM_ITEMS) {
                throw new RequestParseException("リクエストラインが不正なものだった:" + str);
            }

            method = requestLine[0];
            uri = URLDecoder.decode(requestLine[1], "UTF-8");
            protocolVersion = requestLine[2];

            if ("GET".equals(method)) {
                try {
                    uriQueryParse();
                } catch (RequestParseException e) {
                    throw e;
                }
            }

            if (uri.endsWith("/")) {
                uri = uri + "index.html";
            }

        } catch (IOException e) {
            throw new RequestParseException("BufferedReaderで発生した例外:" + e.toString());

        }
    }

    /**
     * URIのクエリーのパースを行うメソッド
     *
     * @throws RequestParseException クエリーに問題があった場合発生する
     */
    private void uriQueryParse() throws RequestParseException {
        String[] s1 = uri.split(URI_QUERY_SEPARATOR);
        uri = s1[0];
        if (s1.length > 1) {
            String[] s2 = s1[1].split(URI_EACH_QUERY_SEPARATOR);
            for (String aS2 : s2) {
                String[] s3 = aS2.split(URI_QUERY_NAME_VALUE_SEPARATOR);
                if (s3.length == URI_QUERY_NUM_ITEMS) {
                    uriQuery.put(s3[0], s3[1]);
                } else {
                    throw new RequestParseException("URIのクエリーが不正なものだった");
                }
            }
        }
    }

    /**
     * リクエストメッセージのメソッドを返す
     *
     * @return HTTPメソッドを返す
     */
    String getMethod() {
        return method;
    }

    /**
     * リクエストメッセージのURIを返す
     *
     * @return URIを返す
     */
    String getUri() {
        return uri;
    }

    /**
     * リクエストメッセージURIに含まれていたQuery名を元にQuery値を返す
     *
     * @param name 探したいQuery名
     * @return Query値を返す。URIに含まれていなかった場合はNullを返す
     */
    String findUriQuery(String name) {
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
        if (uri != null) {
            this.uri = uri;
        }
    }
}
