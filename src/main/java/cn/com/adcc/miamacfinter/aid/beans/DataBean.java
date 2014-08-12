package cn.com.adcc.miamacfinter.aid.beans;

/**
 * Created by Daniel on 14-8-2.
 */
public class DataBean implements IBean {

    public final static String TYPE = "0";

    public String hexData;

    public String label;

    public DataBean(String hexData, String label) {

        this.hexData = hexData;
        this.label = label;

        //根据hexData判定Type
        /*
        switch (hexData.length())
        {
            case 5:
            {
                TYPE="0";
                break;
            }
            case 4:
            {
                TYPE="1B";
                break;
            }
            case 3:
            {
                TYPE="1A0";
                break;
            }
            case 2:
            {
                TYPE="1900";
                break;
            }
            case 1:
            {
                TYPE="18000";
                break;
            }

            default:
            {
            }
        }
        */

    }

    public String asWord() {
        StringBuilder word = new StringBuilder(label).append(",").append(this.TYPE).append(hexData);
        return word.toString();
    }

    public String getHexData() {
        return hexData;
    }

    public void setHexData(String hexData) {
        this.hexData = hexData;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
