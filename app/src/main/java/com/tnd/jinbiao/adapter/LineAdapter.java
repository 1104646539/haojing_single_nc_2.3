package com.tnd.jinbiao.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tnd.jinbiao.model.LineModel;
import com.tnd.multifuction1.R;

import java.util.List;

public class LineAdapter extends BaseAdapter {

	private Context context = null;
	private List<LineModel> list = null;

	public LineAdapter(Context context, List<LineModel> list) {
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
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder vh = null;
		View view;
		if(convertView == null){
			view = View.inflate(context, R.layout.item_project1, null);
			vh = new ViewHolder();
			vh.tvProject = (TextView) view.findViewById(R.id.tv_project_item_project);
			vh.tvScanStart = (TextView) view.findViewById(R.id.tv_scanStart_item_project);
			vh.tvScanEnd = (TextView) view.findViewById(R.id.tv_scanEnd_item_project);
			vh.tvPeakWidth = (TextView) view.findViewById(R.id.tv_peak_width_item_project);
			vh.tvPeakDistance = (TextView) view.findViewById(R.id.tv_peak_distance_item_project);
			vh.tvLjz = (TextView) view.findViewById(R.id.tv_ljz_item_project);
			vh.tvJcx = (TextView) view.findViewById(R.id.tv_jcx_item_project);
			vh.tvA1 = (TextView) view.findViewById(R.id.tv_a1_item_project);
			vh.tvA2 = (TextView) view.findViewById(R.id.tv_a2_item_project);
			vh.tvX0 = (TextView) view.findViewById(R.id.tv_x0_item_project);
			vh.tvP = (TextView) view.findViewById(R.id.tv_p_item_project);
			vh.tvUnit = (TextView) view.findViewById(R.id.tv_unit_item_project);
			view.setTag(vh);

		}else{
			view = convertView;
			vh = (ViewHolder) view.getTag();
		}
		LineModel model = list.get(position);
		vh.tvProject.setText(model.name);
		vh.tvScanStart.setText(model.ScanStart);
		vh.tvScanEnd.setText(model.ScanEnd);
		vh.tvPeakWidth.setText(model.CTWidth);
		vh.tvPeakDistance.setText(model.CTDistance);
		vh.tvLjz.setText(model.Ljz);
		vh.tvJcx.setText(model.Jcx);
		vh.tvA1.setText(model.A1);
		vh.tvA2.setText(model.A2);
		vh.tvX0.setText(model.X0);
		vh.tvP.setText(model.P);
		vh.tvUnit.setText(model.ConcentrateUnit);

		return view;
	}

	static class ViewHolder {

		TextView tvProject;
		TextView tvScanStart;
		TextView tvScanEnd;
		TextView tvPeakWidth;
		TextView tvPeakDistance;
		TextView tvLjz;
		TextView tvJcx;
		TextView tvA1;
		TextView tvA2;
		TextView tvX0;
		TextView tvP;
		TextView tvUnit;
	}

}
