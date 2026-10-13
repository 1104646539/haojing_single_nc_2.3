package com.tnd.jinbiao.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.tnd.jinbiao.base.BaseActivity;
import com.tnd.multifuction1.R;

public class SelectActivity extends BaseActivity {

	private static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_main);
		context = this;
	}

	public void ClickLine(View v) {
		Intent intent = new Intent(this, SampleLineActivity.class);
		startActivity(intent);
	}

	public void ClickNetSetting(View v) {
		Intent intent = new Intent(SelectActivity.this, NetWorkSetting.class);
		startActivity(intent);
	}

	public void ClickResult(View v) {
		Intent intent = new Intent(SelectActivity.this, ResultActivity.class);
		startActivity(intent);
	}

	public void ClickProduct(View v) {

	}

	public void ClickBack(View v) {
		this.back();
	}

	public static Handler mStartActivityHander = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 104:
				Intent intent = new Intent(context, SampleLineActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
				context.startActivity(intent);

				break;
			}
		}
	};
}
