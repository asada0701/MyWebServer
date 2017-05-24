package jp.co.topgate.asada.web;

import com.google.common.base.Strings;
import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * リクエストメッセージのパースを行うクラス
 *
 * @author asada
 */
public class RequestMessageParser {
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
     * InputStreamをreadした時に改行かのチェックを行う
     */
    private static final int REQUEST_MESSAGE_CARRIAGE_RETURN = 13;

    /**
     * ASCIIコード:10(LF)
     * InputStreamをreadした時に改行かのチェックを行う
     */
    private static final int REQUEST_MESSAGE_LINE_FEED = 10;

    private static final String REQUEST_URI_WELCOME_PAGE = "/";

    /**
     * リクエストメッセージのパースを行うメソッド
     *
     * @param inputStream socketのinputStreamを渡す
     * @return リクエストメッセージのオブジェクトを返す
     */
    public static RequestMessage parse(InputStream inputStream) throws RequestParseException {
        RequestMessage requestMessage;
        String method;
        String uri;
        String protocolVersion;
        Map<String, String> headerField = null;

        BufferedInputStream bis = new BufferedInputStream(inputStream);
        try {
            bis.mark(bis.available());
            String[] requestLineAndHeader = readRequestLineAndHeaderField(bis);

            if (requestLineAndHeader[0] == null || requestLineAndHeader[1] == null) {
                throw new RequestParseException("リクエストラインかヘッダーフィールドに異常があります");
            }

            String[] requestLine = requestLineAndHeader[0].split(REQUEST_LINE_SEPARATOR);
            if (requestLine.length != REQUEST_LINE_NUM_ITEMS) {
                throw new RequestParseException("リクエストラインに異常があります");
            }

            method = requestLine[0];
            String[] uriAndUriQuery = splitUri(requestLine[1]);
            uri = uriAndUriQuery[0];
            protocolVersion = requestLine[2];

            requestMessage = new RequestMessage(method, uri, protocolVersion);

            if (!requestLineAndHeader[1].isEmpty()) {
                headerField = parseHeaderField(requestLineAndHeader[1]);
                requestMessage.setHeaderField(headerField);
            }

            if ("GET".equals(method) && uriAndUriQuery[1] != null) {                //GETでクエリーがあった場合の処理
                Map<String, String> uriQuery = parseUriQuery(uriAndUriQuery[1]);
                requestMessage.setUriQuery(uriQuery);
            }

            if ("POST".equals(method) && headerField != null) {      //メッセージボディの処理
                bis.reset();
                if (!headerField.containsKey("Content-Type") && !headerField.containsKey("Content-Length")) {
                    throw new RequestParseException("Content-TypeかContent-Lengthがリクエストに含まれていません");
                }
                int contentLength;
                try {
                    contentLength = Integer.parseInt(headerField.get("Content-Length"));
                } catch (NumberFormatException e) {
                    throw new RequestParseException("Content-Lengthに数字以外の文字が含まれています");
                }
                byte[] messageBody = readMessageBody(bis, contentLength);
                requestMessage.setMessageBody(messageBody);
            }

        } catch (IOException e) {
            throw new RequestParseException("bufferedInputStream周りでの例外:" + e.getMessage());

        }
        return requestMessage;
    }

    /**
     * InputStreamを読み、Stringの配列で返す
     * [0].リクエストライン
     * [1].ヘッダーフィールド
     *
     * @param inputStream サーバーソケットのInputStream
     * @return Stringの配列で返す
     * @throws IOException inputStreamを読んでいる時に発生する例外
     */
    static String[] readRequestLineAndHeaderField(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Main.CHARACTER_ENCODING_SCHEME));
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
     * @param inputStream   サーバーソケットのInputStream
     * @param contentLength ヘッダーに含まれるContent-Lengthを渡す
     * @return メッセージボディの内容がバイトの配列で返される
     * @throws IOException inputStreamを読んでいる時に発生する例外
     */
    static byte[] readMessageBody(InputStream inputStream, int contentLength) throws IOException {
        byte[] result = new byte[contentLength];

        int index = 0, now, before = 0, moreBefore = 0, moremoreBefore = 0;
        while ((now = inputStream.read()) != -1) {
            if (index == 0 && before == REQUEST_MESSAGE_CARRIAGE_RETURN && now == REQUEST_MESSAGE_LINE_FEED) {    //リクエストラインを読み終わる
                index = 1;
            }
            if (index == 1 && moremoreBefore == REQUEST_MESSAGE_CARRIAGE_RETURN && moreBefore == REQUEST_MESSAGE_LINE_FEED
                    && before == REQUEST_MESSAGE_CARRIAGE_RETURN && now == REQUEST_MESSAGE_LINE_FEED) {           //ヘッダーフィールドを読み終わる

                //メッセージボディを読む
                int i = inputStream.read(result);
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

            if (str[0].endsWith(REQUEST_URI_WELCOME_PAGE)) {
                str[0] = str[0] + Main.WELCOME_PAGE_NAME;
            }
            return str;

        } catch (URISyntaxException e) {
            throw new RequestParseException(e);
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
    static Map<String, String> parseHeaderField(String headerField) {
        Map<String, String> result = new HashMap<>();
        String[] str = headerField.split("\n");
        for (String s : str) {
            String[] header = s.split(HEADER_FIELD_NAME_VALUE_SEPARATOR, HEADER_FIELD_NUM_ITEMS);
            if (header.length < 2) {
                throw new RequestParseException("ヘッダーフィールドに問題があった" + s);
            }
            header[1] = header[1].trim();
            result.put(header[0], header[1]);
        }
        return result;
    }
}
