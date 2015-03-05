package cn.com.adcc.miamacfinter;

import cn.com.adcc.miamacfinter.aid.beans.*;
import cn.com.adcc.miamacfinter.aid.clients.*;
import cn.com.adcc.miamacfinter.aid.handlers.IFileHandler;

import java.util.Scanner;

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

    public static void compareFileBeans(CommandFileBean file1, CommandFileBean file2){

        CommandLDUBean lduBean1 = file1.nextLdu();
        CommandLDUBean lduBean2 = file2.nextLdu();

        while(lduBean1 != null && lduBean2 != null){

            RTSBean rtsBean1 = lduBean1.getRtsBean();

            RTSBean rtsBean2 = lduBean2.getRtsBean();

            /*
            System.out.println(rtsBean1.getDst() == rtsBean2.getDst());
            System.out.println(rtsBean1.getWordCount() == rtsBean2.getWordCount());
            System.out.println(rtsBean1.getLabel() == rtsBean2.getLabel());
            */
            System.out.println(rtsBean1.asWord().equals(rtsBean2.asWord()));
            System.out.println(lduBean1.getSotBean().asWord().equals(lduBean2.getSotBean().asWord()));

            int size = lduBean1.getDataBeans().size();

            for(int i = 0; i < size; i++){

                String word1 = lduBean1.getDataBeans().get(i).asWord();

                String word2 = lduBean2.getDataBeans().get(i).asWord();

                System.out.println(word1.equals(word2));
            }

            System.out.println(lduBean1.getEotBean().asWord().equals(lduBean2.getEotBean().asWord()));

            lduBean1 = file1.nextLdu();
            lduBean2 = file2.nextLdu();
        }
    }

    public static String bigString(){

        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < 632 * 7 - 10; i++){
            builder.append('A');
        }

        return builder.toString();
    }
}
