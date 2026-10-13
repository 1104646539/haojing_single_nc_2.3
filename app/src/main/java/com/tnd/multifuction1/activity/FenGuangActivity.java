package com.tnd.multifuction1.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.adapter.FiltrateAdapter;
import com.tnd.multifuction1.adapter.ProjectDetailAdapter;
import com.tnd.multifuction1.model.BCheckOrg;
import com.tnd.multifuction1.model.CheckResult;
import com.tnd.multifuction1.model.FiltrateModel;
import com.tnd.multifuction1.model.Project;
import com.tnd.multifuction1.model.SampleName;
import com.tnd.multifuction1.model.SampleSource;
import com.tnd.multifuction1.resource.SPResource;
import com.tnd.multifuction1.util.APPUtils;
import com.tnd.multifuction1.util.DensityUtil;
import com.tnd.multifuction1.util.Global;
import com.tnd.multifuction1.util.SerialUtils;
import com.tnd.multifuction1.util.ToolUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 分光检测
 */
public class FenGuangActivity<T> extends TestActivity implements View.OnClickListener {


    private static final boolean DEBUG = true;
    private static final String TAG = "FenGuangActivity";
    private Button btnReturn;
    private Button btnCompare;
    private Button btnTest;
    private Button btnSave;
    private TextView tvCountDown;

    private List<Project> projectList = null;
    private TextView etCompare;
    private TextView etYzl;
    private TextView etJudge;
    private EditText et_sample_number;
    private EditText et_sample_weight;
    private Button btnPrint;
    private CheckResult checkResult;
    private static final int READ_DATA_WAIT_TIME = 3000;
    private TextView etSampleName;
    private TextView etCheckedOrg;
    private TextView etSampleSource;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_duogongneng_test);
        super.onCreate(savedInstanceState);
        act = this;
        initView();
//        projectList = new Project().findAll();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void initView() {

        etSampleName = findViewById(R.id.et_sample_name);
        etSampleSource = findViewById(R.id.et_sample_source);
        etCompare = findViewById(R.id.et_compare_value);
        et_sample_weight = findViewById(R.id.et_sample_weight);
        etYzl = findViewById(R.id.et_yzl);
        etJudge = findViewById(R.id.et_judge);
        btnCompare = findViewById(R.id.btn_compare);
        btnTest = findViewById(R.id.btn_test);
        btnSave = findViewById(R.id.btn_save_record);
        et_sample_number = findViewById(R.id.et_sample_number);
        btnReturn = findViewById(R.id.btn_return);
        tvCountDown = findViewById(R.id.tv_count_down);
        etCheckedOrg = findViewById(R.id.et_checked_org);
        btnPrint = findViewById(R.id.btn_print);
        tv_ac = findViewById(R.id.tv_ac);

        etSampleName.setOnClickListener(this);
        etCheckedOrg.setOnClickListener(this);
        etSampleSource.setOnClickListener(this);

        btnPrint.setOnClickListener(this);
        btnCompare.setOnClickListener(this);
        btnTest.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnReturn.setOnClickListener(this);

        sp = getSharedPreferences(SPResource.FILE_NAME, MODE_PRIVATE);
        AC = sp.getFloat(SPResource.KEY_FENGUANG_AC, 0);
        tv_ac.setText(getString(R.string.contrastValue) + df.format(AC));
        btnTest.setEnabled(AC != 0);
    }

    Timer mTimer = null;

    int curDownTime = 0;

    class CountDownTask extends TimerTask {

        @Override
        public void run() {
            if (isFinishing()) return;
            --curDownTime;
            if (curDownTime == 0) {
                mTimer.cancel();
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                mHandler.sendEmptyMessage(ToolUtils.countdown_finish);
                if (!SerialUtils.COM3_SendData(Global.GETALLDATA)) {
                    if (isComparing) {
                        mHandler.sendEmptyMessage(ToolUtils.compare_fail);
                    } else {
                        mHandler.sendEmptyMessage(ToolUtils.test_fail);
                    }
                    return;
                }
                recSecondData();
            } else {
                mHandler.obtainMessage(ToolUtils.update_countdown, curDownTime).sendToTarget();
            }
        }
    }

    private void recSecondData() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isComparing) {
                    if (!getRecData(Ic2)) {
                        mHandler.sendEmptyMessage(ToolUtils.compare_fail);
                    } else {
//                        Ac[i] = (float)Math.Abs(Math.Log10(Ic1[i] / Ic2[i]));
                        if (Ic1[0] == 0 || Ic2[0] == 0) {
                            mHandler.sendEmptyMessage(ToolUtils.compare_fail);
                            return;
                        }
                        AC = (float) Math.abs(Math.log10(Ic1[0] / Ic2[0]));
                        mHandler.sendEmptyMessage(ToolUtils.compare_success);
                    }
                } else {
                    if (!getRecData(Is2)) {
                        mHandler.sendEmptyMessage(ToolUtils.test_fail);
                    } else {
                        if (Is1[0] == 0 || Is2[0] == 0) {
                            mHandler.sendEmptyMessage(ToolUtils.test_success);
                            return;
                        }
//                        Ac[i] = (float)Math.Abs(Math.Log10(Is1[i] / Is2[i]));
//                        m_fvalue[i] = (AC - Ac[i] < 0) ? 0.0f : (((AC - Ac[i]) * 100 / AC));
                        AS = (float) Math.abs(Math.log10(Is1[0] / Is2[0]));
                        yzl = (AC - AS < 0) ? 0.0f : (((AC - AS) * 100 / AC));
                        mHandler.sendEmptyMessage(ToolUtils.test_success);
                    }
                }
            }
        }, READ_DATA_WAIT_TIME);
    }

    private boolean isTesting = false;
    private boolean isComparing = false;

    private CountDownTask timerTask;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return:
                finish();
                break;
            case R.id.btn_compare:
                compare();
                break;
            case R.id.btn_test:
                test();
                break;
            case R.id.btn_save_record:
                if (checkResult == null) {
                    APPUtils.showToast(act, "请先检测");
                    return;
                }
                saveRecord();
                break;
            case R.id.btn_print:
                if (checkResult == null) {
                    APPUtils.showToast(act, "请先检测");
                    return;
                }
//                checkResult= new CheckResult();
//                checkResult.channel = "B1";
//                checkResult.sampleNum = et_sample_number.getText().toString().trim();
//                checkResult.bcheckedOrganization = etCheckedOrg.getText().toString().trim();
//                checkResult.sampleSource = etSampleSource.getText().toString().trim();
//                checkResult.sampleName = etSampleName.getText().toString().trim();
//                checkResult.checker = Global.inspector == null ? "无" : Global.inspector;
//                checkResult.projectName = Global.project.projectName;
//                checkResult.resultJudge = yzl > Global.singleXlz ? "不合格" : "合格";
//                checkResult.testStandard = Global.project.testStandard;
//                checkResult.testValue = df.format(yzl) + "%";
//                checkResult.checkedOrganization = Global.checkOrg == null ? "无" : Global.checkOrg;
//                checkResult.xlz = Global.project.cardXlz + "%";
//                checkResult.weight = et_sample_weight.getText().toString().trim();

                List<CheckResult> checkResults = new ArrayList<>();
                checkResults.add(checkResult);
                byte[] data = ToolUtils.assemblePrintCheck(checkResults, this);
                SerialUtils.COM4_SendData(data);
                break;
//            case R.id.et_sample_num:
//                showInputSampleNumDialog((TextView) v);
//                break;
            case R.id.et_sample_name:
                showSelectDialog((TextView) v, DIALOG_SAMPLE_NAME);
                break;
            case R.id.et_checked_org:
                showSelectDialog((TextView) v, DIALOG_CHECKED_ORG);
                break;
            case R.id.et_sample_source:
                showSelectDialog((TextView) v, DIALOG_SAMPLE_SOURCE);
                break;
            default:

                break;
        }
    }

    public static int DIALOG_CHECKED_ORG = 1;//检测单位
    public static int DIALOG_SAMPLE_SOURCE = 2;//商品来源
    public static int DIALOG_SAMPLE_NAME = 3;//样品名称
    private Dialog dialog;
    private ListView lv;
    private List<BCheckOrg> checkOrgs;//被检测单位
    private List<SampleSource> sampleSources;//商品来源
    private List<SampleName> sampleNames;//样品名称
    private List<BCheckOrg> checkOrgs_s = new ArrayList<>();//被检测单位
    private List<SampleSource> sampleSources_s = new ArrayList<>();//商品来源
    private List<SampleName> sampleNames_s = new ArrayList<>();//样品名称
    public FiltrateAdapter filtrateAdapter;
    public String checkOrg = "", sampleSource = "", sampleName = "";
    public View contentView;
    private EditText et_content;

    private void showSelectDialog(TextView v, int type) {


        if (dialog == null) {
            dialog = new Dialog(this);

            contentView = LayoutInflater.from(this).inflate(R.layout.dialog_filtrate_select, null, false);
            lv = contentView.findViewById(R.id.lv);
            et_content = contentView.findViewById(R.id.et_content);
            filtrateAdapter = new FiltrateAdapter(this);

//            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//            params.width = DensityUtil.dip2px(this, 800);
//            params.height = DensityUtil.dip2px(this, 500);
//            dialog.getWindow().setAttributes(params);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        if (type == DIALOG_CHECKED_ORG) {
            checkOrgs = new BCheckOrg().findAll();
            checkOrgs_s.clear();
            checkOrgs_s.addAll(checkOrgs);
            filtrateAdapter.setData(checkOrgs);
            Log.d(TAG, "checkOrgs=" + checkOrgs.size());
        } else if (type == DIALOG_SAMPLE_SOURCE) {
            sampleSources = new SampleSource().findAll();
            sampleSources_s.clear();
            sampleSources_s.addAll(sampleSources);
            filtrateAdapter.setData(sampleSources);
            Log.d(TAG, "sampleSources=" + sampleSources.size());
        } else if (type == DIALOG_SAMPLE_NAME) {
            sampleNames = new SampleName().findAll();
            sampleNames_s.clear();
            sampleNames_s.addAll(sampleNames);
            filtrateAdapter.setData(sampleNames);
            Log.d(TAG, "sampleNames=" + sampleNames.size());
        }
        et_content.setSingleLine();
        et_content.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        et_content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
//                    Toast.makeText(FenGuangActivity.this, "搜索", Toast.LENGTH_SHORT).show();
                    v.setText(et_content.getText());

                    handled = true;

                    /*隐藏软键盘*/
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(FenGuangActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }
                    et_content.setText("");
                    dialog.dismiss();
                }
                return handled;
            }
        });
        lv.setAdapter(filtrateAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (type == DIALOG_CHECKED_ORG) {
                    checkOrg = checkOrgs.get(position).getName();
                    v.setText(checkOrg);
                } else if (type == DIALOG_SAMPLE_SOURCE) {
                    sampleSource = sampleSources.get(position).getName();
                    v.setText(sampleSource);
                } else if (type == DIALOG_SAMPLE_NAME) {
                    sampleName = sampleNames.get(position).getName();
                    v.setText(sampleName);
                }

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = "";
                if (s == null || s.toString().equals("") || s.toString().trim().equals("")) {
                    str = "";
                } else {
                    str = s.toString().trim();
                }
                onAdapterFilter(str, type);
            }
        });
        dialog.show();
        dialog.setContentView(contentView);
    }

    public void onAdapterFilter(String str, int type) {
        if (str == null || str.equals("")) {
            if (type == DIALOG_CHECKED_ORG) {
//                viewHolder.bcheckedOrg = new BCheckOrg().findAll();
                checkOrgs.clear();
                checkOrgs.addAll(checkOrgs_s);
                filtrateAdapter.setData(checkOrgs);
            } else if (type == DIALOG_SAMPLE_SOURCE) {
//                viewHolder.sampleSources = new SampleSource().findAll();
                sampleSources.clear();
                sampleSources.addAll(sampleSources_s);
                filtrateAdapter.setData(sampleSources);
            } else if (type == DIALOG_SAMPLE_NAME) {
//                viewHolder.sampleNames = new SampleName().findAll();
                sampleNames.clear();
                sampleNames.addAll(sampleNames_s);
                filtrateAdapter.setData(sampleNames);
            }
        } else {
            if (type == DIALOG_CHECKED_ORG) {
//                bcheckedOrg = new BCheckOrg().findAll();
                checkOrgs.clear();
                checkOrgs.addAll(checkOrgs_s);
                filtrateAdapter.setData(filter(str, (List<T>) checkOrgs));
            } else if (type == DIALOG_SAMPLE_SOURCE) {
//                sampleSources = new SampleSource().findAll();
                sampleSources.clear();
                sampleSources.addAll(sampleSources_s);
                filtrateAdapter.setData(filter(str, (List<T>) sampleSources));
            } else if (type == DIALOG_SAMPLE_NAME) {
//                sampleNames = new SampleName().findAll();
                sampleNames.clear();
                sampleNames.addAll(sampleNames_s);
                filtrateAdapter.setData(filter(str, (List<T>) sampleNames));
            }
        }
    }

    private List<T> filter(String str, List<T> data) {
        List<FiltrateModel> filtrateModels = (List<FiltrateModel>) data;
        Iterator<FiltrateModel> iterable = filtrateModels.iterator();
        while (iterable.hasNext()) {
            FiltrateModel m
                    = iterable.next();
            if (!m.getName().contains(str)) {
                iterable.remove();
            }
        }
        return (List<T>) filtrateModels;
    }

    private void saveRecord() {

        if (checkResult.save(checkResult)) {
            APPUtils.showToast(act, "保存成功");
        } else {
            APPUtils.showToast(act, "保存失败");
        }
    }

    private void test() {

        if (!validateBusyOrNot() || !validateCommonDataIsComplete() || !validateSampleComplete())
            return;
        isTesting = true;
        clearShow();
        startCountDown();
        if (!SerialUtils.COM3_SendData(Global.GETALLDATA)) {
            mHandler.sendEmptyMessage(ToolUtils.test_fail);
            return;
        }
        //检测时第一次取值
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!getRecData(Is1)) {
                    mHandler.sendEmptyMessage(ToolUtils.test_fail);
                }
            }
        }, 3000);
    }

    private boolean validateSampleComplete() {

        if (TextUtils.isEmpty(et_sample_number.getText().toString().trim())) {
            APPUtils.showToast(this, "请输入样品编号");
            return false;
        }
        if (TextUtils.isEmpty(etSampleName.getText().toString().trim())) {
            APPUtils.showToast(this, "请选择样品名称");
            return false;
        }
        if (TextUtils.isEmpty(etCheckedOrg.getText().toString().trim())) {
            APPUtils.showToast(this, "请选择被检单位");
            return false;
        }
        if (TextUtils.isEmpty(etSampleSource.getText().toString().trim())) {
            APPUtils.showToast(this, "请选择商品来源");
            return false;
        }
        if (TextUtils.isEmpty(et_sample_weight.getText().toString().trim())) {
            APPUtils.showToast(this, "请输入重量");
            return false;
        }

        return true;
    }

    private TextView tv_ac;
    private Handler mHandler = new Handler() {
        @SuppressLint("StringFormatInvalid")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ToolUtils.compare_success:
                    isComparing = false;
//                    etCompare.setText(df.format(AC));
                    tv_ac.setText(getResources().getString(R.string.contrastValue)
                            + df.format(AC));
                    tvCountDown.setText("对照成功");
                    btnTest.setEnabled(true);
                    Log.d(TAG, "AC=" + AC + "format=" + df.format(AC));
                    sp.edit().putFloat(SPResource.KEY_FENGUANG_AC, AC).commit();
                    break;
                case ToolUtils.compare_fail:
                    cancelTimer();
                    isComparing = false;
                    btnTest.setEnabled(false);
                    tvCountDown.setText("对照失败");
                    break;
                case ToolUtils.test_success:
                    isTesting = false;
                    tvCountDown.setText("检测成功");
                    checkResult = new CheckResult();
                    checkResult.testTime = new Date().getTime();
                    setTestResult();
                    showTestResult();
                    saveTestResult();
                    break;
                case ToolUtils.test_fail:
                    cancelTimer();
                    isTesting = false;
                    tvCountDown.setText("检测失败");
                    break;
                case ToolUtils.update_countdown:
                    int countDown = (int) msg.obj;
                    tvCountDown.setText("倒计时: " + countDown + "s");
                    break;
                case ToolUtils.countdown_finish:
                    if (isComparing) {
                        tvCountDown.setText("对照中...");
                    } else {
                        tvCountDown.setText("检测中...");
                    }
                    break;
            }
        }


    };

    private void saveTestResult() {
        if (checkResult != null) {
            new CheckResult().save(checkResult);
        }
    }

    private void setTestResult() {
        checkResult.channel = "B1";
        checkResult.sampleNum = et_sample_number.getText().toString().trim();
        checkResult.bcheckedOrganization = etCheckedOrg.getText().toString().trim();
        checkResult.sampleSource = etSampleSource.getText().toString().trim();
        checkResult.sampleName = etSampleName.getText().toString().trim();
        checkResult.checker = Global.inspector == null ? "无" : Global.inspector;
        checkResult.projectName = Global.project.projectName;
        checkResult.resultJudge = yzl > Global.singleXlz ? "不合格" : "合格";
        checkResult.testStandard = Global.project.testStandard;
        checkResult.testValue = df.format(yzl) + "%";
        checkResult.checkedOrganization = Global.checkOrg == null ? "无" : Global.checkOrg;
        checkResult.xlz = Global.project.cardXlz + "%";
        checkResult.weight = et_sample_weight.getText().toString().trim();
    }

    private void showTestResult() {

        etCompare.setText(df.format(AS));
        etYzl.setText(checkResult.testValue);
        etJudge.setText(checkResult.resultJudge);
    }


    private float AC;
    private float AS;
    private float yzl;

    private float[] Ic1 = new float[1];
    private float[] Ic2 = new float[1];
    private float[] Is1 = new float[1];
    private float[] Is2 = new float[1];

    private void compare() {

//        if (!validateBusyOrNot() || !validateCommonDataIsComplete() || !validateSampleComplete())
        if (!validateBusyOrNot())
            return;
        clearShow();
        startCountDown();
        if (!SerialUtils.COM3_SendData(Global.GETALLDATA)) {
            mHandler.sendEmptyMessage(ToolUtils.compare_fail);
            return;
        }
        isComparing = true;
        //对照时第一次取值
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!getRecData(Ic1)) {
                    mHandler.sendEmptyMessage(ToolUtils.compare_fail);
                }
            }
        }, READ_DATA_WAIT_TIME);
    }

    @Override
    public void onBackPressed() {
        if (isTesting||isComparing) {
            new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("正在检测，是否终止检测?")
                    .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();

        }else{
            super.onBackPressed();
            finish();
        }
    }

    private boolean getRecData(float[] f) {

        byte[] data = SerialUtils.COM3_RevData();
        if (data == null || data.length == 0) {
            return false;
        }
        String s = new String(data).replace("\n", "").replace("OK", "");
        Log.d(TAG, "getRecData data=" + new String(data));
        try {
            f[0] = Float.parseFloat(s);
            if (DEBUG) Log.i(TAG, "取值成功...====" + s);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (DEBUG) Log.i(TAG, "取值失败...");
            return false;
        }
    }

    private void clearShow() {

        etCompare.setText("");
        etYzl.setText("");
        etJudge.setText("");
    }

    private boolean validateBusyOrNot() {

        if (isTesting) {
            APPUtils.showToast(act, "正在检测中，请稍后...");
            return false;
        }
        if (isComparing) {
            APPUtils.showToast(act, "正在对照中，请稍后...");
            return false;
        }

        return true;
    }

    private void showProjectDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Dialog dialog = builder.create();

        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_project_select, null);
        ListView lv = contentView.findViewById(R.id.lv);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                selectedProject = projectList.get(position);
//                etCheckedOrg.setText("检测项目:" + selectedProject.projectName + "  样品名称:" + selectedProject.sampleName);
//                if (dialog != null && dialog.isShowing()) {
//                    dialog.dismiss();
//                }
//            }
//        });
        ProjectDetailAdapter adapter = new ProjectDetailAdapter(projectList, this);
        lv.setAdapter(adapter);
        dialog.show();
        dialog.setContentView(contentView);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = DensityUtil.dip2px(this, 800);
        params.height = DensityUtil.dip2px(this, 500);
        dialog.getWindow().setAttributes(params);
    }

    private void startCountDown() {

        cancelTimer();
        timerTask = new CountDownTask();
        mTimer = new Timer();
        curDownTime = Global.fenguangReactionTime;
        mTimer.schedule(timerTask, 0, 1000);
    }

    private void cancelTimer() {

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        if (mTimer != null) mTimer.cancel();
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
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
//        if (Global.project == null) {
//            APPUtils.showToast(act, "请先设置检测项目");
//            return false;
//        }
        return true;
    }
}
