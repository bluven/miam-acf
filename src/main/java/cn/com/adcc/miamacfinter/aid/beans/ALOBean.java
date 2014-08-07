package cn.com.adcc.miamacfinter.aid.beans;

import cn.com.adcc.miamacfinter.aid.utils.Utils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by bluven on 14-7-30.
 */
public class ALOBean implements IBean{

    public final static String TYPE = "47";

    private String label = "";

    private int sal = 0;

    private int version = 0;

    /**
     * 解析原始数据，分别是data跟label，例如
     *  472301,374
     * @param data
     * @param label
     * @return
     */
    public static ALOBean parseRaw(String data, String label){

        ALOBean bean = new ALOBean();

        bean.label = label;

        data = Utils.hex2bitFormat(data, false);

        bean.parseDataBits(data);

        return bean;
    };

    /**
     * 解析bit格式的data数据, 提取sal和version
     * @param bits
     */
    public void parseDataBits(String bits){


        String sal = bits.substring(8, 16);
        this.sal = Utils.bit2octReversely(sal);

        String versionS = bits.substring(17, 24);


        this.version =  Integer.parseInt(versionS, 2);

    }

    public String asWord(){

        String salBits = Utils.oct2bitFormat("" + this.sal);

        salBits = StringUtils.reverse(salBits);

        String versionBits = Utils.hex2bitFormat("" + this.version, false);

        if(versionBits.length() < 8){
            versionBits = Utils.leftPadding(versionBits, 8);
        }

        return this.label + "," + this.TYPE + Utils.bit2hex(salBits + versionBits);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getSal() {
        return sal;
    }

    public void setSal(int sal) {
        this.sal = sal;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String toString(){
        return StringUtils.join(label, ":", sal, ":", version);
    }

}
