package jp.co.topgate.asada.web;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * サーバークラス
 *
 * @author asada
 */
public class Server extends Thread {
    private static final int portNumber = 8080;
    private ServerSocket serverSocket = null;
    private Socket socket = new Socket();
    private HTTPHandler httpHandler = new HTTPHandler();

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
     */
    public void run() {
        try {
            while (true) {
                socket = serverSocket.accept();
                httpHandler.requestComes(socket.getInputStream(), socket.getOutputStream());
                socket.close();
                socket = null;
            }
        } catch (SocketException e) {
            /*
            ソケットが発生する前なので、socket.close()ができないため、例外をだして終了する
            java.net.SocketException: Socket is closed
            at java.net.ServerSocket.accept(ServerSocket.java:509)
            ソケット作成中(accept()メソッド)にサーバーソケットをクローズしたため発生する
            */
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
