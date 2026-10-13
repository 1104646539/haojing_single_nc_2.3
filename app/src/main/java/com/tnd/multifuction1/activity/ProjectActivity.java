package com.tnd.multifuction1.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.adapter.ProjectAdapter;
import com.tnd.multifuction1.db.DbHelper;
import com.tnd.multifuction1.model.Project;
import com.tnd.multifuction1.model.SampleName;
import com.tnd.multifuction1.util.APPUtils;
import com.tnd.multifuction1.util.Global;

import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class ProjectActivity extends Activity implements View.OnClickListener, ProjectAdapter.onSelectedItemChangedListener {


    private static final String TAG = ProjectActivity.class.getSimpleName();
    private Button btnClear;
    private Button btnDelete;
    private Button btnSave;
    private Button btnAdd;
    private Button btnReturn;
    private ProjectAdapter adapter;
    private List<Project> projectList;
    private Project projectBean = new Project();
    private ListView lv;
    private Activity act;
    private EditText etProject;
    private EditText etK;
    private EditText etB;
    private EditText etChecker;
//    private EditText etSingleXlz;
    private EditText etCardXlz;
    private Spinner spn_bochange;
    private Spinner spn_project;

    private EditText etUnit;       //单位

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        act = this;
        initView();
        setAdapter();
    }

    private void setAdapter() {

        projectList = projectBean.findAll();
        adapter = new ProjectAdapter(this, projectList, this);
        lv.setAdapter(adapter);
    }
    String[] bochanges;
    List<SampleName.Project> snp;
    private void initView() {

        etChecker = findViewById(R.id.et_checker);
//        etSingleXlz = findViewById(R.id.et_single_xlz);
        etCardXlz = findViewById(R.id.et_card_xlz);
        etProject = findViewById(R.id.et_project_name);
        etK = findViewById(R.id.et_k);
        etB = findViewById(R.id.et_b);
        lv = findViewById(R.id.lv);
        btnClear = findViewById(R.id.btn_clear_data);
        btnDelete = findViewById(R.id.btn_delete_data);
        btnSave = findViewById(R.id.btn_save_project);
        spn_bochange = findViewById(R.id.spn_bochange);
        spn_project = findViewById(R.id.spn_project);
        btnAdd = findViewById(R.id.btn_add_data);
        btnReturn = findViewById(R.id.btn_return);
        etUnit = findViewById(R.id.et_unit);

        btnClear.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnReturn.setOnClickListener(this);
        bochanges =getResources().getStringArray(R.array.project_bochang);
        for (int i = 0; i < bochanges.length; i++) {
            Log.d(TAG,"bochang="+bochanges[i]);
        }
        Log.d(TAG,"bochang="+bochanges.length);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1, Arrays.asList(bochanges));
        adapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        spn_bochange.setAdapter(adapter);
        spn_bochange.setSelection(0);

        List<SampleName> sns =  new SampleName().findAll();
        if (sns!=null&&sns.size()>0){
                SampleName sn =sns.get(0);
            try {
                snp = sn.getProjects();
                if (snp!=null){
                    Log.d(TAG,"snp="+snp.size());
                    for (int i = 0; i < snp.size(); i++) {
                        Log.d(TAG,"i="+i+"spn="+snp.get(i).toString());
                    }
                    ArrayAdapter<SampleName.Project> adapterP = new ArrayAdapter<SampleName.Project>(this, android.R.layout.simple_list_item_1, snp);
                    adapterP.setDropDownViewResource
                            (android.R.layout.simple_spinner_dropdown_item);
                    spn_project.setAdapter(adapterP);
                    spn_project.setSelection(0);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_clear_data:
                showClearDialog();
                break;
            case R.id.btn_delete_data:
                showDeleteDialog();
                break;
            case R.id.btn_save_project:
                updateData();
                break;
            case R.id.btn_add_data:
                if(!validateDataComplete()) return;
                addData();
                break;
            case R.id.btn_return:
                this.finish();
                break;
        }
    }

    private int selectedIndex = -1;

    private void updateData() {

        if (selectedIndex < 0) {
            APPUtils.showToast(act, "请先选中数据");
            return;
        }
        if(!validateDataComplete()) return;
//        float xlz = Float.parseFloat(etCardXlz.getText().toString().trim());
//        if (xlz <= 1 || xlz > 99) {
//            APPUtils.showToast(act, "临界值必须大于1且小于100");
//            return;
//        }
        Project project = projectList.get(selectedIndex);
        project.projectName = snp.get(spn_project.getSelectedItemPosition()).projectName;
        project.k = Float.valueOf(etK.getText().toString().trim());
        project.b = Float.valueOf(etB.getText().toString().trim());
        project.bochang = Integer.valueOf(bochanges[spn_bochange.getSelectedItemPosition()]);
        project.cardXlz = Float.valueOf(df.format(Double.parseDouble(etCardXlz.getText().toString().trim())));
//        project.singleXlz = Double.valueOf(df.format(Double.parseDouble(etSingleXlz.getText().toString().trim())));
//        project.checker = etChecker.getText().toString().trim();
        project.isSelect = false;
        project.unit = etUnit.getText().toString().trim();
        if (project.update(new String[]{ "projectName",  "k","b","bochang","cardXlz","unit"})) {
            if (projectList != null) {
                adapter.notifyDataSetChanged();
                clearText();
            } else {
                setAdapter();
            }
        } else {
            APPUtils.showToast(act, "保存编辑失败");
        }
    }

    private void addData() {

//        if (adapter != null && adapter.getCount() > 0) {
//            APPUtils.showToast(act, "已存在一条数据，请删除后重新添加");
//            return;
//        }

        Project project = new Project();
        project.projectName = snp.get(spn_project.getSelectedItemPosition()).projectName;
        project.k = Float.valueOf(etK.getText().toString().trim());
        project.b = Float.valueOf(etB.getText().toString().trim());
        project.bochang = Integer.valueOf(bochanges[spn_bochange.getSelectedItemPosition()]);
        project.cardXlz = Float.valueOf(df.format(Float.parseFloat(etCardXlz.getText().toString().trim())));
        project.unit = etUnit.getText().toString().trim();
//        project.checker = etChecker.getText().toString().trim();

        try {
            Project project1 =DbHelper.GetInstance().findFirst(new Selector(Project.class).where("projectName","=",project.projectName));
            if (project1!=null){
                APPUtils.showToast(act, "添加失败,该项目已存在");
                return;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (project.save(project)) {
            if (projectList != null) {
                projectList.add(project);
                adapter.notifyDataSetChanged();
            } else {
                setAdapter();
            }
            clearText();
        } else {
            APPUtils.showToast(act, "添加失败");
        }
    }

    private void clearText() {

        etProject.setText("");
        etChecker.setText("");
        etK.setText("");
        etB.setText("");
        etCardXlz.setText("");
        etUnit.setText("");
    }

    private boolean validateDataComplete() {

//        if (TextUtils.isEmpty(etProject.getText())) {
//            APPUtils.showToast(act, "请输入检测项目");
//            return false;
//        }
        if (TextUtils.isEmpty(etCardXlz.getText())) {
            APPUtils.showToast(act, "请输入检出限");
            return false;
        }
        if (TextUtils.isEmpty(etK.getText())){
            APPUtils.showToast(act, "请输入K值");
            return false;
        }
        if (TextUtils.isEmpty(etB.getText())){
            APPUtils.showToast(act, "请输入B值");
            return false;
        }
        return true;
    }



    private void showDeleteDialog() {

        final Project project = adapter.getSelectedItem();
        if (project == null) {
            APPUtils.showToast(this, "请先选中数据");
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("确定删除该数据?")
                .setTitle("提示")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        project.delete(project);
                        projectList.remove(project);
                        adapter.notifyDataSetChanged();
                        clearText();
                        selectedIndex = -1;
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    private void showClearDialog() {

        if (projectList == null || projectList.size() == 0) {
            APPUtils.showToast(this, "暂无数据");
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("确定清空所有数据?")
                .setTitle("提示")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Project().deleteAll(projectList);
                        projectList.clear();
                        adapter.notifyDataSetChanged();
                        selectedIndex = -1;
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void onSelectedItemChanged(int position) {

        if (projectList != null && projectList.size() > 0) {
            selectedIndex = position;
            Project p = projectList.get(position);
            etProject.setText(p.projectName);
            etChecker.setText(p.checker);
            etCardXlz.setText(df.format(p.cardXlz));
            etK.setText(p.k+"");
            etB.setText(p.b+"");
            etUnit.setText(p.unit);

            for (int i = 0; i < bochanges.length; i++) {
                if (bochanges[i].equals(String.valueOf(p.bochang))){
                    spn_bochange.setSelection(i);
                }
            }
            for (int i = 0; i < snp.size(); i++) {
                if (snp.get(i).getProjectName().equals(String.valueOf(p.projectName))){
                    spn_project.setSelection(i);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (projectList == null || projectList.size() == 0) {
            Global.project = null;
        } else {
            Global.project = projectList.get(0);
        }
        super.onDestroy();
    }
}
