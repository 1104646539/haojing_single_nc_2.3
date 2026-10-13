package com.tnd.multifuction1.util;


import com.tnd.multifuction1.model.Project;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017-12-18.
 */

public class Global {

    public static Project project;

    public static final String DEBUG_DEVICE_ID = "CESHIZHUANYONGID";

    /**
     * 上传方式，1代表自动上传，2代表手动上传
     */
    public static int uploadModel = 2;

    public static final byte[] FIT_LIGHT = "BALA".getBytes();

//    public static int cardReactionTime = 1 * 60;
    /**
     * 卡片反应时间
     */
    public static int cardReactionTime ;
    /**
     * 卡片加热时间
     */
    public static int cardWarmTime;
    /**
     * 分光反应时间
     */
    public static int fenguangReactionTime;
    /**
     * 检测单位
     */
    public static String checkOrg;
    /**
     * 检测人员
     */
    public static String inspector;

    public static final byte[] LocationInstruction = "GetLocation".getBytes();

    public static final byte[] LightSame = "LightSame".getBytes();

    public static final boolean DEBUG = false;

    public static final String DATA_BASE_NAME = "data.db";
    /**
     * 数据库版本号
     */
    public static final int DATABASE_VERSION = 1;

    /**
     *T2主板通信串口名称
     */
    public static final String COM3_T2 = "/dev/ttyAMA3";
    /**
     *T3主板通信串口名称
     */
    public static final String COM3_T3 = "/dev/ttySAC3";
    /**
     *T2主板打印串口名称
     */
    public static final String COM4_T2 = "/dev/ttyAMA4";
    /**
     * T3主板打印串口名称
     */
    public static final String COM4_T3 = "/dev/ttySAC4";

    public static final int CHANNEL_COUNT = 20;

    public static final byte[] GETALLDATA = "GetAllData".getBytes();
    /**
     *已经被选中的通信串口的设备标号
     */
    public static int DEV_COM3 = -1;
    /**
     *已经被选中的打印串口的设备标号
     */
    public static int DEV_COM4 = -1;
    /**
     *通信串口波特率
     */
    public static final int BAUD_COM3 = 115200;
    /**
     *打印串口波特率
     */
    public static final int BAUD_COM4 = 9600;
    /**
     * 串口数据位长度
     */
    public static final int SERIAL_PORT_DATABITS = 8;
    /**
     * 串口停止位长度
     */
    public static final int SERIAL_PORT_STOPBITS = 1;


    public static Charset ENCODE = Charset.forName("gbk");

    /**
     * 检测指令
     */
    public static byte[] TEST_INSTRUCTION = "Test".getBytes();
    /**
     * 开关盖指令
     */
    public static byte[] OPEN_CLOSE_LID_INSTRUCTION = "OpenOrCloseLid".getBytes();

    /**
     * 进出卡指令
     */
    public static byte[] OUT_IN_CARD_INSTRUCTION = "CardOutOrIn".getBytes();


    /**
     * 反应指令
     */
//    public static byte[] REACTION_INSTRUCTION = "FYTest".getBytes();
    /**
     *比色指令
     */
    public static byte[] BS_INSTRUCTION = "BSTest".getBytes();
    /**
     * 获取温度指令
     */
    public static byte[] TEMP_INSTRUCTION = "GETTEMP".getBytes();


    public static Integer limitValue = 400;

//    public static List<String> sampleList = new ArrayList<>();

    public static String check_state_no = "";

    public static String check_department = "";

    public static String device_id = "device_天迈";


    /**
     *设备名称
     */
    public static String ASSET_NAME = "";


    /**
     *设备编码
     */
    public static String ASSET_CODE = "";



    public static float singleXlz = 50;

//    public static String uploadUrl = "http://kj.ahlssp.net/jc/dtrans";
//    public static String TESTING_UNIT_NAME="五显镇快检站";
//    public static String TESTING_UNIT_NUMBER="341523111";
    public static String uploadUrl = "http://112.5.17.83:8081/get.aspx";
    public static String TESTING_UNIT_NAME="0109001";
    public static String TESTING_UNIT_NUMBER="0109001";

//    public static String uploadUrl = "http://27.154.242.234:9081/map/rtmap/uploadTestDateFromAndroid.do";
//    public static String TESTING_UNIT_NAME="xinnonghui";
//    public static String TESTING_UNIT_NUMBER="std123456";

    public static String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(date);
    }
    
//    GETTEMP
//    返回 36.1\n;
//
//    BSTest  比色检测
//    返回 OK\n
//
//    FYTest  反应检测
//    返回 OK\n;
//
//    Test
//    返回 255,255,255|。。。。。。。。。\n
//
//            BALA
//    OK\n

}
