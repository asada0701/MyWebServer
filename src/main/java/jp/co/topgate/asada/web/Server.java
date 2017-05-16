package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.app.Handler;
import jp.co.topgate.asada.web.app.HtmlEditor;
import jp.co.topgate.asada.web.app.ProgramBoardHandler;
import jp.co.topgate.asada.web.app.StatusLine;
import jp.co.topgate.asada.web.exception.BindRuntimeException;
import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * サーバークラス
 *
 * @author asada
 */
class Server extends Thread {
    private static final int portNumber = 8080;
    private ServerSocket serverSocket = null;
    private Socket socket = new Socket();

    /**
     * コンストラクタ
     *
     * @throws IOException サーバーソケットでエラーが発生しました
     */
    Server() throws IOException {
        serverSocket = new ServerSocket(portNumber);
    }

    /**
     * サーバーを立ち上げるメソッド
     */
    void startServer() {
        this.start();
    }

    /**
     * サーバーを停止させるメソッド、サーバーが通信中の場合は停止できない
     *
     * @return trueの場合、サーバーの停止に成功
     * @throws IOException サーバーソケットでエラーが発生しました
     */
    boolean stopServer() throws IOException {
        boolean result = false;
        if (socket == null || socket.isClosed()) {
            serverSocket.close();
            result = true;
        }
        return result;
    }

    /**
     * サーバーの緊急停止を行うメソッド、サーバーが通信中でも停止できる
     * サーバーを停止する前に、データを保存する必要がある。
     *
     * @throws IOException サーバーソケットでエラーが発生しました
     */
    void endServer() throws IOException {
        if (socket != null) {
            socket.close();
        }
        serverSocket.close();
    }

    /**
     * Threadクラスのrunメソッドのオーバーライドメソッド
     *
     * @throws BindRuntimeException ポートが使用中であるが、要求されたローカル・アドレスの割り当てに失敗しました
     * @throws RuntimeException     ソケットの入出力でエラーが発生しました
     */
    public void run() {
        try {
            while (true) {
                socket = serverSocket.accept();

                HtmlEditor he = new HtmlEditor();

                try {
                    Handler handler = Handler.getHandler(socket.getInputStream());

                    StatusLine sl = handler.requestComes();

                    handler.returnResponse(socket.getOutputStream(), sl);

                } catch (NullPointerException e) {
                    e.printStackTrace();

                } catch (RequestParseException e) {
                    e.printStackTrace();
                    new ResponseMessage(socket.getOutputStream(), StatusLine.BAD_REQUEST, "");
                }

                socket.close();
                socket = null;

                he.allInitialization();
            }

        } catch (BindException e) {
            throw new BindRuntimeException(e.toString());

        } catch (SocketException e) {
            //e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
