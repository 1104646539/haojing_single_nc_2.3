package com.tnd.jinbiao.adapter;

import java.util.List;

import com.tnd.jinbiao.model.CardCompanyModel;
import com.tnd.multifuction1.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProductAdapter extends BaseAdapter {

	private Context context = null;
	private List<CardCompanyModel> list = null;

	public ProductAdapter(Context context, List<CardCompanyModel> list) {
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

		ViewHolder holder = null;
		if (arg1 == null) {
			holder = new ViewHolder();
			arg1 = View.inflate(context, R.layout.product_listview_item, null);
			holder.name = (TextView) arg1
					.findViewById(R.id.card_listview_item_name);
			holder.start = (TextView) arg1
					.findViewById(R.id.card_listview_item_start);
			holder.end = (TextView) arg1
					.findViewById(R.id.card_listview_item_end);
			holder.ct = (TextView) arg1
					.findViewById(R.id.card_listview_item_ct);
			holder.ct_width = (TextView) arg1
					.findViewById(R.id.card_listview_item_ct_width);
			arg1.setTag(holder);
		}
		holder = (ViewHolder) arg1.getTag();

		CardCompanyModel model = list.get(arg0);
		holder.name.setText(model.getName());
		holder.start.setText(model.getScanStart());
		holder.end.setText(model.getScanEnd());
		holder.ct.setText(model.getCTPeakDistance());
		holder.ct_width.setText(model.getCTPeakWidth());

		return arg1;
	}

	static class ViewHolder {
		TextView name;
		TextView start;
		TextView end;
		TextView ct;
		TextView ct_width;
	}
}
