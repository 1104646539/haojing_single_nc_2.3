package com.tnd.multifuction1.adapter;

import android.app.Activity;
import android.widget.BaseAdapter;


import com.tnd.multifuction1.interfaces.onItemSelectedListener;
import com.tnd.multifuction1.model.UserInputModel;

import java.util.ArrayList;
import java.util.List;

public abstract class UserInputAdapter<T extends UserInputModel> extends BaseAdapter implements onItemSelectedListener {


    protected Activity act;

    protected List<T> list = null;

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getSelectedCount() {

        if (list == null || list.size() == 0) return 0;
        int count = 0;
        for (T t : list) {
            if (t.isSelected) {
                count++;
            }
        }
        return count;
    }

    @Override
    public List<T> getSelectedList() {

        if (list == null || list.size() == 0) return null;

        List<T> selectedList = new ArrayList<>();

        for (T t : list) {
            if (t.isSelected) {
                selectedList.add(t);
            }
        }
        return selectedList;
    }
}
