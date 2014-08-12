package cn.com.adcc.miamacfinter.aid.beans;

/**
 * Created by bluven on 14-8-5.
 */
public class PartialDataBean extends DataBean {

    public final static String TYPE = "1";

    public String header;

    public PartialDataBean(String hexData, String label) {

        super(hexData, label);

        switch (hexData.length())
        {
            case 4:
            {
                header="B";
                break;
            }
            case 3:
            {
                header="A0";
                break;
            }
            case 2:
            {
                header="900";
                break;
            }
            case 1:
            {
                header="8000";
                break;
            }

        }
    }

    public String asWord() {
        StringBuilder word = new StringBuilder(label).append(",").append(this.TYPE).append(this.header).append(hexData);
        return word.toString();
    }
}
