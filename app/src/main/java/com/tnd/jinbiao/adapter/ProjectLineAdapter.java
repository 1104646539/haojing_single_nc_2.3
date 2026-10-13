package com.tnd.jinbiao.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tnd.multifuction1.R;

public class ProjectLineAdapter extends BaseAdapter {

	private Context context;

	public ProjectLineAdapter(Context cotext) {
		this.context = cotext;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			arg1 = View.inflate(context, R.layout.check_project_line_edit, null);
		}

		return arg1;
	}

}
