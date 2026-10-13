package com.tnd.jinbiao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tnd.jinbiao.base.BaseActivity;
import com.tnd.multifuction1.R;


public class CheckProjectActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_main);
	}

	public void ClickShowJin(View v) {
		Intent intent = new Intent(CheckProjectActivity.this,CheckProjectLineActivity.class);
		intent.putExtra("source", "1");
		startActivity(intent);
	}

	public void ClickHiddenJIn(View v) {
		Intent intent = new Intent(CheckProjectActivity.this,CheckProjectLineActivity.class);
		intent.putExtra("source", "2");
		startActivity(intent);
	}

	public void ClickBiJin(View v) {
		Intent intent = new Intent(CheckProjectActivity.this,CheckProjectLineActivity.class);

		intent.putExtra("source", "3");
		startActivity(intent);
	}

	public void ClickRound(View v) {
		Intent intent = new Intent(CheckProjectActivity.this,CheckProjectLineActivity.class);

		intent.putExtra("source", "4");
		startActivity(intent);
	}

	public void ClickZhen(View v) {
		Intent intent = new Intent(CheckProjectActivity.this,CheckProjectLineActivity.class);

		intent.putExtra("source", "5");
		startActivity(intent);
	}

	public void ClickFan(View v) {
		Intent intent = new Intent(CheckProjectActivity.this,CheckProjectLineActivity.class);

		intent.putExtra("source", "6");
		startActivity(intent);
	}

	public void ClickBack(View v) {
		this.back();
	}
}
