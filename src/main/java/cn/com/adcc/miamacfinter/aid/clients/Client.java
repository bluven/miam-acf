package cn.com.adcc.miamacfinter.aid.clients;

import cn.com.adcc.miamacfinter.aid.beans.*;
import cn.com.adcc.miamacfinter.aid.handlers.IFileHandler;
import cn.com.adcc.miamacfinter.aid.states.IState;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bluven on 14-8-6.
 */
public abstract class Client implements IContext, IClient {

    private Timer timer;

    private Map<String, TimerTask> tasks;

    private IState state;

    private String host;

    private Integer port;

    private String aidLabel;

    private String cmuLabel;

    // 待接收文件
    private CommandFileBean inputFileBean;

    private CommandLDUBean inputLduBean;

    private IFileHandler fileHandler;

    public abstract void close();

    public void connect(String aidLabel){
        this.connect("192.168.4.253", 8766, aidLabel, "304");
    }

    public abstract void connect(String host, int port, String aidLabel, String cmuLabel);

    public abstract void sendCommand(String command);

    public void registerFileHandler(IFileHandler handler){
        this.fileHandler = handler;
    }

    public void subscribe() {
        String command = "add,0," + this.aidLabel;
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

    public void sendFile(int fileId, String fileContent){

        CommandFileBean fileBean = BeanBuilder.build(fileId, fileContent, this.cmuLabel);

        fileBean.setFileId(fileId);

        this.state.sendFile(fileBean);
    }

    public void triggerFileSentEvent(int fileId, boolean result){
        this.fileHandler.onSentResult(fileId, result);
    }

    public void triggerFileReceived(){
        this.triggerFileReceived(this.inputFileBean);
        this.inputFileBean = null;
    }

    public void triggerFileReceived(CommandFileBean fileBean){
        if(this.fileHandler != null){
            this.fileHandler.onReceived(fileBean.getFileContent());
        }
    }

    public void scheduleAtFixedRate(TimerTask timerTask, int delay, int interval) {
        timer.scheduleAtFixedRate(timerTask, delay, interval);
    }

    public void saveTask(String name, TimerTask task) {
        this.tasks.put(name, task);
    }

    public void cancelTask(String name){

        TimerTask task = this.tasks.remove(name);

        if(task != null){
            task.cancel();
        }
    }

    public void schedule(TimerTask timerTask, int delay) {
        timer.schedule(timerTask, delay);
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

    public CommandFileBean getInputFileBean() {
        return inputFileBean;
    }

    public void setInputFileBean(CommandFileBean fileBean) {
        this.inputFileBean = fileBean;
    }

    public CommandLDUBean getInputLduBean() {
        return inputLduBean;
    }

    public void setInputLduBean(CommandLDUBean inputLduBean) {
        this.inputLduBean = inputLduBean;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public Map<String, TimerTask> getTasks() {
        return tasks;
    }

    public void setTasks(Map<String, TimerTask> tasks) {
        this.tasks = tasks;
    }
}
