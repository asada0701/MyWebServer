package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.model.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author asada
 */
public class Test {

    public static void main(String[] args) {
        try {
            List<Message> list = doSomething();
            System.out.println(list.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Message> doSomething() throws IOException {
        String filePath = "./src/main/resources/pastData/messageModel.csv";
        List<Message> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            String str;
            while ((str = br.readLine()) != null) {
                System.out.println(str);

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
}
