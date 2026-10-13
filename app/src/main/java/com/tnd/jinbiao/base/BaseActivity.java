package com.tnd.jinbiao.base;


import android.app.Activity;
import android.content.pm.ActivityInfo;

import com.tnd.multifuction1.MyApplication;

public class BaseActivity extends Activity {

	@Override
	protected void onResume() {

		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.keepScreenOn(getApplicationContext(), false);;
		super.onDestroy();
	}
	public void back() {
		finish();
	}
	
}
