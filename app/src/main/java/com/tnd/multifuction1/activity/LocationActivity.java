package com.tnd.multifuction1.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.util.Global;
import com.tnd.multifuction1.util.SerialUtils;

import org.jetbrains.annotations.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class LocationActivity extends Activity implements View.OnClickListener {

    private boolean DEBUG = true;
    private static final String TAG = "LocationActivity";
    private Button btnStartLocation;
    private Button btnReturn;
    private Task task;
    private TextView tvState;
    private TextView tvJingdu;
    private TextView tvWeidu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initView();
    }

    private void initView() {

        tvState = findViewById(R.id.tv_location_state);
        tvJingdu = findViewById(R.id.tv_jingdu);
        tvWeidu = findViewById(R.id.tv_location_weidu);
        btnStartLocation = findViewById(R.id.btn_start_location);
        btnStartLocation.setOnClickListener(this);
        btnReturn = findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(this);
    }

    Timer mTimer;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_return:
                finish();
                break;
            case R.id.btn_start_location:
                if ("开始定位".equals(btnStartLocation.getText().toString())) {
                    startTimer();
                    tvState.setText("正在定位...");
                    btnStartLocation.setText("停止定位");
                }else if ("停止定位".equals(btnStartLocation.getText().toString())) {
                    stopTimer();
                    tvState.setText("未定位");
                    btnStartLocation.setText("开始定位");
                }

                break;
        }
    }

    private void startTimer() {

        if (mTimer != null) {
            mTimer = null;
        }
        mTimer = new Timer();
        if (task != null) {
            task.cancel();
            task = null;
        }
        task = new Task();
        mTimer.schedule(task, 0, 10*1000);
    }

    private void stopTimer() {

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    protected void onDestroy() {
        stopTimer();
        super.onDestroy();
    }

    class Task extends TimerTask {
        @Override
        public void run() {
            if (SerialUtils.COM3_SendData(Global.LocationInstruction)) {
                SystemClock.sleep(1000);
                recGPSData();
            }
        }
    }

    private void recGPSData() {

        if(Global.DEBUG) Log.i(TAG, "准备接收GPS数据");
        byte[] data = new byte[1024];
        int pos = 0;
        for (int i = 0; i < 5; i++) {
            byte[] rec = null;
            if ((rec = SerialUtils.COM3_RevData()) != null && pos + rec.length < 1024) {
                System.arraycopy(rec, 0, data, pos, rec.length);
                pos += rec.length;
                if (rec[rec.length - 1] == 0x0a) {
                    break;
                }
            }
            if(isFinishing()) return;
            SystemClock.sleep(100);
        }
        String str = new String(data).replace("\0", "");
        final String[] s = str.replace("OK", "").replace("\n", "").split(",");
        if(DEBUG) Log.i(TAG, "收到的GPS数据为===" + str);
        if (s.length == 2) {
            try {
                final float f = Float.parseFloat(s[0]);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (f != 0.0f) {
                            tvState.setText("定位成功");
                            tvJingdu.setText(s[0]);
                            tvWeidu.setText(s[1]);
                        } else {
                            tvState.setText("定位失败");

                            tvJingdu.setText("");
                            tvWeidu.setText("");
                        }

                    }
                });
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

//    private void recGPSData() {
//
//        if(Global.DEBUG) Log.i(TAG, "准备接收GPS数据");
//        byte[] data = new byte[1024];
//        int pos = 0;
//        for (int i = 0; i < 5; i++) {
//            byte[] rec = null;
//            if ((rec = SerialUtils.COM3_RevData()) != null && pos + rec.length < 1024) {
//                System.arraycopy(rec, 0, data, pos, rec.length);
//                pos += rec.length;
//                if (rec[rec.length] == 0x0a) {
//                    break;
//                }
//            }
//            if(isFinishing()) return;
//            SystemClock.sleep(100);
//        }
//        String s = new String(data).replace("\0", "");
//        if (!s.contains("GPRMC")) {
//            return;
//        }
//        String[] str = s.split("\n");
//
//        Location location = null;
//        for (int i = 0; i < str.length; i++) {
//            if (str[i].contains("GPRMC")) {
//                location = GPSUtil.getLocationInfo(str[i].split(","));
//            }
//        }
//        if (location != null) {
//            final Location finalLocation = location;
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (finalLocation.locationSuccess) {
//                        tvState.setText("已定位");
//                        tvJingdu.setText("");
//                        tvWeidu.setText("");
//                    } else {
//                        tvState.setText("未定位");
//                        tvJingdu.setText(finalLocation.jingdu);
//                        tvWeidu.setText(finalLocation.weidu);
//                    }
//
//                }
//            });
//        }
//    }

}
