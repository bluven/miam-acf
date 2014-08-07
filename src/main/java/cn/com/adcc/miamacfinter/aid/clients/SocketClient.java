package cn.com.adcc.miamacfinter.aid.clients;

/**
 * Created by bluven on 14-8-4.
 */

import java.io.*;
import java.net.Socket;
import java.net.InetSocketAddress;

import cn.com.adcc.miamacfinter.aid.beans.*;
import cn.com.adcc.miamacfinter.aid.exceptions.BaseException;
import cn.com.adcc.miamacfinter.aid.handlers.IFileReceivedHandler;
import cn.com.adcc.miamacfinter.aid.states.IState;
import cn.com.adcc.miamacfinter.aid.states.InitialState;
import cn.com.adcc.miamacfinter.aid.utils.ClientUtils;
import cn.com.adcc.miamacfinter.aid.utils.Utils;
import org.apache.commons.lang3.StringUtils;

public class SocketClient implements IContext {

    private Socket socket;

    private IState state;

    private String host;

    private Integer port;

    private static SocketClient singleton;

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

    private IFileReceivedHandler fileReceivedHandler;

    protected SocketClient(){
        this.state = new InitialState(this);
    }

    public static SocketClient newInstance(){

        if(SocketClient.singleton == null){
            SocketClient.singleton = new SocketClient();
        }

        return SocketClient.singleton;
    }

    public void connect(String host, int port, String aidLabel, String cmuLabel) throws Exception {

        if(this.socket != null){
            throw new BaseException("已经有连接!!!");
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

        Thread reader = new Thread(new Runnable(){

            public void run() {

                try {
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    while(true){
                        ClientUtils.handleInputData(SocketClient.this.state,input.readLine());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        reader.start();
    }

    public void subscribe() {
        String command = "add,0," + aidLabel;
        this.sendCommand(command);
    }

    public void lock() {
        this.sendCommand("lock,0");
    }

    public void sendCommand(String command) {

        PrintWriter out = null;

        try {
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream())), true);
        } catch (IOException e) {
            throw new BaseException(e);
        }

        command += "\r\n";

        out.write(command);
        out.flush();

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

    /**
     * 处理接受到的数据
     */
    private void handleInputData(String msg){

        if(StringUtils.isBlank(msg)){
            return;
        }

        if(msg.startsWith("ok")){

            state.handleOk();

        } else if(msg.startsWith("err")){

            state.handleError();

        } else if(msg.startsWith("data")){

            /*
            String s270 = msg.substring(16, 19);

            if(s270.equals("270")){
                this.sendCommand("transmit,0,172,00009F");
                return;
            }
            */

            String[] fields = msg.split(",");
            String label = fields[3];
            String data = fields[4];

            data = Utils.must6chars(data, 6);

            if(data.startsWith(ALRBean.TYPE)){

                this.state.handleALR(ALRBean.parseRaw(data, label));

            } else if(data.startsWith(ALOBean.TYPE)){

                this.state.handleALO(ALOBean.parseRaw(data, label));

            } else if(data.startsWith(RTSBean.TYPE)){

                this.state.handleRTS(RTSBean.parseRaw(data, label));

            } else if(data.startsWith(CTSBean.TYPE)){

                this.state.handleCTS(CTSBean.parseRaw(data, label));

            } else if(data.startsWith(NCTSBean.TYPE)){

                this.state.handleNCTS(NCTSBean.parseRaw(data, label));

            } else if(data.startsWith(SOTBean.TYPE)){

                this.state.handleSOT(SOTBean.parseRaw(data, label));

            } else if(data.startsWith(EOTBean.TYPE_NOT_FINAL)){
                this.state.handleEOT(EOTBean.newEOT(data.substring(2), label));

            } else if(data.startsWith(EOTBean.TYPE_FINAL)){

                this.state.handleEOT(EOTBean.newFinalEOT(data.substring(2), label));

            } else if(data.startsWith(ACKBean.TYPE)){

                this.state.handleACK(ACKBean.parseRaw(data, label));

            } else if(false && data.startsWith(DataBean.TYPE)) {

                this.state.handleDataBean(new DataBean(data.substring(1), label));

            } else if(false && data.startsWith(PartialDataBean.TYPE)) {

                this.state.handleDataBean(new PartialDataBean(data.substring(1), label));
            }
        }
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


