package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.aid.beans.CommandFileBean;
import cn.com.adcc.miamacfinter.aid.beans.CommandLDUBean;
import cn.com.adcc.miamacfinter.aid.beans.NAKBean;
import cn.com.adcc.miamacfinter.aid.beans.SOTBean;

/**
 * Created by bluven on 14-8-3.
 */
public class WaitSOTState extends State {

    public void handleSOT(SOTBean sot){

        CommandFileBean fileBean = super.context.getFileBean();

        if(fileBean == null){
            fileBean = new CommandFileBean();
            fileBean.setFileNum(sot.getFileSeqNum());

            if(sot.isFirstSot()){
                super.context.setFileBean(fileBean);
            } else {
                // todo 返回NAK;
                NAKBean nak = new NAKBean();
                nak.setLabel(super.context.getCmuLabel());
                nak.setFileSeqNum(sot.getFileSeqNum());
                nak.setStatusCode("00");

                super.context.transmit(nak);

                super.context.transferTo(new LinkIdleState());
            }
        } else {

            if(fileBean.isFileSeqNumMatch(sot.getFileSeqNum())) {
               // todo 返回NAK
            }
        }

        CommandLDUBean lduBean = new CommandLDUBean();
        lduBean.setRtsBean(super.context.getRtsBean());
        lduBean.setSotBean(sot);
        lduBean.setLDUNum(sot.getLduNum());

        fileBean.appendLDUBean(lduBean);

        super.context.setLduBean(lduBean);

        super.context.transferTo(new WaitEOTState());
    }
}
