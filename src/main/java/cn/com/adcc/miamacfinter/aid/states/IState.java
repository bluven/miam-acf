package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.aid.clients.IContext;
import cn.com.adcc.miamacfinter.aid.beans.*;

/**
 * Created by bluven on 14-8-3.
 */
public interface IState {

    void onConnected() throws Exception;

    void handleOk();

    void handleError();

    void handleRTS(RTSBean bean);

    void handleCTS(CTSBean bean);

    void handleNCTS(NCTSBean bean);

    void handleSOT(SOTBean bean);

    void handleALR(ALRBean bean);

    void handleALO(ALOBean bean);

    void handleDataBean(DataBean data);

    IContext getContext();

    void setContext(IContext context);

    void sendFile(CommandFileBean fileBean);

    void handleEOT(EOTBean eotBean);

    void handleACK(ACKBean ackBean);
}
