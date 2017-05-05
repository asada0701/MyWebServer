package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.User;

import java.io.*;

/**
 * Created by yusuke-pc on 2017/05/04.
 */
public class DeleteSample {
    public static void main(String[] args) {
        User user = new User();
        user.setEmail("");

        Message message = new Message();
        message.setMessageID(3);


        String trID = "            <tr id=\"No." + message.getMessageID() + "\">";

        String path = "./src/main/resources/2/index.html";

        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                if (trID.equals(str)) {
                    System.out.println("どうかな");
                    for (int i = 0; i < 14; i++) {
                        str = br.readLine();
                    }
                }
                builder.append(str).append("\n");
            }
            File file = new File(path);
            if (!file.delete()) {
                throw new IOException("存在しないファイルを編集しようとしました。");
            }

            try (OutputStream os = new FileOutputStream(new File(path))) {
                os.write(builder.toString().getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
