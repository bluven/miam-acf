package cn.com.adcc.miamacfinter.aid.beans;

/**
 * Created by Daniel on 14-8-2.
 */
public class EOTBean implements IBean{

    public final static String TYPE_NOT_FINAL = "70"; //普通EOT，只代表一个LDU的结束

    public final static String TYPE_FINAL = "71"; // 代表一个文件的结束

    public EOTType eotType = EOTType.notFinal;

    private String crc;

    private String label;

    public boolean isFinalEOT(){
        return this.eotType == EOTType.Final;
    }

    public static EOTBean newFinalEOT(String crc, String label){
        return new EOTBean(EOTType.Final, crc, label);
    }

    public static EOTBean newEOT(String crc, String label){
        return new EOTBean(EOTType.notFinal, crc, label);
    }

    public EOTBean(EOTType eotType, String crc, String label) {
        this.eotType = eotType;
        this.crc = crc;
        this.label = label;
    }

    public String asWord() {

        if (eotType==EOTType.notFinal) {
            StringBuilder word = new StringBuilder(label).append(",").append("70").append(crc);
            return word.toString();
        } else {
            StringBuilder word = new StringBuilder(label).append(",").append("71").append(crc);
            return word.toString();
        }
    }

    public static enum EOTType {
        notFinal,Final
    }
}

