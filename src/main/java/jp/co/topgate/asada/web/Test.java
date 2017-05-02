package jp.co.topgate.asada.web;

import java.io.*;

/**
 * Created by yusuke-pc on 2017/05/02.
 */
public class Test {
    public static void main(String[] args) {
        String uri = "./src/main/resources/2/index.html";
        try (BufferedReader br = new BufferedReader(new FileReader(new File(uri)))) {
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                if (str.endsWith("<tr id=\"logth\">")) {
                    builder.append(str).append("\n");
                    for (int i = 0; i < 7; i++) {
                        str = br.readLine();
                        builder.append(str).append("\n");
                    }
                    String name = "asada";
                    String title = "今回やばいな";
                    String message = "めちゃ大変";
                    builder.append("\n").append(setData(name, title, message)).append("\n");
                }
                builder.append(str).append("\n");
            }
            File file = new File(uri);
            file.delete();

            try (OutputStream os = new FileOutputStream(new File(uri))) {
                os.write(builder.toString().getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String setData(String name, String title, String message) {
        String str =
                "            <tr>\n" +
                "                <td>" + "3" + "</td>\n" +
                "                <td>" + title + "</td>\n" +
                "                <td>" + message + "</td>\n" +
                "                <td>" + name + "</td>\n" +
                "                <td>" + "2017-05-02 12:28" + "</td>\n" +
                "                <td>\n" +
                "                    <form action=\"/program/board/\" method=\"post\">\n" +
                "                        <input type=\"hidden\" name=\"param\" value=\"delete1\">\n" +
                "                        <input type=\"submit\" value=\"このコメントを削除する\">\n" +
                "                    </form>\n" +
                "                </td>\n";
                //"            </tr>";
        return str;
    }
}
