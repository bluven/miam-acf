package cn.com.adcc.miamacfinter.aid.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by bluven on 14-7-23.
 */
public class Utils {

    public static char[] rtsArray = new char[10];

    public static Map<Integer, String> rtsMap = new HashMap();

    static {

        rtsMap.put(18, "RTS WORD");
        rtsMap.put(19, "CTS WORD");
        rtsMap.put(2, "STX WORD");
        rtsMap.put(1, "CNTRL WORD");
        rtsMap.put(3, "ETX WORD");
        rtsMap.put(4, "EOT WORD");
        rtsMap.put(11, "PUSH/SCRATCH WORD");
        rtsMap.put(6, "ACK WORD");
        rtsMap.put(21, "NAK WORD");
        rtsMap.put(20, "ACK/NAK WORD");
        rtsMap.put(22, "SYN WORD");
        rtsMap.put(5, "ENQ WORD");
    }

    // data,timestamp,0,230,abcd12
    public static String bitfyLabelAndDate(String label, String data){

        label = oct2bitFormat(label);

        data = hex2bitFormat(data);

        return data + ", " + label;
    }

    public static String long2Date(String num){

        long value = Long.parseLong(num);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return format.format(new Date(value));
    }

    public static String hex2bitFormat(String num){

        return hex2bitFormat(num, true);
    }

    public static String hex2bitFormat(String num, boolean padding){

        String[] result = hex2bit(num);

        String pad = padding? " ": "";

        return StringUtils.join(result, pad);
    }

    public static String oct2bitFormat(String num){

        String[] result = oct2bit(num);

        return StringUtils.join(result, "");
    }

    public static Integer bit2oct(String bits){

        if(bits.length() < 9){
            bits = "0" + bits;
        }

        Double num = 0.0;

        int weightIndex = 0;

        for(int i = 2; i >= 0; i--){

            double weight = Math.pow(10, weightIndex);

            weightIndex += 1;

            int start = i * 3;
            int end = start + 3;

            int tmp = Integer.parseInt(bits.substring(start, end), 2);

            num += (tmp * weight);

        }

        return num.intValue();
    }

    public static Integer bit2octReversely(String bits){

        return bit2oct(StringUtils.reverse(bits));
    }

    public static String[] hex2bit(String num){

        String[] result = new String[num.length()];

        char[] nums = num.toCharArray();

        for(int i = 0; i < nums.length; i++){
            int n = Integer.parseInt(new String(new char[]{nums[i]}), 16);

            String hexNum = Integer.toBinaryString(n);

            if(hexNum.length() < 4) {
                hexNum = leftPadding(hexNum);
            }

            result[i] = hexNum;
        }
        return result;
    }

    public static String[] oct2bit(String num){

        String[] result = new String[num.length()];

        char[] nums = num.toCharArray();

        for(int i = 0; i < nums.length; i++){
            int n = Integer.parseInt(new String(new char[]{nums[i]}), 8);

            String hexNum = Integer.toBinaryString(n);

            if(hexNum.length() < 3) {
                hexNum = leftPadding(hexNum, 3);
            }

            result[i] = hexNum;
        }

        result[0] = result[0].substring(1);
        return result;
    }

    public static String leftPadding(String num){

        return leftPadding(num, 4);
    }


    public static String leftPadding(String num, int numOfPlaceholder){

        int n = Integer.parseInt(num);

        return String.format("%0" + numOfPlaceholder + "d", n);
    }

    public static String bit2char(String bits){

        char[] chs = new char[3];

        int length = bits.length() / 8;

        for(int i = 0; i < length; i++){

            int start = i * 8 + 1;

            int end = start + 7;

            StringBuilder buffer = new StringBuilder().append('0');

            if(end < bits.length()) {
                buffer.append(bits.substring(start, end));
            } else {
                buffer.append(bits.substring(start));
            }

            byte b =  Byte.parseByte(buffer.toString(), 2);

            chs[i] = (char)b;

            if(i == 0 && isRts(chs[i])){
                return rtsMap.get(Integer.valueOf(chs[i]));
            }
        }

        String result = new String(chs);
        return StringUtils.reverse(result);
    }


    public static String bit2hex(String bits){

       String[] hexArray = new String[bits.length()/4];

       for(int i=0; i < hexArray.length; i++){
           String num = bits.substring(i*4, (i + 1) * 4 );
           hexArray[i] = Integer.toHexString(Integer.valueOf(num, 2));
       }

       return StringUtils.join(hexArray);
    }

    public static String leftPadHex(String hex) {
        if (hex.length() == 1){
            hex="0" + hex;
        }
        return  hex;
    }

    public static boolean isRts(char ch){

        Integer i = (int)ch;

        return rtsMap.containsKey(i);
    }

    public static ByteBuf buildCommand(String command){

        command += "\n";

        ByteBuf encoded = Unpooled.buffer(command.length());
        encoded.writeBytes(command.getBytes());

        return encoded;
    }

    public static String must6chars(String data, int num) {

        StringBuilder result = new StringBuilder();

        for(int i = data.length(); i < num; i++){
            result.append('0');
        }

        result.append(data);

        return result.toString();
    }

    public static byte reverseBitsByte(int value) {

        //每一位相互交换	12345678 -> 21436587
        value = (value & 0x55) << 1 | (value & 0xAA) >> 1;
        //每两位相互交换	21436587 -> 43218765
        value = (value & 0x33) << 2 | (value & 0xCC) >> 2;
        //每四位相互交换	43218765 -> 87654321
        return (byte)(value << 4 | value >> 4);
    }

}
