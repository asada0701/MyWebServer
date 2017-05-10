package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.BindRuntimeException;
import jp.co.topgate.asada.web.exception.CipherRuntimeException;
import jp.co.topgate.asada.web.exception.CsvRuntimeException;
import jp.co.topgate.asada.web.model.ModelController;

import java.io.*;
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
     *
     * @throws CsvRuntimeException    CSVファイルの読み込み中か、読み込む段階で例外が発生した
     * @throws CipherRuntimeException 読み込んだデータの複合に失敗した
     */
    void startServer() throws CsvRuntimeException, CipherRuntimeException {
        ModelController.setMessageList(CsvWriter.read(CsvMode.MESSAGE_MODE));   //CSVファイル読み込み

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
     * @throws IOException            サーバーソケットでエラーが発生しました
     * @throws CsvRuntimeException    CSVファイルの読み込み中か、読み込む段階で例外が発生した
     * @throws CipherRuntimeException 読み込んだデータの復号に失敗した
     */
    void endServer() throws IOException, CsvRuntimeException, CipherRuntimeException {
        if (socket != null) {
            socket.close();
        }
        serverSocket.close();

        CsvWriter.write(CsvMode.MESSAGE_MODE, ModelController.getAllMessage());   //CSVファイルに書き込み
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

                BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                bis.mark(bis.available());

                Handler handler = Handler.getHandler(bis);

                bis.reset();

                handler.requestComes(bis);

                handler.returnResponse(socket.getOutputStream());

                socket.close();
                socket = null;

                he.allInitialization();
            }

        } catch (BindException e) {
            throw new BindRuntimeException(e.toString());

        } catch (SocketException e) {

        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
