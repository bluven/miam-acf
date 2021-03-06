package cn.com.adcc.miamacfinter.aid.clients;

import cn.com.adcc.miamacfinter.aid.beans.CommandFileBean;
import cn.com.adcc.miamacfinter.aid.beans.IBean;
import cn.com.adcc.miamacfinter.aid.states.IState;

import java.util.TimerTask;

/**
 * Created by bluven on 14-8-3.
 */
public interface IContext {

    void subscribe();

    void lock();

    void sendCommand(String command);

    void transmit(IBean bean);

    void transferTo(IState state);

    IState getState();

    void setState(IState state);

    String getAidLabel();

    void setAidLabel(String label);

    String getCmuLabel();

    void setCmuLabel(String label);

    void sendALO();

    CommandFileBean getInputFileBean();

    void setInputFileBean(CommandFileBean fileBean);

    void triggerFileReceived();

    CommandFileBean discardInputFile();

    void triggerFileReceived(CommandFileBean fileBean);

    void triggerFileSentEvent(int fileId, boolean result);

    void scheduleAtFixedRate(TimerTask timerTask, int delay, int interval);

    void schedule(TimerTask timerTask, int delay);

    //  保存倒计时任务
    // 对应于协议里的set动作
    void saveTask(String name, TimerTask task);

    // 取消任务并删除
    // 对应于协议里的reset动作
    void cancelTask(String name);

    void incrementCounter(String name);

    boolean isCounterGreaterThan(String name, Integer num);

    void resetCounter(String name);
}
