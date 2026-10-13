package com.tnd.multifuction1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tnd.multifuction1.R;

import java.util.ArrayList;
import java.util.List;

public class SampleSelectAdapter extends BaseAdapter {


    private List<String> sampleList = new ArrayList<>();
    private Context context;

    public SampleSelectAdapter(List<String> sampleList, Context context) {

        this.sampleList = sampleList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return sampleList == null || sampleList.size() == 0 ? 0 : sampleList.size();
    }

    @Override
    public Object getItem(int position) {
        return sampleList == null || sampleList.size() == 0 ? null : sampleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_simple, null);
            vh = new ViewHolder();
            vh.tv = convertView.findViewById(R.id.tv1);
            convertView.setTag(vh);
        }
        vh = (ViewHolder) convertView.getTag();
        vh.tv.setText(sampleList.get(position).split("-")[0]);

        return convertView;
    }

    static class ViewHolder{

        TextView tv;
    }



}
