package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.BindRuntimeException;
import jp.co.topgate.asada.web.model.Message;
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
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException |
                IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {

            throw new IOException("CSVファイル書き出し中に例外が発生しました。CSVファイルの中身を確認してください。");
        } catch (RuntimeException | IOException e) {
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
            //endServerメソッドが呼ばれると、ServerSocket.accept()メソッドで発生する例外
            
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * CSVファイルに投稿された文を書き出すメソッド
     *
     * @throws IOException                        存在しないファイルを編集しようとした場合に発生する
     * @throws NoSuchAlgorithmException           ある暗号アルゴリズムが現在の環境で使用できない場合発生する
     * @throws NoSuchPaddingException             あるパディング・メカニズムが現在の環境で使用できない場合発生する
     * @throws InvalidKeyException                無効な鍵に対する例外
     * @throws IllegalBlockSizeException          提供されたデータの長さが暗号のブロック・サイズと一致しない場合発生する
     * @throws BadPaddingException                データが適切にパディングされない場合に発生する(暗号キーと複合キーが同じかチェックすること
     * @throws InvalidAlgorithmParameterException 無効なアルゴリズム・パラメータの例外
     */
    private void writeCsv() throws IOException, NoSuchPaddingException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
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
