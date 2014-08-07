package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.aid.clients.IContext;
import cn.com.adcc.miamacfinter.aid.beans.*;

/**
 * Created by bluven on 14-8-3.
 */
public class State implements IState {

    protected IContext context;

    public void onConnected() throws Exception {
    }

    public void handleOk() {
    }

    public void handleError() {
    }

    public void handleRTS(RTSBean bean) {

    }

    public void handleCTS(CTSBean bean) {

    }

    public void handleNCTS(NCTSBean nctsBean) {

    }

    public void handleSOT(SOTBean bean) {

    }

    public void handleALR(ALRBean bean) {
    }

    public void handleALO(ALOBean bean) {

        ALRBean alr = new ALRBean(context.getAidLabel(), 1);
        context.transmit(alr);
    }

    public void handleDataBean(DataBean data) {

    }

    public void handleEOT(EOTBean eotBean) {

    }

    public void handleACK(ACKBean ackBean) {

    }

    public IContext getContext() {
        return this.context;
    }

    public void setContext(IContext context) {
        this.context = context;
    }

    public void sendFile(CommandFileBean fileBean) {

    }

}
