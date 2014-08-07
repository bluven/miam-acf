package cn.com.adcc.miamacfinter.aid.clients;

import java.io.*;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import cn.com.adcc.miamacfinter.aid.handlers.IFileReceivedHandler;
import cn.com.adcc.miamacfinter.aid.utils.ClientUtils;

import cn.com.adcc.miamacfinter.aid.beans.*;
import cn.com.adcc.miamacfinter.aid.exceptions.BaseException;
import cn.com.adcc.miamacfinter.aid.states.IState;
import cn.com.adcc.miamacfinter.aid.states.InitialState;

/**
 * Created by bluven on 14-8-6.
 */

public class OldConcurrentClient implements IContext {

    private Socket socket;

    private IState state;

    private String host;

    private Integer port;

    private static OldConcurrentClient singleton;

    private String aidLabel;

    private String cmuLabel;

    // 待发送文件
    private CommandFileBean outFileBean;

    // 在发送的ldubean
    private CommandLDUBean outLduBean;

    // 待接收文件
    private CommandFileBean fileBean;

    private CommandLDUBean lduBean;

    private RTSBean rtsBean;

    private Queue<String> commandQueue;

    private Thread sendThread;

    private Thread receiveThread;

    private IFileReceivedHandler fileReceivedHandler;

    private OldConcurrentClient(){
        this.state = new InitialState(this);
        this.commandQueue = new ArrayBlockingQueue<String>(200);
    }

    public static OldConcurrentClient newInstance(){

        if(OldConcurrentClient.singleton == null){
            OldConcurrentClient.singleton = new OldConcurrentClient();
        }

        return OldConcurrentClient.singleton;
    }

    public void connect(String host, int port, String aidLabel, String cmuLabel) throws Exception{

        if(this.socket != null){
            throw new Exception("已经有连接!!!");
        }

        final Socket socket = new Socket();

        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        socket.connect(new InetSocketAddress(host, port), 5000);

        this.host = host;
        this.port = port;
        this.aidLabel = aidLabel;
        this.cmuLabel = cmuLabel;

        this.socket = socket;
        this.state.onConnected();

        this.receiveThread =  new Thread(new Runnable(){

            public void run() {

                try {
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    while(true){
                        ClientUtils.handleInputData(OldConcurrentClient.this.state, input.readLine());
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
                OldConcurrentClient client = OldConcurrentClient.this;
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


    public void subscribe() {
        String command = "add,0," + aidLabel;
        this.sendCommand(command);
    }

    public void lock() {
        this.sendCommand("lock,0");
    }

    public void sendCommand(String command){
        this.commandQueue.add(command);
    }

    public void transmit(IBean bean) {
        String command = new StringBuilder("transmit,0,").append(bean.asWord().toUpperCase()).toString();
        sendCommand(command);
    }

    public void transferTo(IState state) {
        this.setState(state);
        state.setContext(this);
    }

    public void sendALO() {

        ALOBean bean = new ALOBean();

        bean.setLabel(this.cmuLabel);

        bean.setSal(Integer.parseInt(this.aidLabel));

        bean.setVersion(1);

        this.transmit(bean);
    }

    public void sendFile(CommandFileBean fileBean){
        this.state.sendFile(fileBean);
    }

    public void onFileReceived(IFileReceivedHandler handler){
        this.fileReceivedHandler = handler;
    }

    public void receiveFile(){
        if(this.fileBean != null){
            this.receiveFile(this.fileBean);
        }
    }

    public void receiveFile(CommandFileBean fileBean){
        if(fileReceivedHandler != null){
            fileReceivedHandler.handle(fileBean);
        }
    }

    public boolean isProcessingAnyFile(){
        return this.fileBean != null || this.outFileBean != null;
    }

    public IState getState() {
        return this.state;
    }

    public void setState(IState state) {
        this.state = state;
    }

    public String getAidLabel() {
        return this.aidLabel;
    }

    public void setAidLabel(String label) {
        this.aidLabel = label;
    }

    public String getCmuLabel() {
        return this.cmuLabel;
    }

    public void setCmuLabel(String label) {
        this.cmuLabel = label;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public CommandFileBean getFileBean() {
        return fileBean;
    }

    public void setFileBean(CommandFileBean fileBean) {
        this.fileBean = fileBean;
    }

    public CommandLDUBean getLduBean() {
        return lduBean;
    }

    public void setLduBean(CommandLDUBean lduBean) {
        this.lduBean = lduBean;
    }

    public RTSBean getRtsBean() {
        return this.rtsBean;
    }

    public void setRtsBean(RTSBean rts) {

        this.rtsBean = rts;
    }

    public CommandLDUBean getOutLduBean() {
        return outLduBean;
    }

    public void setOutLduBean(CommandLDUBean outLduBean) {
        this.outLduBean = outLduBean;
    }

    public CommandFileBean getOutFileBean() {
        return outFileBean;
    }

    public void setOutFileBean(CommandFileBean outFileBean) {
        this.outFileBean = outFileBean;
    }

    public IFileReceivedHandler getFileReceivedHandler() {
        return fileReceivedHandler;
    }

    public void setFileReceivedHandler(IFileReceivedHandler fileReceivedHandler) {
        this.fileReceivedHandler = fileReceivedHandler;
    }
}
