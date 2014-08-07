package cn.com.adcc.miamacfinter.aid.clients;

import cn.com.adcc.miamacfinter.aid.beans.CommandFileBean;
import cn.com.adcc.miamacfinter.aid.beans.CommandLDUBean;
import cn.com.adcc.miamacfinter.aid.beans.IBean;
import cn.com.adcc.miamacfinter.aid.beans.RTSBean;
import cn.com.adcc.miamacfinter.aid.states.IState;

import java.io.IOException;

/**
 * Created by bluven on 14-8-3.
 */
public interface IContext {

    void subscribe();

    void lock();

    boolean isProcessingAnyFile();

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

    CommandFileBean getFileBean();

    void setFileBean(CommandFileBean fileBean);

    CommandLDUBean getLduBean();

    void setLduBean(CommandLDUBean lduBean);

    RTSBean getRtsBean();

    void setRtsBean(RTSBean rts);

    CommandFileBean getOutFileBean();

    void setOutFileBean(CommandFileBean fileBean);

    CommandLDUBean getOutLduBean();

    void setOutLduBean(CommandLDUBean lduBean);

    void receiveFile();

    void receiveFile(CommandFileBean fileBean);
}
