package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * WebAppの処理を行うハンドラークラス
 *
 * @author asada
 */
public class WebAppHandler extends Handler {

    private HtmlEditor he;

    /**
     * リクエストが来たときに呼び出すメソッド
     *
     * @param bis SocketのInputStreamをBufferedInputStreamにラップして渡す
     * @throws RuntimeException データを保存しているCSVファイルに異常が見つかった場合に発生する
     */
    @Override
    public void requestComes(BufferedInputStream bis) throws RuntimeException {
        super.requestComes(bis);

        try {
            if (statusCode == ResponseMessage.OK) {
                if ("POST".equals(requestLine.getMethod())) {
                    editHtml(new RequestMessage(bis, requestLine));

                } else if ("GET".equals(requestLine.getMethod())) {
                    //index.htmlをGETされたときにデータを仕込む
                    he = new HtmlEditor(requestLine);

                    he.contribution(readCsv());

                    new ModelController(readCsv());
                }
            }
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException |
                NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {

            throw new RuntimeException("CSVファイルに異常があります。");

        } catch (IOException e) {
            statusCode = ResponseMessage.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * レスポンスを返すときに呼び出すメソッド
     *
     * @param os SocketのOutputStream
     * @throws RuntimeException データを保存しているCSVファイルに異常が見つかった場合に発生する
     */
    @Override
    public void returnResponse(OutputStream os) throws RuntimeException {
        try {
            String path = "";
            if (requestLine != null) {
                path = Handler.getFilePath(requestLine.getUri());
            }
            new ResponseMessage(os, statusCode, path);

            if (he != null) {
                //htmlファイルの初期化
                he.indexInitialization();
                he.searchInitialization();
                he.deleteInitialization();
            }
            writeCsv();
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException |
                NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {

            throw new RuntimeException("CSVファイルに異常があります。");

        } catch (IOException e) {
            /*
            ソケットにレスポンスを書き出す段階で、例外が出た。
            原因としては、ソケットが閉じてしまった場合などが考えられる。
            レスポンスを返せない例外なので、発生しても無視する。
             */
        }
    }

    /**
     * HTMLを編集するメソッド
     *
     * @param requestMessage リクエストメッセージクラスのオブジェクトを渡す
     */
    private void editHtml(RequestMessage requestMessage) throws IOException {
        String param = requestMessage.findMessageBody("param");
        if (param != null && requestLine.getUri().startsWith("/program/board/")) {
            Message message;
            he = new HtmlEditor(requestLine);

            switch (param) {
                case "contribution":
                    String name = requestMessage.findMessageBody("name");
                    String title = requestMessage.findMessageBody("title");
                    String text = requestMessage.findMessageBody("text");
                    String password = requestMessage.findMessageBody("password");

                    ModelController.addMessage(name, title, text, password);

                    he.contribution(ModelController.getAllMessage());
                    break;

                case "search":
                    //投稿した人で絞り込む
                    //メッセージリストからメッセージオブジェクトを特定して、ユーザーオブジェクトの特定をする
                    requestLine.setUri("/program/board/search.html");

                    ArrayList<Message> al = ModelController.findMessageByID(Integer.parseInt(requestMessage.findMessageBody("number")));
                    he.search(al);
                    break;

                case "delete1":
                    //投稿した文の削除をする
                    //メッセージリストからメッセージオブジェクトを特定する。
                    //delete.htmlにメッセージを書いて渡す。

                    requestLine.setUri("/program/board/delete.html");
                    message = ModelController.findMessage(Integer.parseInt(requestMessage.findMessageBody("number")));
                    he.delete1(message);
                    break;

                case "delete2":
                    //投稿した文の削除をする
                    //メッセージリストからメッセージオブジェクトを特定する。ユーザーオブジェクトの特定をする
                    //パスワードが一致した場合、削除する

                    int num = Integer.parseInt(requestMessage.findMessageBody("number"));

                    message = ModelController.findMessage(num);

                    password = requestMessage.findMessageBody("password");

                    if (message != null && password.equals(message.getPassword())) {
                        requestLine.setUri("/program/board/delete.html");
                        he.delete2(message);
                        ModelController.deleteMessage(message);
                        requestLine.setUri("/program/board/result.html");
                    } else {
                        requestLine.setUri("/program/board/delete.html");
                        message = ModelController.findMessage(Integer.parseInt(requestMessage.findMessageBody("number")));
                        he.delete1(message);
                        System.out.println("パスワードが異なる場合の処理");
                    }

                    break;

                case "back":
                    requestLine.setUri("/program/board/index.html");
                    break;

                default:
                    requestLine.setUri("/program/board/index.html");
            }
        }
    }

    /**
     * CSVファイルから過去の投稿された文を読み出すメソッド
     *
     * @return 過去に投稿された文をメッセージクラスのListに格納して返す
     * @throws IOException 読み出し中の例外
     */
    private List<Message> readCsv() throws IOException, NoSuchPaddingException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String filePath = "./src/main/resources/data/message.csv";
        List<Message> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            String str;
            while ((str = br.readLine()) != null) {

                String[] s = str.split(",");
                if (s.length == 6) {
                    Message message = new Message();
                    message.setMessageID(Integer.parseInt(s[0]));

                    String decryptedResult = CipherHelper.decrypt(s[1]);

                    message.setPassword(decryptedResult);

                    message.setName(s[2]);
                    message.setTitle(s[3]);
                    message.setText(s[4]);
                    message.setDate(s[5]);

                    list.add(message);
                } else {
                    throw new IOException("指定されたCSVが規定の形にそっていないため読み込めません。");
                }
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("CSVファイルが見つかりません。");
        }
        return list;
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

                if (m.getText().contains("\n")) {
                    m.setText(m.getText().replaceAll("\r\n", "<br>"));    //改行文字\nを<br>に変換する
                }
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
