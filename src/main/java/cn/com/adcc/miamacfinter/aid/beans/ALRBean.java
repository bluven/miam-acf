package cn.com.adcc.miamacfinter.aid.beans;


import cn.com.adcc.miamacfinter.aid.utils.Utils;

/**
 * Created by bluven on 14-7-30.
 */
public class ALRBean implements IBean{

    public static String TYPE = "48";

    private String label = "";

    private int version = 0;

    public ALRBean(){

    }

    public ALRBean(String label, int version){
        this.label = label;
        this.version = version;
    }

    public static ALRBean parseRaw(String data, String label){

        ALRBean bean = new ALRBean();

        bean.label = label;

        bean.version =  Integer.parseInt(data.substring(4), 16);

        return bean;
    }

    public String asWord(){

        String version = Integer.toHexString(this.version);

        version = Utils.mustXchars(version, 4);

        return this.label + "," + this.TYPE + version;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
