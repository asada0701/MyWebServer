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
    private boolean isSeverStart = false;       //serverが動いているとtrueである

    public void serverStart() throws IOException{
//        if(serverSocket == null){
//            serverSocket = new ServerSocket(portNumber);
//            isSeverStart = true;
//        }
        isSeverStart = true;
    }
    public void serverStop() throws IOException{
//        if(serverSocket != null && !socket.isClosed()){
//            serverSocket.close();
//            isSeverStart = false;
//        }
        isSeverStart = false;
    }
    public void serverRestart() throws IOException{
        isSeverStart = true;
    }

    public boolean isSeverStart() {
        return isSeverStart;
    }

    public void run() {
        try{
            socket = serverSocket.accept();
            System.out.println("リクエストを受けられる状態になりました。");
            InputStream is = socket.getInputStream();
        }catch(IOException e){
            e.printStackTrace();
        }

    }
}
