package jp.co.topgate.asada.web;

import java.io.*;
import java.time.LocalDateTime;

/**
 * Created by yusuke-pc on 2017/05/04.
 */
public class ContributionSample {
    private static int score = 1;

    public static void main(String[] args) {
        String path = "./src/main/resources/2/index.html";

        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                if (str.endsWith("<div id=\"log\">")) {
                    builder.append(str).append("\n");
                    for (int i = 0; i < 9; i++) {
                        str = br.readLine();
                        builder.append(str).append("\n");
                    }
                    builder.append(setData("asada", "title", "メッセージ"));
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

    private static String setData(String name, String title, String text) {
        if (text.contains("\n")) {
            text = text.replaceAll("\n", "<br>");
        }
        String str =
                "            <tr id=\"No." + score + "\">\n" +
                        "                <td>No." + score + "</td>\n" +
                        "                <td>" + title + "</td>\n" +
                        "                <td>" + text + "</td>\n" +
                        "                <td>" + name + "</td>\n" +
                        "                <td>" + getNowDate() + "</td>\n" +
                        "                <td>\n" +
                        "                    <form action=\"/program/board/\" method=\"post\">\n" +
                        "                        <input type=\"hidden\" name=\"param\" value=\"delete1\">\n" +
                        "                        <input type=\"hidden\" name=\"number\" value=\"" + score + "\">\n" +
                        "                        <input type=\"submit\" value=\"このコメントを削除する\">\n" +
                        "                    </form>\n" +
                        "                </td>\n";
        return str;
    }

    private static String getNowDate() {
        LocalDateTime ldt = LocalDateTime.now();
        String s = String.valueOf(ldt.getYear()) + "/" + ldt.getMonthValue() + "/" + ldt.getDayOfMonth() +
                " " + ldt.getHour() + ":" + ldt.getMinute();
        return s;
    }
}
