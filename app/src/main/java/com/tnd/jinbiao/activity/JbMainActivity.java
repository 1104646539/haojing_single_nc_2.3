package com.tnd.jinbiao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.db.DbHelper;
import com.tnd.multifuction1.util.SerialUtils;

public class JbMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jb_main);
        DbHelper.InitDb(getApplicationContext());
        SerialUtils.InitSerialPort(this);
    }
    public void ClickCheck(View v) { //样品检测
        Intent intent = new Intent(this, CheckSelectProjectActivity.class);
        startActivity(intent);
    }

    public void ClickProject(View v) { //项目管理

        Intent intent = new Intent(this, ProjectManagerActivity.class);
        startActivity(intent);
    }

    public void ClickSelect(View v) {  //功能选项

        Intent intent = new Intent(this, SelectActivity.class);
        startActivity(intent);
    }

    public void ClickExit(View v) {   //进出卡
        SerialUtils.CardOutOrIn();
//		ToolUtils.OpenSerialPort(getApplicationContext(), "Card_OutorIn",3);
    }

}