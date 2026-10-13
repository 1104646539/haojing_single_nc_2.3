package com.tnd.multifuction1.util;

import android.util.Log;

/**
 * Created by honghengqiang on 2018/3/14.
 * Log工具类
 */

public class LogUtil {

    public static boolean isShow = true;
    private static StringBuffer stringBuffer = new StringBuffer();

    public static void showDefaultInfo(String... messages){
        stringBuffer.setLength(0);
        if(isShow) {
            for (int i = 0; i < messages.length; i++) {
                stringBuffer.append(messages[i]+"=======");
            }
            Log.d("TAG", stringBuffer.toString());
        }
    }

    public static void showTagInfo(String tag, String... messages){
        stringBuffer.setLength(0);
        if(isShow) {
            for (int i = 0; i < messages.length; i++) {
                stringBuffer.append(messages[i]+"=======");
            }
            Log.d(tag, stringBuffer.toString());
        }
    }

}
