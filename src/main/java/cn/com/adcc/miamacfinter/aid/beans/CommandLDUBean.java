package cn.com.adcc.miamacfinter.aid.beans;

import cn.com.adcc.miamacfinter.aid.utils.Utils;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 14-8-2.
 */
public class CommandLDUBean {

    List<DataBean> dataBeans = new ArrayList<DataBean>();

    //LDU 序列号
    private int lduNum = 0;

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

    /**
     * 是否完整LDU
     *
     * 完整LDU是RTS，SOT， EOT， databeans 完备的
     * @return
     */
    public boolean isComplete(){
        return rtsBean != null && sotBean != null && eotBean != null && isAllDataReceived();
    }

    public boolean isPartial(){
        return !isComplete();
    }

    public String getLduContent()
    {
        //定义临时变量
        StringBuilder lduContent =new StringBuilder();

        //循环数据Word
        for (int i=(dataBeans.size()-1);i>=0;i--)
        {
            lduContent.append(dataBeans.get(i).getHexData());
        }

        //进行二进制转码
        char[] chars = Utils.hexStringToBytes(lduContent.toString());

        return new String(chars);
    }

    public int getWordCount(){
        return this.dataBeans.size() + 2;
    }

    public List<DataBean> getDataBeans() {
        return dataBeans;
    }

    public void setDataBeans(List<DataBean> dataBeans) {
        this.dataBeans = dataBeans;
    }

    public int getLDUNum() {
        return lduNum;
    }

    public void setLDUNum(int lduNum) {
        this.lduNum = lduNum;
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
