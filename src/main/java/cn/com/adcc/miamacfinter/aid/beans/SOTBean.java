package cn.com.adcc.miamacfinter.aid.beans;

import cn.com.adcc.miamacfinter.aid.utils.Utils;

/**
 * Created by bluven on 14-8-3.
 */
public class SOTBean implements IBean{

    public static String TYPE = "6";

    private String label;

    private int gfi = 0;

    private int fileSeqNum = 0;

    private int lduNum = 0;

    public SOTBean(){}

    public SOTBean(int fileNum, int lduNum, String label) {
        this.fileSeqNum = fileNum;
        this.lduNum = lduNum;
        this.label = label;
    }

    public static SOTBean parseRaw(String data, String label){

        SOTBean bean = new SOTBean();

        bean.label = label;

        bean.gfi = Integer.parseInt(data.substring(1, 2), 16);
        bean.fileSeqNum = Integer.parseInt(data.substring(2, 4), 16);
        bean.lduNum = Integer.parseInt(data.substring(4), 16);

        return bean;
    }

    public String asWord() {

        String gfi = Integer.toHexString(this.gfi);
        String fileSeqNum = Utils.twoHex(Integer.toHexString(this.fileSeqNum));
        String lduSeqNum = Utils.twoHex(Integer.toHexString(this.lduNum));

        StringBuilder word = new StringBuilder(label).append(",").append(this.TYPE)
                                                     .append(gfi).append(fileSeqNum).append(lduSeqNum);

        return word.toString();
    }

    public boolean isFirstSot(){
        return this.lduNum == 0;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getGfi() {
        return gfi;
    }

    public void setGfi(int gfi) {
        this.gfi = gfi;
    }

    public int getFileSeqNum() {
        return fileSeqNum;
    }

    public void setFileSeqNum(int fileSeqNum) {
        this.fileSeqNum = fileSeqNum;
    }

    public int getLduNum() {
        return lduNum;
    }

    public void setLduNum(int lduNum) {
        this.lduNum = lduNum;
    }

}
