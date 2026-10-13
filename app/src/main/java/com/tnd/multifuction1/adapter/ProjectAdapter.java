package com.tnd.multifuction1.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.model.Project;

import java.text.DecimalFormat;
import java.util.List;

public class ProjectAdapter extends BaseAdapter {


    private onSelectedItemChangedListener listener;
    private Activity act;
    private List<Project> list;

    public ProjectAdapter(Activity act, List<Project> list, onSelectedItemChangedListener listener) {
        this.act = act;
        this.list = list;
        this.listener = listener;
    }

    public Project getSelectedItem() {

        if(list == null || list.size() == 0) return null;
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).isSelect) return list.get(i);
        }
        return null;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null || list.size() == 0 ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder vh = null;
        if (convertView == null) {
            convertView = View.inflate(act, R.layout.item_project, null);
            vh = new ViewHolder();
            vh.ll = convertView.findViewById(R.id.ll);
            vh.tvProject = convertView.findViewById(R.id.tv_project);
            vh.tvK = convertView.findViewById(R.id.tv_checker);
            vh.tvCardXlz = convertView.findViewById(R.id.tv_card_xlz);
            vh.tvBoChang = convertView.findViewById(R.id.tv_bochang);
            vh.tvJcx = convertView.findViewById(R.id.tv_jcx);
            vh.tvB = convertView.findViewById(R.id.tv_standard);
            vh.tvUnit = convertView.findViewById(R.id.tv_unit);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final Project p = list.get(position);
        final ViewHolder finalVh = vh;
        vh.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(p, finalVh, position);
            }
        });
        boolean state = p.isSelect;
        if (state) {
            finalVh.ll.setBackgroundColor(act.getResources().getColor(R.color.colorPrimary));
        } else {
            finalVh.ll.setBackgroundColor(act.getResources().getColor(R.color.white));
        }

        vh.tvJcx.setText(p.cardXlz+"");
        vh.tvProject.setText(p.projectName);
        vh.tvK.setText(p.k+"");
        vh.tvCardXlz.setText(df.format(p.cardXlz));
        vh.tvBoChang.setText(String.valueOf(p.bochang));
//        vh.tvSingleXlz.setText(df.format(p.singleXlz));
        vh.tvB.setText(p.b+"");
        vh.tvUnit.setText(p.unit);

        return convertView;
    }

    private void setState(Project p, ViewHolder finalVh, int position) {
        boolean state = !p.isSelect;
        if (state) {
            for(int i = 0; i < list.size(); i++) {
                list.get(i).isSelect = false;
            }
            list.get(position).isSelect = true;
            if(listener != null) listener.onSelectedItemChanged(position);
            finalVh.ll.setBackgroundColor(act.getResources().getColor(R.color.colorPrimary));
            notifyDataSetChanged();
        }
    }


    static class ViewHolder {

        LinearLayout ll;
        TextView tvProject;
        TextView tvK;
        TextView tvCardXlz;
        TextView tvJcx;
        TextView tvB;
        TextView tvBoChang;
        TextView tvUnit;
    }

    public interface onSelectedItemChangedListener {

        void onSelectedItemChanged(int position);
    }
}
