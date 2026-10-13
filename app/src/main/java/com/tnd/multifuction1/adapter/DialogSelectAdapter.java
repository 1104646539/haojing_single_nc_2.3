package com.tnd.multifuction1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.model.FiltrateModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DialogSelectAdapter<T> extends RecyclerView.Adapter {
    Context context;
    List<FiltrateModel> filtrateModels = new ArrayList<>();
    int selectPosition = -1;
    boolean isSelect = false;//是否需要选择一行数据

    public int getSelectPosition() {
        return selectPosition;
    }

    public DialogSelectAdapter(Context context, List<FiltrateModel> filtrateModels) {
        this.context = context;
        this.filtrateModels = filtrateModels;
    }

    public DialogSelectAdapter(Context context) {
        this.context = context;
    }

    public void setSelect(boolean select) {
        this.isSelect = select;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_dialog_select, parent, false));
    }
    int old;
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tv_name.setText(filtrateModels.get(position).getName() != null ?
                filtrateModels.get(position).getName() : "");
        if (filtrateModels.get(position).isSelect()) {
            selectPosition = position;
        }
        Log.d("onBindViewHolder","name="
                +filtrateModels.get(position).getName()
        +"isSelect="+filtrateModels.get(position).isSelect());
        viewHolder.view.setSelected(selectPosition == position);

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (selectPosition == position) {
//                    selectPosition = -1;
//                    notifyItemChanged(position);
//                    filtrateModels.get(selectPosition).setSelect(true);
//                } else {
                if (selectPosition == position) {

                } else {
                    if (selectPosition != -1) {
                         old = selectPosition;
                        selectPosition = position;
                        notifyItemChanged(old);
                        filtrateModels.get(old).setSelect(false);
                        filtrateModels.get(selectPosition).setSelect(true);
                        notifyItemChanged(selectPosition);
                    } else {
                        selectPosition = position;
                        filtrateModels.get(selectPosition).setSelect(true);
                        notifyItemChanged(selectPosition);
                    }
                }
                if (onItemClick != null) {
                    onItemClick.onItemClick(filtrateModels.get(selectPosition),
                            old,selectPosition);
                }
            }
        });
    }

    public FiltrateModel getSelect() {
        if (selectPosition == -1) {
            return null;
        }
        return filtrateModels.get(selectPosition);
    }

    public void dataChange() {
        selectPosition = -1;
        notifyDataSetChanged();
    }

    public void dataChange(List<T> filtrateModels) {
        selectPosition = -1;
        this.filtrateModels.clear();
        if (filtrateModels != null) {
            this.filtrateModels.addAll((Collection<? extends FiltrateModel>) filtrateModels);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filtrateModels.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            view = itemView.findViewById(R.id.ll_item);
        }
    }

    OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick {
        void onItemClick(FiltrateModel data,int oldPosition,int newPosition);
    }
}
