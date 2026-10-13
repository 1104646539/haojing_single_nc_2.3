package com.tnd.multifuction1.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.adapter.UserInputAdapter;
import com.tnd.multifuction1.db.DbHelper;
import com.tnd.multifuction1.dialog.OperateBaseDataDialog;
import com.tnd.multifuction1.model.UserInputModel;
import com.tnd.multifuction1.util.APPUtils;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class UserInputDataActivity<T extends UserInputModel> extends Activity implements View.OnClickListener {


    protected String TAG= getClass().getSimpleName();
    protected Activity act;
    protected DbUtils db;
    protected T bean;
    protected List<T> list;
    protected Button btnReturn;
    protected Button btnAddData;
    protected Button btnDeleteData;
    protected Button btnClear;
    protected TextView tvTitle;
    protected ListView listView;
    protected UserInputAdapter<T> adapter;
    private ProgressDialog waitDialog;
    private Button btnEditData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);
        act = this;
        db = DbHelper.GetInstance();
        initView();
    }

    protected Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    protected void initView(){
        tvTitle = findViewById(R.id.tv_title);
        btnClear = findViewById(R.id.btn_clear_data);
        btnDeleteData = findViewById(R.id.btn_delete_data);
        btnAddData = findViewById(R.id.btn_add_data);
        btnEditData = findViewById(R.id.btn_edit_data);
        btnReturn = findViewById(R.id.btn_return);
        listView = findViewById(R.id.listview);

        btnEditData.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnDeleteData.setOnClickListener(this);
        btnAddData.setOnClickListener(this);
        btnReturn.setOnClickListener(this);

        tvTitle.setText(getCustomTitle());
        setAdapter();
    }

    protected void notifyDateSetChanged() {
        adapter.notifyDataSetChanged();
        tvTitle.setText(getCustomTitle() + "(" + adapter.getCount() + ")");
    }

    protected void setAdapter() {

        list = getBeanData();
        adapter = getAdapter();
        listView.setAdapter(adapter);
        notifyDateSetChanged();
    }

    protected List<T> getBeanData() {
        return bean.findAll();
    }

    protected abstract String getCustomTitle();

    protected abstract UserInputAdapter<T> getAdapter();

    protected abstract OperateBaseDataDialog<T> getDialog(T t);

    protected boolean dataIsExist(T t){
        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_clear_data:
                clearData();
                break;
            case R.id.btn_delete_data:
                deleteData();
                break;
            case R.id.btn_edit_data:
                if (adapter.getSelectedCount() == 0) {
                    APPUtils.showToast(act, "请先选中数据");
                    return;
                }
                getDialog(adapter.getSelectedList().get(0)).setCallBack(t -> {
                    bean.saveOrUpdate(t);
                    notifyDateSetChanged();
                    return false;
                }).show();
                break;
            case R.id.btn_add_data:
                getDialog(null).setCallBack(a -> {
                    T t = a;
                    if (dataIsExist(t)) {
                        APPUtils.showToast(act, "数据已存在，保存失败");
                        return true;
                    }
                    if (bean.save(t)) {
                        if (list != null) {
                            list.add(0, t);
                            notifyDateSetChanged();
                        } else {
                            setAdapter();
                        }
                    }
                    return false;
                }).show();
                break;

            case R.id.btn_return:
                finish();
                break;
        }
    }

    protected void clearData() {
        if (list == null || list.size() == 0) {
            APPUtils.showToast(act, "暂无数据");
            return;
        }

        showClearDataDialog();
    }

    protected void deleteData() {
        if (adapter.getSelectedCount() == 0) {
            APPUtils.showToast(act, "请先选中数据");
            return;
        }
        showDeleteDataDialog(adapter.getSelectedList());
    }


    private void showClearDataDialog() {

        new AlertDialog.Builder(act).setMessage("确定清空所有数据？")
                .setTitle("提示")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(bean.deleteAll(list)) {
                            list.clear();
                            notifyDateSetChanged();
                        }
                    }
                }).create().show();

    }

    private void showDeleteDataDialog(final List<T> selectedList) {

        new AlertDialog.Builder(act).setMessage("确定删除选中数据？")
                .setTitle("提示")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectedList.get(0).deleteAll(selectedList)) {
                            list.removeAll(selectedList);
                            notifyDateSetChanged();
                        }
                    }
                }).create().show();
    }
}
