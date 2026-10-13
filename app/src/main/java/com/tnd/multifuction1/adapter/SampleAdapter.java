package com.tnd.multifuction1.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.model.SampleName;

import java.util.List;

public class SampleAdapter extends UserInputAdapter<SampleName> {


    public SampleAdapter(List<SampleName> list, Activity act) {
        this.list = list;
        this.act = act;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder vh = null;
        if (convertView == null) {
            convertView = View.inflate(act, R.layout.item_sample, null);
            vh = new ViewHolder();
            vh.ll = convertView.findViewById(R.id.ll);
            vh.cb = convertView.findViewById(R.id.cb);
            vh.tvSampleName = convertView.findViewById(R.id.tv_sample_name);
            convertView.setTag(vh);
        }
        vh = (ViewHolder) convertView.getTag();
        final SampleName sample = list.get(position);
        vh.tvSampleName.setText(sample.sampleName);
        final ViewHolder finalVh = vh;
        finalVh.cb.setChecked(sample.isSelected);

        vh.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean state = sample.isSelected;
                finalVh.cb.setChecked(!state);
                sample.isSelected = !state;
            }
        });

        return convertView;
    }

    static class ViewHolder{

        CheckBox cb;
        LinearLayout ll;
        TextView tvSampleName;
    }
}
