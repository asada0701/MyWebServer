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

    /**
     * ASCIIコード:13(CR)
     */
    private static final int CARRIAGE_RETURN = 13;

    /**
     * ASCIIコード:10(LF)
     */
    private static final int LINE_FEED = 10;

    private String method = null;
    private String uri = null;
    private Map<String, String> uriQuery = new HashMap<>();
    private String protocolVersion = null;
    private Map<String, String> headerField = new HashMap<>();
    private Map<String, String> charMessageBody = new HashMap<>();

    /**
     * コンストラクタ、リクエストメッセージのパースを行う
     *
     * @param is サーバーソケットのInputStream
     * @throws RequestParseException パースに失敗した場合に投げられる
     */
    public RequestMessage(InputStream is) throws RequestParseException {
        if (is == null) {
            throw new RequestParseException("引数がnullだった");
        }
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream[] baos;
        try {
            bis.mark(bis.available());
            baos = readRequestLineHeaderField(bis);

            String[] requestLine = baos[0].toString().trim().split(REQUEST_LINE_SEPARATOR);
            if (requestLine.length != REQUEST_LINE_NUM_ITEMS) {
                throw new RequestParseException("リクエストラインが不正なものだった:" + baos[0].toString());
            }
            String headerFieldStr = baos[1].toString().trim();

            method = requestLine[0];
            String[] s = splitUri(requestLine[1]);
            uri = s[0];
            if (s[1] != null) {
                uriQuery = uriQueryParse(s[1]);
            }
            protocolVersion = requestLine[2];

            headerField = headerFieldParse(headerFieldStr);

            if ("POST".equals(method)) {
                if (!headerField.containsKey("Content-Type") && !headerField.containsKey("Content-Length")) {
                    throw new RequestParseException("Content-TypeかContent-Lengthがリクエストに含まれていません");
                }

                String contentType = findHeaderByName("Content-Type");
                int contentLength = Integer.parseInt(findHeaderByName("Content-Length"));

                bis.reset();
                if ("application/x-www-form-urlencoded".equals(contentType)) {
                    charMessageBody = messageBodyParse(readCharMessageBody(bis, contentLength));

                } else if ("multipart/form-data".equals(contentType)) {
                    throw new RequestParseException("ファイルアップロード未実装");

                } else if ("application/json".equals(contentType)) {
                    throw new RequestParseException("json未実装");

                } else {
                    throw new RequestParseException(contentType + "は未実装です");
                }
            }

        } catch (IOException e) {
            throw new RequestParseException("bufferedInputStream周りでの例外");

        } catch (NumberFormatException e) {
            throw new RequestParseException("Content-Lengthに数字以外の文字が含まれています");
        }

//        System.out.println();
//        System.out.println("リクエストメッセージの確認");
//        System.out.println(method + " " + uri + " " + protocolVersion);
//        for (String str : headerField.keySet()) {
//            System.out.println(str + ": " + headerField.get(str));
//        }
//        for (String s : charMessageBody.keySet()) {
//            System.out.println(s + ": " + charMessageBody.get(s));
//        }
    }

    /**
     * InputStreamを読み、ByteArrayOutputStreamの配列で返す
     * 添え字について
     * [0].リクエストライン
     * [1].ヘッダーフィールド
     * となっています
     *
     * @param is サーバーソケットのInputStream
     * @return ByteArrayOutputSteamの配列で返す
     * @throws IOException          inputStreamを読んでいる時に発生する例外
     * @throws NullPointerException 引数がnull
     */
    static ByteArrayOutputStream[] readRequestLineHeaderField(InputStream is) throws IOException, NullPointerException {
        Objects.requireNonNull(is);

        ByteArrayOutputStream[] baos = new ByteArrayOutputStream[REQUEST_LINE_NUM_ITEMS - 1];
        baos[0] = new ByteArrayOutputStream();
        baos[1] = new ByteArrayOutputStream();

        int index = 0, now, before = 0, moreBefore = 0, moremoreBefore = 0;
        while ((now = is.read()) != -1) {
            baos[index].write(now);

            if (index == 0 && before == CARRIAGE_RETURN && now == LINE_FEED) {    //リクエストラインを読み終わる
                index = 1;
            }
            if (index == 1 && moremoreBefore == CARRIAGE_RETURN && moreBefore == LINE_FEED
                    && before == CARRIAGE_RETURN && now == LINE_FEED) {           //ヘッダーフィールドを読み終わる
                break;
            }
            moremoreBefore = moreBefore;
            moreBefore = before;
            before = now;
        }
        return baos;
    }

    /**
     * 文字列の場合のメッセージボディの取得メソッド
     *
     * @param is            サーバーソケットのInputStream
     * @param contentLength ヘッダーに含まれるContent-Lengthを渡す
     * @return メッセージボディの文字列が返される
     * @throws IOException           inputStreamを読んでいる時に発生する例外
     * @throws NullPointerException  引数がnull
     * @throws RequestParseException メッセージボディが空
     */
    static String readCharMessageBody(InputStream is, int contentLength) throws IOException, NullPointerException, RequestParseException {
        Objects.requireNonNull(is);

        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        while (!Strings.isNullOrEmpty(br.readLine())) ;
        String str = null;
        if (0 < contentLength) {
            char[] c = new char[contentLength];
            int i = br.read(c);
            str = new String(c);
        }
        return str;
    }

    /**
     * URIをパスとクエリーに分割するメソッド
     *
     * @param rawUri 分割したいURIを渡す
     * @return Stringの配列を返す
     * @throws RequestParseException リクエストのURIが正しい形式に沿っていない場合発生する
     * @throws NullPointerException  引数がnull
     */
    static String[] splitUri(String rawUri) throws RequestParseException, NullPointerException {
        Objects.requireNonNull(rawUri);

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
     * @throws RequestParseException クエリーに問題があった場合発生する
     * @throws NullPointerException  引数がnull
     */
    static Map<String, String> uriQueryParse(String strUri) throws RequestParseException, NullPointerException {
        Objects.requireNonNull(strUri);

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
    static Map<String, String> headerFieldParse(String headerField) {
        Map<String, String> map = new HashMap<>();
        String[] str = headerField.split("\n");
        for (String s : str) {
            String[] header = s.split(HEADER_FIELD_NAME_VALUE_SEPARATOR, HEADER_FIELD_NUM_ITEMS);
            header[1] = header[1].trim();
            map.put(header[0], header[1]);
        }
        return map;
    }

    /**
     * メッセージボディをパースするメソッド
     *
     * @param messageBody パースしたい文字列
     * @return パースした結果をMapで返す
     * @throws RequestParseException リクエストになんらかの異常があった
     * @throws NullPointerException  引数がnull
     */
    static Map<String, String> messageBodyParse(String messageBody) throws RequestParseException, NullPointerException {
        Objects.requireNonNull(messageBody);
        try {
            messageBody = URLDecoder.decode(messageBody, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            throw new RequestParseException("UTF-8でのデコードに失敗");
        }

        Map<String, String> result = new HashMap<>();
        String[] s1 = messageBody.split(MESSAGE_BODY_EACH_QUERY_SEPARATOR);
        for (String aS1 : s1) {
            String[] s2 = aS1.split(MESSAGE_BODY_NAME_VALUE_SEPARATOR);
            if (s2.length == MESSAGE_BODY_NUM_ITEMS) {
                result.put(s2[0], s2[1]);
            } else {
                throw new RequestParseException("リクエストのメッセージボディが不正なものだった");
            }
        }
        return result;
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
     * メッセージボディに含まれていたQuery名を元にQuery値を返す
     *
     * @param key 探したいQuery名
     * @return Query値を返す。URIに含まれていなかった場合はNullを返す
     */
    public String findMessageBody(String key) {
        return charMessageBody.get(key);
    }
}
