package jp.co.topgate.asada.web.app;

import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * リクエストのメッセージボディ
 *
 * @author asada
 */
public class RequestMessageBody {

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

    private byte[] messageBody;

    /**
     * コンストラクタ
     *
     * @param messageBody バイトでメッセージボディを渡す
     */
    public RequestMessageBody(byte[] messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * メッセージボディをパースせずにそのまま返すメソッド
     *
     * @return byteの配列で返す
     */
    public byte[] getMessageBody() {
        return this.messageBody;
    }

    /**
     * メッセージボディをパースするメソッド
     *
     * @return パースした結果をMapで返す
     * @throws RequestParseException パースした結果不正なリクエストだった
     */
    Map<String, String> parseToStringMap() throws RequestParseException {
        String messageBody = new String(this.messageBody);
        try {
            messageBody = URLDecoder.decode(messageBody, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            throw new RequestParseException("UTF-8でのデコードに失敗");
        }

        Map<String, String> result = new HashMap<>();
        String[] s1 = messageBody.split(MESSAGE_BODY_EACH_QUERY_SEPARATOR);
        for (String s : s1) {
            String[] s2 = s.split(MESSAGE_BODY_NAME_VALUE_SEPARATOR);
            if (s2.length == MESSAGE_BODY_NUM_ITEMS) {
                result.put(s2[0], s2[1]);
            } else {
                throw new RequestParseException("リクエストのメッセージボディが不正なものだった:" + s2[0]);
            }
        }
        return result;
    }
}
