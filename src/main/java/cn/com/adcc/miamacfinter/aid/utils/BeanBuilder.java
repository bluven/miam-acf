package cn.com.adcc.miamacfinter.aid.utils;

import cn.com.adcc.miamacfinter.aid.beans.*;

import java.util.HashMap;
import java.util.Map;

public class BeanBuilder {


    //构建Msg的CommandFileBean
    public CommandFileBean build(String Msg,int fileNum) {
        //定义变量
        CommandFileBean fileBean=new CommandFileBean();
        try {
            //添加报文信息
            Msg='1'+"W"+"G"+(char)0+((char)0+Msg);
            //判定Msg长度,
            if (Msg.length()>627)
            {
                //进行报文拆分
                String firstLDUMsg= Msg.substring(0,626);
                CommandLDUBean firstlduBean =GetLDUBeans(firstLDUMsg,fileNum,0);

                fileBean.appendLDUBean(firstlduBean);

                String lastLDUMsg=Msg.substring(627,Msg.length());
                int lastLDUCount = (int)(lastLDUMsg.length()/632);
                double lastLDUMod=lastLDUMsg.length()%632;
                for(int i=0;i<lastLDUCount;i++)
                {
                    String msg =lastLDUMsg.substring(i*632,(i+1)*632);
                    CommandLDUBean lastlduBean = GetLDUBeans(msg,fileNum,i+1);

                    fileBean.appendLDUBean(lastlduBean);
                }
                if (lastLDUMod!=0)
                {
                    String modMsg=lastLDUMsg.substring(lastLDUCount*632,lastLDUCount*632+(int)lastLDUMod);
                    CommandLDUBean modlduBean = GetLDUBeans(modMsg, fileNum, lastLDUCount);
                    fileBean.appendLDUBean(modlduBean);
                }
            }
            else
            {
                //(valu & 0xFFFF) << 8 | (valu & 0xFFFF) >> 8;
                CommandLDUBean lduBean =GetLDUBeans(Msg,fileNum,0);
                fileBean.appendLDUBean(lduBean);
            }

            return fileBean;
        }
        catch (Exception ex)
        {
            String s= ex.getMessage();

            System.out.print(s);
        }
        finally {
            return  fileBean;
        }
    }

    public String GetMsgCrc(String msg)
    {
        byte[] toCrc= new byte[msg.length()];
        for (int i=0;i<msg.length();i++)
        {
            char c=msg.charAt(i);
            byte h= ReverseBitsByte(c);
            //char j=(char)h;
            toCrc[i]=h;
            //System.out.println(Integer.toHexString(j));
        }

        short sh=  CRC_Calculate.CRC429(toCrc,new byte[0]);

        return Integer.toHexString(sh).toUpperCase();
       // System.out.println(Integer.toHexString(sh));
    }

    //高低位转换
    private byte ReverseBitsByte(int valu)
    {
        //每一位相互交换	12345678 -> 21436587
        valu = (valu & 0x55) << 1 | (valu & 0xAA) >> 1;
        //每两位相互交换	21436587 -> 43218765
        valu = (valu & 0x33) << 2 | (valu & 0xCC) >> 2;
        //每四位相互交换	43218765 -> 87654321
        return (byte)(valu << 4 | valu >> 4);
    }

    public CommandLDUBean GetLDUBeans(String Msg,int fileNum,int lduNum) {
            CommandLDUBean LDUBean=null;
            LDUBean=new CommandLDUBean();
            //计算WordCount
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
                DataBean dataBean=new DataBean(hex,"304");
                LDUBean.appendData(dataBean);
            }

            if (mod!=0)
            {
                String lastHex=data.substring(0,(int)mod);
                DataBean dataBean=new DataBean(lastHex,"304");
                LDUBean.appendData(dataBean);
            }

            //将字符进行16进制转换，并进行切分构建 DataBean
            LDUBean.setRtsBean(new RTSBean('G', wordCount, "304"));
            LDUBean.setSotBean(new SOTBean(fileNum,0,"304"));

            // short crc = CRC_Calculate.CRC429();
            String CRC= GetMsgCrc(Msg);

            if(CRC.length()==8)
            {
                CRC=CRC.substring(4,7);
            }

            LDUBean.setEotBean(EOTBean.newFinalEOT(CRC, "304"));
           // fileBean.LDUBeans.add(LDUBean);
            return  LDUBean;
            //Integer.toHexString();
    }
}
