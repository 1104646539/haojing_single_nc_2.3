package com.tnd.multifuction1.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.tnd.multifuction1.BuildConfig;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.adapter.TestAdapter;
import com.tnd.multifuction1.model.CheckResult;
import com.tnd.multifuction1.model.Project;
import com.tnd.multifuction1.model.SampleName;
import com.tnd.multifuction1.resource.SPResource;
import com.tnd.multifuction1.thread.UploadThread;
import com.tnd.multifuction1.util.APPUtils;
import com.tnd.multifuction1.util.DensityUtil;
import com.tnd.multifuction1.util.Global;
import com.tnd.multifuction1.util.PreferencesUtils;
import com.tnd.multifuction1.util.SerialUtils;
import com.tnd.multifuction1.util.ToolUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import static com.tnd.multifuction1.util.Global.CHANNEL_COUNT;

/**
 * 卡片检测
 */
public class PesticideTestActivity2 extends TestActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener, AdapterView.OnItemSelectedListener {

    private static final int BS_START = 6;
    private static final float COMPARE_MIN_VALUE = 0.3f;
    private static final String TAG = PesticideTestActivity2.class.getSimpleName();
    private Button btnReturn;
    private Button btnTest;
    private ImageView cbSelectAll;
    private LinearLayout llSelectAll;

//    private TextView tvCurrentTemp;

    private Timer tempTimer;
    private Timer reactionTimer;

    private Button btnPrint;

    private Button btnUpload;

    private static final int TEST_START = 0;
    private static final int TEST_END = 2;
    private static final int COMPARE_START = 1;
    private static final int COMPARE_END = 7;

    private static final int OPEN_CLOSE_LIB = 3;
    private static final int ENTER_OUT_CARD = 4;
    private static final int GET_TEMP = 5;
    private CountDownLatch mCountDownLatch;
    private float ac1;
    private float Ac;
    private Button btnCompare;
    private boolean isComparing;
    private boolean isTesting = false;
    private TextView tvCompareValue;
    private float[] as1List;
    private float[] as2List;
    private List<Double> AsList;
    private double COMPARE_FACTOR = 0.01;
    private SharedPreferences sp;
    private TextView tv_status;
    private TextView tv_alis;

    private RecyclerView rv_data;
    private TestAdapter testAdapter;
    private List<CheckResult> resultList = new ArrayList<>();
    private long countDownDelay = 6500;//倒计时结束后延时处理
    private List<Project> projects = new ArrayList<>();
    private Spinner spn_project;
    private Project mProject;
    private TextView tv_yzl;
    private CheckResult tempResult;
    private float[] d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesticide_test2);
        initView();
        act = this;
//        tempTimer = new Timer();
//        tempTimer.schedule(tempTask, 0, 10 * 1000);
        initSp();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void initSp() {

        sp = getSharedPreferences(SPResource.FILE_NAME, MODE_PRIVATE);
        Ac = sp.getFloat(SPResource.KEY_COMPARE_VALUE, 0);
        if (Ac <= COMPARE_MIN_VALUE) {
            tvCompareValue.setText(getResources().getString(R.string.contrastValue));
        } else {
            tvCompareValue.setText(getResources().getString(R.string.contrastValue)
                    + df.format(Ac));
        }
        if (Ac >= COMPARE_MIN_VALUE) {
            btnTest.setEnabled(true);
        }
//        Ac = 88;
    }

    private boolean isUploading = false;

    private void initView() {
        projects = new Project().findAll();
        Log.d(TAG, "projects=" + projects.size());
        tv_status = findViewById(R.id.tv_status);
        tvCompareValue = findViewById(R.id.tv_compare_value);
        btnPrint = findViewById(R.id.btn_print);
        btnPrint.setOnClickListener(this);
        cbSelectAll = findViewById(R.id.cb_select_all);
        llSelectAll = findViewById(R.id.ll_select_all);
        tv_alis = findViewById(R.id.tv_alis);
        tv_yzl = findViewById(R.id.tv_yzl);
        llSelectAll.setOnClickListener(this);
        tv_alis.setOnClickListener(this);
        btnTest = findViewById(R.id.btn_test);
        btnCompare = findViewById(R.id.btn_compare);
        btnCompare.setOnClickListener(this);
        rv_data = findViewById(R.id.rv_data);
        spn_project = findViewById(R.id.spn_project);
        ArrayAdapter<Project> adapter = new ArrayAdapter<Project>(this,
                R.layout.item_select_project, R.id.tv_project_name, projects);
        adapter.setDropDownViewResource
                (R.layout.item_select_project_drop);
        spn_project.setAdapter(adapter);
        spn_project.setSelection(0);
        mProject = projects.get(0);
        spn_project.setOnItemSelectedListener(this);
//        openLight(mProject.bochang);
        btnUpload = findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(this);


        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        testAdapter = new TestAdapter(this);
        testAdapter.setOnAllSelectListener(new TestAdapter.OnAllSelectListener() {
            @Override
            public void onAllSelect(boolean isAllSelect) {
                tv_alis.setSelected(isAllSelect);
            }
        });
        rv_data.setLayoutManager(manager);
        rv_data.setAdapter(testAdapter);

        for (int i = 0; i < CHANNEL_COUNT; i++) {
            CheckResult checkResult = new CheckResult();
            checkResult.channel = "A" + (i + 1);
            resultList.add(checkResult);
        }
        testAdapter.setData(resultList);

        if (Global.uploadModel == 1) {
            btnUpload.setVisibility(View.GONE);
        }
        btnReturn = findViewById(R.id.btn_return);

        btnTest.setOnClickListener(this);
        btnReturn.setOnClickListener(this);

//        tvCurrentTemp = findViewById(R.id.tv_current_temp);
    }

    private void openLight(int bochang) {
        Log.d(TAG, "bochang=" + bochang);
        SerialUtils.COM3_SendData(("Light" + bochang).getBytes());
    }

    Random random = new Random();

    //region 定时刷新温度任务
//    TimerTask tempTask = new TimerTask() {
//        @Override
//        public void run() {
//            sendData(GET_TEMP);
//        }
//    };
    //endregion

    public double nextDouble(final double min, final double max) throws Exception {
        if (max < min) {
            throw new Exception("min > max");
        }
        if (min == max) {
            return min;
        }
        return min + ((max - min) * random.nextDouble());
    }

    private int reactionTime = Global.cardWarmTime;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_test:
                if (isNc()) {
                    if (Ac < COMPARE_MIN_VALUE) {
                        APPUtils.showToast(this, "请先对照");
                        return;
                    }
                }
                if (isTesting || isComparing) {
                    new AlertDialog.Builder(this).setTitle("提示")
                            .setMessage("正在检测，请等待").create().show();
                    return;
                }
                test();
                break;
            case R.id.tv_alis:
                if (isTesting || isComparing) {
                    new AlertDialog.Builder(this).setTitle("提示")
                            .setMessage("正在检测，请等待").create().show();
                    return;
                }
                tv_alis.setSelected(!tv_alis.isSelected());
                testAdapter.setAllSelect(tv_alis.isSelected());

                break;
            case R.id.btn_return:
                onBackPressed();
                break;
            case R.id.btn_print:
                if (isTesting || isComparing) {
                    new AlertDialog.Builder(this).setTitle("提示")
                            .setMessage("正在检测，请等待").create().show();
                    return;
                }
                print();
                break;
            case R.id.btn_compare:
//                Ac = 20;
//                saveAc2Sp();
                if (isTesting || isComparing) {
                    new AlertDialog.Builder(this).setTitle("提示")
                            .setMessage("正在检测，请等待").create().show();
                    return;
                }
                compare();
                break;
            case R.id.btn_upload:
                if (isTesting || isComparing) {
                    new AlertDialog.Builder(this).setTitle("提示")
                            .setMessage("正在检测，请等待").create().show();
                    return;
                }
                upload(true);
                break;
            case R.id.ll_select_all://全选
                if (isTesting || isComparing) {
                    new AlertDialog.Builder(this).setTitle("提示")
                            .setMessage("正在检测，请等待").create().show();
                    return;
                }
                cbSelectAll.setSelected(!cbSelectAll.isSelected());
                testAdapter.setAllSelect(cbSelectAll.isSelected());
                break;
            default:

                break;
        }
    }

    private void upload(boolean autoUpload) {

        if (!ToolUtils.isNetworkConnected(this)) {
            APPUtils.showToast(this, "请先连接网络");
            return;
        }
        if (testAdapter.getSelectedCount() == 0) {
            APPUtils.showToast(this, "请先选中数据");
            return;
        }
        if (isUploading) {
            APPUtils.showToast(this, "正在上传数据，请稍后...");
            return;
        }
        List<CheckResult> uploadList = new ArrayList<>();
        List<CheckResult> selectedList = testAdapter.getSelectedList();
        for (CheckResult checkResult : selectedList) {
            if (checkResult != null
                    && !TextUtils.isEmpty(checkResult.sampleName)
                    && !TextUtils.isEmpty(checkResult.testValue)
                    && !TextUtils.isEmpty(checkResult.resultJudge)) {
                uploadList.add(checkResult);
            }
        }
        if (uploadList.isEmpty()) {
            APPUtils.showToast(this, "璇峰厛瀹屾垚閫夋嫨鏁版嵁");
            return;
        }
        isUploading = true;

        UploadThread t = new UploadThread(this, uploadList, new UploadThread.onUploadListener() {
            @Override
            public void onSuccess(List<CheckResult> list, int returnId, int position, String result) {
                if (!act.isFinishing()) {
                    if (autoUpload) {
                        APPUtils.showToast(act, "上传成功");
                    }
                    updateUploadState2Db(list);
                }
            }

            @Override
            public void onFail(String failInfo) {
                if (!act.isFinishing()) {
                    runOnUiThread(() -> APPUtils.showToast(act, failInfo));
                }
            }
        });
        t.start();
    }

    private void updateUploadState2Db(List<CheckResult> list) {

        new CheckResult().updateAll(list, new String[]{"uploadId"});
    }


    private int compareChannelIndex = 0;

    private void compare() {
        if (testAdapter.getSelectedCount() == 0
                || testAdapter.getSelectedCount() > 1) {
            APPUtils.showToast(this, "请选择一个通道进行对照");
            return;
        }
        compareChannelIndex = testAdapter.getSelectedArray()[0] - 1;
        Log.d(TAG, "compareChannelIndex:" + compareChannelIndex);
        if (!validateBusyOrNot()) {
            return;
        }
        isComparing = true;
//        reactionTime = Global.cardWarmTime * 60;//new
//        if (Global.DEBUG) {
//            reactionTime = Global.cardWarmTime;
//        }

        startReaction();
    }


    private void print() {
        if (resultList == null || resultList.isEmpty()) {
            APPUtils.showToast(act, "请先检测");
            return;
        }
        List<CheckResult> printList = testAdapter.getSelectedList();
        if (printList == null || printList.size() == 0) {
            APPUtils.showToast(act, "请先选择数据");
        } else {


            byte[] data = ToolUtils.assemblePrintCheck(printList, this);
            Log.d(TAG, "data:" + new String(data));

//            if (SerialUtils.COM4_SendData(data)) {
//                APPUtils.showToast(act, "打印数据发送成功");
//            } else {
//                APPUtils.showToast(act, "打印失败");
//            }

            ToolUtils.printData(printList, this);   //含二维码小票数据打印
        }

    }

    //region 测试专用
    private void t1() {

        int selectedCount = testAdapter.getSelectedCount();
        if (selectedCount == 0) {
            APPUtils.showToast(this, "请勾选检测通道");
            return;
        }
        selectedChannels = new int[selectedCount];

        String result = "205,155,55|255,255,255|255,255,255|255,255,255|5,255,25|255,255,255|5,25,155|255,255,255|255,255,255|255,255,255|255,255,255|255,255,255";
        String[] st = result.split("\\|");
        if (st.length != CHANNEL_COUNT) {
            mHandler.sendEmptyMessage(ToolUtils.test_fail);
            return;
        }

        List<String[]> list = new ArrayList<>();
        for (int i = 0; i < st.length; i++) {
            String[] s = st[i].split(",");
            if (s.length != 3) {
                mHandler.sendEmptyMessage(ToolUtils.test_fail);
                return;
            }
            list.add(s);
        }

        List<Integer> intList = new ArrayList<>();
        try {
            int sum;
            int index = 0;
            for (int j = 0; j < list.size(); j++) {
                sum = 0;
                if (index == selectedChannels.length) break;
                if (selectedChannels[index] == j + 1) {
                    index++;
                    for (int i = 0; i < list.get(j).length; i++) {
                        sum += Integer.parseInt(list.get(j)[i]);
                    }
                    intList.add(sum);
                }
            }
            mHandler.obtainMessage(ToolUtils.test_success, intList).sendToTarget();
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(ToolUtils.test_fail);
            return;
        }
    }
    //endregion

    private void showOperateDialog() {

        View view = View.inflate(this, R.layout.dialog_operation_help, null);
        Dialog dialog = new AlertDialog.Builder(this).create();
        view.findViewById(R.id.btn_ok).setOnClickListener((v) -> dialog.dismiss());
        dialog.show();
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(this, 600));
    }

    private boolean validateBusyOrNot() {

        if (isTesting) {
            APPUtils.showToast(this, "正在检测...");
            return false;
        }
        if (isComparing) {
            APPUtils.showToast(this, "正在对照...");
            return false;
        }
        return true;
    }

    int[] selectedChannels = null;

    private void test() {
        isUploading = false;
//        if (!validateBusyOrNot() || !validateCommonDataIsComplete() || !validateDataIsComplete())
//            return;

        int selectedCount = testAdapter.getSelectedCount();
        if (selectedCount == 0) {
            APPUtils.showToast(this, "请勾选检测通道");
            return;
        } else if (!testAdapter.verification()) {
            return;
        }

        isTesting = true;
        selectedChannels = new int[selectedCount];
        selectedChannels = testAdapter.getSelectedArray();
//        reactionTime = Global.cardWarmTime;//new
//        if (Global.DEBUG) {
//            reactionTime = Global.cardWarmTime;
//        }
        startReaction();
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ToolUtils.test_fail:
                    Log.d("jiance", "ToolUtils.test_fail" + ToolUtils.test_fail);
                    failHandle();
                    break;
                case ToolUtils.update_countdown:
                    Log.d("jiance", "ToolUtils.update_countdown" + ToolUtils.update_countdown);
                    int i = (int) msg.obj;
                    tv_status.setText("检测中: " + i + "s");
                    break;
                case ToolUtils.test_success:
                    Log.d("jiance", "ToolUtils.test_success" + ToolUtils.test_success);
                    isTesting = false;
                    showTestResult();
                    if (Global.uploadModel == 1) {
                        upload(true);
                    }
                    tv_status.setText("检测成功");
                    cbSelectAll.setSelected(false);
                    spn_project.setEnabled(true);
                    break;
                case ToolUtils.testing:
                    Log.d("jiance", "ToolUtils.testing" + ToolUtils.testing);
                    tv_status.setText("检测中...");
                    break;
                case ToolUtils.compare_fail:
                    Log.d("jiance", "ToolUtils.compare_fail" + ToolUtils.compare_fail);
                    failHandle();
                    break;
                case ToolUtils.compare_success:
                    Log.d("jiance", "ToolUtils.compare_success" + ToolUtils.compare_success);
                    isComparing = false;
                    spn_project.setEnabled(true);
                    if (isNc()) {
                        if (Ac < COMPARE_MIN_VALUE) {
//                            APPUtils.showToast(PesticideTestActivity2.this,"ac="+Ac);
                            tv_status.setText("对照失败,请重新对照");
                            showFailedHintDialog();
                        } else {
                            tv_status.setText("对照成功");
                            tvCompareValue.setText(getResources().
                                    getString(R.string.contrastValue) +
                                    df.format(Ac));
                            PreferencesUtils.putString(PesticideTestActivity2.this, "Ac", df.format(Ac));
                            btnTest.setEnabled(true);
                        }
                    } else {
                        tv_status.setText("对照成功");
//                            tvCompareValue.setText(getResources().
//                                    getString(R.string.contrastValue) +
//                                    df.format(Ac * COMPARE_FACTOR));
                        btnTest.setEnabled(true);
                    }
                    break;
            }
        }
    };
    AlertDialog failedHintDialog;

    /**
     *
     */
    private void showFailedHintDialog() {
        if (failedHintDialog == null) {
            failedHintDialog = new AlertDialog.Builder(this)
                    .setTitle("提示信息")
                    .setMessage("对照失败，请检查操作规范或试剂是否失效！")
                    .create();
        }
        failedHintDialog.show();
    }

    /**
     * 恢复倒计时
     */
    public void countDownClear() {
        if (isNc()) {
            reactionTime = Global.cardWarmTime;
        } else {
            reactionTime = 3;
        }
    }

    List<CheckResult> savaDatas;

    //2019.6.14 before
    private void showTestResult() {
        savaDatas = new ArrayList<>();
        int index = 0;
        double factor = 50.0 / Global.project.cardXlz;
        List<CheckResult> crs = testAdapter.getData();
        for (int i = 0; i < CHANNEL_COUNT; i++) {
            if (selectedChannels.length > index && selectedChannels[index] == i + 1) {
                double xlz = getXLZ(i);//限量标准值

                CheckResult cr = crs.get(i);
                if (isNc()) {
                    double value = AsList.get(i) * 100;
                    value = value > 100 ? 100 : value;
                    value = value < 0 ? 0 : value;
                    value = Double.parseDouble(df.format(value));

//                    APPUtils.showToast(this, "value=" + value + " xlz=" + xlz);
                    Log.d(TAG, "i=" + i + " value=" + value + " xlz=" + xlz);

                    CheckResult tempResult = new CheckResult(
                            Global.checkOrg == null ? "无" : Global.checkOrg,
                            cr.bcheckedOrganization,
                            mProject.projectName,
                            cr.sampleNum,
                            cr.sampleName,
                            cr.sampleSource,
                            "A" + (i + 1), System.currentTimeMillis(),
                            value + "%",
                            value <= 50 ? "合格" : "不合格",
//                        Global.project.cardXlz + "%",
                            xlz + "%",
                            Global.project.testStandard,
                            Global.inspector == null ? "无" : Global.inspector,
                            cr.weight, new Random().nextInt(100000) + "",
                            Global.project.unit);
                    tempResult.isSelected = cr.isSelected;
                    Log.d(TAG, "i=" + i + "sn=" + cr.sn);
                    tempResult.sn = cr.sn;
                    resultList.set(i, tempResult);
                    savaDatas.add(tempResult);
                    index++;
                } else if (isGyhwm()) {
//                    float logresult = Math.abs(log((ac1/as1List[i]), 10));
                    float logresult = (float) Math.log10(d[i]) - (float) Math.log10(as1List[i]);
                    double value = mProject.k * (logresult) + mProject.b;

//                    APPUtils.showToast(this, "value=" + value + " xlz=" + xlz);
                    Log.d(TAG, "value =" + value + "ac=" + ac1 + "as=" + as1List[i] + "logresult=" + logresult);
                    Log.d(TAG, "i=" + i + " value=" + value + " xlz=" + xlz);
                    value = Double.parseDouble(df.format(value));

//                    APPUtils.showToast(this, "value=" + value);

                    Log.d(TAG, "value b=" + value);
                    Log.d(TAG, "i=" + i + " value=" + value + " xlz=" + xlz);
                    double jcx = mProject.cardXlz;//检出限

                    if (xlz == 999) {
                        Log.d(TAG, "value a=" + value);
                        Log.d(TAG, "i=" + i + " value=" + value + " xlz=" + xlz);
//                        APPUtils.showToast(this, "value=" + value + " xlz=" + xlz);
//                        if (value < jcx) {
//                            value = 0;
//                        }
                        if (value <= 0) {
                            value = 0;
                        }

                        tempResult = new CheckResult(
                                Global.checkOrg == null ? "无" : Global.checkOrg,
                                cr.bcheckedOrganization,
                                mProject.projectName,
                                cr.sampleNum,
                                cr.sampleName,
                                cr.sampleSource,
                                "A" + (i + 1), System.currentTimeMillis(),
                                df.format(value) + "",
                                value >= 0 ? "阴性" : "阳性",
                                "不做要求",
                                Global.project.testStandard,
                                Global.inspector == null ? "无" : Global.inspector,
                                cr.weight, new Random().nextInt(100000) + "",
                                Global.project.unit);


                    } else if (xlz == 888) {
                        Log.d(TAG, "value a=" + value);
                        Log.d(TAG, "i=" + i + " value=" + value + " xlz=" + xlz);
//                        APPUtils.showToast(this, "value=" + value + " xlz=" + xlz);
//                        if (value < jcx) {
//                            value = 0;
//                        }
                        if (value <= 0) {
                            value = 0;
                        }

                        tempResult = new CheckResult(
                                Global.checkOrg == null ? "无" : Global.checkOrg,
                                cr.bcheckedOrganization,
                                mProject.projectName,
                                cr.sampleNum,
                                cr.sampleName,
                                cr.sampleSource,
                                "A" + (i + 1), System.currentTimeMillis(),
                                df.format(value) + "",
                                value >= 0 ? "阴性" : "阳性",
                                "加工助剂",
                                Global.project.testStandard,
                                Global.inspector == null ? "无" : Global.inspector,
                                cr.weight, new Random().nextInt(100000) + "",
                                Global.project.unit);


                    } else if (xlz == 666) {
                        Log.d(TAG, "value a=" + value);
                        Log.d(TAG, "i=" + i + " value=" + value + " xlz=" + xlz);
//                        APPUtils.showToast(this, "value=" + value + " xlz=" + xlz);
//                        if (value < jcx) {
//                            value = 0;
//                        }
                        if (value <= 0) {
                            value = 0;
                        }

                        tempResult = new CheckResult(
                                Global.checkOrg == null ? "无" : Global.checkOrg,
                                cr.bcheckedOrganization,
                                mProject.projectName,
                                cr.sampleNum,
                                cr.sampleName,
                                cr.sampleSource,
                                "A" + (i + 1), System.currentTimeMillis(),
                                df.format(value) + "",
                                value >= 0 ? "阴性" : "阳性",
                                "内源性物质",
                                Global.project.testStandard,
                                Global.inspector == null ? "无" : Global.inspector,
                                cr.weight, new Random().nextInt(100000) + "",
                                Global.project.unit);


                    } else if (xlz == 555) {
                        Log.d(TAG, "value a=" + value);
                        Log.d(TAG, "i=" + i + " value=" + value + " xlz=" + xlz);
//                        APPUtils.showToast(this, "value=" + value + " xlz=" + xlz);
//                        if (value < jcx) {
//                            value = 0;
//                        }
                        if (value <= 0) {
                            value = 0;
                        }

                        tempResult = new CheckResult(
                                Global.checkOrg == null ? "无" : Global.checkOrg,
                                cr.bcheckedOrganization,
                                mProject.projectName,
                                cr.sampleNum,
                                cr.sampleName,
                                cr.sampleSource,
                                "A" + (i + 1), System.currentTimeMillis(),
                                df.format(value) + "",
                                value >= 0 ? "阴性" : "阳性",
                                "无限量值",
                                Global.project.testStandard,
                                Global.inspector == null ? "无" : Global.inspector,
                                cr.weight, new Random().nextInt(100000) + "",
                                Global.project.unit);


                    } else {//限量标准大于0
                        Log.d(TAG, "value2 a=" + value);

//                        if (value < jcx) {
//                            value = 0;
//                        }
                        if (value <= 0) {
                            value = 0;
                        }
                        Log.d(TAG, "i=" + i + " value=" + value + " xlz=" + xlz);
//                        APPUtils.showToast(this, "value=" + value + " xlz=" + xlz);
                        tempResult = new CheckResult(
                                Global.checkOrg == null ? "无" : Global.checkOrg,
                                cr.bcheckedOrganization,
                                mProject.projectName,
                                cr.sampleNum,
                                cr.sampleName,
                                cr.sampleSource,
                                "A" + (i + 1), System.currentTimeMillis(),
                                value == 0 ? "未检出" : df.format(value) + "",
                                value > xlz ? "合格" : "不合格",
                                df.format(xlz) + "",
                                Global.project.testStandard,
                                Global.inspector == null ? "无" : Global.inspector,
                                cr.weight, new Random().nextInt(100000) + "",
                                Global.project.unit);


                    }
                    tempResult.isSelected = cr.isSelected;
                    tempResult.sn = cr.sn;
                    resultList.set(i, tempResult);
                    savaDatas.add(tempResult);

                    index++;
                } else {
//                    float logresult = Math.abs(log((ac1/as1List[i]), 10));
                    float logresult = (float) Math.log10(d[i]) - (float) Math.log10(as1List[i]);
                    double value = mProject.k * (logresult) + mProject.b;

//                    APPUtils.showToast(this, "value=" + value + " xlz=" + xlz);
                    Log.d(TAG, "value =" + value + "ac=" + ac1 + "as=" + as1List[i] + "logresult=" + logresult);
                    Log.d(TAG, "i=" + i + " value=" + value + " xlz=" + xlz);
                    value = Double.parseDouble(df.format(value));

//                    APPUtils.showToast(this, "value=" + value);

                    Log.d(TAG, "value b=" + value);
                    Log.d(TAG, "i=" + i + " value=" + value + " xlz=" + xlz);
                    double jcx = mProject.cardXlz;//检出限

                    if (xlz == 999) {
                        Log.d(TAG, "value a=" + value);
                        Log.d(TAG, "i=" + i + " value=" + value + " xlz=" + xlz);
//                        APPUtils.showToast(this, "value=" + value + " xlz=" + xlz);
//                        if (value < jcx) {
//                            value = 0;
//                        }
                        if (value <= 0) {
                            value = 0;
                        }

                        tempResult = new CheckResult(
                                Global.checkOrg == null ? "无" : Global.checkOrg,
                                cr.bcheckedOrganization,
                                mProject.projectName,
                                cr.sampleNum,
                                cr.sampleName,
                                cr.sampleSource,
                                "A" + (i + 1), System.currentTimeMillis(),
                                df.format(value) + "",
                                value <= 0 ? "阴性" : "阳性",
                                "不做要求",
                                Global.project.testStandard,
                                Global.inspector == null ? "无" : Global.inspector,
                                cr.weight, new Random().nextInt(100000) + "",
                                Global.project.unit);


                    } else if (xlz == 888) {
                        Log.d(TAG, "value a=" + value);
                        Log.d(TAG, "i=" + i + " value=" + value + " xlz=" + xlz);
//                        APPUtils.showToast(this, "value=" + value + " xlz=" + xlz);
//                        if (value < jcx) {
//                            value = 0;
//                        }
                        if (value <= 0) {
                            value = 0;
                        }

                        tempResult = new CheckResult(
                                Global.checkOrg == null ? "无" : Global.checkOrg,
                                cr.bcheckedOrganization,
                                mProject.projectName,
                                cr.sampleNum,
                                cr.sampleName,
                                cr.sampleSource,
                                "A" + (i + 1), System.currentTimeMillis(),
                                df.format(value) + "",
                                value <= 0 ? "阴性" : "阳性",
                                "加工助剂",
                                Global.project.testStandard,
                                Global.inspector == null ? "无" : Global.inspector,
                                cr.weight, new Random().nextInt(100000) + "",
                                Global.project.unit);


                    } else if (xlz == 666) {
                        Log.d(TAG, "value a=" + value);
                        Log.d(TAG, "i=" + i + " value=" + value + " xlz=" + xlz);
//                        APPUtils.showToast(this, "value=" + value + " xlz=" + xlz);
//                        if (value < jcx) {
//                            value = 0;
//                        }
                        if (value <= 0) {
                            value = 0;
                        }

                        tempResult = new CheckResult(
                                Global.checkOrg == null ? "无" : Global.checkOrg,
                                cr.bcheckedOrganization,
                                mProject.projectName,
                                cr.sampleNum,
                                cr.sampleName,
                                cr.sampleSource,
                                "A" + (i + 1), System.currentTimeMillis(),
                                df.format(value) + "",
                                value <= 0 ? "阴性" : "阳性",
                                "内源性物质",
                                Global.project.testStandard,
                                Global.inspector == null ? "无" : Global.inspector,
                                cr.weight, new Random().nextInt(100000) + "",
                                Global.project.unit);


                    } else if (xlz == 555) {
                        Log.d(TAG, "value a=" + value);
                        Log.d(TAG, "i=" + i + " value=" + value + " xlz=" + xlz);
//                        APPUtils.showToast(this, "value=" + value + " xlz=" + xlz);
//                        if (value < jcx) {
//                            value = 0;
//                        }
                        if (value <= 0) {
                            value = 0;
                        }

                        tempResult = new CheckResult(
                                Global.checkOrg == null ? "无" : Global.checkOrg,
                                cr.bcheckedOrganization,
                                mProject.projectName,
                                cr.sampleNum,
                                cr.sampleName,
                                cr.sampleSource,
                                "A" + (i + 1), System.currentTimeMillis(),
                                df.format(value) + "",
                                value <= 0 ? "阴性" : "阳性",
                                "无限量值",
                                Global.project.testStandard,
                                Global.inspector == null ? "无" : Global.inspector,
                                cr.weight, new Random().nextInt(100000) + "",
                                Global.project.unit);


                    } else {//限量标准大于0
                        Log.d(TAG, "value2 a=" + value);

//                        if (value < jcx) {
//                            value = 0;
//                        }
                        if (value <= 0) {
                            value = 0;
                        }
                        Log.d(TAG, "i=" + i + " value=" + value + " xlz=" + xlz);
//                        APPUtils.showToast(this, "value=" + value + " xlz=" + xlz);
                        tempResult = new CheckResult(
                                Global.checkOrg == null ? "无" : Global.checkOrg,
                                cr.bcheckedOrganization,
                                mProject.projectName,
                                cr.sampleNum,
                                cr.sampleName,
                                cr.sampleSource,
                                "A" + (i + 1), System.currentTimeMillis(),
                                value == 0 ? "未检出" : df.format(value) + "",
                                value <= xlz ? "合格" : "不合格",
                                df.format(xlz) + "",
                                Global.project.testStandard,
                                Global.inspector == null ? "无" : Global.inspector,
                                cr.weight, new Random().nextInt(100000) + "",
                                Global.project.unit);


                    }
                    tempResult.isSelected = cr.isSelected;
                    tempResult.sn = cr.sn;
                    resultList.set(i, tempResult);
                    savaDatas.add(tempResult);

                    index++;
                }
            } else {
                CheckResult cr = new CheckResult();
                cr.channel = "A" + (i + 1);
                resultList.set(i, cr);
            }

        }
        Log.d(TAG, "resultList=" + resultList.toString());
        testAdapter.setData(resultList);
//        testAdapter.notifyDataSetChanged();
        boolean isSuccess = new CheckResult().saveBindingIdAll(savaDatas);
        Log.d(TAG, "isSuccess=" + isSuccess);
    }

    public static float log(float value, float base) {
        return (float) ((float) Math.log(value) / (float) Math.log(base));
    }

    private double getXLZ(int index) {
        double jcx = 0;
        Log.d(TAG, "getXLZ index=" + index);
        List<CheckResult> crs = testAdapter.getData();
        CheckResult checkResult = crs.get(index);
        if (checkResult != null && checkResult.sn != null) {
            try {
                List<SampleName.Project> snps = checkResult.sn.getProjects();
                if (snps != null) {
                    for (int i = 0; i < snps.size(); i++) {
                        if (snps.get(i).getProjectName().equals(mProject.projectName)) {
                            jcx = snps.get(i).jcx;
                            Log.d(TAG, "getXLZ jcx=" + jcx);
                            return jcx;
                        }
                    }
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        return jcx;
    }

    protected boolean validateCommonDataIsComplete() {

        if (TextUtils.isEmpty(((CheckResult) testAdapter.getData()
                .get(compareChannelIndex)).bcheckedOrganization)) {
            APPUtils.showToast(act, "被检单位");
            return false;
        }
        if (TextUtils.isEmpty(((CheckResult) testAdapter.getData()
                .get(compareChannelIndex)).sampleSource)) {
            APPUtils.showToast(act, "请输入商品来源");
            return false;
        }
        return true;
    }

    private void failHandle() {
        spn_project.setEnabled(true);
        if (isTesting) {
            isTesting = false;
            cancelTimer();
            tv_status.setText("检测失败");
        } else {
            isComparing = false;
            cancelTimer();
            tv_status.setText("对照失败");
            showFailedHintDialog();
        }
    }

    private void startCountDown() {

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        timerTask = new BSCountDownTask();
        mTimer = new Timer();
        //curDownTime = Global.cardWarmTime * 60;//old
        curDownTime = Global.cardReactionTime;//new
        if (Global.DEBUG) {
            curDownTime = Global.cardReactionTime;
        }
//
        mTimer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    boolean isFinishing = false;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //拦截返回键
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //判断触摸UP事件才会进行返回事件处理
            if (event.getAction() == KeyEvent.ACTION_UP) {
                onBackPressed();
            }
            //只要是返回事件，直接返回true，表示消费掉
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (isTesting || isComparing) {
            new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("正在检测，是否终止检测?")
                    .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isFinishing = true;
                            dialog.dismiss();
                            countDownLatch();
                            finish();
                        }
                    }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            countDownLatch();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
//        Log.i(TAG, "onDestroy()");
        if (mTimer != null) mTimer.cancel();
        if (reactionTimer != null) reactionTimer.cancel();
        if (tempTimer != null) tempTimer.cancel();
        if (timerTask != null) timerTask.cancel();
        countDownLatch();
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void cancelTimer() {

        if (mTimer != null) {
            mTimer.cancel();
        }
        if (reactionTimer != null) {
            reactionTimer.cancel();
        }
    }

    Timer mTimer = null;

    int curDownTime = 0;

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_in_out_card:
                if (validateBusyOrNot()) {
                    new Thread() {
                        @Override
                        public void run() {
                            sendData(ENTER_OUT_CARD);
                        }
                    }.start();
                }
                break;
            case R.id.action_open_close_lib:
                if (validateBusyOrNot()) {
                    new Thread() {
                        @Override
                        public void run() {
                            sendData(OPEN_CLOSE_LIB);
                        }
                    }.start();
                }
                break;
            case R.id.action_operate_procedure:
                showOperateDialog();
                break;
        }
        return true;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mProject = projects.get(position);
        if (isNc()) {
            tv_yzl.setText("抑制率");
//            tv_yzl.setText("吸光度");
            tvCompareValue.setVisibility(View.VISIBLE);
            initSp();
        } else {
            Ac = 0;
            tv_yzl.setText("检测值");
            APPUtils.showToast(this, "请先进行空白对照");
            tvCompareValue.setVisibility(View.GONE);
            btnTest.setEnabled(false);
        }


        countDownClear();
        openLight(mProject.bochang);
        Log.d(TAG, "onItemSelected mProject=" + mProject.toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class BSCountDownTask extends TimerTask {

        @Override
        public void run() {
            if (isFinishing()) return;
            mHandler.obtainMessage(ToolUtils.update_countdown, --curDownTime)
                    .sendToTarget();
            if (curDownTime <= 0) {
                mTimer.cancel();
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                Log.d(TAG, "BSCountDownTask testing=" + isTesting);
                mHandler.sendEmptyMessage(ToolUtils.testing);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isTesting) {
                            Log.d(TAG, "BSCountDownTask TEST_END=");
                            sendData(TEST_END);
                        } else {
                            Log.d(TAG, "BSCountDownTask COMPARE_END=");
                            sendData(COMPARE_END);
                        }
                    }
                });

            }

        }
    }

    private void
    startReaction() {
        countDownClear();
        final int time = reactionTime;
        spn_project.setEnabled(false);
        if (reactionTimer != null) {
            reactionTimer.cancel();
            reactionTimer = null;
        }
        reactionTimer = new Timer();
        reactionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (reactionTime == time) {
                            tv_status.setText("检测中:" + reactionTime + "s");
                            if (isNc()) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        if (isTesting) {
                                            sendData(TEST_START);
                                        } else {
                                            sendData(COMPARE_START);
                                        }
                                    }
                                }.start();
                            }
                        } else if (reactionTime == 0) {
                            mHandler.sendEmptyMessage(ToolUtils.testing);
                            if (!isNc()) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isTesting) {
                                            sendData(TEST_START);
                                        } else {
                                            sendData(COMPARE_START);
                                        }
                                    }
                                });
                            } else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isTesting) {
                                            Log.d(TAG, "BSCountDownTask TEST_END=");
                                            sendData(TEST_END);
                                        } else {
                                            Log.d(TAG, "BSCountDownTask COMPARE_END=");
                                            sendData(COMPARE_END);
                                        }
                                    }
                                });

                            }
                            if (reactionTimer != null) {
                                reactionTimer.cancel();
                                reactionTimer = null;
                            }
                        } else {
                            tv_status.setText("检测中:" + reactionTime + "s");
                        }
                        reactionTime--;
                    }
                });
            }
        }, 0, 1000);
    }

    private void executeTest(final int flag) {
        if (BuildConfig.DEBUG) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (flag == TEST_START) {
                        as1List = createMockValues(1000f);
                        d = createMockValues(700f);
                        if (!isNc()) {
                            mHandler.sendEmptyMessage(ToolUtils.test_success);
                        }
                    } else if (flag == TEST_END) {
                        as2List = createMockValues(700f);
                        buildMockAsList();
                        mHandler.sendEmptyMessage(ToolUtils.test_success);
                    }
                    countDownLatch();
                }
            }, 3000);
            return;
        }
        long delTime = 0;
        if (isNc()) {
            delTime = 3000;
        } else {
            delTime = 3000;
        }
        if (Global.DEBUG) Log.i(TAG, "发送数据====" + new String(Global.GETALLDATA));
        if (!SerialUtils.COM3_SendData(Global.GETALLDATA)) {
            sendEmptyMessage(false);
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    byte[] response = SerialUtils.COM3_RevData();
                    if (response == null || response.length == 0) {
                        sendEmptyMessage(false);
                    } else {
                        Log.i(TAG, "接收到的检测数据======" + new String(response));
                        float[] fList = dealTestData(response);
                        if (fList == null) {
                            sendEmptyMessage(false);
                        } else {
                            if (flag == TEST_START) {
                                as1List = fList;
                                if (!isNc()) {
                                    mHandler.sendEmptyMessage(ToolUtils.test_success);
                                }
                                return;
                            } else if (flag == TEST_END) {
                                as2List = fList;
                                float[] tempAcList = new float[CHANNEL_COUNT];
                                for (int i = 0; i < as2List.length; i++) {
                                    tempAcList[i] = (float) Math.log10(as1List[i] / as2List[i]);
                                    Log.d(TAG, "as1List[i]=" + as1List[i] + "as2List[i]=" + as2List[i]);
                                }
                                AsList = new ArrayList<>();
                                for (int i = 0; i < as2List.length; i++) {
                                    double temp = 0;
                                    if (Ac - tempAcList[i] < 0) {
                                        temp = 0;
                                    } else {
                                        temp = (Ac - tempAcList[i]) / Ac;
                                    }
                                    Log.d(TAG, "temp=" + temp);
                                    AsList.add(temp > 1 ? 1 : temp);
                                }
                                mHandler.sendEmptyMessage(ToolUtils.test_success);
                            }
                        }
                    }
                    countDownLatch();
                }
            }, delTime);
        }
    }

    private float[] dealTestData(byte[] response) {
        float[] ds = null;
        String result = new String(response).replace("OK", "").replace("\n", "");
        Log.d(TAG, "dealTestData result=" + result);
        String[] st = result.split(",");
        if (st.length != CHANNEL_COUNT) {
            return null;
        }

        ds = new float[CHANNEL_COUNT];
        for (int i = 0; i < CHANNEL_COUNT; i++) {
            ds[i] = Float.valueOf(st[i]);
        }
        return ds;
    }


    private float[] computeAc(byte[] response) {

        float[] ds = null;
        String result = new String(response).replace("OK", "").replace("\n", "");
        String[] st = result.split(",");
        if (st.length != CHANNEL_COUNT) {
            return null;
        }
        ds = new float[CHANNEL_COUNT];
        for (int i = 0; i < CHANNEL_COUNT; i++) {
            ds[i] = Float.valueOf(st[i]);
        }
        return ds;
    }

    private void sendEmptyMessage(boolean isSucc) {

        if (isSucc) {
            if (isComparing) {
                mHandler.sendEmptyMessage(ToolUtils.compare_success);
            } else {
                mHandler.sendEmptyMessage(ToolUtils.test_success);
            }
        } else {
            if (isComparing) {
                mHandler.sendEmptyMessage(ToolUtils.compare_fail);
            } else {
                mHandler.sendEmptyMessage(ToolUtils.test_fail);
            }
        }
    }

    private synchronized void sendData(int flag) {
        Log.d(TAG, "sendData flag=" + flag);
        if (isFinishing) return;
        initLatch();
        SystemClock.sleep(100);
        if (isFinishing) {
            Log.i(TAG, "判断到窗口正在关闭...");
            return;
        }
        switch (flag) {
            case COMPARE_START:
                compareRecData(flag);
                break;
            case COMPARE_END:
                compareRecData(flag);
                break;
            case TEST_START:
                if (DEBUG) Log.i(TAG, "===开始检测第一次取值");
                executeTest(flag);
                break;
            case TEST_END:
                if (DEBUG) Log.i(TAG, "===开始检测第2次取值");
                executeTest(flag);
                break;
        }
//        waitCountDownLatch();
    }

    private void compareRecData(final int flag) {
        if (BuildConfig.DEBUG) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    d = flag == COMPARE_START
                            ? createMockValues(1000f)
                            : createMockValues(500f);
                    if (flag == COMPARE_START) {
                        ac1 = d[compareChannelIndex];
                        if (!isNc()) {
                            mHandler.sendEmptyMessage(ToolUtils.compare_success);
                        }
                    } else {
                        Ac = (float) Math.log10(ac1) - (float) Math.log10(d[compareChannelIndex]);
                        mHandler.sendEmptyMessage(ToolUtils.compare_success);
                        saveAc2Sp();
                    }
                    countDownLatch();
                }
            }, 3500);
            return;
        }
        if (Global.DEBUG) Log.i(TAG, "发送数据====" + new String(Global.GETALLDATA));
        if (!SerialUtils.COM3_SendData(Global.GETALLDATA)) {
            sendEmptyMessage(false);
            Log.i(TAG, "compareRecData1====false");
        } else {
            Log.i(TAG, "compareRecData2====");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    byte[] response = SerialUtils.COM3_RevData();
                    if (response == null || response.length == 0) {
                        sendEmptyMessage(false);
                    } else {
                        if (Global.DEBUG) {
                            APPUtils.showToast(act, new String(response), true);
                            Log.i(TAG, "接收到的检测数据======" + new String(response));
                        }
                        d = computeAc(response);
                        if (d == null) {
                            sendEmptyMessage(false);
                        } else {
                            for (int i = 0; i < d.length; i++) {
                                Log.d(TAG, "compareRecData d" + i + "=" + d[i]);
                            }
                            if (Global.DEBUG) Log.i(TAG, "Ac======" + (d != null ? d[0] : 0));
                            if (flag == COMPARE_START) {
                                ac1 = d[compareChannelIndex];
                                Log.d(TAG, "ac1=" + ac1);
                                if (!isNc()) {//多功能只需取一次值便对照完成
                                    mHandler.sendEmptyMessage(ToolUtils.compare_success);
                                }
                                return;
                            } else {
                                Ac = (float) Math.log10(ac1) - (float) Math.log10(d[compareChannelIndex]);  //计算对照值
                                mHandler.sendEmptyMessage(ToolUtils.compare_success);
                                saveAc2Sp();
                                Log.d(TAG, "ac1=" + ac1 + "d[compareChannelIndex]=" + d[compareChannelIndex] + "Ac=" + Ac);
                                if (Global.DEBUG) Log.i(TAG, "AC2======" + Ac);
                            }
                        }
                    }
                    countDownLatch();
                    if (Global.DEBUG) Log.i(TAG, "countDownLatch().......");
                }
            }, 3500);
        }
    }

    private float[] createMockValues(float baseValue) {
        float[] values = new float[CHANNEL_COUNT];
        for (int i = 0; i < values.length; i++) {
            values[i] = baseValue + i * 10;
        }
        return values;
    }

    private void buildMockAsList() {
        float[] tempAcList = new float[CHANNEL_COUNT];
        for (int i = 0; i < CHANNEL_COUNT; i++) {
            tempAcList[i] = (float) Math.log10(as1List[i] / as2List[i]);
        }
        AsList = new ArrayList<>();
        for (int i = 0; i < CHANNEL_COUNT; i++) {
            double temp = Ac - tempAcList[i] < 0
                    ? 0
                    : (Ac - tempAcList[i]) / Ac;
            AsList.add(temp > 1 ? 1 : temp);
        }
    }

    private void saveAc2Sp() {

        sp.edit().putFloat(SPResource.KEY_COMPARE_VALUE, Ac).apply();
    }


    private void postForAction(long delayMills) {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                countDownLatch();
            }
        }, delayMills);
    }

    private void countDownLatch() {

//        Log.i(TAG, "置零...");
        if (mCountDownLatch != null) {
            mCountDownLatch.countDown();
        }
    }

    private void initLatch() {

        if (mCountDownLatch != null) {
            mCountDownLatch.countDown();
            mCountDownLatch = null;
        }
        mCountDownLatch = new CountDownLatch(1);
    }


    private void waitCountDownLatch() {
        try {
//            Log.i(TAG, "阻塞...");
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 現在是否是检测有机磷和氨基甲酸酯类农药項目
     *
     * @return
     */
    private boolean isNc() {
        Log.d(TAG, "mProject=" + mProject.getName());
        if (mProject != null && mProject.getName().equals("有机磷和氨基甲酸酯类农药")) {
            return true;
        }
        return false;
    }

    /**
     * 現在是否是检测过氧化物酶項目
     *
     * @return
     */
    private boolean isGyhwm() {
        Log.d(TAG, "mProject=" + mProject.getName());
        if (mProject != null && mProject.getName().equals("过氧化物酶")) {
            return true;
        }
        return false;
    }


    private void executeBSData(byte[] data) {

        if (!SerialUtils.COM3_SendData(data)) {
            countDownLatch();
            mHandler.sendEmptyMessage(ToolUtils.test_fail);
            return;
        }
        if (Global.DEBUG) Log.i(TAG, "发送数据====" + new String(data));
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                countDownLatch();
                byte[] response = SerialUtils.COM3_RevData();
                if (response == null || response.length != 3) {
                    mHandler.sendEmptyMessage(ToolUtils.test_fail);
                } else {
                    String result = new String(response);
                    if (Global.DEBUG) Log.i(TAG, "接收数据====" + result);
                    if (response[0] == 'O' && response[1] == 'K' && response[2] == '\n') {

                    } else {
                        mHandler.sendEmptyMessage(ToolUtils.test_fail);
                    }
                }
            }
        }, 4500);
    }

    private BSCountDownTask timerTask;
}
