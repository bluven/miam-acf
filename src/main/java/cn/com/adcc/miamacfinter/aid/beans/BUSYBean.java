package cn.com.adcc.miamacfinter.aid.beans;


public class BUSYBean implements IBean {

    public final static String TYPE = "44";

    private String label;

    private String dst;

    private String statusCode;

    public static BUSYBean parseRaw(String data, String label){

    	BUSYBean bean = new BUSYBean();

        bean.label = label;

        bean.dst = data.substring(2, 4);

        bean.statusCode = data.substring(4);

        return bean;
    };

    public String asWord() {

        StringBuilder word = new StringBuilder(label).append(",").append(this.TYPE).append(dst).append(statusCode);

        return word.toString();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCodeAsInt(){
        return Integer.parseInt(statusCode, 16);
    }

    public void setStatusCode(int statusCode){
        this.statusCode = Integer.toHexString(statusCode);
    }
}
