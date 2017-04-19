package jp.co.topgate.asada.web;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
class Server extends Thread {
    private static final int portNumber = 8080;
    private ServerSocket serverSocket = null;

    Server() throws IOException {
        serverSocket = new ServerSocket(portNumber);
    }

    void startServer() {
        this.start();
    }

    boolean stopServer() throws IOException {
        serverSocket.close();
        return serverSocket.isClosed();
    }

    boolean endServer() throws IOException {
        return stopServer();
    }

    public void run() {
        try {
            HTTPHandler httpHandler = new HTTPHandler();
            while (true) {
                Socket socket = serverSocket.accept();
                httpHandler.requestComes(socket.getInputStream(), socket.getOutputStream());
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
