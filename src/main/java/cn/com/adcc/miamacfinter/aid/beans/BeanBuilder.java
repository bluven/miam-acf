package cn.com.adcc.miamacfinter.aid.beans;

import cn.com.adcc.miamacfinter.aid.utils.CRC_Calculate;
import cn.com.adcc.miamacfinter.aid.utils.Utils;
import org.apache.commons.lang3.StringUtils;

public class BeanBuilder {

    public static CommandFileBean build(int fileNum, String msg, String cmuLabel){
        return build('G', 'W', '1',  cmuLabel, msg, fileNum);
    }

    //构建Msg的CommandFileBean
    public static CommandFileBean build(char dst, char purpose, char origin,
                                         String label, String msg, int fileNum) {

        final int WORD_NUM_LIMIT = 632;

        //定义变量
        CommandFileBean fileBean = new CommandFileBean();

        msg = new StringBuilder().append(origin).append(purpose).append(dst).append((char)0).append((char)0).append(msg).toString();

        //判定Msg长度,

        // 一个LDU最多只能有255个word，其中还必须包含SOT及EOT，这样最多只能有253个word用来传输，一个full word只能传输2.5个字节，253 * 2.5 = 632.5

        int lduNum = Math.floorDiv(msg.length(), WORD_NUM_LIMIT) + 1;

        for(int i=0; i < lduNum; i++) {

            String  partialMsg;

            EOTBean.EOTType eotType ;

            if(i == lduNum -1){
                partialMsg = msg.substring(i*632);
                eotType = EOTBean.EOTType.Final;
            } else {
                eotType = EOTBean.EOTType.notFinal;
                partialMsg = msg.substring(i*WORD_NUM_LIMIT, (i+1) * WORD_NUM_LIMIT);
            }

            CommandLDUBean lastlduBean = null;

            lastlduBean = GetLDUBeans(dst, purpose, origin, label, partialMsg, fileNum, i, eotType);

            fileBean.appendLDUBean(lastlduBean);
        }

        return fileBean;
    }

    public static CommandLDUBean GetLDUBeans(char dst, char purpose, char origin, String label,
                                             String Msg, int fileNum, int lduNum, EOTBean.EOTType eotType) {

            CommandLDUBean lduBean=new CommandLDUBean();

            //计算WordCount

            // msg内容是符合ISO-5字符集的，不会出现双字节字符，每个429word只能传输2.5个字节, 所以wordCount=msg.length / 2.5

            double mod = (2*Msg.length())%5;
            int wordCount = (int)(Msg.length()/2.5);

            //构建DataBean
            char[] dataChars=Msg.toCharArray();
            StringBuilder data=new StringBuilder();

            for(char c:dataChars)
            {
                String ch= Integer.toHexString(c).toUpperCase();

                if (ch.length()==1)
                {
                    data.insert(0,ch);
                    data.insert(0,'0');
                }
                else
                {
                    data.insert(0,ch);
                }
            }

            for (int i=1;i<=wordCount;i++)
            {
                int k=data.length()-5*i;
                String hex=data.substring(k,k+5);
                DataBean dataBean=new DataBean(hex,label);
                lduBean.dataBeans.add(dataBean);
            }

            if (mod!=0)
            {
                String lastHex=data.substring(0,(int)mod);
                PartialDataBean dataBean=new PartialDataBean(lastHex,label);
                lduBean.dataBeans.add(dataBean);
            }

            //将字符进行16进制转换，并进行切分构建 DataBean
            lduBean.setRtsBean(new RTSBean(dst, lduBean.getWordCount(), label));
            lduBean.setSotBean(new SOTBean(fileNum, lduNum, label));

            // short crc = CRC_Calculate.CRC429();
            String CRC = getMsgCrc(Msg);

            CRC=StringUtils.leftPad(CRC,4,'0');

            lduBean.setEotBean(new EOTBean(eotType, CRC,label));

           // fileBean.LDUBeans.add(LDUBean);
            return  lduBean;
    }

    public static String getMsgCrc(String msg)
    {
        //声明临时变量
        byte[] toCrc= new byte[msg.length()];
        for (int i=0;i<msg.length();i++)
        {
            char c=msg.charAt(i);
            byte h= Utils.reverseBitsByte(c);
            toCrc[i]=h;
        }

        short sh=  CRC_Calculate.CRC429(toCrc, new byte[0]);

        String crc= Integer.toHexString(sh).toUpperCase();
        if(crc.length()==8)
        {
            crc=crc.substring(4,8);
        }

        return StringUtils.leftPad(crc,4,'0');

    }


}
