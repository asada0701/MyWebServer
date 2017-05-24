package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.HtmlInitializeException;
import jp.co.topgate.asada.web.exception.RequestParseException;
import jp.co.topgate.asada.web.exception.SocketRuntimeException;

import java.io.*;
import java.net.*;

/**
 * サーバークラス
 *
 * @author asada
 */
class Server extends Thread {
    private ServerSocket serverSocket = null;
    private Socket clientSocket = new Socket();

    /**
     * コンストラクタ
     *
     * @throws IOException サーバーソケットでエラーが発生しました
     */
    Server(int portNumber) throws IOException {
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
        if (clientSocket == null) {
            serverSocket.close();
            return true;
        }
        return false;
    }

    /**
     * サーバーの緊急停止を行うメソッド、サーバーが通信中でも停止できる
     * サーバーを停止する前に、データを保存する必要がある。
     *
     * @throws IOException サーバーソケットでエラーが発生しました
     */
    void endServer() throws IOException {
        if (clientSocket != null) {
            clientSocket.close();
        }
        serverSocket.close();
    }

    /**
     * Threadクラスのrunメソッドのオーバーライドメソッド
     *
     * @throws RuntimeException ソケットの入出力でエラーが発生しました
     */
    public void run() {
        try {
            while (true) {
                clientSocket = serverSocket.accept();
                try {
                    RequestMessage requestMessage = RequestMessageParser.parse(clientSocket.getInputStream());

                    Handler handler = Handler.getHandler(requestMessage);

                    handler.handleRequest(clientSocket.getOutputStream());

                } catch (RequestParseException e) {                 //リクエストメッセージに問題があった場合の例外処理
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.addHeaderWithContentType(ContentType.errorResponseContentType);
                    responseMessage.writeToOutputStream(clientSocket.getOutputStream(), StatusLine.BAD_REQUEST, "");

                } catch (HtmlInitializeException e) {               //HTMLファイルに問題が発生した場合の例外処理
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.addHeaderWithContentType(ContentType.errorResponseContentType);
                    responseMessage.writeToOutputStream(clientSocket.getOutputStream(), StatusLine.INTERNAL_SERVER_ERROR, "");
                    throw new SocketRuntimeException(e.getMessage(), e.getCause());
                }

                clientSocket.close();
                clientSocket = null;
            }

        } catch (BindException e) {
            throw new SocketRuntimeException(e.getMessage(), e.getCause());

        } catch (SocketException e) {


        } catch (IOException e) {
            throw new SocketRuntimeException(e.getMessage(), e.getCause());
        }
    }
}
