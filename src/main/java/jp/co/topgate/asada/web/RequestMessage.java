package jp.co.topgate.asada.web;

import com.google.common.base.Strings;
import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * リクエストメッセージクラス
 * HTTP/1.1対応
 * サーバーが受け取ったリクエストをパースして、必要な情報を返す
 *
 * @author asada
 */
class RequestMessage {
    /**
     * ヘッダーフィールドのフィールド名とフィールド値を分割する（その後のスペースは自由のため注意）
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

    private Map<String, String> headerFieldUri = new HashMap<>();
    private Map<String, String> messageBody = new HashMap<>();

    /**
     * コンストラクタ、リクエストメッセージのパースを行う
     *
     * @param bis サーバーソケットのInputStream
     * @throws RequestParseException パースに失敗した場合に投げられる
     */
    RequestMessage(BufferedInputStream bis, RequestLine rl) throws RequestParseException {
        if (bis == null || rl == null) {
            throw new RequestParseException("引数のどちらかがnullだった");
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));
            String str = br.readLine();
            if (str == null) {
                throw new RequestParseException("BufferedReaderのreadLineメソッドの戻り値がnullだった");
            }

            //ヘッダーフィールドの処理
            while (!Strings.isNullOrEmpty(str = br.readLine())) {

                String[] header = str.split(HEADER_FIELD_NAME_VALUE_SEPARATOR);
                if (header.length == HEADER_FIELD_NUM_ITEMS) {
                    header[1] = header[1].trim();
                    headerFieldUri.put(header[0], header[1]);
                } else if (header.length > HEADER_FIELD_NUM_ITEMS) {
                    header[1] = header[1].trim();
                    headerFieldUri.put(header[0], header[1] + HEADER_FIELD_NAME_VALUE_SEPARATOR + header[2]);
                } else {
                    throw new RequestParseException("ヘッダーフィールドが不正なものだった:" + str);
                }
            }

            //POSTの場合のみ、メッセージボディの処理
            if ("POST".equals(rl.getMethod())) {
                messageBodyParse(br);
            }
        } catch (IOException e) {
            throw new RequestParseException("BufferedReaderで発生した例外:" + e.toString());

        }
    }

    /**
     * メッセージボディをパースするメソッド
     *
     * @param br コンストラクタで作成したBufferedReaderのオブジェクト
     * @throws IOException           BufferedReaderから発生した例外
     * @throws RequestParseException リクエストになんらかの異常があった
     */
    private void messageBodyParse(BufferedReader br) throws IOException, RequestParseException {
        if (br == null) {
            throw new RequestParseException("引数BufferedReaderがnullだった");
        }
        String str = null;
        String contentLengthS = findHeaderByName("Content-Length");
        if (contentLengthS != null) {
            //Content-Lengthが含まれている
            int contentLength = Integer.parseInt(contentLengthS);
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
        }
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
    String findMessageBody(String key) {
        return messageBody.get(key);
    }
}
