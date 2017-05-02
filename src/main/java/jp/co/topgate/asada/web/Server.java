package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.BindRuntimeException;
import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.IOException;
import java.net.*;

/**
 * サーバークラス
 *
 * @author asada
 */
public class Server extends Thread {
    private static final int portNumber = 8080;
    private ServerSocket serverSocket = null;
    private Socket socket = new Socket();

    /**
     * コンストラクタ
     *
     * @throws IOException サーバーソケットでエラーが発生しました
     */
    public Server() throws IOException {
        serverSocket = new ServerSocket(portNumber);
    }

    /**
     * サーバーを立ち上げるメソッド
     */
    public void startServer() {
        this.start();
    }

    /**
     * サーバーを停止させるメソッド、サーバーが通信中の場合は停止できない
     *
     * @return trueの場合、サーバーの停止に成功
     * @throws IOException サーバーソケットでエラーが発生しました
     */
    public boolean stopServer() throws IOException {
        boolean result = false;
        if (socket == null || socket.isClosed()) {
            serverSocket.close();
            result = true;
        }
        return result;
    }

    /**
     * サーバーの緊急停止を行うメソッド、サーバーが通信中でも停止できる
     *
     * @throws IOException サーバーソケットでエラーが発生しました
     */
    public void endServer() throws IOException {
        if (socket != null) {
            socket.close();
        }
        serverSocket.close();
    }

    /**
     * Threadクラスのrunメソッドのオーバーライドメソッド
     * SocketExceptionはserverSocket.accept中にserverSocket.closeメソッドを呼び出すと発生するのでここで消す
     *
     * @throws BindRuntimeException ポートが使用中であるが、要求されたローカル・アドレスの割り当てに失敗しました
     * @throws RuntimeException     ソケットの入出力でエラーが発生しました
     */
    public void run() {
        try {
            while (true) {
                socket = serverSocket.accept();
                try {
                    //リクエストメッセージの問題がなかった処理
                    RequestMessage requestMessage = new RequestMessage(socket.getInputStream());
                    HttpHandlerFactory.getHttpHandler(requestMessage.getUri());
                } catch (RequestParseException e) {
                    //リクエストメッセージに問題があった=400
                    HttpHandlerFactory.getHttpHandler("");
                }
                socket.close();
            }
        } catch (BindException e) {
            throw new BindRuntimeException(e.toString());

        } catch (SocketException e) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
