package jp.co.topgate.asada.web;

import java.util.Scanner;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {

    public static void main(String[] args) {
        String choices;
        Scanner scan = new Scanner(System.in);
        do{
            System.out.println("--------------------");
            System.out.println("1:サーバーを立ち上げる");
            System.out.println("2:サーバーを停止する");
            System.out.println("3:サーバーを再起動する");
            System.out.println("4:終了する");
            do{
                System.out.print("1,2,3,4のいずれかの数字を入力してください:");
                choices = scan.next();
            }while(!(choices.equals("1") || choices.equals("2") || choices.equals("3") || choices.equals("4")));
            switch(choices){
                case "1":
                    break;
                case "2":
                    break;
                case "3":
                    break;
                case "4":
                    break;
                default:
                    break;
            }
        }while(!choices.equals("4"));
        System.out.println("HTTPServerを終了します");
    }
}
