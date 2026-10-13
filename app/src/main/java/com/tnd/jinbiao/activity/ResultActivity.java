package com.tnd.jinbiao.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.tnd.jinbiao.ToolUtils;
import com.tnd.jinbiao.adapter.ResultAdapterT;
import com.tnd.jinbiao.base.BaseActivity;
import com.tnd.jinbiao.model.LineModel;
import com.tnd.jinbiao.model.PeopleModel;
import com.tnd.jinbiao.model.ResultModel;
import com.tnd.jinbiao.model.SampleModel;
import com.tnd.jinbiao.model.SampleTypeModel;
import com.tnd.jinbiao.model.ShiJiModel;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.adapter.FiltrateAdapter;
import com.tnd.multifuction1.db.DbHelper;
import com.tnd.multifuction1.model.CheckResult;
import com.tnd.multifuction1.model.FiltrateModel;
import com.tnd.multifuction1.model.SampleName;
import com.tnd.multifuction1.thread.UploadThread;
import com.tnd.multifuction1.util.BrightCommandM;
import com.tnd.multifuction1.util.Global;
import com.tnd.multifuction1.util.SerialUtils;
import com.tnd.util.APPUtils;

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

public class ResultActivity extends BaseActivity implements ResultAdapterT.OnSelectedItemListener {

    private ListView listview = null;
    private ResultAdapterT adapter = null;
    private ResultModel selectResultMode;
    private TextView startTime = null;
    private TextView endTime = null;
    private Spinner company_spinner = null;
    private Spinner project_spinner = null;
    private Spinner result_spinner = null;
    private Spinner shijiSpinner = null;
    private Spinner persionSpinner = null;
    private Spinner sampleSpinner = null;
    private Spinner typeSpinner = null;
    private Spinner result_spinner_result = null;
    private List<LineModel> project_list = null;
    private List<PeopleModel> companylist = null;
    private List<PeopleModel> persionlist = null;
    private List<SampleModel> samplelist = null;
    private List<SampleTypeModel> typelist = null;
    private List<ShiJiModel> shijilist = null;
    private ArrayAdapter<String> company_adapter = null;
    private ArrayAdapter<String> project_adapter = null;
    private ArrayAdapter<String> shiji_adapter = null;
    private ArrayAdapter<String> persion_adapter = null;
    private ArrayAdapter<String> sample_adapter = null;
    private ArrayAdapter<String> type_adapter = null;
    private String[] company_list = null;
    private String[] project_name = null;
    private String[] shiji_list = null;
    private String[] persion_list = null;
    private String[] sample_list = null;
    private String[] type_list = null;
    private long starttime_long = 0;
    private long endtime_long = 0;
    private DbUtils db;
    private Activity act;
    private LineModel linemodel = null;
    private PeopleModel company_model = null;
    public PeopleModel persion_model = null;
    public SampleModel sample_model = null;
    public SampleTypeModel type_model = null;
    public ShiJiModel shiji_model = null;
    private List<ResultModel> result_list = null;
    private CheckBox activity_result_checkbox_name;
    private CheckBox activity_result_checkbox_shiji;
    private CheckBox activity_result_checkbox_company;
    private CheckBox activity_result_checkbox_persion;
    private CheckBox activity_result_checkbox_result;
    private CheckBox activity_result_checkbox_sample;
    private CheckBox activity_result_checkbox_type;
    private ArrayAdapter<String> resultAdapter;
    private boolean isUploading = false;
    private String TAG = "ResultActivity";
    private Button btnExportData;

    private final static String EXPORT_DIR = "/胶体金检测/";
    private final static String[] EXCEL_HEADER = {"检测时间","样品编号", "商品名称", "检测项目", "检测值", "检测结果", "被检单位", "检测人员", "商品来源", "重量（kg）", "上传状态"};
    private ProgressDialog waitDialog;


    private TextView tv_check_sample;
    private String path;
    private Dialog dialog;
    private View inflate;
    private ListView lv;
    private EditText et_weight;
    private EditText et_content;
    private FiltrateAdapter filtrateAdapter;
    private SimpleDateFormat formatterData;;
    private Button btn_back;
    static List<SampleName> sampleNames_s = new ArrayList<>();//样品名称
    static List<SampleName> sampleNames;
    private String[] sampleName_s = null;
    private EditText et_Sample_Num = null;
    private EditText et_SampleTime= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_main);
        act = this;
        db = DbHelper.GetInstance();
        initView();
        getData();

        result_list = new ArrayList<ResultModel>();
        adapter = new ResultAdapterT(this, result_list);
        adapter.OnSelectedItem(this);
        listview.setAdapter(adapter);
        query();
    }
    private void showDeleteDialog(final int position) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定删除此记录？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ResultModel model = result_list.get(position);
                try {
                    db.delete(model);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                result_list.remove(position);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void getData() {
        try {

            companylist = db.findAll(Selector.from(PeopleModel.class).where("source", "=", 1));
            persionlist = db.findAll(Selector.from(PeopleModel.class).where("source", "=", 2));
//            shijilist = db.findAll(Selector.from(ShiJiModel.class));
            samplelist = db.findAll(Selector.from(SampleModel.class));
            typelist = db.findAll(Selector.from(SampleTypeModel.class));
            project_list = db.findAll(Selector.from(LineModel.class));
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (companylist == null) {
            companylist = new ArrayList<PeopleModel>();
        }

        if (project_list == null) {
            project_list = new ArrayList<LineModel>();
        }
        if (shijilist == null) {
            shijilist = new ArrayList<ShiJiModel>();
        }
        if (persionlist == null) {
            persionlist = new ArrayList<PeopleModel>();
        }
        if (samplelist == null) {
            samplelist = new ArrayList<SampleModel>();
        }
        if (typelist == null) {
            typelist = new ArrayList<SampleTypeModel>();
        }

        company_list = new String[companylist.size()];
        shiji_list = new String[shijilist.size()];
        project_name = new String[project_list.size()];
        persion_list = new String[persionlist.size()];
        sample_list = new String[samplelist.size()];
        type_list = new String[typelist.size()];
        for (int i = 0; i < persionlist.size(); i++) {
            PeopleModel model = persionlist.get(i);
            persion_list[i] = model.getName();
        }
        for (int i = 0; i < samplelist.size(); i++) {
            SampleModel model = samplelist.get(i);
            sample_list[i] = model.getName();
        }
        for (int i = 0; i < typelist.size(); i++) {
            SampleTypeModel model = typelist.get(i);
            type_list[i] = model.getName();
        }

        for (int i = 0; i < companylist.size(); i++) {
            PeopleModel model = companylist.get(i);
            company_list[i] = model.getName();
        }

        for (int i = 0; i < project_list.size(); i++) {
            LineModel model = project_list.get(i);
            project_name[i] = model.getName();
        }
        for (int i = 0; i < shijilist.size(); i++) {
            ShiJiModel model = shijilist.get(i);
            shiji_list[i] = model.getName();
        }

        if (company_list == null || company_list.length <= 0) {
            company_list = new String[1];
            company_list[0] = "请先添加检测单位";
        }
        if (shiji_list == null || shiji_list.length <= 0) {
            shiji_list = new String[1];
            shiji_list[0] = "请先添加试剂厂商";
        }
        if (persion_list == null || persion_list.length <= 0) {
            persion_list = new String[1];
            persion_list[0] = "请先添加检验员";
        }
        if (sample_list == null || sample_list.length <= 0) {
            sample_list = new String[1];
            sample_list[0] = "请先添加样品名称";
        }
        if (type_list == null || type_list.length <= 0) {
            type_list = new String[1];
            type_list[0] = "请先添加样品类型";
        }

        if (project_name == null || project_name.length <= 0) {
            project_name = new String[1];
            project_name[0] = "请先添加检测项目";
        }

        company_adapter = new ArrayAdapter<String>(ResultActivity.this,	R.layout.item_simple_spiner, company_list);

        project_adapter = new ArrayAdapter<String>(ResultActivity.this,	R.layout.item_simple_spiner, project_name);
        shiji_adapter = new ArrayAdapter<String>(ResultActivity.this,R.layout.item_simple_spiner, shiji_list);
        persion_adapter = new ArrayAdapter<String>(ResultActivity.this,	R.layout.item_simple_spiner, persion_list);
        sample_adapter = new ArrayAdapter<String>(ResultActivity.this, R.layout.item_simple_spiner, sample_list);
        type_adapter = new ArrayAdapter<String>(ResultActivity.this, R.layout.item_simple_spiner, type_list);
        resultAdapter = new ArrayAdapter<String>(ResultActivity.this, R.layout.item_simple_spiner, getResources().getStringArray(R.array.check_company_name));
        company_spinner.setAdapter(company_adapter);
        project_spinner.setAdapter(project_adapter);
        shijiSpinner.setAdapter(shiji_adapter);
        persionSpinner.setAdapter(persion_adapter);
        sampleSpinner.setAdapter(sample_adapter);
        typeSpinner.setAdapter(type_adapter);
        result_spinner.setAdapter(resultAdapter);
        project_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (project_list.size() > 0) {
                    LineModel model = project_list.get(arg2);
                    linemodel = model;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        company_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (companylist.size() > 0) {
                    PeopleModel model = companylist.get(arg2);
                    company_model = model;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        persionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (persionlist.size() > 0) {
                    PeopleModel model = persionlist.get(arg2);
                    persion_model = model;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        sampleSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int arg2, long id) {
                // TODO Auto-generated method stub
                if (samplelist.size() > 0) {
                    SampleModel model = samplelist.get(arg2);
                    sample_model = model;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int arg2, long id) {
                // TODO Auto-generated method stub
                if (typelist.size() > 0) {
                    SampleTypeModel model = typelist.get(arg2);
                    type_model = model;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        shijiSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (shijilist.size() > 0) {
                    ShiJiModel model = shijilist.get(arg2);
                    shiji_model = model;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    public void query() {

        WhereBuilder whereBuilder = WhereBuilder.b();
        if (!GetQueryCondition(whereBuilder)) return;
        try {
            result_list.clear();
            List<ResultModel> list = db.findAll(Selector.from(ResultModel.class).where(whereBuilder));
            result_list.addAll(list);
        } catch (DbException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * 获取查询条件
     * @param whereBuilder
     * @return
     */
    private boolean GetQueryCondition(WhereBuilder whereBuilder) {

        Boolean name_check = activity_result_checkbox_name.isChecked();
        Boolean shiji_check = activity_result_checkbox_shiji.isChecked();
        Boolean company_check = activity_result_checkbox_company.isChecked();
        Boolean persion_check = activity_result_checkbox_persion.isChecked();
        Boolean result_check = activity_result_checkbox_result.isChecked();
        Boolean sample_check = activity_result_checkbox_sample.isChecked();
        Boolean type_check = activity_result_checkbox_type.isChecked();

        if (starttime_long > 0 && endtime_long <= 0) {
            Toast.makeText(ResultActivity.this, "请输入结束日期", Toast.LENGTH_LONG).show();
            return false;
        }

        if (endtime_long > 0 && starttime_long <= 0) {
            Toast.makeText(ResultActivity.this, "请输入起始日期", Toast.LENGTH_LONG).show();
            return false;
        }
        if (starttime_long > 0 && endtime_long > 0) {
            whereBuilder.and("time", ">=", starttime_long);
            whereBuilder.and("time", "<=", endtime_long);
        }

        if (name_check) {
            whereBuilder.and("project_name", "=", project_spinner.getSelectedItem().toString());
        }
        if (shiji_check) {
            whereBuilder.and("shiji", "=", shijiSpinner.getSelectedItem().toString());
        }
        if (company_check) {
            whereBuilder.and("company_name", "=", company_spinner.getSelectedItem().toString());
        }
        if (persion_check) {
            whereBuilder.and("persion", "=", persionSpinner.getSelectedItem().toString());
        }
        if (result_check) {
            whereBuilder.and("check_result", "=", result_spinner_result.getSelectedItem().toString());
        }
        if (sample_check) {
            whereBuilder.and("sample_name", "=", tv_check_sample.getText().toString());
        }
        if (type_check) {
            whereBuilder.and("sample_type", "=", typeSpinner.getSelectedItem().toString());

        }
        return true;
    }

    public void PrintInfo(View v) {

        if (selectResultMode == null) {
            Toast.makeText(ResultActivity.this, "请先选择要打印的数据!", Toast.LENGTH_LONG).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setMessage("是否打印二维码?")
                .setTitle("提示")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id = selectResultMode.id + "";
                        String printData = ToolUtils.GetPrintInfo(selectResultMode, ResultActivity.this);
                        APPUtils.showToast(ResultActivity.this, printData);
                        SerialUtils.COM4_SendData(BrightCommandM.t1b63(0));
                        SerialUtils.COM4_SendData(BrightCommandM.t1b61(0));
                        byte[] data = printData.getBytes(Charset.forName("gb2312"));
                        if(!SerialUtils.COM4_SendData(data)){
                            APPUtils.showToast(ResultActivity.this, "打印数据发送失败");
                        }
                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                        // 增加打印二维码
                        SerialUtils.COM4_SendData(BrightCommandM.t1b61(1));
                        SerialUtils.COM4_SendData(BrightCommandM.t1d77(2));
                        SerialUtils.COM4_SendData(BrightCommandM.t1d28Qr(getResultData(selectResultMode)));
                        SerialUtils.COM4_SendData(BrightCommandM.t1d77(2));
                        SerialUtils.COM4_SendData(BrightCommandM.t1b61(1));

                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                        SerialUtils.COM4_SendData(BrightCommandM.t1b61(0));
                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                        SerialUtils.COM4_SendData(BrightCommandM.t0A());

                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id = selectResultMode.id + "";
                        String printData = ToolUtils.GetPrintInfo(selectResultMode, ResultActivity.this);
                        APPUtils.showToast(ResultActivity.this, printData);
                        SerialUtils.COM4_SendData(BrightCommandM.t1b63(0));
                        SerialUtils.COM4_SendData(BrightCommandM.t1b61(0));
                        byte[] data = printData.getBytes(Charset.forName("gb2312"));
                        if(!SerialUtils.COM4_SendData(data)){
                            APPUtils.showToast(ResultActivity.this, "打印数据发送失败");
                        }
                        /*SerialUtils.COM4_SendData(BrightCommandM.t0A());
                        // 增加打印二维码
                        SerialUtils.COM4_SendData(BrightCommandM.t1b61(1));
                        SerialUtils.COM4_SendData(BrightCommandM.t1d77(2));
                        SerialUtils.COM4_SendData(BrightCommandM.t1d28Qr(getResultData(selectResultMode)));
                        SerialUtils.COM4_SendData(BrightCommandM.t1d77(2));
                        SerialUtils.COM4_SendData(BrightCommandM.t1b61(1));
                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                        SerialUtils.COM4_SendData(BrightCommandM.t1b61(0));*/
                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                    }
                })
                .create().show();

       /* String id = selectResultMode.id + "";
        String printData = ToolUtils.GetPrintInfo(selectResultMode, this);
        APPUtils.showToast(this, printData);
        SerialUtils.COM4_SendData(BrightCommandM.t1b63(0));
        SerialUtils.COM4_SendData(BrightCommandM.t1b61(0));
        byte[] data = printData.getBytes(Charset.forName("gb2312"));
        if(!SerialUtils.COM4_SendData(data)){
            APPUtils.showToast(this, "打印数据发送失败");
        }
        SerialUtils.COM4_SendData(BrightCommandM.t0A());
        // 增加打印二维码
        SerialUtils.COM4_SendData(BrightCommandM.t1b61(1));
        SerialUtils.COM4_SendData(BrightCommandM.t1d77(2));
        SerialUtils.COM4_SendData(BrightCommandM.t1d28Qr(getResultData(selectResultMode)));
        SerialUtils.COM4_SendData(BrightCommandM.t1d77(2));
        SerialUtils.COM4_SendData(BrightCommandM.t1b61(1));

        SerialUtils.COM4_SendData(BrightCommandM.t0A());
        SerialUtils.COM4_SendData(BrightCommandM.t1b61(0));
        SerialUtils.COM4_SendData(BrightCommandM.t0A());
        SerialUtils.COM4_SendData(BrightCommandM.t0A());

        SerialUtils.COM4_SendData(BrightCommandM.t0A());*/
    }

    private String getResultData(ResultModel result) {
        StringBuilder sb = new StringBuilder();
        sb.append("检测项目：" + result.project_name);
        sb.append("\n检测时间：" + ToolUtils.long2String(result.time,
                "yyyy-MM-dd HH:mm:ss") );
        sb.append("\n样品名称：" + result.sample_name);

        sb.append("\n重量：" + result.weight + " kg");
        sb.append("\n样品编码：" + result.sample_number);
        sb.append("\n检测单位：" + result.company_name);
        sb.append("\n商品来源:" + result.sample_unit);
        sb.append("\n检测人员：" + result.persion);
        sb.append("\n临界值：" + result.lin);
        sb.append("\n限量标准：" + result.xian);

        sb.append("\n检测值：" + result.check_value);
        sb.append("\n结果判定：" + result.check_result);
        Log.d("resultPrint", sb.toString());
        return sb.toString();
    }

    public void initView() {
        listview = (ListView) findViewById(R.id.result_listview);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position);
                return true;
            }
        });
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                adapter.setSeclection(arg2);
                adapter.notifyDataSetChanged();
                selectResultMode = result_list.get(arg2);
            }
        });
        startTime = (TextView) findViewById(R.id.result_start_time);
        activity_result_checkbox_name = (CheckBox) findViewById(R.id.activity_result_checkbox_name);
        activity_result_checkbox_shiji = (CheckBox) findViewById(R.id.activity_result_checkbox_shiji);
        activity_result_checkbox_company = (CheckBox) findViewById(R.id.activity_result_checkbox_company);
        activity_result_checkbox_persion = (CheckBox) findViewById(R.id.activity_result_checkbox_persion);
        activity_result_checkbox_result = (CheckBox) findViewById(R.id.activity_result_checkbox_result);
        activity_result_checkbox_sample = (CheckBox) findViewById(R.id.activity_result_checkbox_sample);
        activity_result_checkbox_type = (CheckBox) findViewById(R.id.activity_result_checkbox_type);
        btnExportData = findViewById(R.id.btn_export_data);
        startTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showCalendarDialog(0);
            }
        });

        endTime = (TextView) findViewById(R.id.result_end_time);
        endTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showCalendarDialog(1);
            }
        });
        company_spinner = (Spinner) findViewById(R.id.result_spinner_company);
        project_spinner = (Spinner) findViewById(R.id.result_spinner_name);
        result_spinner = (Spinner) findViewById(R.id.result_spinner_result);
        persionSpinner = (Spinner) findViewById(R.id.result_persion_spinner);
        sampleSpinner = (Spinner) findViewById(R.id.result_spinner_sample);
        typeSpinner = (Spinner) findViewById(R.id.result_spinner_type);
        shijiSpinner = (Spinner) findViewById(R.id.result_shiji_spinner);
        result_spinner_result = (Spinner) findViewById(R.id.result_spinner_result);
        tv_check_sample = (TextView) findViewById(R.id.tv_check_sample);

        tv_check_sample.setText("");
        tv_check_sample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog == null) {
                    dialog = new Dialog(ResultActivity.this);
                    inflate = LayoutInflater.from(ResultActivity.this)
                            .inflate(R.layout.dialog_filtrate_select, null, false);
                    lv = inflate.findViewById(R.id.lv);
                    et_content = inflate.findViewById(R.id.et_content);
                    filtrateAdapter = new FiltrateAdapter(ResultActivity.this);
                }

                et_content.setText("");
                if (sampleNames_s == null || sampleNames_s.isEmpty()) {
                    try {
                        List<SampleName> sns = com.tnd.multifuction1.db.DbHelper.GetInstance().findAll(Selector.from(SampleName.class)
                                .orderBy("time", true));
                        if (sns == null) {
                            sns = new ArrayList<>();
                        }
                        if (sampleNames_s == null) {
                            sampleNames_s = new ArrayList<>();
                        } else {
                            sampleNames_s.clear();
                        }
                        sampleNames_s.addAll(sns);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
//                sampleNames_s.clear();
                    if (sampleNames == null) {
                        sampleNames = new ArrayList<>();
                    } else {
                        sampleNames.clear();
                    }
                    if (sampleNames_s != null) {
                        sampleNames.addAll(sampleNames_s);
                    }
                } else {
                    sampleNames.clear();
                    sampleNames.addAll(sampleNames_s);
                }
                filtrateAdapter.setData(sampleNames);
                lv.setAdapter(filtrateAdapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        tv_check_sample.setText(sampleNames.get(position).getName());
                        //将最近选择的排列在最前面
                        sampleNames.get(position).setTime(new Date().getTime());
                        new SampleName().saveOrUpdate(sampleNames.get(position));
                        SampleName sn = sampleNames.get(position);
                        if (position < sampleNames_s.size()) {
                            sampleNames_s.remove(sn);
                            sampleNames_s.add(0, sn);
                        }
                        sampleNames.remove(position);
                        sampleNames.add(0, sn);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });

                et_content.setSingleLine();
                et_content.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                et_content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_NEXT) {
                            tv_check_sample.setText(et_content.getText());

                            handled = true;

                            dialog.dismiss();
                            et_content.setText("");
                        }
                        return handled;
                    }
                });
                dialog.show();
                dialog.setContentView(inflate);

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
                        onAdapterFilter(str);
                    }

                    private void onAdapterFilter(String str) {
                        if (str == null || str.equals("")) {
                            sampleNames = new ArrayList<>();
                            sampleNames.addAll(sampleNames_s);
                            filtrateAdapter.setData(sampleNames);
                        } else {
                            sampleNames = new ArrayList<>();
                            sampleNames.addAll(sampleNames_s);
                            filtrateAdapter.setData(filter(str, sampleNames));
                        }
                    }
                    private List<SampleName> filter(String str, List<SampleName> data) {
                        Iterator<SampleName> iterable = data.iterator();
                        while (iterable.hasNext()) {
                            FiltrateModel m
                                    = iterable.next();
                            if (!m.getName().contains(str)) {
                                iterable.remove();
                            }
                        }
                        return data;
                    }

                });
            }
        });

        /*sampleUnitSpinner.setVisibility(View.VISIBLE);
        typeSpinner.setVisibility(View.VISIBLE);
        tv_check_type.setVisibility(View.GONE);
        tv_checkactivity_sampleunit.setVisibility(View.GONE);*/





    }

    public void ClickQuery(View v) {
        query();
    }

    public void ClickClear(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                ResultActivity.this);
        builder.setTitle("提示");
        builder.setMessage("确定删除全部数据?");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                for (int i = 0; i < result_list.size(); i++) {
                    ResultModel resultModel = result_list.get(i);
                    try {
                        db.delete(resultModel);// 自定义sql查询
                    } catch (DbException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                result_list.clear();
                adapter.notifyDataSetChanged();
            }
        });

        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        builder.create().show();
    }

    private ResultModel selectedResult;
    public void ClickUploadData(View v) {
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
        CheckResult checkResult = new CheckResult();
        checkResult.sampleName = selectedResult.sample_name;
        checkResult.projectName = selectedResult.project_name;
        checkResult.testTime = selectedResult.time;
        checkResult.twh = selectedResult.twh;

        checkResult.weight = selectedResult.weight;
        if ("阴性".equals(selectedResult.check_result)) {
            checkResult.resultJudge = "合格";
        } else if ("阳性".equals(selectedResult.check_result)){
            checkResult.resultJudge = "不合格";
        }
        checkResult.testValue = selectedResult.check_value;
        checkResult.resultJudge = selectedResult.check_result;

        Collections.addAll(list, checkResult);
        UploadThread t = new UploadThread(act, list, new UploadThread.onUploadListener() {
            @Override
            public void onSuccess(List<CheckResult> list, int returnId, int position, String result) {
                if (!act.isFinishing()) {
                    mHandler.obtainMessage(ToolUtils.upload_success, position, returnId, list).sendToTarget();
                }
                if (result.contains("上传成功0条") &&result.contains("重复1条")) {
                    APPUtils.showToast(ResultActivity.this, "平台已上传该条数据，请勿重复上传！！！");
                }
                if (result.contains("上传成功1条")) {
                    APPUtils.showToast(ResultActivity.this, "上传数据成功");
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
                    selectResultMode.uploadId = returnId;
                    try {
                        db.update(selectResultMode, new String[]{"uploadId"});
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    isUploading = false;
                    APPUtils.showToast(act, "上传成功");
                    break;
                case ToolUtils.upload_fail:
                    String result = (String) msg.obj;
                    isUploading = false;
                    Log.d(TAG, result);
                    APPUtils.showToast(act, result);

//                    tvDataCount.setText("" + result);
                    break;
                default:

                    break;
            }
        }
    };

    public void ClickBack(View v) {
        this.back();
    }

    public void showCalendarDialog(final int index) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int monthYear = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, new OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {

                if (index == 0) {
                    String time = arg1 + "-" + String.format("%02d", arg2 + 1)
                            + "-" + String.format("%02d", arg3) + " 00:00";
                    long t = 0;
                    try {
                        t = stringToLong(time, "yyyy-MM-dd HH:mm");
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    starttime_long = t;
                    startTime.setText(time);
                } else {
                    String time = arg1 + "-" + String.format("%02d", arg2 + 1)
                            + "-" + String.format("%02d", arg3) + " 23:59";
                    long t = 0;
                    try {
                        t = stringToLong(time, "yyyy-MM-dd HH:mm");
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    endtime_long = t;
                    endTime.setText(time);
                }

            }
        }, year, monthYear, day).show();

    }

    // string类型转换为long类型
    // strTime要转换的String类型的时间
    // formatType时间格式
    // strTime的时间格式和formatType的时间格式必须相同
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    // date类型转换为long类型
    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    @Override
    public void onSelectedItem(int position) {
        selectedResult = result_list.get(position);
    }

    /**
     * 导出数据
     * @param view
     */
    public void ClickExportData(View view) {
        if (result_list == null || result_list.size() == 0) {
            APPUtils.showToast(ResultActivity.this,"无数据导出！！！");
            return;
        }

        btnExportData.setEnabled(false);
        showWaitDialog();
        new Thread() {
            @Override
            public void run() {
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
//            sheet.addCell(new Label(0, 2, "检测信息详情", subMainHeaderCellFormat));
            sheet.mergeCells(0, 2, 9, 2);

            for (int i = 0; i < EXCEL_HEADER.length; i++) {
                sheet.addCell(new Label(i, 3, EXCEL_HEADER[i], headerCellFormat));
            }

            //{"检测单位","检验员", "样品名称", "检测项目名", "商品来源","检测值",  "检测结果"};
            if (result_list != null) {
                for (int i = 0; i < result_list.size(); i++) {
                    ResultModel result = result_list.get(i);
                    int lineIdx = 4 + i;
                    sheet.addCell(new Label(0, lineIdx, ToolUtils.long2String(result.time, "yyyy-MM-dd HH:mm:ss"), defaultCellFormat)); //检测时间
                    sheet.addCell(new Label(1, lineIdx, result.sample_number, defaultCellFormat)); //样品编号
                    sheet.addCell(new Label(2, lineIdx, result.sample_name, defaultCellFormat)); //商品名
                    sheet.addCell(new Label(3, lineIdx, result.project_name, defaultCellFormat)); //检测项目
                    sheet.addCell(new Label(4, lineIdx, result.check_value, defaultCellFormat)); //检测值
                    sheet.addCell(new Label(5, lineIdx, result.check_result, defaultCellFormat)); //检测结果
                    sheet.addCell(new Label(6, lineIdx, result.company_name, defaultCellFormat)); //被检单位

                    sheet.addCell(new Label(7, lineIdx, result.persion, defaultCellFormat)); //检测人员
                    sheet.addCell(new Label(8, lineIdx, result.orgin, defaultCellFormat)); //商品来源
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

            com.tnd.multifuction1.util.APPUtils.showToast(this, "数据导出成功,存储位置：" + saveFile.getAbsolutePath(), true);
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


    private void showWaitDialog() {
        waitDialog = new ProgressDialog(this);
        waitDialog.setMessage("正在导出,请稍后...");
        waitDialog.setCancelable(false);
        waitDialog.show();
    }
}
