package jp.co.topgate.asada.web;

import java.io.IOException;
import java.util.Scanner;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {
    private static final String START_NUM = "1";
    private static final String STOP_NUM = "2";
    private static final String END_NUM = "3";

    public static void main(String[] args) {
        try {
            Server server = new Server();
            String choices;
            Scanner scan = new Scanner(System.in);
            do{
                System.out.println("--------------------");
                System.out.println(START_NUM + ": START");
                System.out.println(STOP_NUM + ": STOP");
                System.out.println(END_NUM + ": END");
                do{
                    System.out.print("please select :");
                    choices = scan.next();
                }while(!(choices.equals(START_NUM) || choices.equals(STOP_NUM) || choices.equals(END_NUM)));
                switch(choices){
                    case START_NUM:
                        switch (server.getState()){
                            case TERMINATED:
                                server = new Server();
                            case NEW:
                                server.serverStart();
                                System.out.println("start up http server..");
                                break;
                            case RUNNABLE:
                                System.out.println("http server is already running..");
                        }
                        break;
                    case STOP_NUM:
                        switch (server.getState()){
                            case NEW:
                            case TERMINATED:
                                System.out.println("http server is not running..");
                                break;
                            case RUNNABLE:
                                if(server.serverStop()){
                                    System.out.println("http server stops..");
                                }else{
                                    System.out.println("wait a second, http server is returning a response..");
                                }
                                break;
                        }
                        break;
                    case END_NUM:
                        if(server.serverEnd()){
                            System.out.println("bye..");
                        }else{
                            System.out.println("wait a second, http server is returning a response..");
                            choices = "";
                        }
                        break;
                }
            }while(!choices.equals(END_NUM));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
