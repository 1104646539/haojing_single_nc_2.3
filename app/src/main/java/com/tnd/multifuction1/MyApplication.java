package com.tnd.multifuction1;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.PowerManager;

import com.tnd.multifuction1.db.DbHelper;
import com.tnd.multifuction1.util.Global;

public class MyApplication extends Application {
    private static PowerManager.WakeLock wakeLock;
    private static Context mContext;
    private static MyApplication myApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        myApp = this;
        DbHelper.InitDb(mContext);
//        ColumnConverterFactory.registerColumnConverter(SampleName.Project.class,new SampleName.ProjectConverter());
//        ColumnConverterFactory.registerColumnConverter(SampleName.ProjectList.class,new SampleName.ProjectListConverter());
        if (!Global.DEBUG) {
//            CrashHandler.getInstance().init(this);
        }
    }

    public static MyApplication getInstance() {
        return myApp;
    }
    @SuppressLint("InvalidWakeLockTag")
    public static void keepScreenOn(Context context, boolean on) {
        if (on) {
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, "==KeepScreenOn==");
            wakeLock.acquire();
        } else {
            if (wakeLock != null) {
                wakeLock.release();
                wakeLock = null;
            }
        }
    }
    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        super.onTerminate();
        keepScreenOn(getApplicationContext(), false);
    }
    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
        keepScreenOn(getApplicationContext(), false);
    }
    @SuppressLint("NewApi")
    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);
        keepScreenOn(getApplicationContext(), false);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}