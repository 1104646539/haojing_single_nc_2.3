package com.tnd.jinbiao.adapter;

import java.util.List;

import com.tnd.jinbiao.model.ShiJiModel;
import com.tnd.multifuction1.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class CheckShijiAdapter extends BaseAdapter {

	private Context context = null;
	private List<ShiJiModel> list = null;

	public CheckShijiAdapter(Context context, List<ShiJiModel> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holer = null;

		if (arg1 == null) {
			holer = new ViewHolder();
			arg1 = View.inflate(context, R.layout.check_shiji_listview_item,null);
			holer.box = (CheckBox) arg1.findViewById(R.id.shiji_listview_box);
			holer.name = (TextView) arg1.findViewById(R.id.shiji_listview_name);
			holer.code = (TextView) arg1.findViewById(R.id.shiji_listview_code);
			arg1.setTag(holer);
		}

		holer = (ViewHolder) arg1.getTag();

		final ShiJiModel model = list.get(arg0);

		holer.box.setChecked(model.getIs_check());

		holer.box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				model.setIs_check(arg1);
			}
		});

		holer.name.setText(model.getName());
		holer.code.setText(model.getCode());
		return arg1;
	}

	static class ViewHolder {
		CheckBox box;
		TextView name;
		TextView code;
	}

}
