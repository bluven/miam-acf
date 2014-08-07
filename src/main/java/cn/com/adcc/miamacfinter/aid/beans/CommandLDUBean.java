package cn.com.adcc.miamacfinter.aid.beans;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 14-8-2.
 */
public class CommandLDUBean {

    List<DataBean> dataBeans = new ArrayList<DataBean>();

    //LDU 序列号
    private int LDUNum = 0;

    private RTSBean rtsBean;

    private SOTBean sotBean;

    private EOTBean eotBean;

    public boolean matchACK(ACKBean ack){
        return sotBean.getFileSeqNum() == ack.getFileSeqNum() && sotBean.getLduNum() == ack.getLduSeqNum();
    }

    public boolean isAllDataReceived(){
        // dataBeans 只包含data word， rts的wordCount是包含sot及eot的
        return dataBeans.size() == (rtsBean.getWordCount() - 2);
    }

    public void inspect(){

        System.out.println(rtsBean.asWord());

        System.out.println(sotBean.asWord());

        for(DataBean data: dataBeans){
            System.out.println(data.asWord());
        }

        System.out.println(eotBean.asWord());
    }

    public List<DataBean> getDataBeans() {
        return dataBeans;
    }

    public void setDataBeans(List<DataBean> dataBeans) {
        this.dataBeans = dataBeans;
    }

    public int getLDUNum() {
        return LDUNum;
    }

    public void setLDUNum(int LDUNum) {
        this.LDUNum = LDUNum;
    }

    public RTSBean getRtsBean() {
        return rtsBean;
    }

    public void setRtsBean(RTSBean rtsBean) {
        this.rtsBean = rtsBean;
    }

    public SOTBean getSotBean() {
        return sotBean;
    }

    public void setSotBean(SOTBean sotBean) {
        this.sotBean = sotBean;
    }

    public EOTBean getEotBean() {
        return eotBean;
    }

    public void setEotBean(EOTBean eotBean) {
        this.eotBean = eotBean;
    }

    public void appendData(DataBean data) {
        if(dataBeans == null){
            dataBeans = new ArrayList<DataBean>();
        }

        this.dataBeans.add(data);
    }
}
