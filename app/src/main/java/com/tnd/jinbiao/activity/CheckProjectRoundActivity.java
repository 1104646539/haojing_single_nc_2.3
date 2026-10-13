package com.tnd.jinbiao.activity;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.tnd.jinbiao.adapter.ResultListViewAdapter;
import com.tnd.jinbiao.base.BaseActivity;
import com.tnd.jinbiao.model.LineModel;
import com.tnd.multifuction1.R;


public class CheckProjectRoundActivity extends BaseActivity {

	private ListView listview = null;
	private ResultListViewAdapter adapter = null;
	private List<LineModel> list = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check_projectround_edit);

		adapter = new ResultListViewAdapter(this, list);
		listview = (ListView) findViewById(R.id.check_project_round_listview);
		listview.setAdapter(adapter);
	}

	public void ClickBack(View v) {
		this.back();
	}

}
