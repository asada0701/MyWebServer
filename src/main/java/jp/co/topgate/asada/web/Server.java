package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.BindRuntimeException;

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
     * @throws IOException
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
     * @return サーバーの停止に成功したか返す
     * @throws IOException
     */
    public boolean stopServer() throws IOException {
        boolean result = false;
        if (socket == null) {
            serverSocket.close();
            result = true;
        }
        return result;
    }

    /**
     * サーバーの緊急停止を行うメソッド、サーバーが通信中でも停止できる
     *
     * @throws IOException
     */
    public void endServer() throws IOException {
        serverSocket.close();
    }

    /**
     * Threadクラスのrunメソッドのオーバーライドメソッド
     * SocketExceptionはserverSocket.accept中にserverSocket.closeメソッドを呼び出すと発生する
     *
     * @throws BindException    ポートが使用中であるが、ローカル要求されたアドレスの割り当てに失敗しました。
     * @throws ConnectException ソケットをリモート・アドレスとポートに接続しようとした際にエラーが発生しました。
     * @throws IOException      ソケットの入出力でエラーが発生しました。
     */
    public void run() {
        HttpHandler httpHandler = new HttpHandler();
        try {
            while (true) {
                socket = serverSocket.accept();
                httpHandler.requestComes(socket.getInputStream(), socket.getOutputStream());
                socket.close();
                socket = null;
            }
        } catch (BindException e) {
            throw new BindRuntimeException();

        } catch (SocketException e) {


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
