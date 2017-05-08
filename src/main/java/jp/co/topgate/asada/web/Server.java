package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.BindRuntimeException;
import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

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
     *
     * @throws IOException サーバーソケットでエラーが発生しました
     */
    void endServer() throws IOException {
        try {
            writeCsv();

        } catch (IOException e) {
            //入出力例外

        } catch (Exception e) {
            //IOException以外の例外は暗号(CipherHelperクラスで発生した例外)
        }

        if (socket != null) {
            socket.close();
        }
        serverSocket.close();

        serverSocket = null;
    }

    /**
     * Threadクラスのrunメソッドのオーバーライドメソッド
     * endServerメソッドを呼び出すと、serverSocketが閉じ、nullを参照するようになる
     * 77行目で、serverSocketがnullを参照するので、NullPointerExceptionが発生する
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

                Handler handler = HandlerFactory.getHandler(bis);

                bis.reset();

                handler.requestComes(bis);

                handler.returnResponse(socket.getOutputStream());

                socket.close();
                socket = null;
            }
        } catch (BindException e) {
            throw new BindRuntimeException(e.toString());
        } catch (SocketException e) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * CSVファイルに投稿された文を書き出すメソッド
     *
     * @throws Exception
     */
    private void writeCsv() throws Exception {
        String filePath = "./src/main/resources/data/message.csv";
        List<Message> list = ModelController.getAllMessage();

        File file = new File(filePath);
        if (!file.delete()) {
            throw new IOException("存在しないファイルを編集しようとしました。");
        }

        try (OutputStream os = new FileOutputStream(new File(filePath))) {
            StringBuffer buffer = new StringBuffer();
            for (Message m : list) {
                buffer.append(m.getMessageID()).append(",");

                String original = String.valueOf(m.getMessageID());

                String result = CipherHelper.encrypt(original);

                buffer.append(result);

                buffer.append(",").append(m.getName()).append(",");
                buffer.append(m.getTitle()).append(",").append(m.getText()).append(",").append(m.getDate()).append("\n");
            }
            os.write(buffer.toString().getBytes());
            os.flush();
        }
    }
}
