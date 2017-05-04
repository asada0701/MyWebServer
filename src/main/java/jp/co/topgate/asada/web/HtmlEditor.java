package jp.co.topgate.asada.web;

import java.io.IOException;

/**
 * Created by yusuke-pc on 2017/05/04.
 */
public interface HtmlEditor {
    /**
     * HTMLファイルの編集を行うメソッド
     *
     * @param requestMessage リクエストメッセージクラスを渡す
     * @throws IOException HTMLファイルの編集に発生した例外を投げる
     */
    void editHtml(RequestMessage requestMessage) throws IOException;
}
