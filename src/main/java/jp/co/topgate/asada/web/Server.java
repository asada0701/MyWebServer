package jp.co.topgate.asada.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
public class Server extends Thread{
    public static final int portNumber = 80;
    private ServerSocket serverSocket = null;
    private Socket socket = null;

    public void serverStart() throws IOException{
        serverSocket = new ServerSocket(portNumber);
    }
    public void serverStop() throws IOException{
        serverSocket.close();
    }
    public void serverRestart() throws IOException{

    }

    public void run() {
        socket = serverSocket.accept();
        System.out.println("リクエストを受けられる状態になりました。");
        try{
            InputStream is = socket.getInputStream();
        }catch(IOException e){
            e.printStackTrace();
        }

    }
}
