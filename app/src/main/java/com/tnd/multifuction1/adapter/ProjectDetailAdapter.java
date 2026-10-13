package com.tnd.multifuction1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.model.Project;

import java.util.List;

public class ProjectDetailAdapter extends BaseAdapter {


    private List<Project> list;
    private Context context;

    public ProjectDetailAdapter(List<Project> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list == null || list.size() == 0 ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null || list.size() == 0 ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_project_detail, null);
            vh.tvSampleName = convertView.findViewById(R.id.tv_checker);
            vh.tvProject = convertView.findViewById(R.id.tv_project);
            vh.tvSampleNo = convertView.findViewById(R.id.tv_single_xlz);
            convertView.setTag(vh);
        }
        vh = (ViewHolder) convertView.getTag();
//        Project p = list.get(position);
//        vh.tvSampleNo.setText(p.sampleNo);
//        vh.tvProject.setText(p.projectName);
//        vh.tvSampleName.setText(p.sampleName);

        return convertView;
    }

    static class ViewHolder {

        TextView tvSampleNo;
        TextView tvProject;
        TextView tvSampleName;
    }

}
