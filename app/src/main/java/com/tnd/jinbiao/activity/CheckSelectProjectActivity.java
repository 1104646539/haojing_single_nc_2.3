package com.tnd.jinbiao.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.tnd.jinbiao.base.BaseActivity;
import com.tnd.multifuction1.R;

public class CheckSelectProjectActivity extends BaseActivity {

	private static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_select_project);
		context = getApplicationContext();
	}

	public void ClickLin(View v) {
		Intent intent1 = new Intent(context, CheckActivity.class);
		intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
		intent1.putExtra("source", "1");// 定量
		context.startActivity(intent1);
	}

	public void ClickDinXin(View v) {
		Intent intent2 = new Intent(context, CheckActivity.class);
		intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
		intent2.putExtra("source", "2");// 定性
		context.startActivity(intent2);

	}

	public void ClickExit(View v) {
		this.back();
	}

	public static Handler mStartActivityHander = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 101:
				Intent intent1 = new Intent(context, CheckActivity.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
				intent1.putExtra("source", "1");// 定量
				context.startActivity(intent1);

				break;
			case 102:
				Intent intent2 = new Intent(context, CheckActivity.class);
				intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
				intent2.putExtra("source", "2");// 定性
				context.startActivity(intent2);

				break;
			}
		}
	};
}
