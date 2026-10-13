package com.tnd.multifuction1.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.table.TableUtils;
import com.lidroid.xutils.exception.DbException;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.adapter.CheckResultAdapter;
import com.tnd.multifuction1.db.DbHelper;
import com.tnd.multifuction1.model.BCheckOrg;
import com.tnd.multifuction1.model.CheckResult;
import com.tnd.multifuction1.model.Inspector;
import com.tnd.multifuction1.model.SampleSource;
import com.tnd.multifuction1.thread.UploadThread;
import com.tnd.multifuction1.util.APPUtils;
import com.tnd.multifuction1.util.Global;
import com.tnd.multifuction1.util.ToolUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * 数据管理
 */
public class ResultQueryActivity extends Activity implements View.OnClickListener, CheckResultAdapter.onSelectedItemChangedListener {


    private static final String TAG = "ResultQueryActivity";
    private Button btnReturn;
    private Button btnExportData;

    private final static String EXPORT_DIR = "/有机磷和氨基甲酸酯类农药检测/";
    private final static String[] EXCEL_HEADER = {"检测时间","样品编号", "商品名称", "检测项目", "检测值", "检测结果", "被检单位", "检测人员", "商品来源", "重量（kg）", "上传状态"};

    private static String[] HEADERS = new String[]{"检测编号", "检测项目", "检测人员", "通道",
            "临界值", "抑制率/检测值", "检测结果", "被检单位",
            "样品名称", "上传状态", "商品来源",
            "限量标准", "检测时间"};
    private List<CheckResult> resultList;
    private ListView lv;
    private CheckResultAdapter adapter;
    private CheckResult selectedResult;
    private Button btnClear;
    private Button btnDelete;
    private Button btnPrint;
    private Button btnUpload;
    private Activity act;
    private Button btnQuery;
    private CheckBox cbChecker;
    private CheckBox cbCheckedOrg;
    private CheckBox cbSampleName;
    //    private CheckBox cbSampleNum;
    private CheckBox cbUploadState;
    private CheckBox cbSource;
    private CheckBox cbTestTime;
    private CheckBox cbResultJudge;
    private Spinner spnChecker;
    private Spinner spnCheckedOrg;
    private Spinner spnSampleName;
    private Spinner spnResult;
    //    private Spinner spnSampleNum;
    private Spinner spnUploadState;
    private Spinner spnSampleSource;
    private Spinner spnResultJudge;
    private TextView tvStartTime;
    private TextView tvEndTime;
    DbUtils dbUtils = DbHelper.GetInstance();
    private TextView tvDataCount;
    private ProgressDialog waitDialog;
    private TextView tvStatistics;

    private TextView tvSampleNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_query);
        act = this;
        initView();
        resultList = new CheckResult().findAll();
        setResultAdapter();
        updateDataCountShow();

//        CheckResult checkResult = new CheckResult();
//        checkResult.testValue = 60 + "";
//        checkResult.xlz = 50 + "";
//        checkResult.bcheckedOrganization = "bcheckedOrganization";
//        checkResult.channel = "B1";
//        checkResult.id = 1;
//        checkResult.projectName = "农药残留";
//        checkResult.sampleName = new SampleName().findAll().get(0).getSampleName();
//        checkResult.sampleNum = new SampleName().findAll().get(0).getSampleNumber();
//        checkResult.resultJudge = "合格";
//        checkResult.testTime = new Date().getTime();
//        checkResult.twh = new Random().nextInt(100000) + "";
//        checkResult.save(checkResult);
        setQueryConditionAdapter();

        spnResult.setAdapter(new ArrayAdapter<>(this, R.layout.simple_list_item, new String[]{"全部", "合格", "不合格"}));
    }

    private void setQueryConditionAdapter() {

        String[] checkers = null, checkedOrgs = null, sampleNames = null, sampleNums = null, sampleSources = null, uploadStates = null, resultJudge = null;

        String tableName = TableUtils.getTableName(CheckResult.class);
        try {
            //region 获取数据库数据
            List<String> list = new ArrayList<>();
            Cursor cursor1 = dbUtils.execQuery("select distinct checker from " + tableName);
            while (cursor1.moveToNext()) {
                list.add(cursor1.getString(0));
            }
            cursor1.close();
            if (list.size() > 0) {
                checkers = list.toArray(new String[list.size()]);
            }

            list.clear();
            Cursor cursor2 = dbUtils.execQuery("select distinct checkedOrganization from " + tableName);
            while (cursor2.moveToNext()) {
                list.add(cursor2.getString(0));
            }
            cursor2.close();
            if (list.size() > 0) {
                checkedOrgs = list.toArray(new String[list.size()]);
            }

            list.clear();
            Cursor cursor3 = dbUtils.execQuery("select distinct sampleName from " + tableName);
            while (cursor3.moveToNext()) {
                list.add(cursor3.getString(0));
            }
            if (list.size() > 0) {
                sampleNames = list.toArray(new String[list.size()]);
            }


            list.clear();
            cursor3.moveToFirst();
//            Cursor cursor4 = dbUtils.execQuery("select distinct checker from " + tableName);
            while (cursor3.moveToNext()) {
                list.add(cursor3.getString(0));
            }
            cursor3.close();
            if (list.size() > 0) {
                sampleNums = list.toArray(new String[list.size()]);
            }

            list.clear();
            Cursor cursor5 = dbUtils.execQuery("select distinct sampleSource from " + tableName);
            while (cursor5.moveToNext()) {
                list.add(cursor5.getString(0));
            }
            cursor5.close();
            if (list.size() > 0) {
                sampleSources = list.toArray(new String[list.size()]);
            }

            //endregion

            int layoutId = R.layout.simple_list_item;
            if (checkers != null) {
                spnChecker.setAdapter(new ArrayAdapter<>(this, layoutId, checkers));
            }

            if (checkedOrgs != null) {
                spnCheckedOrg.setAdapter(new ArrayAdapter<>(this, layoutId, checkedOrgs));
            }

            if (sampleNames != null) {
                spnSampleName.setAdapter(new ArrayAdapter<>(this, layoutId, sampleNames));
            }

//            if (sampleNums != null) {
//                spnSampleNum.setAdapter(new ArrayAdapter<>(this, layoutId, sampleNums));
//            }

            if (sampleSources != null) {
                spnSampleSource.setAdapter(new ArrayAdapter<>(this, layoutId, sampleSources));
            }
            uploadStates = getResources().getStringArray(R.array.upload_state);
            resultJudge = getResources().getStringArray(R.array.result_judge);
            spnUploadState.setAdapter(new ArrayAdapter<>(this, layoutId, uploadStates));
            spnResultJudge.setAdapter(new ArrayAdapter<>(this, layoutId, resultJudge));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setResultAdapter() {

        adapter = new CheckResultAdapter(resultList, this, this);
        lv.setAdapter(adapter);
        selectedResult = null;
    }

    private void updateDataCountShow() {

        if (resultList == null) {
            tvDataCount.setText(String.format(getResources().getString(R.string.query_data_count), 0));
        } else {
            tvDataCount.setText(String.format(getResources().getString(R.string.query_data_count), resultList.size()));
        }
    }

    private void initView() {

        tvStatistics = findViewById(R.id.tv_statistics);
        tvDataCount = findViewById(R.id.tv_data_count);
        cbChecker = findViewById(R.id.cb_checker);
        cbCheckedOrg = findViewById(R.id.cb_checked_organization);
        cbSampleName = findViewById(R.id.cb_sample_name);
//        cbSampleNum = findViewById(R.id.cb_sample_num);
        cbUploadState = findViewById(R.id.cb_upload_state);
        cbSource = findViewById(R.id.cb_sample_source);
        cbTestTime = findViewById(R.id.cb_test_time);
        cbResultJudge = findViewById(R.id.cb_result_judge);

        spnChecker = findViewById(R.id.spn_checker);
        spnResult = findViewById(R.id.spn_result);
        spnCheckedOrg = findViewById(R.id.spn_checked_organization);
        spnSampleName = findViewById(R.id.spn_sample_name);
//        spnSampleNum = findViewById(R.id.spn_sample_num);
        spnUploadState = findViewById(R.id.spn_upload_state);
        spnSampleSource = findViewById(R.id.spn_sample_source);
        spnResultJudge = findViewById(R.id.spn_result_judge);

        tvStartTime = findViewById(R.id.tv_start_time);
        tvEndTime = findViewById(R.id.tv_end_time);

        lv = findViewById(R.id.lv);
        btnExportData = findViewById(R.id.btn_export_data);
        btnExportData.setOnClickListener(this);
        btnReturn = findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(this);
        btnClear = findViewById(R.id.btn_clear_data);
        btnDelete = findViewById(R.id.btn_delete_data);
        btnPrint = findViewById(R.id.btn_print);
        btnUpload = findViewById(R.id.btn_upload);
        btnQuery = findViewById(R.id.btn_query);
        tvSampleNumber = findViewById(R.id.tv_sample_number);

        tvStatistics.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnPrint.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
        tvSampleNumber.setOnClickListener(this);
    }

    private boolean isUploading = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear_data:
                showClearDialog();
                break;
            case R.id.btn_delete_data:
                showDeleteDialog();
                break;
            case R.id.btn_print:
                if (adapter.getSelectList().size() == 0) {
                    APPUtils.showToast(this, "请先选中数据");
                    return;
                }
//                byte[] data = ToolUtils.assemblePrintData(adapter.getSelectList()
//                        , this);
//                if (SerialUtils.COM4_SendData(data)) {
//                    APPUtils.showToast(this, "打印数据发送成功");
//                } else {
//                    APPUtils.showToast(this, "打印数据发送失败");
//                }
                ToolUtils.printData(adapter.getSelectList(), this);
                break;
            case R.id.btn_upload:
                upload();
                break;
            case R.id.btn_return:
                finish();
                break;
            case R.id.btn_export_data:
                btnExportData.setEnabled(false);
                showWaitDialog();
                new Thread() {
                    @Override
                    public void run() {
//                        exportData();
                        exportData2();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (waitDialog != null && waitDialog.isShowing()) {
                                    waitDialog.dismiss();
                                    waitDialog = null;
                                }
                                btnExportData.setEnabled(true);
                            }
                        }, 0);
                    }
                }.start();
                break;
            case R.id.btn_query:
                Selector selector = queryData();
                try {
                    resultList = dbUtils.findAll(selector);
                    setResultAdapter();
                    updateDataCountShow();
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_start_time:
                showSelectDateDialog(tvStartTime, false);
                break;
            case R.id.tv_end_time:
                showSelectDateDialog(tvEndTime, true);
                break;
            case R.id.tv_statistics:
                startActivity(new Intent(this, StatisticsActivity.class));
                break;
            case R.id.tv_sample_number:
                Log.d("","点击了我");
                tvSampleNumber.setSelected(!tvSampleNumber.isSelected());
                adapter.setAllSelect(tvSampleNumber.isSelected());
                lv.setAdapter(adapter);
                break;
        }
    }

    private void showWaitDialog() {

        waitDialog = new ProgressDialog(this);
        waitDialog.setMessage("正在导出,请稍后...");
        waitDialog.setCancelable(false);
        waitDialog.show();
    }

    private void showSelectDateDialog(final TextView tv, final boolean isEndTime) {

        final DatePicker datePicker = new DatePicker(this);
        if (tv.getTag() != null) {
            Calendar cal = (Calendar) tv.getTag();
            datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
        }
        new AlertDialog.Builder(this)
                .setView(datePicker)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
                        tv.setText(str);

                        Calendar cal = Calendar.getInstance();
                        if (isEndTime) {
                            cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 23, 59, 59);
                        } else {
                            cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);
                        }
                        tv.setTag(cal);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    private Selector queryData() {

        Selector selector = Selector.from(CheckResult.class).expr("1 = 1");
//        if (cbChecker.isChecked() && spnChecker.getSelectedItem() != null) {
//            selector.and("checker", "=", spnChecker.getSelectedItem().toString());
//        }
//        if (cbCheckedOrg.isChecked() && spnCheckedOrg.getSelectedItem() != null) {
//            selector.and("checkedOrganization", "=", spnCheckedOrg.getSelectedItem().toString());
//        }
//        if (cbSampleName.isChecked() && spnSampleName.getSelectedItem() != null) {
//            selector.and("sampleName", "like", spnSampleName.getSelectedItem().toString() + "%");
//        }
////        if (cbSampleNum.isChecked() && spnSampleNum.getSelectedItem() != null) {
////            selector.and("sample", "like", "%" + spnSampleNum.getSelectedItem().toString());
////        }
//        if (cbUploadState.isChecked() && spnUploadState.getSelectedItem() != null) {
//            if ("已上传".equals(spnUploadState.getSelectedItem().toString())) {
//                selector.and("uploadId", ">", 0);
//            } else {
//                selector.and("uploadId", "=", 0);
//            }
//
//        }
//        if (cbSource.isChecked() && spnSampleSource.getSelectedItem() != null) {
//            selector.and("sampleSource", "=", spnSampleSource.getSelectedItem().toString());
//        }
        if (cbTestTime.isChecked() && !TextUtils.isEmpty(tvStartTime.getText().toString()) && !TextUtils.isEmpty(tvEndTime.getText().toString())) {
            if (tvStartTime.getTag() != null && tvEndTime.getTag() != null) {
                Calendar startTimeCal = (Calendar) tvStartTime.getTag();
                Calendar endTimeCal = (Calendar) tvEndTime.getTag();
                selector.and("testTime", ">", startTimeCal.getTime().getTime());
                selector.and("testTime", "<", endTimeCal.getTime().getTime());
            }
        } else if (cbTestTime.isChecked()) {
            Toast.makeText(this, "请选择时间", Toast.LENGTH_SHORT).show();
        }
        if (spnResult.getSelectedItem() != null) {
            if (!spnResult.getSelectedItem().toString().equals("全部")) {
                selector.and("resultJudge", "=", spnResult.getSelectedItem().toString());
            }
        }
//        if (cbResultJudge.isChecked() && spnResultJudge.getSelectedItem() != null) {
//            selector.and("resultJudge", "=", spnResultJudge.getSelectedItem().toString());
//        }
        return selector.orderBy("id", true);
    }

    private void upload() {
//
        if (!ToolUtils.isNetworkConnected(this)) {
            APPUtils.showToast(this, "请先连接网络");
            return;
        }
        if (selectedResult == null) {
            APPUtils.showToast(this, "请先选中数据");
            return;
        }
        if (isUploading) {
            APPUtils.showToast(this, "正在上传数据，请稍后...");
            return;
        }
        isUploading = true;
        List<CheckResult> list = new ArrayList<>();
        List<CheckResult> list1 = new ArrayList<>();
        list1=adapter.getSelectList();
        for(int i=0;i<list1.size();i++){
            Collections.addAll(list, list1.get(i));
            Log.d("","选中的List:"+list1.get(i));
        }
        UploadThread t = new UploadThread(act, list, new UploadThread.onUploadListener() {
            @Override
            public void onSuccess(List<CheckResult> list, int returnId, int position, String result) {
                if (!act.isFinishing()) {
                    mHandler.obtainMessage(ToolUtils.upload_success, position, returnId, list).sendToTarget();
                }
            }

            @Override
            public void onFail(String failInfo) {
                if (!act.isFinishing()) {
                    mHandler.obtainMessage(ToolUtils.upload_fail, failInfo).sendToTarget();
                }
            }
        });
        t.start();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ToolUtils.upload_success:
                    Log.d(TAG, "arg1=" + msg.arg1 + "arg2=" + msg.arg2);
                    int returnId = msg.arg2;
                    List<CheckResult> list = (List<CheckResult>) msg.obj;
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).uploadId = returnId;
                        if (list.get(i).update(new String[]{"uploadId"})) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                    isUploading = false;
                    APPUtils.showToast(act, "上传成功");
                    break;
                case ToolUtils.upload_fail:
                    String result = (String) msg.obj;
                    isUploading = false;
                    APPUtils.showToast(act, result);

//                    tvDataCount.setText("" + result);
                    break;
                default:

                    break;
            }
        }
    };

    private void showDeleteDialog() {

        if (adapter.getSelectList().size() == 0) {
            APPUtils.showToast(this, "请先选中数据");
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("确定删除该数据?")
                .setTitle("提示")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        new CheckResult().delete(selectedResult);
//                        resultList.remove(selectedResult);
//                        adapter.notifyDataSetChanged();
//                        selectedResult = null;
//                        updateDataCountShow();
                        new CheckResult().deleteAll(adapter.getSelectList());
                        resultList.removeAll(adapter.getSelectList());
                        adapter.notifyDataSetChanged();
                        updateDataCountShow();
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

        if (resultList == null || resultList.size() == 0) {
            APPUtils.showToast(this, "暂无数据");
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("确定清空所有数据?")
                .setTitle("提示")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new CheckResult().deleteAll(resultList);
                        resultList.clear();
                        adapter.notifyDataSetChanged();
                        selectedResult = null;
                        updateDataCountShow();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }


    private void exportData2() {
        String rootPath = Environment.getExternalStorageDirectory() + EXPORT_DIR;
        // for debug
//        rootPath = Environment.getExternalStorageDirectory() + "/UserData/";

        File file = new File(rootPath);
        if (!file.exists()) {
            file.mkdirs();
            Uri uri = Uri.parse("file://" + file.getAbsolutePath() + "/");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_STARTED, uri));
        }

        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        // 输出的excel的路径
        String filePath = rootPath + df.format(new Date()) + ".xls";
        // 创建Excel工作薄
        WritableWorkbook wwb;
        // 新建立一个jxl文件,即在SDcard盘下生成test.xls
        OutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
            wwb = Workbook.createWorkbook(os);
            WritableSheet sheet = wwb.createSheet("数据", 0);
            /*for (int i = 0; i < HEADERS.length; i++) {
                sheet.addCell(new Label(i, 0, HEADERS[i]));
            }*/

            //set column width
            for (int i = 0; i < EXCEL_HEADER.length; i++) {
                String header = EXCEL_HEADER[i];
                if (i == 0) {
                    sheet.setColumnView(i, header.getBytes().length + 9);
                } else {
                    sheet.setColumnView(i, header.getBytes().length + 2);

                }
            }
            //set row height
            sheet.setRowView(0, 600);

            //write header
            WritableFont defaultFont = new WritableFont(WritableFont.ARIAL, 11);
            WritableCellFormat defaultCellFormat = new WritableCellFormat(defaultFont);
            defaultCellFormat.setAlignment(Alignment.CENTRE);
            defaultCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

            WritableFont defaultHeaderFont = new WritableFont(WritableFont.ARIAL, 11, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);
            WritableFont mainHeaderFont = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            WritableCellFormat mainHeaderCellFormat = new WritableCellFormat(mainHeaderFont);
            mainHeaderCellFormat.setAlignment(Alignment.CENTRE);
            mainHeaderCellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            mainHeaderCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            sheet.addCell(new Label(0, 0, "检测信息导出", mainHeaderCellFormat));
            sheet.mergeCells(0, 0, 9, 0);


            WritableCellFormat headerCellFormat = new WritableCellFormat(defaultHeaderFont);
            headerCellFormat.setAlignment(Alignment.CENTRE);
            headerCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            sheet.addCell(new Label(0, 1, "导出时间", headerCellFormat));
            sheet.addCell(new Label(1, 1, Global.getCurrentDate(), defaultCellFormat));
            sheet.addCell(new Label(2, 1, "", defaultCellFormat));
            sheet.mergeCells(2, 1, 5, 1);

            WritableFont subMainHeaderFont = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD);
            WritableCellFormat subMainHeaderCellFormat = new WritableCellFormat(subMainHeaderFont);
            subMainHeaderCellFormat.setAlignment(Alignment.CENTRE);
            subMainHeaderCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
//            sheet.addCell(new Label(0, 2, "经营户信息详情", subMainHeaderCellFormat));
            sheet.mergeCells(0, 2, 9, 2);

            for (int i = 0; i < EXCEL_HEADER.length; i++) {
                sheet.addCell(new Label(i, 3, EXCEL_HEADER[i], headerCellFormat));
            }

            if (resultList != null) {
                for (int i = 0; i < resultList.size(); i++) {
                    CheckResult result = resultList.get(i);
                    int lineIdx = 4 + i;
                    sheet.addCell(new Label(0, lineIdx, ToolUtils.long2String(result.testTime, "yyyy-MM-dd HH:mm:ss"), defaultCellFormat)); //检测时间
                    sheet.addCell(new Label(1, lineIdx, result.sampleNum, defaultCellFormat)); //编号
                    sheet.addCell(new Label(2, lineIdx, result.sampleName, defaultCellFormat)); //商品名
                    sheet.addCell(new Label(3, lineIdx, result.projectName, defaultCellFormat)); //检测项目
                    sheet.addCell(new Label(4, lineIdx, result.testValue, defaultCellFormat)); //检测值
                    sheet.addCell(new Label(5, lineIdx, result.resultJudge, defaultCellFormat)); //检测结果
                    sheet.addCell(new Label(6, lineIdx, result.bcheckedOrganization, defaultCellFormat)); //被检单位

                    sheet.addCell(new Label(7, lineIdx, result.checker, defaultCellFormat)); //检测人员
                    sheet.addCell(new Label(8, lineIdx, result.sampleSource, defaultCellFormat)); //商品来源
                    sheet.addCell(new Label(9, lineIdx, result.weight, defaultCellFormat)); //重量
                    Log.d("uploadId", result.uploadId + "");
                    sheet.addCell(new Label(10, lineIdx, result.uploadId == 0 ? "未上传" : "已上传", defaultCellFormat)); //上传状态

                    //sheet.addCell(new Label(11, i + 1, ToolUtils.long2String(result.testTime, "yyyy-MM-dd HH:mm:ss")));
                }
            }
            ((FileOutputStream) os).getFD().sync();
            wwb.write();
            wwb.close();

            File saveFile = new File(filePath);

            APPUtils.showToast(this, "数据导出成功,存储位置：" + saveFile.getAbsolutePath(), true);
            sendMediaScanIntent(filePath);
            //APPUtils.showToast(this, "数据导出成功，导出路径为：" + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            APPUtils.showToast(this, "数据导出失败");
        }

    }

    private void exportData() {

        String rootPath = Environment.getExternalStorageDirectory() + EXPORT_DIR;
        // for debug
        rootPath = Environment.getExternalStorageDirectory() + "/UserData/";

        File file = new File(rootPath);
        if (!file.exists()) {
            file.mkdirs();
            Uri uri = Uri.parse("file://" + file.getAbsolutePath() + "/");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_STARTED, uri));
        }

        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        // 输出的excel的路径
        String filePath = rootPath + df.format(new Date()) + ".xls";
        // 创建Excel工作薄
        WritableWorkbook wwb;
        // 新建立一个jxl文件,即在SDcard盘下生成test.xls
        OutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
            wwb = Workbook.createWorkbook(os);
            WritableSheet sheet = wwb.createSheet("数据", 0);
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.addCell(new Label(i, 0, HEADERS[i]));
            }
            if (resultList != null) {
                for (int i = 0; i < resultList.size(); i++) {
                    CheckResult result = resultList.get(i);
                    sheet.addCell(new Label(0, i + 1, result.id + ""));
                    sheet.addCell(new Label(1, i + 1, result.projectName));
                    sheet.addCell(new Label(2, i + 1, result.checker));
                    sheet.addCell(new Label(3, i + 1, result.channel));
                    sheet.addCell(new Label(4, i + 1, result.xlz));
                    sheet.addCell(new Label(5, i + 1, result.testValue));
                    sheet.addCell(new Label(6, i + 1, result.resultJudge));
                    sheet.addCell(new Label(7, i + 1, result.checkedOrganization));
                    sheet.addCell(new Label(8, i + 1, result.sampleName));
//                    sheet.addCell(new Label(9, i + 1, result.sampleNum == null ? "" : result.sampleNum));
//                    sheet.addCell(new Label(10, i + 1, result.hasUpload ? "已上传" : "未上传"));
                    sheet.addCell(new Label(9, i + 1, result.sampleSource));
                    sheet.addCell(new Label(10, i + 1, result.testStandard));
                    sheet.addCell(new Label(11, i + 1, ToolUtils.long2String(result.testTime, "yyyy-MM-dd HH:mm:ss")));
                }
            }
            ((FileOutputStream) os).getFD().sync();
            wwb.write();
            wwb.close();

            File saveFile = new File(filePath);

            APPUtils.showToast(this, "数据导出成功,存储位置：" + saveFile.getAbsolutePath(), true);
            sendMediaScanIntent(filePath);
            //APPUtils.showToast(this, "数据导出成功，导出路径为：" + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            APPUtils.showToast(this, "数据导出失败");
        }

    }

    private void sendMediaScanIntent(String fileName) {

        File file = new File(fileName);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        intent.setData(contentUri);
        sendBroadcast(intent);
    }

    @Override
    public void onSelectedItemChanged(int position) {

        selectedResult = resultList.get(position);
    }

    @Override
    public void onSelectedItemEdit(CheckResult result, int position) {
        showEditDialog(result, position);
    }

    Dialog editDialog;
    EditText dialog_et_weight;
    EditText dialog_et_sample_number;
    Spinner dialog_spn_checker;
    Spinner dialog_spn_sample_unit;
    Spinner dialog_sample_source;
    TextView dialog_tv_confirm;
    TextView dialog_tv_cancel;

    private void showEditDialog(CheckResult result, int position) {
        if (editDialog == null) {
            initDialog();
        }
        editDialog.show();

        if (result != null) {
            dialog_et_weight.setText(result.weight + "");
            dialog_et_sample_number.setText(result.sampleNum + "");

            List<Inspector> inspectors = new Inspector().findAll();
            if (inspectors != null && inspectors.size() > 0) {
                dialog_spn_checker.setAdapter(new ArrayAdapter<>(this, R.layout.simple_list_item, inspectors));
                if (result.checker != null) {
                    for (int i = 0; i < inspectors.size(); i++) {
                        if (inspectors.get(i).equals(result.checker)) {
                            dialog_spn_checker.setSelection(i);
                        }
                    }
                }
            }

            List<SampleSource> sampleSources = new SampleSource().findAll();
            if (sampleSources != null && sampleSources.size() > 0) {
                dialog_sample_source.setAdapter(new ArrayAdapter<>(this, R.layout.simple_list_item, sampleSources));
                if (result.checker != null) {
                    for (int i = 0; i < sampleSources.size(); i++) {
                        if (sampleSources.get(i).equals(result.sampleSource)) {
                            dialog_sample_source.setSelection(i);
                        }
                    }
                }
            }

            List<BCheckOrg> checkOrgs = new BCheckOrg().findAll();
            if (checkOrgs != null && checkOrgs.size() > 0) {
                dialog_spn_sample_unit.setAdapter(new ArrayAdapter<>(this, R.layout.simple_list_item, checkOrgs));
                if (result.checker != null) {
                    for (int i = 0; i < checkOrgs.size(); i++) {
                        if (checkOrgs.get(i).equals(result.checkedOrganization)) {
                            dialog_spn_sample_unit.setSelection(i);
                        }
                    }
                }
            }
        }
        dialog_tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.weight = dialog_et_weight.getText().toString();
                result.bcheckedOrganization = dialog_spn_sample_unit.getSelectedItem() == null ? "" : dialog_spn_sample_unit.getSelectedItem().toString();
                result.sampleSource = dialog_sample_source.getSelectedItem() == null ? "" : dialog_sample_source.getSelectedItem().toString();
                result.checker = dialog_spn_checker.getSelectedItem() == null ? "" : dialog_spn_checker.getSelectedItem().toString();
                result.sampleNum = dialog_et_sample_number.getText() == null ? "" : dialog_et_sample_number.getText().toString();

                result.update(new String[]{"weight", "bcheckedOrganization", "sampleSource", "checker", "sampleNum"});
                resultList.set(position, result);
                adapter.notifyDataSetChanged();
                editDialog.dismiss();
            }
        });
        dialog_tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog.dismiss();
            }
        });
    }

    private void initDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_result, null);
        editDialog = new Dialog(this);
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.show();
        editDialog.setContentView(view);
        dialog_et_weight = view.findViewById(R.id.et_weight);
        dialog_spn_checker = view.findViewById(R.id.spn_checker);
        dialog_spn_sample_unit = view.findViewById(R.id.spn_sample_unit);
        dialog_et_sample_number = view.findViewById(R.id.et_sample_number);
        dialog_sample_source = view.findViewById(R.id.spn_sample_source);
        dialog_tv_confirm = view.findViewById(R.id.tv_confirm);
        dialog_tv_cancel = view.findViewById(R.id.tv_cancel);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
