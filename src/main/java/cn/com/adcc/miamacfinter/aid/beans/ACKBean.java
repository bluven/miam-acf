package cn.com.adcc.miamacfinter.aid.beans;

import cn.com.adcc.miamacfinter.aid.utils.Utils;

public class ACKBean implements IBean {

    public final static String TYPE = "46";

    private String label;

    private int fileSeqNum;

    private int lduSeqNum;

    public static ACKBean parseRaw(String data, String label){

    	ACKBean bean = new ACKBean();

        bean.label = label;

        bean.fileSeqNum = Integer.parseInt(data.substring(2, 4), 16);

        bean.lduSeqNum = Integer.parseInt(data.substring(4), 16);

        return bean;
    };

    public String asWord() {

        String fileSeqNum = Utils.twoHex(Integer.toHexString(this.fileSeqNum));
        String lduSeqNum = Utils.twoHex(Integer.toHexString(this.lduSeqNum));

        StringBuilder word = new StringBuilder(label).append(",").append(this.TYPE).append(fileSeqNum).append(lduSeqNum);

        return word.toString();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getFileSeqNum() {
        return fileSeqNum;
    }

    public void setFileSeqNum(int fileSeqNum) {
        this.fileSeqNum = fileSeqNum;
    }

    public int getLduSeqNum() {
        return lduSeqNum;
    }

    public void setLduSeqNum(int lduSeqNum) {
        this.lduSeqNum = lduSeqNum;
    }
}