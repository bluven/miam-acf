package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.aid.beans.*;

/**
 * 接受数据状态
 *
 * Created by bluven on 14-8-4.
 */

public class WaitEOTState extends State {

    private CommandLDUBean lduBean;

    public WaitEOTState(CommandLDUBean lduBean){
        this.lduBean = lduBean;
    }

    public void handleDataBean(DataBean data){

        if(lduBean == null){
            // todo: 没有LDU时处理
            return;
        }

        lduBean.appendData(data);

    }

    public void handleEOT(EOTBean eot){

        if(lduBean == null){
            // todo: 没有LDU时处理
            return;
        }

        lduBean.setEotBean(eot);

        if(lduBean.isAllDataReceived()){

            // todo: CRC validate

            if(eot.isFinalEOT()){
                context.receiveFile();
            } else {
                // todo: 可能需要进一步处理
            }
        } else {

            // todo: 异常处理，需要返回NAK
        }

        super.context.transferTo(new LinkIdleState());

        SOTBean sotBean = this.lduBean.getSotBean();

        ACKBean ack = new ACKBean();
        ack.setLabel(context.getCmuLabel());
        ack.setFileSeqNum(sotBean.getFileSeqNum());
        ack.setLduSeqNum(sotBean.getLduNum());
        context.transmit(ack);

        super.context.cancelTask("T9");
    }
}
