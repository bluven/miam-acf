package cn.com.adcc.miamacfinter.aid.beans;

import cn.com.adcc.miamacfinter.aid.utils.CRC_Calculate;
import cn.com.adcc.miamacfinter.aid.utils.Utils;
import org.apache.commons.lang3.StringUtils;

public class BeanBuilder {

    public static CommandFileBean build(int fileNum, String msg, String cmuLabel){
        return build('G', 'W', '1',  cmuLabel, msg, fileNum);
    }
    //构建Msg的CommandFileBean
    public static CommandFileBean build(char dst, char purpose, char origin, String label,
                                 String Msg, int fileNum) {
        //定义变量
        CommandFileBean fileBean=new CommandFileBean();
        try
        {
            Msg = new StringBuilder().append(origin).append(purpose).append(dst).append((char)0).append((char)0).append(Msg).toString();
            
            //判定Msg长度,
            if (Msg.length()>632)
            {
                //进行报文拆分
                String firstLDUMsg = Msg.substring(0,632);

                CommandLDUBean firstlduBean = GetLDUBeans(dst, purpose, origin, label, firstLDUMsg,fileNum,0, EOTBean.EOTType.notFinal);

                fileBean.appendLDUBean(firstlduBean);

                String endLDUMsg=Msg.substring(632,Msg.length());
                int endLDUCount = (int)(endLDUMsg.length()/632);
                double endLDUMod=endLDUMsg.length()%632;

                for(int i=0;i<endLDUCount;i++)
                {
                    String ldu = endLDUMsg.substring(i*632,(i+1)*632);

                    CommandLDUBean lastlduBean=null;
                    if ((i== (endLDUCount-1))&(endLDUMod==0))
                    {
                        lastlduBean = GetLDUBeans(dst, purpose, origin, label, ldu, fileNum, i+1, EOTBean.EOTType.Final);
                    }
                    else
                    {
                        lastlduBean = GetLDUBeans(dst, purpose, origin, label, ldu, fileNum, i+1, EOTBean.EOTType.notFinal);
                    }

                    fileBean.appendLDUBean(lastlduBean);
                }
                if (endLDUMod!=0)
                {
                    String modMsg=endLDUMsg.substring(endLDUCount*632, endLDUCount*632+(int)endLDUMod);
                    CommandLDUBean modlduBean = GetLDUBeans(dst, purpose, origin, label, modMsg, fileNum, endLDUCount, EOTBean.EOTType.Final);
                    fileBean.appendLDUBean(modlduBean);
                }
            }
            else
            {
                CommandLDUBean lduBean =GetLDUBeans(dst, purpose, origin, label, Msg,fileNum,0, EOTBean.EOTType.Final);
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

    public static CommandLDUBean GetLDUBeans(char dst, char purpose, char origin, String label,
                                             String Msg, int fileNum, int lduNum, EOTBean.EOTType eotType) {

            CommandLDUBean lduBean=new CommandLDUBean();

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
