package cn.com.adcc.miamacfinter.aid.clients;

import cn.com.adcc.miamacfinter.aid.exceptions.BaseException;
import cn.com.adcc.miamacfinter.aid.states.InitialState;
import cn.com.adcc.miamacfinter.aid.utils.ClientUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by bluven on 14-8-7.
 */
public class ConcurrentClient extends Client {

    private Socket socket;

    private Queue<String> commandQueue;

    private Thread sendThread;

    private Thread receiveThread;

    private static ConcurrentClient singleton;

     private ConcurrentClient(){
        this.setState(new InitialState(this));
        this.commandQueue = new ArrayBlockingQueue<String>(200);
    }

    public static ConcurrentClient newInstance(){

        if(ConcurrentClient.singleton == null){
            ConcurrentClient.singleton = new ConcurrentClient();
        }

        return ConcurrentClient.singleton;
    }

    public void connect(String host, int port, String aidLabel, String cmuLabel) {

        if(this.socket != null){
            throw new BaseException("已经有连接!!!");
        }

        try{

            final Socket socket = new Socket();

            socket.setKeepAlive(true);
            socket.setTcpNoDelay(true);
            socket.connect(new InetSocketAddress(host, port), 5000);

            this.setHost(host);
            this.setPort(port);
            this.setAidLabel(aidLabel);
            this.setCmuLabel(cmuLabel);

            this.socket = socket;
            this.getState().onConnected();
        } catch (Exception e){
            throw new BaseException(e);
        }

        this.receiveThread = new Thread(new Runnable(){

            public void run() {

                try {
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    while(true){
                        ClientUtils.handleInputData(ConcurrentClient.this.getState(), input.readLine());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        this.receiveThread.start();

        this.sendThread = new Thread(new Runnable() {

            public void run() {

                PrintWriter out = null;
                ConcurrentClient client = ConcurrentClient.this;
                try{
                    out = new PrintWriter(new BufferedWriter(
                                    new OutputStreamWriter(client.socket.getOutputStream())), true);


                    while(true){

                        String command = client.commandQueue.poll();

                        if(command == null){
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {

                            command += "\r\n";

                            out.write(command);
                            out.flush();
                        }
                    }
                } catch (IOException e) {
                    throw new BaseException(e);
                }
            }
        });

        this.sendThread.start();
    }

    public void sendCommand(String command){
        this.commandQueue.add(command);
    }
}
