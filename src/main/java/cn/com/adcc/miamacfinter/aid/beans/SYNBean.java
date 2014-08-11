package cn.com.adcc.miamacfinter.aid.beans;

import cn.com.adcc.miamacfinter.aid.utils.Utils;

public class SYNBean implements IBean {

    public final static String TYPE = "49";

    private String label;

    private int fileSeqNum;

    private String statusCode;

    public static SYNBean parseRaw(String data, String label){

    	SYNBean bean = new SYNBean();

        bean.label = label;

        bean.fileSeqNum = Integer.parseInt(data.substring(2, 4), 16);

        bean.statusCode = data.substring(4);

        return bean;
    }

    public String asWord() {

        String fileSeqNum = Utils.twoHex(Integer.toHexString(this.fileSeqNum));

        StringBuilder word = new StringBuilder(label).append(",").append(this.TYPE)
                                                     .append(fileSeqNum).append(fileSeqNum).append(statusCode);

        return word.toString();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusCode(int statusCode){
        this.statusCode = Integer.toHexString(statusCode);
    }

    public int getFileSeqNum() {
        return fileSeqNum;
    }

    public void setFileSeqNum(int fileSeqNum) {
        this.fileSeqNum = fileSeqNum;
    }
}