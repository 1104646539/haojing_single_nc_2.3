package com.tnd.jinbiao.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tnd.jinbiao.base.BaseActivity;
import com.tnd.jinbiao.model.SharedPreferencesUtil;
import com.tnd.multifuction1.R;

public class NetWorkSetting extends BaseActivity {

	//private EditText port = null;
	//private EditText bo = null;
private EditText min_txt;
	// private String[] port_str = null;
	// private String[] bo_str = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network_setting_main);

//		port = (EditText) findViewById(R.id.setting_port);
//		bo = (EditText) findViewById(R.id.setting_boTe);
//
//		port.setText(SharedPreferencesUtil.getObjWithkey(NetWorkSetting.this,
//				ToolUtils.PORT_KEY));
//		bo.setText(SharedPreferencesUtil.getObjWithkey(NetWorkSetting.this,
//				ToolUtils.BOPING_KEY));
		min_txt=(EditText) findViewById(R.id.min_txt);
		min_txt.setText(SharedPreferencesUtil.getTime(getApplicationContext(), "time")+"");
	}

	public void ClickTrue(View v) {
//		if (port.getText().toString().trim().length() <= 0) {
//			Toast.makeText(NetWorkSetting.this, "请输入端口号", Toast.LENGTH_SHORT)
//					.show();
//			return;
//		}
//
//		if (bo.getText().toString().trim().length() <= 0) {
//			Toast.makeText(NetWorkSetting.this, "请输入波特率", Toast.LENGTH_SHORT)
//					.show();
//			return;
//		}
		if (min_txt.getText().toString().trim().length() <= 0) {
			Toast.makeText(NetWorkSetting.this, "请输入分钟", Toast.LENGTH_SHORT)
					.show();
			return;
		}
//		String port_str = port.getText().toString().trim();
//		String bo_str = bo.getText().toString().trim();
		String time=min_txt.getText().toString().trim();
//		SharedPreferencesUtil.saveObjWithKey(NetWorkSetting.this,
//				ToolUtils.PORT_KEY, port_str);
//
//		SharedPreferencesUtil.saveObjWithKey(NetWorkSetting.this,
//				ToolUtils.BOPING_KEY, bo_str);
		SharedPreferencesUtil.setTime(NetWorkSetting.this,
				"time", Integer.parseInt(time));
		Toast.makeText(NetWorkSetting.this, "设置成功", Toast.LENGTH_SHORT).show();
		finish();
	}

	public void ClickBack(View v) {
		this.back();
	}
}
