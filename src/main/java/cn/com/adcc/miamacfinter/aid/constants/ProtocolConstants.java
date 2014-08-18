package cn.com.adcc.miamacfinter.aid.constants;

/**
 * Created by bluven on 14-8-9.
 */
public class ProtocolConstants {

    public static final int T1_MIN = 0;

    public static final int T1_MAX = 100;

    public static final int T2_MIN = 500;

    public static final int T2_MAX = 700;

    public static final int T3_MIN = 0;

    public static final int T3_MAX = 100;

    public static final int T4_MIN = 15000;

    public static final int T4_MAX = 18000;

    public static final int T5_MIN = 500;

    public static final int T5_MAX = 700;

    public static final int T6_MIN = 50;

    public static final int T6_MAX = 500;

    public static final int T7_MIN = 10;

    public static final int T7_MAX = 100;

    public static final int T8_MIN = 0;

    public static final int T8_MAX = 200;

    public static final int T9_MIN = 2500;

    public static final int T9_MAX = 2700;

    public static final int T10_MIN = 2700;

    public static final int T10_MAX = 3000;

    public static final int T11_MIN = 0;

    public static final int T11_MAX = 100;

    public static final int T12_MIN = 200;

    public static final int T12_MAX = 250;

    public static final int T13_MIN = 0;

    public static final int T13_MAX = 200;

    public static final int T14_MIN = 120000;

    // In-complete file timer, 接收方（sink）用
    // 在多LDU文件发送的情况下， 接收方反馈ACK或NAK后(不考虑最后一个LDU)，启动T14计时器,
    // 在T14倒计前仍未收到下一个LDU的RTS时, 取消当前文件的接收，
    // 并反馈SYN至发送方
    public static final int T14_MAX = 132000;

    public static final int T15_MIN = 0;

    public static final int T15_MAX = 180;

    public static final int T16_MIN = 220;

    public static final int T16_MAX = 330;

    public static final int N1 = 5;

    public static final int N2 = 20;

    public static final int N3 = 5;

    public static final int N4 = 3;

    public static final int N5 = 3;

    public static final int N6 = 3;

    public static final String MISSING_SOT_WORD = "80";

    public static final String LDU_SEQ_NUM_ERR = "81";

    public static final String MISSING_EOT_WORD = "83";

    public static final String LDU_TIME_OUT_ERR = "86";

    public static final String LDU_WORD_COUNT_ERR = "88";

    public static final String FILE_TIME_OUT_ERR = "8E";

    public static final String NEW_FILE_WITH_PRE_INCOMPLETE = "95";
}
