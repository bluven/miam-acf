package cn.com.adcc.miamacfinter;

import cn.com.adcc.miamacfinter.aid.clients.*;
import cn.com.adcc.miamacfinter.aid.handlers.IFileHandler;

import java.io.Console;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bluven on 14-8-5.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        testClient();
    }

    public static void testClient(){

        int counter = 1;

        final IClient client = SocketClient.newInstance();

        client.registerFileHandler(new IFileHandler() {

            private int counter = 1;

            public void onReceived(String content) {
                System.out.println(content);
            }

            public void onSentResult(int fileId, boolean result) {
                System.out.println("File Sent Result: " + fileId  + ", " + result);
            }
        });

        // 374: cabin
        // 304: cmu
        client.connect("374");
//        client.connect("127.0.0.1", 1234, "374", "304");

        Scanner in = new Scanner(System.in);

        while(true){
            System.out.print(">> ");
            String input = in.nextLine();

            if(input.equals("close")){
                client.close();
            }

            if(input.equals("connect")){
                client.connect("127.0.0.1", 1234, "374", "304");
            }

            if(input.equals("send")){
                client.sendFile(counter, "test");
                counter += 1;
            }
        }
    }
}
