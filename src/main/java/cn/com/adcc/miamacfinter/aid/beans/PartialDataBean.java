package cn.com.adcc.miamacfinter.aid.beans;

/**
 * Created by bluven on 14-8-5.
 */
public class PartialDataBean extends DataBean {

    public static String TYPE = "1";

    public PartialDataBean(String hexData, String label) {
        super(hexData, label);
    }

    public String asWord() {
        StringBuilder word = new StringBuilder(label).append(",").append(this.TYPE).append(hexData);
        return word.toString();
    }
}
