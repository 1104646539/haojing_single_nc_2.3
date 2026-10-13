package com.tnd.multifuction1.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.resource.SPResource;
import com.tnd.multifuction1.util.Global;

import org.jetbrains.annotations.Nullable;

public class UploadSettingActivity extends Activity {


    private EditText etCheckStationNo;
    private EditText etCheckOrg;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_setting);

        etCheckStationNo = findViewById(R.id.et_check_station_number);
        etCheckOrg = findViewById(R.id.et_check_org);

        sp = getSharedPreferences(SPResource.FILE_NAME, MODE_PRIVATE);
        String checkStationNo = sp.getString(SPResource.KEY_CHECK_STATION_NUMBER, "");
        String checkOrg = sp.getString(SPResource.KEY_CHECK_ORGANIZATION, "");

        etCheckStationNo.setText(checkStationNo);
        etCheckOrg.setText(checkOrg);
    }

    @Override
    protected void onDestroy() {
        saveSp();
        super.onDestroy();
    }

    private void saveSp() {
        String checkStateNo = etCheckStationNo.getText().toString();
        String checkOrg = etCheckOrg.getText().toString();
        sp.edit().putString(SPResource.KEY_CHECK_STATION_NUMBER, checkStateNo).commit();
        sp.edit().putString(SPResource.KEY_CHECK_ORGANIZATION, checkOrg).commit();
        Global.check_state_no = checkStateNo;
        Global.check_department = checkOrg;
    }
}
