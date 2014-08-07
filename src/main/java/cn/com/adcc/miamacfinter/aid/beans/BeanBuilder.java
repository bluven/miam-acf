package cn.com.adcc.miamacfinter.aid.beans;

public class BeanBuilder {

    //构建Msg的CommandFileBean
    public static CommandFileBean build(char dst, char purpose, char origin, String label,
                                 String Msg, int fileNum, EOTBean.EOTType eotType) {
        //定义变量
        CommandFileBean fileBean=new CommandFileBean();
        try
        {
            Msg = new StringBuilder().append(origin).append(purpose).append(dst).append(Msg).toString();
            
            //判定Msg长度,
            if (Msg.length()>627)
            {
                //进行报文拆分
                String firstLDUMsg = Msg.substring(0,626);

                CommandLDUBean firstlduBean = GetLDUBeans(dst, purpose, origin, label, firstLDUMsg,fileNum,0, eotType);

                fileBean.appendLDUBean(firstlduBean);

                String endLDUMsg=Msg.substring(627,Msg.length());
                int endLDUCount = (int)(endLDUMsg.length()/632);
                double endLDUMod=endLDUMsg.length()%632;

                for(int i=0;i<endLDUCount;i++)
                {
                    String ldu = endLDUMsg.substring(i*632,(i+1)*632);
                    CommandLDUBean lastlduBean = GetLDUBeans(dst, purpose, origin, label, ldu, fileNum, i+1, eotType);

                    fileBean.appendLDUBean(lastlduBean);
                }
                if (endLDUMod!=0)
                {
                    String modMsg=endLDUMsg.substring(endLDUCount*632, endLDUCount*632+(int)endLDUMod);
                    CommandLDUBean modlduBean = GetLDUBeans(dst, purpose, origin, label, modMsg, fileNum, endLDUCount, eotType);
                    fileBean.appendLDUBean(modlduBean);
                }
            }
            else
            {
                CommandLDUBean lduBean =GetLDUBeans(dst, purpose, origin, label, Msg,fileNum,0, eotType);
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
                DataBean dataBean=new DataBean(hex,label);
                LDUBean.dataBeans.add(dataBean);
            }

            if (mod!=0)
            {
                String lastHex=data.substring(0,(int)mod);
                DataBean dataBean=new DataBean(lastHex,label);
                LDUBean.dataBeans.add(dataBean);
            }

            //将字符进行16进制转换，并进行切分构建 DataBean
            LDUBean.setRtsBean(new RTSBean(dst, wordCount, label));
            LDUBean.setSotBean(new SOTBean(fileNum, lduNum, label));

            // short crc = CRC_Calculate.CRC429();
            String CRC = new String();

            LDUBean.setEotBean(new EOTBean(eotType, CRC,label));

           // fileBean.LDUBeans.add(LDUBean);
            return  LDUBean;
            //Integer.toHexString();
    }
}
