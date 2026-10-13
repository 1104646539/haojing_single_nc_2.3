package com.tnd.multifuction1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.model.Print;

import java.util.ArrayList;
import java.util.List;

public class PrintAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Print> prints = new ArrayList<>();
    private List<Print> filters_multiple = new ArrayList();
    private List<Print> filters_merge = new ArrayList();
    private List<Print> filters_re = new ArrayList();

    private boolean isZT = true;//是否是逐条打印

    public void setZT(boolean ZT) {
        isZT = ZT;
    }

    public List<Print> getData() {
        return isZT ? filters_multiple : filters_merge;
    }

    public boolean isZT() {
        return isZT;
    }

    public PrintAdapter(Context context) {
        this.context = context;

    }

    private void initData() {
        filters_multiple.clear();
        filters_merge.clear();
        filters_re.clear();
        for (int i = 0; i < prints.size(); i++) {
            Print print = prints.get(i);
            print.readPosition = i;
            if (print.isMultiple) {
                filters_multiple.add(print);
            }
            if (print.isMerge) {
                filters_merge.add(print);
            }
            if (print.isRequired) {
                filters_re.add(print);
            }
        }
        Log.d("initData", "filters_multiple=" + filters_multiple.size()
                + "filters_merge=" + filters_merge.size() + "filters_re=" + filters_re.size());
        Log.d("initData", "prints=" + prints.toString());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_print, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Print print = null;
        if (position == filters_re.size()) {
            viewHolder.contentView.setVisibility(View.GONE);
            return;
        } else if (position > filters_re.size()) {
            position = position - 1;
        }
        viewHolder.contentView.setVisibility(View.VISIBLE);
        if (isZT) {//逐条
            print = filters_multiple.get(position);
        } else {
            print = filters_merge.get(position);
        }
        if (print.isRequired) {
            viewHolder.tv_name.setSelected(true);
            viewHolder.iv_check.setSelected(true);
        } else {
            viewHolder.iv_check.setSelected(isZT ? print.isSelectMultiple
                    : print.isSelectMerge);
        }

        viewHolder.tv_name.setText(print.p_name);
        int finalPosition = position;
        viewHolder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isZT) {
                    if (filters_multiple.get(finalPosition).isRequired) {
                        return;
                    }
                    boolean isSelected = !filters_multiple.get(finalPosition).isSelectMultiple;
                    viewHolder.iv_check.setSelected(isSelected);
                    filters_multiple.get(finalPosition).setSelect(isSelected);
                    filters_multiple.get(finalPosition).isSelectMultiple = isSelected;
                    prints.get(filters_multiple.get(finalPosition).readPosition).isSelectMultiple = isSelected;
                    Log.d("onClick", "finalPosition=" + finalPosition + "isSelected=" + isSelected);
                } else {
                    if (filters_merge.get(finalPosition).isRequired) {
                        return;
                    }
                    boolean isSelected = !filters_merge.get(finalPosition).isSelectMerge;
                    viewHolder.iv_check.setSelected(isSelected);
                    filters_merge.get(finalPosition).setSelect(isSelected);
                    filters_merge.get(finalPosition).isSelectMerge = isSelected;
                    prints.get(filters_merge.get(finalPosition).readPosition).isSelectMerge = isSelected;
                    Log.d("onClick", "finalPosition=" + finalPosition + "isSelected=" + isSelected);
                }
                for (int i = 0; i < prints.size(); i++) {
                    Log.d("onClick", "name=" + prints.get(i).getP_name() +
                            "isMultiple=" + prints.get(i).isMultiple() +
                            "isMerge=" + prints.get(i).isMerge() +
                            "isSelectMerge=" + prints.get(i).isSelectMerge() +
                            "isSelectMultiple=" + prints.get(i).isSelectMultiple());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return getFilterData();
    }

    private int getFilterData() {
        if (isZT) {
            return filters_re.size() % 2 == 0 ? filters_multiple.size() : filters_multiple.size() + 1;
        } else {
            return filters_re.size() % 2 == 0 ? filters_merge.size() : filters_merge.size() + 1;
        }
    }

    public void setData(List<Print> prints) {
        this.prints.clear();
        initData();
        if (prints != null) {
            this.prints.addAll(prints);
            initData();
            notifyDataSetChanged();
        }
    }

    public List<Print> getRealData() {
        return prints;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_check;
        TextView tv_name;
        View contentView;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_check = itemView.findViewById(R.id.iv_check);
            tv_name = itemView.findViewById(R.id.tv_name);
            contentView = itemView.findViewById(R.id.contentView);
        }
    }
}
