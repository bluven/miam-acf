package cn.com.adcc.miamacfinter.aid.beans;

import cn.com.adcc.miamacfinter.aid.utils.Utils;

/**
 * Created by bluven on 14-7-30.
 */
public class RTSBean implements IBean {

    public final static String TYPE = "41";

    private String label;

    private char dst = 'g';

    private int wordCount = 0;

    public RTSBean(){

    }

    public RTSBean(char dst, int wordCount, String label) {
        this.dst = dst;
        this.wordCount = wordCount;
        this.label = label;
    }

    public static RTSBean parseRaw(String data, String label){

        RTSBean bean = new RTSBean();

        bean.label = label;

        bean.dst = (char)Integer.parseInt(data.substring(2, 4), 16);

        bean.wordCount = Integer.parseInt(data.substring(4), 16);

        return bean;
    };

    public String asWord() {

        String dst = Utils.leftPadHex(Integer.toHexString((int)this.dst));
        String wordCount = Utils.leftPadHex(Integer.toHexString(this.wordCount));

        StringBuilder word = new StringBuilder(this.label).append(',').append(this.TYPE).append(dst).append(wordCount);

        return word.toString();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public char getDst() {
        return dst;
    }

    public void setDst(char dst) {
        this.dst = dst;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }
}
