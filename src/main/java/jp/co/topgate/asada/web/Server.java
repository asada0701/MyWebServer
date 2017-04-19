package jp.co.topgate.asada.web;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
class Server extends Thread {
    private static final int portNumber = 8080;
    private ServerSocket serverSocket = null;
    private Socket socket = new Socket();
    private HTTPHandler httpHandler = new HTTPHandler();

    Server() throws IOException {
        serverSocket = new ServerSocket(portNumber);
    }

    void startServer() {
        this.start();
    }

    boolean stopServer() throws IOException {
        boolean result = false;
        if (socket == null) {
            serverSocket.close();
            result = true;
        }
        return result;
    }

    boolean endServer() throws IOException {
        return stopServer();
    }

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
            e.printStackTrace();
        }
    }
}
