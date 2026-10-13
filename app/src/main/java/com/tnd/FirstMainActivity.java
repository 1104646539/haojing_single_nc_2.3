package com.tnd;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tnd.bean.AppInfoBean;
import com.tnd.jinbiao.activity.JbMainActivity;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.activity.MainActivity;
import com.tnd.multifuction1.util.ToolUtils;
import com.tnd.util.APPUtils;

import java.util.List;

public class FirstMainActivity extends Activity implements View.OnClickListener {
    private Button btn_open_1;// 农残模块
    private Button btn_open_2;// 比色法模块
    private Button btn_open_3;// 金标卡模块
    private Button btn_open_4;// ATP荧光模块
    private long exitTime = 0;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_first_main);
        initView();
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("多功能食品安全检测仪" + " V" + ToolUtils.getLocalVersionName(this) + "D");
        btn_open_1 = (Button) findViewById(R.id.btn_open_1);
        btn_open_2 = (Button) findViewById(R.id.btn_open_2);
        btn_open_3 = (Button) findViewById(R.id.btn_open_3);
        btn_open_4 = (Button) findViewById(R.id.btn_open_4);
        btn_open_1.setOnClickListener(this);
        btn_open_2.setOnClickListener(this);
        btn_open_3.setOnClickListener(this);
        btn_open_4.setOnClickListener(this);
    }

    /**
     * 四个按钮的点击时间
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        openApp(v);
    }

    /**
     * 打开app
     *
     * @param v
     */
    private void openApp(View v) {
        List<AppInfoBean> allAppInfo = APPUtils
                .getAllAppInfo(FirstMainActivity.this);
        switch (v.getId()) {
            case R.id.btn_open_1:// 打开农残
                // com.nong.nongcan:就是包名
                for (int i = 0; i < allAppInfo.size(); i++) {
                    if (allAppInfo.get(i).packageName.equals("com.val.pesticide")) {
                        // Intent intent = getPackageManager()
                        // .getLaunchIntentForPackage("com.nong.nongcan");
                        Intent intent = new Intent()
                                .setComponent(new ComponentName(
                                        "com.val.pesticide",
                                        "com.val.pesticide.activity.WelcomActivity"));
                        if (intent != null) {
                            startActivity(intent);
                        }
                    }

                }

                break;
            case R.id.btn_open_2:// 打开多功能
//                for (int i = 0; i < allAppInfo.size(); i++) {
//                    if (allAppInfo.get(i).packageName
//                            .equals("com.tnd.multifuction")) {
//                        // Intent intent = getPackageManager()
//                        // .getLaunchIntentForPackage("com.more.morefunction");
//
//                        Intent intent = new Intent()
//                                .setComponent(new ComponentName(
//                                        "com.tnd.multifuction",
//                                        "com.tnd.multifuction.activity.MainActivity"));
//                        if (intent != null) {
//                            startActivity(intent);
//                        }
//                    }
//
//                }
                startActivity(new Intent(FirstMainActivity.this, MainActivity.class));
                break;
            case R.id.btn_open_3:// 打开金标
                startActivity(new Intent(FirstMainActivity.this, JbMainActivity.class));
//                for (int i = 0; i < allAppInfo.size(); i++) {
//                    if (allAppInfo.get(i).packageName.equals("com.example.usb")) {
//                        // Intent intent = getPackageManager()
//                        // .getLaunchIntentForPackage("com.example.r02_v1");
//                        Intent intent = new Intent()
//                                .setComponent(new ComponentName(
//                                        "com.example.usb",
//                                        "com.example.usb.activity.MainActivity"));
//                        if (intent != null) {
//                            startActivity(intent);
//                        }
//                    }
//
//                }

                break;
            case R.id.btn_open_4:// 打开ATP
                for (int i = 0; i < allAppInfo.size(); i++) {
                    if (allAppInfo.get(i).packageName
                            .equals("com.val.pesticide_atp")) {
                        // Intent intent = getPackageManager()
                        // .getLaunchIntentForPackage("com.atp.atp");
                        Intent intent = new Intent()
                                .setComponent(new ComponentName(
                                        "com.val.pesticide_atp",
                                        "com.val.pesticide.atp.activity.WelcomActivity"));
                        if (intent != null) {
                            startActivity(intent);
                        }
                    }

                }

                break;
            default:
                break;
        }
    }

    /**
     * 点击两次退出程序
     */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
}