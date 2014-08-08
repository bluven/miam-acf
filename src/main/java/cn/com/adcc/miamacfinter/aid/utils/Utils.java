package cn.com.adcc.miamacfinter.aid.utils;

import java.math.BigInteger;
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


    /**
     * 将16进制格式转换成二进制格式
     *
     * Utils里没有真正的二进制数据，所谓二进制格式也只是由0，1组成的字符串
     * 比如，1F是16进制格式，其二进制格式是:00011111
     *
     * @param hex
     * @return
     */
    public static String hex2bit(String hex){
        return new BigInteger(hex, 16).toString(2);
    }

    public static String hex2bit(Integer hexNum){
        return Integer.toBinaryString(hexNum);
    }

    /**
     * 将16进制格式转换成二进制格式
     *
     * 根据padding会分隔二进制形式以便阅读
     *
     * @param hexNum
     * @param padding
     * @return
     */
    public static String hex2bit(String hexNum, boolean padding){

        String binNum = hex2bit(hexNum);

        String pad = padding? " ": "";

        String[] digits = new String[hexNum.length()];

        for(int i = 0; i < hexNum.length(); i++){

            int start = i * 4;
            int end = start + 4;

            digits[i] = binNum.substring(start, end);
        }

        return StringUtils.join(digits, pad);
    }

    /**
     * 将8进制数转换成2进制格式,
     *
     * 8进制数的最高位最大是3，转换后的二进制格式只有8位，如
     *
     *  374 -> 11 111 100
     *
     *  174 >  1 111 100
     *
     *  如果生成的二进制数不满8位必须补零，超过8位则需要截断
     *
     *  这里的进制转换不遵循正常的进制替换规则
     *
     * @param num
     *
     */
    public static String oct2bit(String num){

        StringBuilder result = new StringBuilder();

        char[] nums = num.toCharArray();

        for(int i = 0; i < nums.length; i++){

            int n = Integer.parseInt(new String(new char[]{nums[i]}), 8);

            String bits = Integer.toBinaryString(n);

            if(bits.length() < 3) {
                bits = mustXchars(bits, 3);
            }

            result.append(bits);
        }

        result.deleteCharAt(0);

        return result.toString();
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

    public static String leftPadding(String num, int numOfPlaceholder){

        int n = Integer.parseInt(num);

        return String.format("%0" + numOfPlaceholder + "d", n);
    }

    public static String bit2hex(String bits){

       String[] hexArray = new String[bits.length()/4];

       for(int i=0; i < hexArray.length; i++){
           String num = bits.substring(i * 4, (i + 1) * 4 );
           hexArray[i] = Integer.toHexString(Integer.valueOf(num, 2));
       }

       return StringUtils.join(hexArray);
    }

    public static String twoHex(String hex) {
        return hex.length() == 1 ? "0" + hex : hex;
    }


    public static String mustXchars(String data, int num) {

        StringBuilder result = new StringBuilder();

        for(int i = data.length(); i < num; i++){
            result.append('0');
        }

        result.append(data);

        return result.toString();
    }

    public static String must6chars(String data) {

        return mustXchars(data, 6);
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
