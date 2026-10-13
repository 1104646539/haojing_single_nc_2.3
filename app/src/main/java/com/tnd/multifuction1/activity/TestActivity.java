package com.tnd.multifuction1.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.adapter.SampleSelectAdapter;
import com.tnd.multifuction1.model.SampleName;
import com.tnd.multifuction1.resource.SPResource;
import com.tnd.multifuction1.util.APPUtils;
import com.tnd.multifuction1.util.Global;

import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TestActivity extends Activity {


    protected boolean DEBUG = Global.DEBUG;
    protected Activity act;
    protected EditText etCheckedOrg;
    protected EditText etSampleSource;
    private SharedPreferences sp;
    protected DecimalFormat df = new DecimalFormat("0.000");
    protected List<String> sampleList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        initCommonView();
    }

    private void initData() {

        List<SampleName> list = new SampleName().findAll();
//        list.forEach(n -> sampleList.add(n.sampleName));
        if (list != null && list.size() > 0) {
            sampleList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                sampleList.add(list.get(i).sampleName);
            }
        }
    }

    private void initSP() {

        sp = getSharedPreferences(SPResource.FILE_NAME, Context.MODE_PRIVATE);
        etCheckedOrg.setText(sp.getString(SPResource.KEY_CHECKED_ORG, ""));
        etSampleSource.setText(sp.getString(SPResource.KEY_SAMPLE_SOURCE, ""));
    }

    private void initCommonView() {

//        etCheckedOrg = findViewById(R.id.et_checked_org);
//        etSampleSource = findViewById(R.id.et_sample_source);
    }

    protected boolean validateCommonDataIsComplete() {

        if (TextUtils.isEmpty(etCheckedOrg.getText().toString())) {
            APPUtils.showToast(act, "请输入被检单位");
            return false;
        }
        if (TextUtils.isEmpty(etSampleSource.getText().toString())) {
            APPUtils.showToast(act, "请输入商品来源");
            return false;
        }
        if (Global.project == null) {
            APPUtils.showToast(act, "请先设置检测项目");
            return false;
        }
        return true;
    }

    protected void showSampleDialog(final EditText et) {

        if (sampleList == null || sampleList.size() == 0) {
            APPUtils.showToast(this, "请先添加样品名称");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_sample_select, null);
        final Dialog dialog = builder.create();
        final ListView lv = view.findViewById(R.id.lv);
        lv.setAdapter(new SampleSelectAdapter(sampleList, this));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sampleName = (String) lv.getAdapter().getItem(position);
                et.setText(sampleName);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                et.setSelection(et.getText().toString().length());
            }
        });
        ((EditText)view.findViewById(R.id.et_checker)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    lv.setAdapter(new SampleSelectAdapter(sampleList, act));
                    return;
                }
                List<String> list = new ArrayList<>();
                for (String str : sampleList) {
                    if (str.contains(s)) {
                        list.add(str);
                    }
                }
                lv.setAdapter(new SampleSelectAdapter(list, act));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.setContentView(view);

    }

    @Override
    protected void onDestroy() {
        if (sp!=null) {
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(SPResource.KEY_CHECKED_ORG, etCheckedOrg.getText().toString().trim());
            edit.putString(SPResource.KEY_SAMPLE_SOURCE, etSampleSource.getText().toString().trim()).apply();
        }
        super.onDestroy();
    }
}
