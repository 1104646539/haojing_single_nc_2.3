package com.tnd.multifuction1.util;

import android.app.Activity;
import android.util.Log;

import com.friendlyarm.AndroidSDK.HardwareControler;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by Administrator on 2017-12-18.
 */

public class SerialUtils {


    private static final String TAG = "SerialUtils";
    /**
     * 初始化串口
     */
    public static final void InitSerialPort(Activity act){
        try{
        int dev = -1;
        if(Global.DEV_COM3 == -1){
            dev = HardwareControler.openSerialPort(Global.COM3_T3, Global.BAUD_COM3, Global.SERIAL_PORT_DATABITS, Global.SERIAL_PORT_STOPBITS);
            if(dev < 0){  //不是T3主板
                dev = HardwareControler.openSerialPort(Global.COM3_T2, Global.BAUD_COM3, Global.SERIAL_PORT_DATABITS, Global.SERIAL_PORT_STOPBITS);
                if(dev < 0) {  //不是T3主板也不是T2主板，串口识别失败
                    APPUtils.showToast(act, "通信串口识别失败");
                }else{
                    Global.DEV_COM3 = dev;
                }
            }else{
                Global.DEV_COM3 = dev;
            }
        }
        if(Global.DEV_COM4 == -1){
            dev = HardwareControler.openSerialPort(Global.COM4_T3, Global.BAUD_COM4, Global.SERIAL_PORT_DATABITS, Global.SERIAL_PORT_STOPBITS);
            if(dev < 0){  //不是T3主板
                dev = HardwareControler.openSerialPort(Global.COM4_T2, Global.BAUD_COM4, Global.SERIAL_PORT_DATABITS, Global.SERIAL_PORT_STOPBITS);
                if(dev < 0) {  //不是T3主板也不是T2主板，串口识别失败
                    APPUtils.showToast(act, "打印串口识别失败");
                }else{
                    Global.DEV_COM4 = dev;
                }
            }else{
                Global.DEV_COM4 = dev;
            }
        }
        }catch (UnsatisfiedLinkError e){
            APPUtils.showToast(act, "串口识别失败");
        }
//        System.out.println("打开的通信串口设备id为：" + Global.DEV_COM3);
//        System.out.println("打开的打印串口设备id为：" + Global.DEV_COM4);
    }

    public static synchronized void CardOutOrIn(){
        if(Global.DEV_COM3 > 0){
            HardwareControler.write(Global.DEV_COM3, "Card_OutorIn".getBytes(Charset.forName("gb2312")));
        }
    }

    /**
     * COM3发送数据
     * @param data
     * @return
     */
    public static synchronized boolean COM3_SendData(byte[] data){

        if(Global.DEV_COM3 > 0){
            return sendData(data, Global.DEV_COM3);
        }
        return false;
    }

    private static synchronized boolean sendData(byte[] data,int fd) {
        try {
            Log.d(TAG,"sendData = "+new String(data,"GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] bs = new byte[1024];
        HardwareControler.read(fd, bs, bs.length);
        int write = HardwareControler.write(fd, data);
        if(write == data.length){
            Log.i(TAG, "串口数据发送成功。。。");
            return true;
        }else {
            Log.i(TAG, "串口数据发送失败。。。");
            return false;
        }
    }


    /**
     * 读取COM3接收到的数据
     * @return
     */
    public static synchronized byte[] COM3_RevData(){

        byte[] rec = new byte[1024];
        int len = HardwareControler.read(Global.DEV_COM3, rec, rec.length);

        if(len == 0){
            return null;
        }
        byte[] data = new byte[len];
        System.arraycopy(rec, 0, data, 0, len);
        return data;
    }

    /**
     * COM4发送数据
     * @param data
     * @return
     */
    public static synchronized boolean COM4_SendData(byte[] data){

        if(Global.DEV_COM4 > 0){
            return sendData(data, Global.DEV_COM4);
        }
        return false;
    }
}
