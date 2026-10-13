package com.tnd.jinbiao.adapter;

import java.util.List;

import com.tnd.jinbiao.model.LineModel;
import com.tnd.multifuction1.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ResultListViewAdapter extends BaseAdapter {

	private Context context = null;
	private List<LineModel> list = null;

	public ResultListViewAdapter(Context cotext, List<LineModel> list) {
		this.context = cotext;
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

		ViewHoler holder = null;

		if (arg1 == null) {

			holder = new ViewHoler();
			arg1 = View.inflate(context, R.layout.line_and_round_listview_item,	null);
			holder.project_name = (TextView) arg1.findViewById(R.id.check_project_linadnround_name);
			holder.start = (TextView) arg1.findViewById(R.id.check_project_linadnround_start);
			holder.end = (TextView) arg1.findViewById(R.id.check_project_linadnround_end);
			holder.ct = (TextView) arg1.findViewById(R.id.check_project_linadnround_ct);
			holder.ct_width = (TextView) arg1.findViewById(R.id.check_project_linadnround_ct_width);
			holder.xian = (TextView) arg1.findViewById(R.id.check_project_linadnround_xian);
			holder.value = (TextView) arg1.findViewById(R.id.check_project_linadnround_value);

			holder.k = (TextView) arg1.findViewById(R.id.check_project_linadnround_k);

			holder.b = (TextView) arg1.findViewById(R.id.check_project_linadnround_b);

			holder.x = (TextView) arg1.findViewById(R.id.check_project_linadnround_x);

			holder.p = (TextView) arg1.findViewById(R.id.check_project_linadnround_p);

			holder.unitZh = (TextView) arg1.findViewById(R.id.check_project_linadnround_long);

			arg1.setTag(holder);
		}else{
			holder = (ViewHoler) arg1.getTag();
		}

		LineModel model = list.get(arg0);
		holder.project_name.setText(model.getName());
		holder.start.setText(model.getScanStart());
		holder.end.setText(model.getScanEnd());
		holder.ct.setText(model.getCTDistance());
		holder.ct_width.setText(model.getCTWidth());
		holder.xian.setText(model.getJcx());
		holder.value.setText(model.getLjz());
		holder.k.setText(model.getA1());
		holder.b.setText(model.getA2());
		holder.x.setText(model.getX0());
		holder.p.setText(model.getP());
		holder.unitZh.setText(model.getConcentrateUnit());

		return arg1;
	}

	static class ViewHoler {
		TextView project_name;
		TextView start;

		TextView end;
		TextView ct;

		TextView ct_width;
		TextView xian;

		TextView value;
		TextView k;

		TextView b;
		TextView x;
		TextView p;
		TextView unitZh;

	}
}
