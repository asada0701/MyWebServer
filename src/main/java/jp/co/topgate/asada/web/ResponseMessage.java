package jp.co.topgate.asada.web;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
class ResponseMessage {
    /**
     * ヘッダーフィールドのコロン
     */
    private static final String HEADER_FIELD_COLON = ": ";
    /**
     * HTTPステータスコード:200
     */
    static final int STATUS_OK = 200;
    /**
     * HTTPステータスコード:400
     */
    static final int STATUS_BAD_REQUEST = 400;
    /**
     * HTTPステータスコード:404
     */
    static final int STATUS_NOT_FOUND = 404;

    private String protocolVersion = null;
    private Map<Integer, String> reasonPhrase = new HashMap<>();
    private List<String> headerField = new ArrayList<>();
    private File messageBody = null;

    /**
     * コンストラクタ
     */
    ResponseMessage() {
        protocolVersion = "HTTP/1.1";

        reasonPhrase.put(STATUS_OK, "OK");
        reasonPhrase.put(STATUS_BAD_REQUEST, "Bad Request");
        reasonPhrase.put(STATUS_NOT_FOUND, "Not Found");
    }

    void setProtocolVersion(String protocolVersion) {
        if (protocolVersion != null) {
            this.protocolVersion = protocolVersion;
        }
    }

    void addReasonPhrase(int statusCode, String reasonPhrase) {
        if (reasonPhrase != null) {
            this.reasonPhrase.put(statusCode, reasonPhrase);
        }
    }

    void addHeader(String name, String value) {
        if (name != null && value != null) {
            headerField.add(name + HEADER_FIELD_COLON + value);
        }
    }

    void setMessageBody(File messageBody) {
        if (messageBody != null && messageBody.exists() && messageBody.isFile()) {
            this.messageBody = messageBody;
        }
    }

    /**
     * リソースファイルを送りたい時のメソッド
     */
    void returnResponse(OutputStream os, int statusCode, File resource, ResourceFileType rft) {
        addHeader("Content-Type", rft.getContentType());
        setMessageBody(resource);

        InputStream in = null;
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(protocolVersion).append(" ").append(statusCode).append(" ").append(reasonPhrase.get(statusCode));
            builder.append("\n");
            for (String s : headerField) {
                builder.append(s).append("\n");
            }
            builder.append("\n");
            os.write(builder.toString().getBytes());
            in = new FileInputStream(messageBody);
            int num;
            while ((num = in.read()) != -1) {
                os.write(num);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * エラーメッセージを送りたい時のメソッド
     */
    void returnErrorResponse(OutputStream os, int statusCode) {
        this.addHeader("Content-Type", "text/html");
        String stringMessageBody = null;
        switch (statusCode) {
            case STATUS_BAD_REQUEST:
                stringMessageBody = "<html><head><title>400 Bad Request</title></head>" +
                        "<body><h1>Bad Request</h1>" +
                        "<p>Your browser sent a request that this server could not understand.<br /></p></body></html>";
                break;
            case STATUS_NOT_FOUND:
                stringMessageBody = "<html><head><title>404 Not Found</title></head>" +
                        "<body><h1>Not Found</h1>" +
                        "<p>お探しのページは見つかりませんでした。<br /></p></body></html>";
                break;
            default:
                System.out.println("存在しないステータスコードが指定されました。");
        }
        if (stringMessageBody != null) {
            PrintWriter pw = new PrintWriter(os, true);
            StringBuilder builder = new StringBuilder();
            builder.append(protocolVersion + " " + statusCode + " " + reasonPhrase).append("\n");
            for (String s : headerField) {
                builder.append(s).append("\n");
            }
            builder.append("\n");
            builder.append(stringMessageBody);
            pw.println(builder.toString());
            pw.close();
        }
    }

    String getProtocolVersion() {
        return this.protocolVersion;
    }

    String findReasonPhraseByStatusCode(int statusCode) {
        return reasonPhrase.get(statusCode);
    }

    List<String> getHeaderField() {
        return this.headerField;
    }

    File getMessageBody() {
        return this.messageBody;
    }
}
