package jp.co.topgate.asada.web;

import com.google.common.base.Strings;
import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
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

    /**
     * ヘッダーフィールドのフィールド名とフィールド値を分割する
     */
    private static final String HEADER_FIELD_NAME_VALUE_SEPARATOR = ":";

    /**
     * ヘッダーフィールドの項目数
     */
    private static final int HEADER_FIELD_NUM_ITEMS = 2;

    /**
     * ASCIIコード:13(CR)
     */
    private static final int CARRIAGE_RETURN = 13;

    /**
     * ASCIIコード:10(LF)
     */
    private static final int LINE_FEED = 10;

    private String method;
    private String uri;
    private Map<String, String> uriQuery = new HashMap<>();
    private String protocolVersion;
    private Map<String, String> headerField = new HashMap<>();
    private byte[] messageBody;

    /**
     * コンストラクタ、リクエストメッセージのパースを行う
     *
     * @param is サーバーソケットのInputStream
     * @throws RequestParseException パースに失敗した場合に投げられる
     */
    public RequestMessage(InputStream is) throws RequestParseException {
        BufferedInputStream bis = new BufferedInputStream(is);
        try {
            bis.mark(bis.available());
            String[] lineAndHeader = readRequestLineAndHeaderField(bis);
            if (lineAndHeader[0] == null || lineAndHeader[1] == null) {
                throw new RequestParseException("リクエストラインかヘッダーフィールドに異常があります");
            }

            String[] requestLine = lineAndHeader[0].split(REQUEST_LINE_SEPARATOR);
            if (requestLine.length != REQUEST_LINE_NUM_ITEMS) {
                throw new RequestParseException("リクエストラインに異常があります");
            }

            method = requestLine[0];
            String[] s = splitUri(requestLine[1]);
            uri = s[0];
            if (s[1] != null) {
                uriQuery = uriQueryParse(s[1]);
            }
            protocolVersion = requestLine[2];

            headerField = headerFieldParse(lineAndHeader[1]);

            if ("POST".equals(method)) {
                bis.reset();
                if (!headerField.containsKey("Content-Type") && !headerField.containsKey("Content-Length")) {
                    throw new RequestParseException("Content-TypeかContent-Lengthがリクエストに含まれていません");
                }
                int contentLength;
                try {
                    contentLength = Integer.parseInt(findHeaderByName("Content-Length"));
                } catch (NumberFormatException e) {
                    throw new RequestParseException("Content-Lengthに数字以外の文字が含まれています");
                }
                messageBody = readMessageBody(bis, contentLength);
            }

        } catch (IOException e) {
            throw new RequestParseException("bufferedInputStream周りでの例外:" + e.getMessage());

        }
    }

    /**
     * InputStreamを読み、ByteArrayOutputStreamの配列で返す
     * [0].リクエストライン
     * [1].ヘッダーフィールド
     *
     * @param is サーバーソケットのInputStream
     * @return Stringの配列で返す
     * @throws IOException inputStreamを読んでいる時に発生する例外
     */
    static String[] readRequestLineAndHeaderField(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String[] result = new String[2];
        result[0] = br.readLine();

        StringBuilder builder = new StringBuilder();
        String str;
        while (!Strings.isNullOrEmpty(str = br.readLine())) {
            builder.append(str).append("\n");
        }
        result[1] = builder.toString();
        return result;
    }

    /**
     * inputStreamからリクエストメッセージボディを読むメソッド
     *
     * @param is            サーバーソケットのInputStream
     * @param contentLength ヘッダーに含まれるContent-Lengthを渡す
     * @return メッセージボディの内容がバイトの配列で返される
     * @throws IOException inputStreamを読んでいる時に発生する例外
     */
    static byte[] readMessageBody(InputStream is, int contentLength) throws IOException {
        byte[] result = new byte[contentLength];

        int index = 0, now, before = 0, moreBefore = 0, moremoreBefore = 0;
        while ((now = is.read()) != -1) {

            if (index == 0 && before == CARRIAGE_RETURN && now == LINE_FEED) {    //リクエストラインを読み終わる
                index = 1;
            }
            if (index == 1 && moremoreBefore == CARRIAGE_RETURN && moreBefore == LINE_FEED
                    && before == CARRIAGE_RETURN && now == LINE_FEED) {           //ヘッダーフィールドを読み終わる

                //メッセージボディを読む
                int i;
                do {
                    i = is.read(result);
                } while (i != contentLength);
                break;
            }
            moremoreBefore = moreBefore;
            moreBefore = before;
            before = now;
        }
        return result;
    }

    /**
     * URIをパスとクエリーに分割するメソッド
     *
     * @param rawUri 分割したいURIを渡す
     * @return Stringの配列を返す
     * @throws RequestParseException リクエストのURIが正しい形式に沿っていない場合発生する
     */
    static String[] splitUri(String rawUri) throws RequestParseException {
        String[] str = new String[URI_QUERY_NUM_ITEMS];
        try {
            URI uri = new URI(rawUri);
            str[0] = uri.getPath();
            str[1] = uri.getQuery();

            if (str[0].endsWith("/")) {
                str[0] = str[0] + "index.html";
            }
            return str;

        } catch (URISyntaxException e) {
            throw new RequestParseException(e.getMessage());
        }
    }

    /**
     * URIのクエリーのパースを行うメソッド
     *
     * @param strUri uriのクエリーの部分を渡す
     * @return URIクエリーのマップを返す(名前, 値)
     * @throws RequestParseException URIのクエリーに問題があった
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
     * ヘッダーフィールドのパースを行うメソッド
     *
     * @param headerField パースしたい文字列
     * @return パースした結果をMapで返す
     */
    static Map<String, String> headerFieldParse(final String headerField) {
        Map<String, String> map = new HashMap<>();
        String[] str = headerField.split("\n");
        for (String s : str) {
            String[] header = s.split(HEADER_FIELD_NAME_VALUE_SEPARATOR, HEADER_FIELD_NUM_ITEMS);
            if (header.length < 2) {
                throw new RequestParseException("ヘッダーフィールドに問題があった" + s);
            }
            header[1] = header[1].trim();
            map.put(header[0], header[1]);
        }
        return map;
    }

    /**
     * リクエストメッセージのメソッドを返す
     *
     * @return HTTPメソッド
     */
    public String getMethod() {
        return method;
    }

    /**
     * リクエストメッセージのURIを返す
     *
     * @return URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * リクエストメッセージにURIをセットする
     *
     * @param uri セットしたい文字列
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * リクエストメッセージのプロトコルバージョンを返す
     *
     * @return プロトコルバージョン
     */
    public String getProtocolVersion() {
        return protocolVersion;
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

    /**
     * ヘッダーフィールドのヘッダ名を元にヘッダ値を返す
     *
     * @param fieldName 探したいヘッダ名
     * @return ヘッダ値を返す。ヘッダーフィールドに含まれていなかった場合はNullを返す
     */
    String findHeaderByName(String fieldName) {
        return headerField.get(fieldName);
    }

    /**
     * メッセージボディを返す
     *
     * @return メッセージボディ
     */
    public byte[] getMessageBody() {
        return messageBody;
    }
}
