package com.tnd.multifuction1.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.model.CheckResult;
import com.tnd.multifuction1.resource.SPResource;
import com.tnd.multifuction1.util.APPUtils;
import com.tnd.multifuction1.util.DensityUtil;
import com.tnd.multifuction1.util.Global;
import com.tnd.multifuction1.util.SerialUtils;
import com.tnd.multifuction1.util.ToolUtils;
import com.tnd.multifuction1.view.MaskedEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class PesticideTestActivity extends TestActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, Toolbar.OnMenuItemClickListener {

    private static final int BS_START = 6;
    private static final int COMPARE_MIN_VALUE = 10;
    private static final String TAG = "PesticideTestActivity";
    private static int CHANNEL_COUNT = 12;
    private Button btnReturn;
    //    private Button btnOutInCard;
//    private Button btnOpenCloseLid;
    private Button btnTest;
    private CheckBox[] cbs = new CheckBox[CHANNEL_COUNT];
    private EditText[] etValues = new EditText[CHANNEL_COUNT];
    private EditText[] etResults = new EditText[CHANNEL_COUNT];
    private MaskedEditText[] etSampleNames = new MaskedEditText[CHANNEL_COUNT];
    private CheckBox cbSelectAll;

    //    private TextView tvCountDown;
    private TextView tvCurrentTemp;

    private Timer tempTimer;
    private Timer reactionTimer;

    private Button btnPrint;

    private static final int TEST_START = 0;
    private static final int TEST_END = 2;
    private static final int COMPARE_START = 1;
    private static final int COMPARE_END = 7;

    private static final int OPEN_CLOSE_LIB = 3;
    private static final int ENTER_OUT_CARD = 4;
    private static final int GET_TEMP = 5;
    private CountDownLatch mCountDownLatch;
    private int ac1;
    private int Ac;
    private Button btnCompare;
    private boolean isComparing;
    private boolean isTesting = false;
    private TextView tvCompareValue;
    private List<Integer> as1List;
    private List<Integer> as2List;
    private List<Double> AsList;
    private double COMPARE_FACTOR = 0.01;
    private SharedPreferences sp;
    private Toolbar toolBar;
    private MenuItem stateMenuItem;
    private Button btnUpload;
    //    CyclicBarrier mCyclic = new CyclicBarrier(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_pesticide_test);
        super.onCreate(savedInstanceState);
        initView();
        act = this;
        tempTimer = new Timer();
        tempTimer.schedule(tempTask, 0, 10 * 1000);
        initSp();
    }

    private void initSp() {

        sp = getSharedPreferences(SPResource.FILE_NAME, MODE_PRIVATE);
        Ac = sp.getInt(SPResource.KEY_COMPARE_VALUE, 0);
        if (Ac >= COMPARE_MIN_VALUE) {
            tvCompareValue.setText(df.format(Ac * COMPARE_FACTOR));
        }
    }


    private void initView() {

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.inflateMenu(R.menu.tool_bar_menu);
        toolBar.setOnMenuItemClickListener(this);
        stateMenuItem = toolBar.getMenu().getItem(0);

        tvCompareValue = findViewById(R.id.tv_compare_value);
//        tvCountDown = findViewById(R.id.tv_count_down);
        btnPrint = findViewById(R.id.btn_print);
        btnPrint.setOnClickListener(this);
        cbSelectAll = findViewById(R.id.cb_select_all);
        cbSelectAll.setOnCheckedChangeListener(this);
        btnTest = findViewById(R.id.btn_test);
        btnCompare = findViewById(R.id.btn_compare);
        btnCompare.setOnClickListener(this);
        btnUpload = findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(this);
        if (Global.uploadModel == 1) {
            btnUpload.setVisibility(View.GONE);
        }
//        btnOpenCloseLid = findViewById(R.id.btn_open_close_lid);
//        btnOutInCard = findViewById(R.id.btn_out_in_card);
        btnReturn = findViewById(R.id.btn_return);

        btnTest.setOnClickListener(this);
//        btnOpenCloseLid.setOnClickListener(this);
//        btnOutInCard.setOnClickListener(this);
        btnReturn.setOnClickListener(this);

        cbs[0] = findViewById(R.id.cb_A1);
        cbs[1] = findViewById(R.id.cb_A2);
        cbs[2] = findViewById(R.id.cb_A3);
        cbs[3] = findViewById(R.id.cb_A4);
        cbs[4] = findViewById(R.id.cb_A5);
        cbs[5] = findViewById(R.id.cb_A6);
        cbs[6] = findViewById(R.id.cb_A7);
        cbs[7] = findViewById(R.id.cb_A8);
        cbs[8] = findViewById(R.id.cb_A9);
        cbs[9] = findViewById(R.id.cb_A10);
        cbs[10] = findViewById(R.id.cb_A11);
        cbs[11] = findViewById(R.id.cb_A12);
        for (int i = 0; i < cbs.length; i++) {
            cbs[i].setOnCheckedChangeListener(this);
        }

        etValues[0] = findViewById(R.id.et_A1_value);
        etValues[1] = findViewById(R.id.et_A2_value);
        etValues[2] = findViewById(R.id.et_A3_value);
        etValues[3] = findViewById(R.id.et_A4_value);
        etValues[4] = findViewById(R.id.et_A5_value);
        etValues[5] = findViewById(R.id.et_A6_value);
        etValues[6] = findViewById(R.id.et_A7_value);
        etValues[7] = findViewById(R.id.et_A8_value);
        etValues[8] = findViewById(R.id.et_A9_value);
        etValues[9] = findViewById(R.id.et_A10_value);
        etValues[10] = findViewById(R.id.et_A11_value);
        etValues[11] = findViewById(R.id.et_A12_value);

        etResults[0] = findViewById(R.id.et_A1_result);
        etResults[1] = findViewById(R.id.et_A2_result);
        etResults[2] = findViewById(R.id.et_A3_result);
        etResults[3] = findViewById(R.id.et_A4_result);
        etResults[4] = findViewById(R.id.et_A5_result);
        etResults[5] = findViewById(R.id.et_A6_result);
        etResults[6] = findViewById(R.id.et_A7_result);
        etResults[7] = findViewById(R.id.et_A8_result);
        etResults[8] = findViewById(R.id.et_A9_result);
        etResults[9] = findViewById(R.id.et_A10_result);
        etResults[10] = findViewById(R.id.et_A11_result);
        etResults[11] = findViewById(R.id.et_A12_result);

        etSampleNames[0] = findViewById(R.id.et_A1_sample_name);
        etSampleNames[1] = findViewById(R.id.et_A2_sample_name);
        etSampleNames[2] = findViewById(R.id.et_A3_sample_name);
        etSampleNames[3] = findViewById(R.id.et_A4_sample_name);
        etSampleNames[4] = findViewById(R.id.et_A5_sample_name);
        etSampleNames[5] = findViewById(R.id.et_A6_sample_name);
        etSampleNames[6] = findViewById(R.id.et_A7_sample_name);
        etSampleNames[7] = findViewById(R.id.et_A8_sample_name);
        etSampleNames[8] = findViewById(R.id.et_A9_sample_name);
        etSampleNames[9] = findViewById(R.id.et_A10_sample_name);
        etSampleNames[10] = findViewById(R.id.et_A11_sample_name);
        etSampleNames[11] = findViewById(R.id.et_A12_sample_name);
        for (int i = 0; i < CHANNEL_COUNT; i++) {
            etSampleNames[i].setIconCallback(et -> showSampleDialog(et));
        }

        tvCurrentTemp = findViewById(R.id.tv_current_temp);
    }

    Random random = new Random();

    //region 定时刷新温度任务
    TimerTask tempTask = new TimerTask() {
        @Override
        public void run() {
            sendData(GET_TEMP);
        }
    };
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

    private int reactionTime = Global.cardReactionTime;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_test:
                if (Ac < COMPARE_MIN_VALUE) {
                    APPUtils.showToast(this, "请先对照");
                    return;
                }
                test();
                break;
//            case R.id.btn_open_close_lid:
//                if (!validateBusyOrNot()) return;
//                new Thread() {
//                    @Override
//                    public void run() {
//                        sendData(OPEN_CLOSE_LIB);
//                    }
//                }.start();
//                break;
//            case R.id.btn_out_in_card:
//                if (!validateBusyOrNot()) return;
//                new Thread() {
//                    @Override
//                    public void run() {
//                        sendData(ENTER_OUT_CARD);
//                    }
//                }.start();
//                break;
            case R.id.btn_return:
                isFinishing = true;
                countDownLatch();
                finish();
                break;
            case R.id.btn_print:
                print();
                break;
            case R.id.btn_compare:
                compare();
                break;
            case R.id.btn_upload:
                upload(false);
                break;
            default:

                break;
        }
    }

    private void upload(boolean autoUpload) {

//        List<CheckResult> uploadList = null;
//        if (autoUpload) {
//            uploadList = resultList;
//        }else{
//            uploadList = new ArrayList<>();
//            for (int i = 0; i < CHANNEL_COUNT; i++) {
//                if (cbs[i].isChecked() && etValues[i].getTag() != null) {
//                    uploadList.add((CheckResult) etValues[i].getTag());
//                }
//            }
//            if (uploadList.size() == 0) {
//                return;
//            }
//        }
//        UploadThread t = new UploadThread(this, uploadList, new UploadThread.onUploadListener() {
//            @Override
//            public void onSuccess(List<CheckResult> list, int returnId) {
//                if (!act.isFinishing()) {
//                    if (!autoUpload) {
//                        APPUtils.showToast(act, "上传成功");
//                    }
//                    updateUploadState2Db(list);
//                }
//            }
//
//            @Override
//            public void onFail(String failInfo) {
//                if (!act.isFinishing()) {
//                    runOnUiThread(()->APPUtils.showToast(act,failInfo));
//                }
//            }
//        });
//        t.start();
    }

    private void updateUploadState2Db(List<CheckResult> list) {

        new CheckResult().updateAll(list, new String[]{"uploadId"});
    }

    private int compareChannelIndex = 0;

    private void compare() {

        if (!validateBusyOrNot() || !validateCommonDataIsComplete()) {
            return;
        }
        int selectedCount = getSelectedChannelCount();
        if (selectedCount != 1) {
            APPUtils.showToast(this, "请选择一个通道进行对照");
            return;
        }
        isComparing = true;
        tvCompareValue.setText("");
        compareChannelIndex = getSelectedChannelNum() - 1;
        reactionTime = Global.cardReactionTime;
        startReaction();
    }

    private void startReaction() {

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
                        if (--reactionTime == 0) {
                            stateMenuItem.setTitle("");
                            cancel();
//                            showReactionFinishDialog();

                            startCountDown();  //开始倒计时
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
                        } else {
                            stateMenuItem.setTitle("反应剩余时间:" + reactionTime + "s");
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    private void showReactionFinishDialog() {

        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("反应结束")
                .create().show();
    }

    private void print() {

        if (resultList == null) {
            APPUtils.showToast(act, "请先检测");
            return;
        }
        List<CheckResult> printList = new ArrayList<>();
        for (int i = 0; i < CHANNEL_COUNT; i++) {
            if (cbs[i].isChecked() && etValues[i].getTag() != null) {
                printList.add((CheckResult) etValues[i].getTag());
            }
        }
        if (printList.size() == 0) {
            APPUtils.showToast(act, "请先选择数据");
        } else {
            byte[] data = ToolUtils.assemblePrintData(printList);
            if (SerialUtils.COM4_SendData(data)) {
                APPUtils.showToast(act, "打印数据发送成功");
            } else {
                APPUtils.showToast(act, "打印失败");
            }
        }

    }

    //region 测试专用
    private void t1() {

        int selectedCount = getSelectedChannelCount();
        if (selectedCount == 0) {
            APPUtils.showToast(this, "请勾选检测通道");
            return;
        }
        selectedChannels = new int[selectedCount];
        getChannelArray(selectedChannels);

        String result = "205,155,55|255,255,255|255,255,255|255,255,255|5,255,25|255,255,255|5,25,155|255,255,255|255,255,255|255,255,255|255,255,255|255,255,255";
        String[] st = result.split("\\|");
        if (st.length != Global.CHANNEL_COUNT) {
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

        if (!validateBusyOrNot() || !validateCommonDataIsComplete() || !validateDataIsComplete())
            return;

        int selectedCount = getSelectedChannelCount();
        if (selectedCount == 0) {
            APPUtils.showToast(this, "请勾选检测通道");
            return;
        }
        isTesting = true;
        selectedChannels = new int[selectedCount];
        getChannelArray(selectedChannels);
        reactionTime = Global.cardReactionTime;
        startReaction();
    }

    private boolean validateDataIsComplete() {

        for (int i = 0; i < cbs.length; i++) {
            if (cbs[i].isChecked() && TextUtils.isEmpty(etSampleNames[i].getText().toString())) {
                APPUtils.showToast(this, "请选择样品");
                return false;
            }
        }

        return true;
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ToolUtils.test_fail:
                    failHandle();
                    break;
                case ToolUtils.update_countdown:
                    int i = (int) msg.obj;
                    stateMenuItem.setTitle("显色剩余时间: " + i + "s");
                    break;
                case ToolUtils.test_success:
                    isTesting = false;
                    showTestResult();
                    showColors();
                    if (Global.uploadModel == 1) {
                        upload(true);
                    }
                    stateMenuItem.setTitle("检测成功");
                    break;
                case ToolUtils.testing:
                    stateMenuItem.setTitle("检测中...");
                    break;
                case ToolUtils.compare_fail:
                    failHandle();
                    break;
                case ToolUtils.compare_success:
                    isComparing = false;
                    if (Ac < COMPARE_MIN_VALUE) {
                        stateMenuItem.setTitle("对照失败");
                    } else {
                        stateMenuItem.setTitle("对照成功");
                        tvCompareValue.setText(df.format(Ac * COMPARE_FACTOR));
                        btnTest.setEnabled(true);
                    }
                    break;
            }
        }
    };

    List<CheckResult> resultList;

    private void showTestResult() {

        resultList = new ArrayList<>();
        int index = 0;
        double factor = 50.0 / Global.project.cardXlz;
        for (int i = 0; i < Global.CHANNEL_COUNT; i++) {

            if (index == selectedChannels.length) break;
            if (selectedChannels[index] == i + 1) {
                double value = 100 - AsList.get(index) * 100.0 * factor;
                value = value >= 100 ? 100 : value;
                value = value < 0 ? 0 : value;
                value = Double.parseDouble(df.format(value));
                CheckResult tempResult = new CheckResult(
                        etCheckedOrg.getText().toString(),
                        etCheckedOrg.getText().toString(),
                        Global.project.projectName,
                        etSampleNames[i].getText().toString(),
                        "", etSampleSource.getText().toString(), "A" + (i + 1), System.currentTimeMillis(),
                        df.format(value) + "%", value < 50 ? "合格" : "不合格",
                        Global.project.cardXlz + "",
                        Global.project.testStandard,
                        Global.project.checker,
                        "",new Random().nextInt(100000)+"",
                        Global.project.unit);

                etValues[i].setText(tempResult.testValue);
                etResults[i].setText(tempResult.resultJudge);
                etValues[i].setTag(tempResult);
                resultList.add(tempResult);
                index++;
            }
        }
        if (resultList.size() > 0) {
            new CheckResult().saveBindingIdAll(resultList);
        }
    }

    int compareColor = 0;

    private void showColors() {

        int index = 0;
        for (int i = 0; i < Global.CHANNEL_COUNT; i++) {

            if (index == selectedChannels.length) break;
            if (selectedChannels[index] == i + 1) {
                etValues[i].setBackgroundColor(colors.get(index));
                index++;
            }
        }
    }

    private void failHandle() {

        if (isTesting) {
            isTesting = false;
            cancelTimer();
            stateMenuItem.setTitle("检测失败");
        } else {
            isComparing = false;
            cancelTimer();
            stateMenuItem.setTitle("对照失败");
        }
    }

    private void getChannelArray(int[] channels) {

        int index = 0;
        for (int i = 0; i < cbs.length; i++) {
            if (cbs[i].isChecked()) {
                channels[index++] = i + 1;
            }
        }
    }

    private int getSelectedChannelCount() {

        int count = 0;
        for (int i = 0; i < cbs.length; i++) {
            if (cbs[i].isChecked()) {
                count++;
            }
        }
        return count;
    }

    private int getSelectedChannelNum() {

        int count = 0;
        for (int i = 0; i < cbs.length; i++) {
            if (cbs[i].isChecked()) {
                return i + 1;
            }
        }
        return count;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        CheckBox cb = (CheckBox) buttonView;
        switch (cb.getId()) {
            case R.id.cb_select_all:
                for (int i = 0; i < cbs.length; i++) {
                    cbs[i].setChecked(isChecked);
                }
                break;
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
        curDownTime = Global.cardWarmTime;
        mTimer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    boolean isFinishing = false;

    @Override
    public void onBackPressed() {

        isFinishing = true;
//        Log.i(TAG, "关闭中...");
        countDownLatch();
        finish();
    }

    @Override
    protected void onDestroy() {
//        Log.i(TAG, "onDestroy()");
        if (mTimer != null) mTimer.cancel();
        if (reactionTimer != null) reactionTimer.cancel();
        if (tempTimer != null) tempTimer.cancel();
        countDownLatch();
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void cancelTimer() {

        if (mTimer != null) {
            mTimer.cancel();
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

    class BSCountDownTask extends TimerTask {

        @Override
        public void run() {
            if (isFinishing()) return;
            mHandler.obtainMessage(ToolUtils.update_countdown, --curDownTime).sendToTarget();

            if (curDownTime == 0) {
                mTimer.cancel();
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                mHandler.sendEmptyMessage(ToolUtils.testing);
                if (isTesting) {
                    sendData(TEST_END);
                } else {
                    sendData(COMPARE_END);
                }
            }
        }
    }

    private void executeTest(final int flag) {

        if (Global.DEBUG) Log.i(TAG, "发送数据====" + new String(Global.TEST_INSTRUCTION));
        if (!SerialUtils.COM3_SendData(Global.TEST_INSTRUCTION)) {
            sendEmptyMessage(false);
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    byte[] response = SerialUtils.COM3_RevData();
                    if (response == null || response.length == 0 || response[response.length - 1] != '\n') {
                        sendEmptyMessage(false);
                    } else {
//                        Log.i(TAG, "接收到的检测数据======" + new String(response));
                        List<Integer> fList = dealTestData(response);
                        if (fList == null) {
                            sendEmptyMessage(false);
                        } else {
                            if (flag == TEST_START) {
                                as1List = fList;
                                openOrCloseLib();
                                showColors();
                                return;
                            } else if (flag == TEST_END) {
                                as2List = fList;
                                List<Integer> tempAcList = new ArrayList<>();
                                for (int i = 0; i < as2List.size(); i++) {
                                    tempAcList.add(Math.abs(as1List.get(i) - as2List.get(i)));
                                }
                                //(AC - Ac[i] < 0) ? 0.0f : (((AC - Ac[i]) * 100 / AC));
                                AsList = new ArrayList<>();
                                for (int i = 0; i < as2List.size(); i++) {
                                    double temp = tempAcList.get(i) * 1.0 / Ac;
                                    AsList.add(temp > 1 ? 1 : temp);
                                }
                                mHandler.sendEmptyMessage(ToolUtils.test_success);
                            }
                        }
                    }
                    countDownLatch();
                }
            }, 6500);
        }
    }

    List<Integer> colors = new ArrayList<>();

    private List<Integer> dealTestData(byte[] response) {

        String result = new String(response).replace("OK", "").replace("\n", "");
        String[] st = result.split("\\|");
        if (st.length != Global.CHANNEL_COUNT) {
            return null;
        }

        List<String[]> list = new ArrayList<>();
        for (int i = 0; i < st.length; i++) {
            String[] s = st[i].split(",");
            if (s.length != 3) {
                return null;
            }
            list.add(s);
        }

        List<Integer> floatList = new ArrayList<>();
        colors.clear();
        try {
            int index = 0;
            for (int j = 0; j < list.size(); j++) {
                if (index == selectedChannels.length) break;
                if (selectedChannels[index] == j + 1) {
                    index++;
                    Integer[] in = new Integer[4];
                    for (int i = 0; i < list.get(j).length; i++) {
                        in[i] = Integer.parseInt(list.get(j)[i]);
                    }
                    floatList.add((int) ToolUtils.computeGrayValue(in[0], in[1], in[2]));
                    colors.add(Color.rgb(in[0], in[1], in[2]));
                }
            }
            return floatList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private int[] computeAc(byte[] response) {

        int[] ds = null;
        String result = new String(response).replace("OK", "").replace("\n", "");
        String[] st = result.split("\\|");
        if (st.length != Global.CHANNEL_COUNT) {
            return null;
        }

        List<String[]> list = new ArrayList<>();
        for (int i = 0; i < st.length; i++) {
            String[] s = st[i].split(",");
            if (s.length != 3) {
                sendEmptyMessage(false);
                return null;
            }
            list.add(s);
        }

        try {
            Integer[] in = new Integer[4];
            for (int i = 0; i < list.get(compareChannelIndex).length; i++) {
                in[i] = Integer.parseInt(list.get(compareChannelIndex)[i]);
            }
            double ac = ToolUtils.computeGrayValue(in[0], in[1], in[2]);
            compareColor = Color.rgb(in[0], in[1], in[2]);
            ds = new int[]{(int) ac};
            return ds;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
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

        if (isFinishing) return;
        initLatch();
        SystemClock.sleep(200);
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
            case BS_START:
                if (DEBUG) Log.i(TAG, "===开始比色");
                executeBSData(Global.BS_INSTRUCTION);
                break;
            case TEST_END:
                if (DEBUG) Log.i(TAG, "===开始检测第2次取值");
                executeTest(flag);
                break;
            case OPEN_CLOSE_LIB:
                if (DEBUG) Log.i(TAG, "===开关盖。。。");
                if (!SerialUtils.COM3_SendData(Global.OPEN_CLOSE_LID_INSTRUCTION)) {
                    APPUtils.showToast(this, "发送失败");
                    mCountDownLatch.countDown();
                } else {
                    postForAction(4200);
                }
                break;
            case ENTER_OUT_CARD:
                if (DEBUG) Log.i(TAG, "===进出卡。。。");
                if (!SerialUtils.COM3_SendData(Global.OUT_IN_CARD_INSTRUCTION)) {
                    APPUtils.showToast(this, "发送失败");
                    mCountDownLatch.countDown();
                } else {
                    postForAction(3000);
                }
                break;
            case GET_TEMP:
                if (DEBUG) Log.i(TAG, "====获取温度中。。。");
                getTemperature();
                break;
        }
        waitCountDownLatch();
    }

    private void compareRecData(final int flag) {

        if (Global.DEBUG) Log.i(TAG, "发送数据====" + new String(Global.TEST_INSTRUCTION));
        if (!SerialUtils.COM3_SendData(Global.TEST_INSTRUCTION)) {
            sendEmptyMessage(false);
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    byte[] response = SerialUtils.COM3_RevData();
                    if (response == null || response.length == 0 || response[response.length - 1] != '\n') {
                        sendEmptyMessage(false);
                    } else {
                        if (Global.DEBUG) {
                            APPUtils.showToast(act, new String(response), true);
                            Log.i(TAG, "接收到的检测数据======" + new String(response));
                        }
                        int[] d = computeAc(response);
                        if (Global.DEBUG) Log.i(TAG, "Ac======" + d[0]);
                        if (d == null) {
                            sendEmptyMessage(false);
                        } else {
                            etValues[compareChannelIndex].setBackgroundColor(compareColor);
                            if (flag == COMPARE_START) {
                                ac1 = d[0];
                                openOrCloseLib();
                                return;
                            } else {
                                Ac = Math.abs(Math.abs(ac1 - d[0]));  //计算对照值
                                mHandler.sendEmptyMessage(ToolUtils.compare_success);
                                saveAc2Sp();
                                if (Global.DEBUG) Log.i(TAG, "AC======" + Ac);
                            }
                        }
                    }
                    countDownLatch();
                    if (Global.DEBUG) Log.i(TAG, "countDownLatch().......");
                }
            }, 6500);
        }
    }

    private void saveAc2Sp() {

        sp.edit().putInt(SPResource.KEY_COMPARE_VALUE, Ac).apply();
    }

    private void openOrCloseLib() {

        new Thread() {
            @Override
            public void run() {
                SerialUtils.COM3_SendData(Global.OPEN_CLOSE_LID_INSTRUCTION);
                SystemClock.sleep(500);
                countDownLatch();
            }
        }.start();
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

    private void getTemperature() {

        if (SerialUtils.COM3_SendData(Global.TEMP_INSTRUCTION)) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    countDownLatch();
                    byte[] rec = SerialUtils.COM3_RevData();
                    if (rec != null) {
                        final String temp = new String(rec).replace("\n", "").replace("OK", "");
                        if (!TextUtils.isEmpty(temp)) {
                            if (DEBUG) Log.i(TAG, "获取温度成功...===" + temp);
                            tvCurrentTemp.setText("实时温度：" + temp + "℃");
                        }
                    }
                }
            }, 1500);
        } else {
            countDownLatch();
        }

    }

    private void waitCountDownLatch() {
        try {
//            Log.i(TAG, "阻塞...");
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
