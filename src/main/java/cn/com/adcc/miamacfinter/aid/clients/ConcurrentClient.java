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
public class ConcurrentClient extends NewSocketClient {

    private transient boolean running;

    private Queue<String> commandQueue;

    private Thread sendThread;

    private static ConcurrentClient singleton;

    protected ConcurrentClient(){

        this.running = true;

        this.setState(new InitialState(this));

        this.commandQueue = new ArrayBlockingQueue<String>(200);
    }

    public static ConcurrentClient newInstance(){

        if(ConcurrentClient.singleton == null){
            ConcurrentClient.singleton = new ConcurrentClient();
        }

        return ConcurrentClient.singleton;
    }

    public void close() {

        try {
            super.close();
            this.sendThread.join(1000);

        } catch (Exception e) {
            throw new BaseException(e);
        }
    }

    public void connect(String host, int port, String aidLabel, String cmuLabel) {
        super.doConnect(host, port, aidLabel, cmuLabel);
        startRead();
        startSend();
    }

    /*
    public void connect(String host, int port, String aidLabel, String cmuLabel) {

        if(this.socket != null){
            throw new BaseException("已经有连接!!!");
        }

        try{

            Socket socket = new Socket();

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

        this.startRead();

        this.startSend();
    }
    */

    public void startSend(){

        this.sendThread = new Thread(new Runnable() {

            public void run() {


                ConcurrentClient client = ConcurrentClient.this;

                try{

                    PrintWriter out = new PrintWriter(
                                        new BufferedWriter(
                                            new OutputStreamWriter(
                                                    client.getSocket().getOutputStream())), true);


                    while(running){

                        String command = client.commandQueue.poll();

                        if(command == null){
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                throw new BaseException(e);
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
