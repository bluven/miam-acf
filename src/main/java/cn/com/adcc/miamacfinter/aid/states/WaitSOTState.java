package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.aid.beans.*;
import cn.com.adcc.miamacfinter.aid.clients.IClient;
import cn.com.adcc.miamacfinter.aid.clients.IContext;
import cn.com.adcc.miamacfinter.aid.constants.ProtocolConstants;

/**
 * Created by bluven on 14-8-3.
 */
public class WaitSOTState extends State {

    private RTSBean rts;

    public WaitSOTState(RTSBean rts){
        this.rts = rts;
    }

    public void handleRTS(RTSBean rts){
        IContext context = super.getContext();
        context.transferTo(new LinkIdleState());
        context.getState().handleRTS(rts);
    }

    public void handleSOT(SOTBean sot){

        CommandFileBean fileBean = null;

        if(sot.isFirstSot()){

            fileBean = new CommandFileBean();
            fileBean.setFileNum(sot.getFileSeqNum());
            super.context.setInputFileBean(fileBean);
        } else {
            fileBean = context.getInputFileBean();
        }

        if(! fileBean.isFileSeqNumMatch(sot.getFileSeqNum())) {
            // 上一个文件没有接收完
            this.handleSyncError(sot, ProtocolConstants.NEW_FILE_WITH_PRE_INCOMPLETE);
            return;
        }

        if(! fileBean.isNextOrDup(sot.getLduNum())){
            // LDU 序列不对
            handleSyncError(sot, ProtocolConstants.LDU_SEQ_NUM_ERR);
            return;
        }

        CommandLDUBean lduBean = new CommandLDUBean();
        lduBean.setRtsBean(this.rts);
        lduBean.setSotBean(sot);
        lduBean.setLDUNum(sot.getLduNum());

        // 暂时不在SOT时将ldu放入file
        //fileBean.appendLDUBean(lduBean);

        super.context.transferTo(new WaitEOTState(lduBean));
    }

    public void handleDataBean(DataBean data){
        sendBackNAK(ProtocolConstants.MISSING_EOT_WORD);
    }

    public void handleEOT(EOTBean eot){
        sendBackNAK(ProtocolConstants.MISSING_SOT_WORD);
    }

    private void sendBackNAK(String statusCode){

        IContext context = getContext();

        CommandFileBean fileBean = context.getInputFileBean();

        if(fileBean != null){

            NAKBean nak = new NAKBean();
            nak.setLabel(context.getCmuLabel());
            nak.setFileSeqNum(fileBean.getFileNum());
            nak.setStatusCode(statusCode);

            context.transmit(nak);
        }

        context.transferTo(new LinkIdleState());
    }

    private void handleSyncError(SOTBean sot, String statusCode){

        IContext context = super.getContext();

        context.cancelTask("T14");

        // todo 返回NAK;
        SYNBean syn = new SYNBean();
        syn.setLabel(super.context.getCmuLabel());
        syn.setFileSeqNum(sot.getFileSeqNum());
        syn.setStatusCode(statusCode);

        context.transmit(syn);

        context.setInputFileBean(null);

        context.transferTo(new LinkIdleState());
    }


}
