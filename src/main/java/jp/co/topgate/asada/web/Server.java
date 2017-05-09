package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.BindRuntimeException;
import jp.co.topgate.asada.web.model.ModelController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
        try {
            CsvWriter.write(ModelController.getAllMessage());
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException |
                IllegalBlockSizeException | BadPaddingException | InvalidKeyException | IOException e) {

            throw new IOException("CSVファイル書き出し中に例外が発生しました。CSVファイルの中身を確認してください。");
        } catch (RuntimeException e) {
            throw e;
        }

        if (socket != null) {
            socket.close();
        }
        serverSocket.close();

        serverSocket = null;
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

                BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                bis.mark(bis.available());

                Handler handler = Handler.getHandler(bis);

                bis.reset();

                handler.requestComes(bis);

                handler.returnResponse(socket.getOutputStream());

                socket.close();
                socket = null;
            }
        } catch (BindException e) {
            throw new BindRuntimeException(e.toString());

        } catch (SocketException e) {
            //endServerメソッドが呼ばれると、ServerSocket.accept()メソッドで発生する例外

        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
    }


}
