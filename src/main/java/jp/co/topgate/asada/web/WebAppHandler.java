package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;

import java.io.*;
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
     */
    @Override
    public void requestComes(BufferedInputStream bis) {
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
        } catch (IOException e) {
            statusCode = ResponseMessage.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * レスポンスを返すときに呼び出すメソッド
     *
     * @param os SocketのOutputStream
     */
    @Override
    public void returnResponse(OutputStream os) {
        try {
            String path = "";
            if (requestLine != null) {
                path = HandlerFactory.getFilePath(requestLine.getUri());
            }
            new ResponseMessage(os, statusCode, path);

            if (he != null) {
                //htmlファイルの初期化
                he.indexInitialization();
                he.searchInitialization();
                he.deleteInitialization();
            }

            writeCsv();

        } catch (IOException e) {

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

                    message = ModelController.findMessage(Integer.parseInt(requestMessage.findMessageBody("number")));

                    if (message != null) {
                        requestLine.setUri("/program/board/delete.html");
                        he.delete2(message);
                        ModelController.deleteMessage(message);
                        requestLine.setUri("/program/board/result.html");
                    } else {
                        requestLine.setUri("/program/board/delete.html");
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

    private List<Message> readCsv() throws IOException {
        String filePath = "./src/main/resources/data/message.csv";
        List<Message> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            String str;
            while ((str = br.readLine()) != null) {

                String[] s = str.split(",");
                if (s.length == 6) {
                    Message message = new Message();
                    message.setMessageID(Integer.parseInt(s[0]));
                    message.setPassword(s[1]);
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

    private void writeCsv() throws IOException {
        String filePath = "./src/main/resources/data/message.csv";
        List<Message> list = ModelController.getAllMessage();

        File file = new File(filePath);
        if (!file.delete()) {
            throw new IOException("存在しないファイルを編集しようとしました。");
        }

        try (OutputStream os = new FileOutputStream(new File(filePath))) {
            StringBuilder builder = new StringBuilder();
            for (Message m : list) {
                builder.append(m.getMessageID()).append(",").append(m.getPassword()).append(",").append(m.getName()).append(",");
                builder.append(m.getTitle()).append(",").append(m.getText()).append(",").append(m.getDate()).append("\n");
            }
            os.write(builder.toString().getBytes());
            os.flush();
        }
    }
}
