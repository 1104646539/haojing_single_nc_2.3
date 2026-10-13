package com.tnd.multifuction1.util;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Administrator on 2017-11-28.
 */

public class APPUtils {


    public static void showToast(final Activity activity, final String content){
        showToast(activity, content,false);
    }

    public static void showToast(final Activity activity, final String content, final boolean longShow){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, content, longShow ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
            }
        });
    }
}
