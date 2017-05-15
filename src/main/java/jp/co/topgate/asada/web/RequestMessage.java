package jp.co.topgate.asada.web;

import com.google.common.base.Strings;
import jp.co.topgate.asada.web.exception.RequestParseException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

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

    private String method = null;
    private String uri = null;
    private Map<String, String> uriQuery = new HashMap<>();
    private String protocolVersion = null;
    private Map<String, String> headerFieldUri = new HashMap<>();
    private Map<String, String> charMessageBody = new HashMap<>();

    /**
     * コンストラクタ、リクエストメッセージのパースを行う
     *
     * @param is サーバーソケットのInputStream
     * @throws RequestParseException パースに失敗した場合に投げられる
     * @throws NullPointerException  引数がnull
     */
    public RequestMessage(InputStream is) throws RequestParseException, NullPointerException {
        Objects.requireNonNull(is);

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String str = br.readLine();
            if (Strings.isNullOrEmpty(str)) {
                throw new RequestParseException("BufferedReaderの中身がnullもしくは空である");
            }
            String[] requestLine = str.split(REQUEST_LINE_SEPARATOR);
            if (requestLine.length != REQUEST_LINE_NUM_ITEMS) {
                throw new RequestParseException("リクエストラインが不正なものだった:" + str);
            }

            method = requestLine[0];

            String[] s = splitUri(requestLine[1]);
            uri = s[0];
            if (s[1] != null) {
                uriQuery = uriQueryParse(s[1]);
            }

            protocolVersion = requestLine[2];

            if (uri.endsWith("/")) {
                uri = uri + "index.html";
            }

            while (!Strings.isNullOrEmpty(str = br.readLine())) {
                String[] header = str.split(HEADER_FIELD_NAME_VALUE_SEPARATOR, 2);
                if (header.length >= HEADER_FIELD_NUM_ITEMS) {
                    header[1] = header[1].trim();
                    headerFieldUri.put(header[0], header[1]);
                } else {
                    throw new RequestParseException("ヘッダーフィールドが不正なものだった:" + str);
                }
            }

            if ("POST".equals(method)) {
                String contentType = findHeaderByName("Content-Type");
                String contentLength = findHeaderByName("Content-Length");

                if (contentType == null || contentLength == null) {
                    throw new RequestParseException("予期していないContent-Type");
                }

                if ("application/x-www-form-urlencoded".equals(contentType)) {
                    charMessageBody = charMessageBodyParse(br, Integer.parseInt(contentLength));

                } else if ("multipart/form-data".equals(contentType)) {
                    throw new RequestParseException("ファイルアップロード未実装");

                } else if ("application/json".equals(contentType)) {
                    throw new RequestParseException("json未実装");

                } else {
                    throw new RequestParseException(contentType + "は未実装です");
                }
            }
        } catch (NumberFormatException e) {
            throw new RequestParseException("コンテンツレングスが数字ではなかった");

        } catch (IOException e) {
            throw new RequestParseException("BufferedReaderで発生した例外:" + e.toString());
        }
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
     * メッセージボディをパースするメソッド
     *
     * @param br コンストラクタで作成したBufferedReaderのオブジェクト
     * @throws IOException           BufferedReaderから発生した例外
     * @throws RequestParseException リクエストになんらかの異常があった
     * @throws NullPointerException  引数がnull
     */
    private static Map<String, String> charMessageBodyParse(BufferedReader br, int contentLength) throws IOException, RequestParseException, NullPointerException {
        Objects.requireNonNull(br);

        Map<String, String> messageBody = new HashMap<>();
        String str = null;
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
        return messageBody;
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
        return headerFieldUri.get(fieldName);
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
