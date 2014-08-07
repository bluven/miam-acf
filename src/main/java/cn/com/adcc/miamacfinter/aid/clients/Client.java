package cn.com.adcc.miamacfinter.aid.clients;

import cn.com.adcc.miamacfinter.aid.beans.*;
import cn.com.adcc.miamacfinter.aid.handlers.IFileReceivedHandler;
import cn.com.adcc.miamacfinter.aid.states.IState;

/**
 * Created by bluven on 14-8-6.
 */
public abstract class Client implements IContext {

    private IState state;

    private String host;

    private Integer port;

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

    public abstract void connect(String host, int port, String aidLabel, String cmuLabel);

    public abstract void sendCommand(String command);

    public void onFileReceived(IFileReceivedHandler handler){
        this.fileReceivedHandler = handler;
    }

    public void subscribe() {
        String command = "add,0," + aidLabel;
        this.sendCommand(command);
    }

    public void lock() {
        this.sendCommand("lock,0");
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
