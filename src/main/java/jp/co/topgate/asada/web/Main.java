package jp.co.topgate.asada.web;

import java.io.IOException;

/**
 * クライアントクラス
 *
 * @author asada
 */
public class Main {

    /**
     * サーバーのポート番号
     */
    private static final int PORT_NUMBER = 8080;

    /**
     * サーバーが扱う文字符号化スキーム
     */
    public static final String CHARACTER_ENCODING_SCHEME = "UTF-8";

    /**
     * サーバーのウェルカムページのファイル名
     */
    public static final String WELCOME_PAGE_NAME = "index.html";

    /**
     * サーバーが対応しているプロトコルバージョン
     */
    public static final String PROTOCOL_VERSION = "HTTP/1.1";

    public static void main(String[] args) {
        System.out.println("this server's port number is " + PORT_NUMBER);
        try {
            System.out.println("start up http server..");
            Server.run(PORT_NUMBER);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}

