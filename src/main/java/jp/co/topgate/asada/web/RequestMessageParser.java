package jp.co.topgate.asada.web;

import com.google.common.base.Strings;
import jp.co.topgate.asada.web.exception.HttpVersionException;
import jp.co.topgate.asada.web.exception.RequestParseException;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTPリクエストのパースを行うクラス。
 * parseメソッドを呼び出すとリクエストメッセージのオブジェクトが返ってくる。
 * ただし、HTTPリクエストのメッセージボディに関しては、RequestMessageクラスの内部でパースを行うこと。
 *
 * @author asada
 */
class RequestMessageParser {
    /**
     * リクエストラインを分割する
     */
    private static final String REQUEST_LINE_SEPARATOR = " ";

    /**
     * リクエスト行の項目数
     */
    private static final int REQUEST_LINE_NUM_ITEMS = 3;

    /**
     * request target absolute-path [ "?" query ]
     */
    private static final String URI_QUERY_SEPARATOR = "\\?";

    /**
     * 複数のクエリーをクエリー毎に分割する
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
     * コンストラクタ
     * インスタンス化禁止
     */
    private RequestMessageParser() {

    }

    /**
     * リクエストメッセージのパースを行うメソッド
     *
     * @param inputStream 読みたいInputStreamを渡す
     * @return リクエストメッセージのオブジェクトを返す
     * @throws RequestParseException リクエストメッセージに問題があった場合に発生する
     * @throws HttpVersionException  リクエストメッセージのプロトコルバージョンがHTTP/1.1以外の場合発生する
     */
    static RequestMessage parse(InputStream inputStream) throws RequestParseException, HttpVersionException {
        String method;
        String uri;
        Map<String, String> uriQuery = null;
        Map<String, String> headerField;
        byte[] messageBody = null;

        try {
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            bis.mark(bis.available());

            //リクエストラインの処理
            String[] requestLine = readRequestLine(bis);
            method = requestLine[0];
            uri = requestLine[1];

            //URIのクエリーの処理
            String[] uriAndQuery = requestLine[1].split(URI_QUERY_SEPARATOR, URI_QUERY_NUM_ITEMS);
            if (uriAndQuery.length == URI_QUERY_NUM_ITEMS) {
                uri = uriAndQuery[0];
                uriQuery = parseUriQuery(uriAndQuery[1]);
            }

            //ヘッダーフィールドの処理
            bis.reset();
            headerField = readHeaderField(bis);
            if (headerField == null) {
                return new RequestMessage(method, uri, uriQuery);
            }

            //メッセージボディの処理
            String sContentLength = headerField.get("Content-Length");
            if (sContentLength != null && NumberUtils.isNumber(sContentLength)) {
                int contentLength;
                try {
                    contentLength = Integer.parseInt(sContentLength);
                } catch (NumberFormatException e) {
                    //TODO ヘッダーフィールドのContent-Length が int の最大値を越えた場合の処理
                    //ここの、NumberFormatExceptionは int の最大値である2147483647を超えた場合に発生する。
                    //サイズが大きいファイルはメモリを消費するので、inputStreamのまま処理するのが正解だと思われる。
                    throw new RequestParseException("POSTで送られきたContent-Length が int の最大値である2147483647を越えた");
                }
                bis.reset();
                int requestLineAndHeaderLength = countRequestLineAndHeaderLength(bis);

                bis.reset();
                messageBody = readMessageBody(bis, requestLineAndHeaderLength, contentLength);
            }
            return new RequestMessage(method, uri, uriQuery, headerField, messageBody);

        } catch (IOException e) {
            throw new RequestParseException(e.getMessage(), e.getCause());
        }
    }

    /**
     * URIのクエリーのパースを行うメソッド
     *
     * @param strUri uriのクエリーの部分を渡す
     * @return URIクエリーのマップを返す(名前, 値)
     * @throws RequestParseException URIのクエリーに問題があった
     */
    static Map<String, String> parseUriQuery(String strUri) throws RequestParseException {
        Map<String, String> uriQuery = new HashMap<>();

        //URIのクエリー部分をクエリー毎に分割し、
        String[] queryList = strUri.split(URI_EACH_QUERY_SEPARATOR);
        for (String query : queryList) {

            //分割したクエリーをnameとvalueに分割する
            String[] nameAndValue = query.split(URI_QUERY_NAME_VALUE_SEPARATOR, URI_QUERY_NUM_ITEMS);
            if (nameAndValue.length == URI_QUERY_NUM_ITEMS) {
                uriQuery.put(nameAndValue[0], nameAndValue[1]);

            } else {
                throw new RequestParseException("URIのクエリーが不正なものだった");
            }
        }
        return uriQuery;
    }

    /**
     * inputStreamからリクエストラインを読み取るメソッド
     * 返り値の配列の添え字は
     * [0].method
     * [1].uri
     * [2].protocolVersion
     * となっています。
     *
     * @param inputStream 読みたいInputStreamを渡す
     * @return stringの配列を返す
     * @throws IOException           inputStreamを読んでいる時に発生する
     * @throws RequestParseException リクエストメッセージが空の場合発生する
     * @throws HttpVersionException  リクエストメッセージのプロトコルバージョンが{@link Main}で定義されているものと異なる
     */
    static String[] readRequestLine(InputStream inputStream) throws IOException, RequestParseException, HttpVersionException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Main.CHARACTER_ENCODING_SCHEME));
        String str = bufferedReader.readLine();
        if (Strings.isNullOrEmpty(str)) {
            throw new RequestParseException("リクエストメッセージが空である");
        }

        String[] requestLine = str.split(REQUEST_LINE_SEPARATOR, REQUEST_LINE_NUM_ITEMS);
        if (requestLine.length != REQUEST_LINE_NUM_ITEMS) {
            throw new RequestParseException("リクエストラインが不正なものである");
        }
        if (!requestLine[2].equals(Main.PROTOCOL_VERSION)) {
            throw new HttpVersionException(requestLine[2]);
        }
        requestLine[1] = URLDecoder.decode(requestLine[1], Main.CHARACTER_ENCODING_SCHEME);
        return requestLine;
    }

    /**
     * inputStreamからヘッダーフィールドを読み取るメソッド
     *
     * @param inputStream 読みたいInputStreamを渡す
     * @return ヘッダーフィールドをMapで返す、ヘッダーフィールドが空の場合はnullを返す
     * @throws IOException           inputStreamを読んでいる時に発生する
     * @throws RequestParseException ヘッダーフィールドに異常があった場合に発生する
     */
    static Map<String, String> readHeaderField(InputStream inputStream) throws IOException, RequestParseException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Main.CHARACTER_ENCODING_SCHEME));

        //一行目はリクエストラインなので無視する
        bufferedReader.readLine();

        Map<String, String> uriQuery = new HashMap<>();
        String str;
        while (!Strings.isNullOrEmpty(str = bufferedReader.readLine())) {

            String[] s2 = str.split(HEADER_FIELD_NAME_VALUE_SEPARATOR, HEADER_FIELD_NUM_ITEMS);
            if (s2.length != HEADER_FIELD_NUM_ITEMS) {
                throw new RequestParseException("ヘッダーフィールドに異常がありました");
            }
            uriQuery.put(s2[0], s2[1].trim());
        }

        if (uriQuery.size() > 0) {
            return uriQuery;
        } else {
            return null;
        }
    }

    /**
     * リクエストラインとヘッダーフィールドのバイト数を計算して返す
     *
     * @param inputStream 読みたいInputStreamを渡す
     * @return リクエストラインとヘッダーフィールドの長さ
     * @throws IOException inputStreamを読んでいる時に発生する例外
     */
    static int countRequestLineAndHeaderLength(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new BufferedReader(new InputStreamReader(inputStream, Main.CHARACTER_ENCODING_SCHEME)));
        StringBuilder builder = new StringBuilder();
        String str;
        while (!Strings.isNullOrEmpty(str = bufferedReader.readLine())) {
            builder.append(str).append("\r\n");
        }
        builder.append("\r\n");
        return builder.toString().length();
    }

    /**
     * inputStreamからリクエストメッセージボディを読むメソッド
     *
     * @param inputStream                読みたいInputStreamを渡す
     * @param requestLineAndHeaderLength リクエストラインとヘッダーフィールドの長さ
     * @param messageBodyLength          ヘッダーに含まれるContent-Lengthを渡す
     * @return メッセージボディの内容がバイトの配列で返される
     * @throws IOException inputStreamを読んでいる時に発生する例外
     */
    static byte[] readMessageBody(InputStream inputStream, int requestLineAndHeaderLength, int messageBodyLength) throws IOException {

        long num = inputStream.skip(requestLineAndHeaderLength);

        byte[] messageBody = new byte[messageBodyLength];
        int i = inputStream.read(messageBody);
        return messageBody;
    }
}
