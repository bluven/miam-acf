package cn.com.adcc.miamacfinter.aid.beans;

import cn.com.adcc.miamacfinter.aid.utils.Utils;

/**
 * Created by bluven on 14-7-30.
 */
public class CTSBean implements IBean{

    public final static String TYPE = "42";

    private String label;

    private char dst;

    private int wordCount;

    public static CTSBean parseRaw(String data, String label){

        CTSBean bean = new CTSBean();

        bean.label = label;

        bean.dst = (char)Integer.parseInt(data.substring(2, 4), 16);

        bean.wordCount = Integer.parseInt(data.substring(4), 16);

        return bean;
    };

    public boolean matchRTS(RTSBean rts){
        return this.dst == rts.getDst() && this.wordCount == rts.getWordCount();
    }

    public String asWord() {

        String dst = Utils.leftPadHex(Integer.toHexString((int) this.dst));
        String wordCount = Utils.leftPadHex(Integer.toHexString(this.wordCount));

        StringBuilder word = new StringBuilder(label).append(',').append(this.TYPE).append(dst).append(wordCount);

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
