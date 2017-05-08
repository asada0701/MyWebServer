package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;

import java.io.*;
import java.util.List;

/**
 * @author asada
 */
public class Test {

    public static void main(String[] args) {
        try {
            ModelController.addMessage("name", "title", "text", "password");
            writeCsv();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * CSVファイルに投稿された文を書き出すメソッド
     *
     * @throws Exception
     */
    private static void writeCsv() throws Exception {
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
