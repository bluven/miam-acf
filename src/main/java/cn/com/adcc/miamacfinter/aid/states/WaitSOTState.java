package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.aid.beans.*;

/**
 * Created by bluven on 14-8-3.
 */
public class WaitSOTState extends State {

    private RTSBean rts;

    public WaitSOTState(RTSBean rts){
        this.rts = rts;
    }

    public void handleSOT(SOTBean sot){

        CommandFileBean fileBean = super.context.getInputFileBean();

        if(fileBean == null){
            fileBean = new CommandFileBean();
            fileBean.setFileNum(sot.getFileSeqNum());

            if(sot.isFirstSot()){
                super.context.setInputFileBean(fileBean);
            } else {
                // LDU 序列不对
                handleSyncError(sot, "81");
                return;
            }
        }

        if(!fileBean.isFileSeqNumMatch(sot.getFileSeqNum())) {
            // 上一个文件没有接收完
            this.handleSyncError(sot, "95");
            return;
        }

        CommandLDUBean lduBean = new CommandLDUBean();
        lduBean.setRtsBean(this.rts);
        lduBean.setSotBean(sot);
        lduBean.setLDUNum(sot.getLduNum());

        fileBean.appendLDUBean(lduBean);

        super.context.transferTo(new WaitEOTState(lduBean));
    }

    private void handleSyncError(SOTBean sot, String statusCode){

        // todo 返回NAK;
        SYNBean syn = new SYNBean();
        syn.setLabel(super.context.getCmuLabel());
        syn.setFileSeqNum(sot.getFileSeqNum());
        syn.setStatusCode(statusCode);
        super.context.transmit(syn);

        this.context.setInputFileBean(null);

        super.context.transferTo(new LinkIdleState());
    }
}
