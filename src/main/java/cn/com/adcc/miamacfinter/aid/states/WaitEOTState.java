package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.aid.beans.*;

/**
 * 接受数据状态
 *
 * Created by bluven on 14-8-4.
 */

public class WaitEOTState extends State {

    public void handleDataBean(DataBean data){

        CommandLDUBean lduBean = super.context.getLduBean();

        if(lduBean == null){
            // todo: 没有LDU时处理
            return;
        }

        lduBean.appendData(data);

    }

    public void handleEOT(EOTBean eot){

        super.context.transferTo(new LinkIdleState());

        CommandLDUBean lduBean = super.context.getLduBean();
        SOTBean sotBean = lduBean.getSotBean();

        ACKBean ack = new ACKBean();
        ack.setLabel(context.getCmuLabel());
        ack.setFileSeqNum(sotBean.getFileSeqNum());
        ack.setLduSeqNum(sotBean.getLduNum());

        String ackS = ack.asWord();
        context.transmit(ack);
        //context.sendCommand(ackS);
        System.out.println("ack:" + ackS);

        if(lduBean == null){
            // todo: 没有LDU时处理
            return;
        }

        lduBean.setEotBean(eot);

        if(true || lduBean.isAllDataReceived()){

            context.setLduBean(null);

            // todo: CRC validate

            if(true || eot.isFinalEOT()){
                context.receiveFile();
                context.setFileBean(null);
            }



        }

    }
}
