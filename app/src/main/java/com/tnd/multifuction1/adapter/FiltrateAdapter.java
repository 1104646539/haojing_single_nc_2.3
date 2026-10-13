package com.tnd.multifuction1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.model.FiltrateModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FiltrateAdapter<T> extends BaseAdapter {
    private Context context;
    private List<FiltrateModel> filtrateModels = new ArrayList<>();


    public FiltrateAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<T> sampleSources) {
        this.filtrateModels.clear();
        if (sampleSources != null) {
            this.filtrateModels.addAll((Collection<? extends FiltrateModel>) sampleSources);
        }
        notifyDataSetChanged();
    }
//    public void setData(List<CheckOrg> checkOrgs) {
//        this.filtrateModels.clear();
//        this.filtrateModels.addAll(checkOrgs);
//        notifyDataSetChanged();
//    }

    @Override
    public int getCount() {
        return filtrateModels.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_filtrate_dialog, parent,false);
            holder.tv = convertView.findViewById(R.id.item_filtrate_name);
            convertView.setTag(holder);
        }
        holder.tv.setText(filtrateModels.get(position).getName() != null ? filtrateModels.get(position).getName() : "");
        return convertView;
    }

    public class ViewHolder {
        TextView tv;
    }
}
