package com.tnd.jinbiao.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.friendlyarm.AndroidSDK.HardwareControler;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.tnd.jinbiao.ToolUtils;
import com.tnd.jinbiao.base.BaseActivity;
import com.tnd.jinbiao.dialog.DateTimePickerDialog;
import com.tnd.jinbiao.model.LineModel;
import com.tnd.jinbiao.model.PeopleModel;
import com.tnd.jinbiao.model.ResultModel;
import com.tnd.jinbiao.model.SampleModel;
import com.tnd.jinbiao.model.SampleTypeModel;
import com.tnd.jinbiao.model.ShiJiModel;
import com.tnd.multifuction1.R;
import com.tnd.multifuction1.adapter.FiltrateAdapter;
import com.tnd.multifuction1.db.DbHelper;
import com.tnd.multifuction1.model.FiltrateModel;
import com.tnd.multifuction1.model.SampleName;
import com.tnd.multifuction1.util.BrightCommandM;
import com.tnd.multifuction1.util.Global;
import com.tnd.multifuction1.util.SerialUtils;
import com.tnd.util.APPUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class CheckActivity extends BaseActivity implements View.OnClickListener {


    /**
     * 读取数据超时次数，如果超过3次，则终止此次检测
     */
    private int readTimeOutCount = 0;
    private Spinner companySpinner = null;
    private Spinner persionSpinner = null;
    private Spinner shijiSpinner = null;
    private Spinner sampleSpinner=null;
    private Spinner projectSpinner = null;
    private Spinner typeSpinner=null;
    private Spinner sampleUnitSpinner = null;

    private EditText etJcx = null;
    private EditText etLjz = null;
    private EditText etDr = null;
    private EditText etConcentrate = null;
    private EditText etResult = null;
    private TextView long_tv = null;


    private EditText et_Sample_Num = null;
    private EditText et_SampleTime= null;

    private String[] company_list = null;
    private String[] persion_list = null;
    private String[] shiji_list = null;
    private String[] sample_list = null;
    private String[] project_list = null;
    private String[] type_list=null;
    private String[] sampleUnit_list = null;

    private List<PeopleModel> persionlist = null;
    private List<PeopleModel> companylist = null;
    private List<ShiJiModel> shijilist = null;
    private List<SampleModel> samplelist = null;
    private List<LineModel> projectlist = null;
    private List<SampleTypeModel> typelist=null;
    private List<PeopleModel> sampleUnitList = null;

    private ArrayAdapter<String> company_adater = null;
    private ArrayAdapter<String> persion_adapter = null;
    private ArrayAdapter<String> shiji_adapter = null;
    private ArrayAdapter<String> sample_adapter = null;
    private ArrayAdapter<String> project_adapter = null;
    private ArrayAdapter<String> type_adapter = null;
    private ArrayAdapter<String> sampleUnit_adapter = null;

    private Button upload_data;
    private Button btn_Imm_Check;
    private DbUtils db;

    private final String TAG = CheckActivity.class.getSimpleName();

    private static final String ACTION_USB_PERMISSION = "com.example.usb";
    private Button move_time;
    private final int MSG_USB_GETDATA = 0xCC;

    private boolean isGet = false;

    private int baurt = 9600;

    /**
     * 正在上传的标志，指示是否正在进行上传操作
     */
    private Boolean uploadingFlag = false;

    public LineModel selectedProject = null;
    public PeopleModel company_model = null;
    public PeopleModel persion_model = null;
    public ShiJiModel shiji_model = null;
    public SampleModel sample_model = null;
    public SampleTypeModel type_model = null;
    public PeopleModel sampleUnit_model = null;

    private String source = null;
    String testTime = null;

    String s =null;
    private ResultModel resultModel;

    static List<SampleName> sampleNames_s = new ArrayList<>();//样品名称
    static List<SampleName> sampleNames;
    private String[] sampleName_s = null;
    private ArrayAdapter<String> sampleName= null;
    public SampleName samplename = null;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        db = DbHelper.GetInstance();
        initView();
        upload_data.setEnabled(true);
        source = getIntent().getStringExtra("source");

        if (!source.equals("1")) {
            etConcentrate.setVisibility(View.GONE);
            long_tv.setVisibility(View.GONE);
        }

        try {
            persionlist = db.findAll(Selector.from(PeopleModel.class).where("source", "=", 2));
            companylist = db.findAll(Selector.from(PeopleModel.class).where("source", "=", 1));
            sampleUnitList = db.findAll(Selector.from(PeopleModel.class).where("source", "=", 3));
            shijilist = db.findAll(Selector.from(ShiJiModel.class));
            samplelist = db.findAll(Selector.from(SampleModel.class));
            typelist=db.findAll(Selector.from(SampleTypeModel.class));
            projectlist = db.findAll(Selector.from(LineModel.class));//.where("source", "<", 4)

            if (persionlist == null) {
                persionlist = new ArrayList<PeopleModel>();
            }

            if (companylist == null) {
                companylist = new ArrayList<PeopleModel>();
            }

            if (shijilist == null) {
                shijilist = new ArrayList<ShiJiModel>();
            }
            if (samplelist == null) {
                samplelist = new ArrayList<SampleModel>();
            }
            if(typelist==null){
                typelist=new ArrayList<SampleTypeModel>();
            }
            if (projectlist == null) {
                projectlist = new ArrayList<LineModel>();
            }
            if (sampleUnitList == null) {
                sampleUnitList = new ArrayList<PeopleModel>();
            }

            if(sampleNames == null){
                sampleNames = new ArrayList<SampleName>();
            }

            company_list = new String[companylist.size()];
            persion_list = new String[persionlist.size()];
            shiji_list = new String[shijilist.size()];
            sample_list=new String[samplelist.size()];
            project_list = new String[projectlist.size()];
            type_list=new String[typelist.size()];
            sampleUnit_list = new String[sampleUnitList.size()];
            sampleName_s =new String[sampleNames.size()];

            for (int i = 0; i < persionlist.size(); i++) {
                PeopleModel model = persionlist.get(i);
                persion_list[i] = model.getName();
            }

            for (int i = 0; i < companylist.size(); i++) {
                PeopleModel model = companylist.get(i);
                company_list[i] = model.getName();
            }

//			for (int i = 0; i < shijilist.size(); i++) {
//				ShiJiModel model = shijilist.get(i);
//				shiji_list[i] = model.getName();
//			}
            for (int i = 0; i < samplelist.size(); i++) {
                SampleModel model = samplelist.get(i);
                sample_list[i] = model.getName();
            }

            for (int i = 0; i < projectlist.size(); i++) {
                LineModel model = projectlist.get(i);
                project_list[i] = model.getName();
            }
            for (int i = 0; i < typelist.size(); i++) {
                SampleTypeModel model = typelist.get(i);
                type_list[i] = model.getName();
            }
            for (int i = 0; i < sampleUnitList.size(); i++) {
                PeopleModel model = sampleUnitList.get(i);
                sampleUnit_list[i] = model.getName();
            }
            for(int i = 0; i < sampleNames.size(); i++){
                SampleName sampleName =  sampleNames.get(i);
                sampleName_s[i] = sampleName.getSampleName();
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

        if (company_list == null || company_list.length <= 0) {
            company_list = new String[1];
            company_list[0] = "请先添加检测单位";
        }

        if (persion_list == null || persion_list.length <= 0) {
            persion_list = new String[1];
            persion_list[0] = "请先添加检验员";
        }

//		if (shiji_list == null || shiji_list.length <= 0) {
//			shiji_list = new String[1];
//			shiji_list[0] = "请先添加试剂厂商";
//		}
        if (sample_list == null || sample_list.length <= 0) {
            sample_list = new String[1];
            sample_list[0] = "请先添加样品名称";
        }
        if (type_list == null || type_list.length <= 0) {
            type_list = new String[1];
            type_list[0] = "请先添加样品类型";
        }
        if (project_list == null || project_list.length <= 0) {
            project_list = new String[1];
            project_list[0] = "请先添加检测项目";
        }
        if (sampleUnit_list == null || sampleUnit_list.length <= 0) {
            sampleUnit_list = new String[1];
            sampleUnit_list[0] = "请先添加送检单位";
        }


        company_adater = new ArrayAdapter<String>(CheckActivity.this, R.layout.item_simple_spiner, company_list);//simple_spinner_item
        persion_adapter = new ArrayAdapter<String>(CheckActivity.this, R.layout.item_simple_spiner, persion_list);
        shiji_adapter = new ArrayAdapter<String>(CheckActivity.this, R.layout.item_simple_spiner, shiji_list);
        sample_adapter= new ArrayAdapter<String>(CheckActivity.this, R.layout.item_simple_spiner, sample_list);
        project_adapter = new ArrayAdapter<String>(CheckActivity.this, R.layout.item_simple_spiner, project_list);
        type_adapter = new ArrayAdapter<String>(CheckActivity.this, R.layout.item_simple_spiner, type_list);
        sampleUnit_adapter = new ArrayAdapter<String>(CheckActivity.this, R.layout.item_simple_spiner, sampleUnit_list);
        sampleName = new ArrayAdapter<String>(CheckActivity.this, R.layout.item_simple_spiner, sampleName_s);

        companySpinner.setAdapter(company_adater);
        persionSpinner.setAdapter(persion_adapter);
        shijiSpinner.setAdapter(shiji_adapter);
        /*sampleSpinner.setAdapter(sample_adapter);                      //从样品名称中获取样品名称*/
        sampleSpinner.setAdapter(sampleName);                            //从编码表中获取样品名称
        projectSpinner.setAdapter(project_adapter);
        typeSpinner.setAdapter(type_adapter);
        sampleUnitSpinner.setAdapter(sampleUnit_adapter);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                if (typelist.size() > 0) {
                    SampleTypeModel model = typelist.get(position);
                    type_model = model;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        projectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (projectlist.size() > 0) {
                    selectedProject = projectlist.get(arg2);
                    A1 = selectedProject.getA1();
                    A2 = selectedProject.getA2();
                    X0 = selectedProject.getX0();
                    P = selectedProject.getP();
                    etJcx.setText(selectedProject.getJcx());
                    etLjz.setText(selectedProject.getLjz());
                    clearTestDataShow();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
                // TODO Auto-generated method stub

            }
        });

        sampleUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if(sampleUnitList.size() > 0){
                    PeopleModel model = sampleUnitList.get(arg2);
                    sampleUnit_model = model;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        persionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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

        shijiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
        sampleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (samplelist.size() > 0) {
                    SampleModel model = samplelist.get(position);
                    sample_model = model;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void initView() {

        companySpinner = (Spinner) findViewById(R.id.check_company_spinner);
        persionSpinner = (Spinner) findViewById(R.id.check_persion_spinner);
        shijiSpinner = (Spinner) findViewById(R.id.check_shiji_spinner);
        sampleSpinner = (Spinner) findViewById(R.id.check_sample_spinner);

        projectSpinner = (Spinner) findViewById(R.id.check_project_spinner);
        typeSpinner=(Spinner) findViewById(R.id.check_type_spinner);
        sampleUnitSpinner = (Spinner) findViewById(R.id.checkactivity_sampleunit_spinner);

        etJcx = (EditText) findViewById(R.id.check_edit_jcx);
        etLjz = (EditText) findViewById(R.id.check_edit_lin);
        etDr = (EditText) findViewById(R.id.check_edit_value);
        etConcentrate = (EditText) findViewById(R.id.check_edit_long);
        long_tv = (TextView) findViewById(R.id.check_edit_tv_long);

        tv_check_sample = (TextView) findViewById(R.id.tv_check_sample);
        et_Sample_Num = (EditText) findViewById(R.id.checkactivity_et_SampleNum);

        et_SampleTime = (EditText)findViewById(R.id.checkactivity_et_SampleTime);
        et_SampleTime.setOnClickListener(this);
        String time = GetCurrentTime();

        et_SampleTime.setText(time);

        etResult = (EditText) findViewById(R.id.check_edit_result);
        btn_Imm_Check = (Button) findViewById(R.id.btn_Imm_Check);
        move_time= (Button) findViewById(R.id.move_time);
        upload_data= (Button) findViewById(R.id.upload_data);

        //为按钮设置焦点，防止进入检测界面后立即弹出键盘的行为
        btn_Imm_Check.setFocusable(true);
        btn_Imm_Check.setFocusableInTouchMode(true);
        btn_Imm_Check.requestFocus();
        btn_Imm_Check.requestFocusFromTouch();



        tv_check_sample.setText("请选择样品名称");
        tv_check_sample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog == null) {
                    dialog = new Dialog(CheckActivity.this);
                    inflate = LayoutInflater.from(CheckActivity.this)
                            .inflate(R.layout.dialog_filtrate_select, null, false);
                    lv = inflate.findViewById(R.id.lv);
                    et_content = inflate.findViewById(R.id.et_content);
                    filtrateAdapter = new FiltrateAdapter(CheckActivity.this);
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
                        et_Sample_Num.setText("" + sampleNames.get(position).getSampleNumber());
                        et_Sample_Num.setEnabled(false);
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

        sampleUnitSpinner.setVisibility(View.VISIBLE);
        typeSpinner.setVisibility(View.VISIBLE);
        /*tv_check_type.setVisibility(View.GONE);
        tv_checkactivity_sampleunit.setVisibility(View.GONE);*/




        if(sampleNames_s == null || sampleNames_s.isEmpty()){
            try {
                List<SampleName> sns = DbHelper.GetInstance().findAll(Selector.from(SampleName.class)
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
            if (sampleNames == null) {
                sampleNames = new ArrayList<>();
            } else {
                sampleNames.clear();
            }
            if (sampleNames_s != null) {
                sampleNames.addAll(sampleNames_s);
            }
        }else {
            sampleNames.clear();
            sampleNames.addAll(sampleNames_s);
        }
        //上传数据按钮事件
        upload_data.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ClickUpload();
            }
        });
    }

    private void ClickUpload() {

        if(resultModel == null){
            APPUtils.showToast(CheckActivity.this, "请先检测");
            return;
        }
        if(uploadingFlag){
            Toast.makeText(this, "正在上传数据，请稍后...", Toast.LENGTH_LONG).show();
            return;
        }
        uploadingFlag = true;
        String msg = getUploadData();
        GetDataFrom(msg);
    }
//	{
//		"sample_number":"W123456987",
//		"test_unit_name":"",
//		"test_item":"",
//		"sample_unit":"",
//		"sample_type":"",
//		"test_results":"",
//		"critical_value":"",
//		"test_man":"",
//		" test_time ":"2017-09-23 15:45:12",
//		" sample_time ":"2017-09-23  15:45:12"
//		}

    private String getUploadData() {

        StringBuffer sb = new StringBuffer();
        sb.append("data={");
        sb.append("\"sample_number\":");
        sb.append("\"" + et_Sample_Num.getText().toString().trim() + "\"" + ",");
        sb.append("\"test_unit_name\":");
        sb.append("\"" + companylist.get(companySpinner.getSelectedItemPosition()).getName() + "\"" + ",");
        sb.append("\"test_item\":");
        sb.append("\"" + projectlist.get(projectSpinner.getSelectedItemPosition()).getName() + "\"" + ",");
        sb.append("\"sample_unit\":");
        sb.append("\"" + sampleUnitList.get(sampleUnitSpinner.getSelectedItemPosition()).getName() + "\"" + ",");
        sb.append("\"sample_type\":");
        sb.append("\"" + typelist.get(typeSpinner.getSelectedItemPosition()).getName() + "\"" + ",");
        sb.append("\"test_results\":");
        sb.append("\"" + etResult.getText().toString().trim() + "\"" + ",");
        sb.append("\"critical_value\":");
        if(source.equals("2")){
            sb.append("\"" + etDr.getText().toString().trim() + "\"" + ",");
        }else{
            sb.append("\"" + etConcentrate.getText().toString().trim() + "\"" + ",");
        }
        sb.append("\"test_man\":");
        sb.append("\"" + persionlist.get(persionSpinner.getSelectedItemPosition()).getName() + "\"" + ",");
        sb.append("\"test_time\":");
        sb.append("\"" + testTime + "\"" + ",");
        sb.append("\"sample_time\":");
        sb.append("\"" + et_SampleTime.getText().toString().trim() + "\"");
        sb.append("}");

        return sb.toString();
    }

    private void GetDataFrom(final String msg) {
        // TODO Auto-generated method stub
        //在子线程中操作网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                //urlConnection请求服务器，验证
                Message message = Message.obtain();
                try {
                    //1：url对象
                    String urlString="http://www.huaxialj.com/gspt_all_api/api_hltstkj/pda/API/Application/root/Controller/root_antibiotic/Antibiotic_test_results_colloidal.php";
                    Log.i("uploadurl", urlString);
                    URL url = new URL(urlString);
                    //2;url.openconnection
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    byte[] data = msg.getBytes("utf-8");
                    //3
                    conn.setDoInput(true);                  //打开输入流，以便从服务器获取数据
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setUseCaches(false);               //使用Post方式不能使用缓存
                    conn.setConnectTimeout(10 * 1000);
                    //设置请求体的类型是文本类型
                    conn.setRequestProperty("Content-Type", "application/X0-www-form-urlencoded");
                    //设置请求体的长度
                    conn.setRequestProperty("Content-Length", String.valueOf(data.length));

                    conn.getOutputStream().write(data);
                    //4
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        String result = getStringFromInputStream(is);
                        String[] st = result.split(",");
                        String str = null;
                        if (st.length == 2) {
                            str = st[0];
                            str = str.substring(str.indexOf(':') + 2);
                            str = str.replace("\"", "");
                        }
                        if (str != null) {
                            String s = decodeUnicode(str);
                            System.out.println("=====================服务器返回的信息：" + result);
                            System.out.println("=====================上传结果：" + s);
                            if(s != null){
                                if(s.contains("成功")){
                                    message.obj = s;
                                }else{
                                    message.obj = "上传失败";
                                }
                            }else{
                                message.obj = "上传失败";
                            }
                        }
                    }else{
                        message.obj = "上传失败";
                    }
                } catch (UnknownHostException ex){
                    ex.printStackTrace();
                    message.obj = "上传失败,请检查网络连接！";
                }catch (IOException ex){
                    ex.printStackTrace();
                    message.obj = "上传失败,请检查网络连接！";
                }catch (Exception e) {
                    e.printStackTrace();
                    message.obj = "上传失败！";
                }finally {
                    uploadingFlag = false;
                    message.what = 200;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();

        try {
            while (start > -1) {
                end = dataStr.indexOf("\\u", start + 2);
                String charStr = "";
                if (end == -1) {
                    charStr = dataStr.substring(start + 2, dataStr.length());
                } else {
                    charStr = dataStr.substring(start + 2, end);
                }
                char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
                buffer.append(new Character(letter).toString());
                start = end;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }

    /**
     * 根据输入流返回一个字符串
     * @param is
     * @return
     * @throws Exception
     */
    private static String getStringFromInputStream(InputStream is) throws Exception{

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        byte[] buff=new byte[1024];
        int len=-1;
        while((len=is.read(buff))!=-1){
            baos.write(buff, 0, len);
        }
        is.close();
        String html=baos.toString();
        baos.close();


        return html;
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_USB_GETDATA:
                    if (isGet) {
                        ToolUtils.hiddenHUD();
                        String str = bytes2HexString((byte[]) msg.obj);
                        etResult.setText(str);

                        Toast.makeText(CheckActivity.this, str, Toast.LENGTH_LONG).show();
                    }

                    break;

                case 100:
                    ToolUtils.hiddenHUD();
                    if(timer!=null){
                        timer.cancel();
                        timer = null;
                    }

                    if(task!=null){
                        task.cancel();
                    }

//				HardwareControler.close(devfd);
                    upload_data.setEnabled(true);
                    //getUploadData();
                    //ClickUplaad();
                    break;
                case 200:
                    String result= (String) msg.obj;
                    if (result.contains("成功")) {
                        Toast.makeText(getApplicationContext(), "上传成功！", Toast.LENGTH_SHORT).show();
                    } else if(result.contains("失败")) {
                        Toast.makeText(getApplicationContext(), "上传失败！", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };

    /**
     * 保存检测结果
     * @param result
     */
    public void saveCheck_ResultData(String result) {

        try {
            long time = new Date().getTime();// new Date()为获取当前系统时间
            resultModel = new ResultModel();
            testTime = GetCurrentTime();
            resultModel.number = testTime;
            resultModel.company_name = companySpinner.getSelectedItem().toString();
            resultModel.persion = persionSpinner.getSelectedItem().toString();
//			resultModel.shiji = shiji_model.getName();
            resultModel.sample_name =  tv_check_sample.getText().toString();
            resultModel.sample_number = et_Sample_Num.getText().toString();
            resultModel.sample_type = typeSpinner.getSelectedItem().toString();
            resultModel.project_name = selectedProject.getName();
//            resultModel.sample_unit = sampleUnit_model.getName();
            resultModel.xian = etJcx.getText().toString();
            resultModel.lin = etLjz.getText().toString();
            resultModel.check_value = etDr.getText().toString();
            resultModel.style_long = etConcentrate.getText().toString();
            resultModel.check_result = result;
            resultModel.time = time;
            resultModel.weight = "1";
            resultModel.orgin = sampleUnitSpinner.getSelectedItem().toString();
            resultModel.concentrateUnit = selectedProject.ConcentrateUnit;
            resultModel.sample_unit = sampleUnitSpinner.getSelectedItem().toString();
            resultModel.shiji = sampleUnitSpinner.getSelectedItem().toString();
            resultModel.twh = new Random().nextInt(100000) + "";
            upload_data.setEnabled(true);

            db.save(resultModel);

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    class MytaskTime extends TimerTask {
        @Override
        public void run() {
            recLen--;
            Message message = new Message();
            Log.d(TAG, "发送handler信息");
            message.what = 1;
            handlerTime.sendMessage(message);
        }
    };
    private MytaskTime tasktime ;

    Timer timerRe =null;
    TimerTask taskTime=null;
    private int recLen=0;

    Handler handlerTime = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 1:
                    move_time.setText(""+recLen+"S后开始检测");
                    if(recLen < 0){
                        taskTime.cancel();
                        timerRe.cancel();
                        timerRe = null;
                        move_time.setText("定时检测");

                        ClickMoveNow();
                    }
                    break;
            }
        }
    };

    private boolean ValidateDataToTest(){
        if (company_model == null) {
            Toast.makeText(this, "请先选择检测单位", Toast.LENGTH_LONG).show();
            return false;
        }

        if (persion_model == null) {
            Toast.makeText(this, "请先选择检验员", Toast.LENGTH_LONG).show();
            return false;
        }
//		if (shiji_model == null) {
//			Toast.makeText(this, "请先选择试剂厂商", Toast.LENGTH_LONG).show();
//			return false;
//		}
        if (tv_check_sample.getText() == null || tv_check_sample.getText().toString().isEmpty() || "请选择样品名称".equals(tv_check_sample.getText().toString())) {
            Toast.makeText(this, "请先选样品名称", Toast.LENGTH_LONG).show();
            return false;
        }
        if (type_model == null) {
            Toast.makeText(this, "请先选样品类型", Toast.LENGTH_LONG).show();
            return false;
        }

        if (selectedProject == null) {
            Toast.makeText(this, "请先选择检测项目", Toast.LENGTH_LONG).show();
            return false;
        }
        if(sampleUnit_model == null){
            Toast.makeText(this, "请先选择商品来源", Toast.LENGTH_LONG).show();
            return false;
        }
//		if(et_Sample_Num.getText().toString().trim().isEmpty()){
//			Toast.makeText(this, "请输入样品编号", Toast.LENGTH_LONG).show();
//			return false;
//		}
        if(TextUtils.isEmpty(selectedProject.card_name)
                || new Integer("0").equals(selectedProject.ScanEnd)
                || new Integer("0").equals(selectedProject.ScanStart)
                || new Integer("0").equals(selectedProject.CTWidth)
                || new Integer("0").equals(selectedProject.CTDistance)){
            APPUtils.showToast(CheckActivity.this, "该检测项目参数不完整或不正确，请转至项目管理并重新编辑");
            return false;
        }
        return true;
    }

    public void ClickMoveNow() {

//		if(!ValidateDataToTest()){
//			return;
//		}
//
//		ToolUtils.showHUD(CheckActivity.this, "请稍等...");
//		String message = "SendData" + "," + selectedProject.getSource() + ","
//				+ selectedProject.getScanStart() + "," + selectedProject.getScanEnd() + ","
//				+ selectedProject.getCTWidth() + "," + selectedProject.getCTDistance() + ","
//				+ etLjz.getText().toString() + "," + "1" + "," + "1" + ","
//				+ "1" + "," + "1";
//		SendData(message);

    }

    private void clearTestDataShow(){

        etDr.setText("");
        etResult.setText("");
        etConcentrate.setText("");
    }

    public void ClickStart(View v) {

        if(!ValidateDataToTest()){
            return;
        }
        clearTestDataShow();

        String message = getTestInstruction();
        readTimeOutCount = 0;
        SendData(message);
    }

    private String getTestInstruction() {

        int sou = selectedProject.source;
        if(sou == 3) sou = 2;
        return "SendData" + "," + sou + ","
                + selectedProject.getScanStart() + "," + selectedProject.getScanEnd() + ","
                + selectedProject.getCTWidth() + "," + selectedProject.getCTDistance() + ","
                + etLjz.getText().toString() + "," + "1" + "," + "1" + "," + "1"
                + "," + "1";
    }

    private String A1;
    private String A2;
    private String X0;
    private String P;

    /**
     * 打印数据
     * @param v
     */
    public void PrintInfo(View v) {
        if (resultModel == null) {
            Toast.makeText(this, "请先检测", Toast.LENGTH_LONG).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("是否打印二维码?")
                .setTitle("提示")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String message = null ;
                        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
                        Date curDate  = new Date(System.currentTimeMillis());//获取当前时间
                        String time = formatter.format(curDate);
                        String printData = null;
                        printData = ToolUtils.GetPrintInfo(resultModel,CheckActivity.this, source);
                        SerialUtils.COM4_SendData(BrightCommandM.t1b63(0));
                        SerialUtils.COM4_SendData(BrightCommandM.t1b61(0));
                        byte[] data = printData.getBytes(Charset.forName("gb2312"));
                        if(!SerialUtils.COM4_SendData(data)){
                            APPUtils.showToast(CheckActivity.this, "打印数据发送失败");
                        }

                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                        // 增加打印二维码
                        SerialUtils.COM4_SendData(BrightCommandM.t1b61(1));
                        SerialUtils.COM4_SendData(BrightCommandM.t1d77(2));
                        SerialUtils.COM4_SendData(BrightCommandM.t1d28Qr(getData(resultModel, CheckActivity.this)));
                        SerialUtils.COM4_SendData(BrightCommandM.t1d77(2));
                        SerialUtils.COM4_SendData(BrightCommandM.t1b61(1));

                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String message = null ;
                        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
                        Date curDate  = new Date(System.currentTimeMillis());//获取当前时间
                        String time = formatter.format(curDate);
                        String printData = null;
                        printData = ToolUtils.GetPrintInfo(resultModel, CheckActivity.this, source);
                        SerialUtils.COM4_SendData(BrightCommandM.t1b63(0));
                        SerialUtils.COM4_SendData(BrightCommandM.t1b61(0));
                        byte[] data = printData.getBytes(Charset.forName("gb2312"));
                        if(!SerialUtils.COM4_SendData(data)){
                            APPUtils.showToast(CheckActivity.this, "打印数据发送失败");
                        }

                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                    }
                })
                .create().show();
    }

    private String getData(ResultModel result, Context context) {
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

    private String devName = "/dev/ttyAMA3";
    private int speed = 9600;
    private int dataBits = 8;
    private int stopBits = 1;
    private int devfd = -1;
    private Timer timer = null;
    private String dateCheck="";
    private String timeCheck="";

    private void SendData(String message) {

        SimpleDateFormat formatterData = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        dateCheck= formatterData.format(curDate);
        timeCheck= formatterTime.format(curDate);
        if (buf != null && buf.length > 0) {
            buf = null;
        }
        if(strResultBuffer!=null&&strResultBuffer.length()>0){
            strResultBuffer=null;
        }
        buf = new byte[BUFSIZE];
        strResultBuffer=new StringBuilder(256*200);

        byte[] data = message.getBytes(Charset.forName("gb2312"));
        if(!SerialUtils.COM3_SendData(data)){
            APPUtils.showToast(this, "数据发送失败");
            return;
        }
        ToolUtils.showHUD(CheckActivity.this, "请稍等...");

        timer = new Timer();
        if (task != null) {
            task.cancel();
        }
        task = new MyTimerTask();
        timer.schedule(task, 4000, 2000);
    }

    private MyTimerTask task ;
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if(readTimeOutCount++ > 3){
                handler.sendEmptyMessage(100);
                APPUtils.showToast(CheckActivity.this, "接收数据失败");
                Log.d(TAG, "接收数据超时了，结束了本次数据接收");
            }
            Message message = new Message();
            message.what = 1;
            Log.d(TAG, "发送handler信息");
            handlerMess.sendMessage(message);
        }
    };
    private final int BUFSIZE = 1024;
    private byte[] buf;
    private StringBuilder strResultBuffer=null;
    private String strResult=null;
    private Handler handlerMess = new Handler() {
        @Override
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int i = HardwareControler.select(Global.DEV_COM3, 0, 0);
                    if (i == 1) {

                        int retSize = HardwareControler.read(Global.DEV_COM3, buf, BUFSIZE);
                        if (retSize > 0) {
                            String strResultOld = null;

                            strResultOld = new String(buf, 0, retSize, Charset.forName("gbk"));
                            strResultBuffer.append(strResultOld);
                            if(!strResultOld.endsWith("\n")){
                                return;
                            }
                            strResult = strResultBuffer.toString();
                            strResult = strResult.replace("\n", "").replace("OK","");
                            String check_resultTxt = "";

                            String[] data = strResult.split(",");
                            if(data.length != 2){  //如果数据格式错误
                                APPUtils.showToast(CheckActivity.this, "数据格式错误，接收数据失败");
                                handler.sendEmptyMessage(100);
                                return;
                            }
                            CalcAndShowTestResult(data);
//						if (strResult.contains(",")) {
//							check_resultTxt = strResult.substring(strResult.lastIndexOf(",") + 1);
//							etResult.setText(check_resultTxt);
//
//							strResult = strResult.substring(0, strResult.indexOf(","));
//							if (!(strResult != null && strResult.length() > 2)) {
//								Toast.makeText(getApplicationContext(), "无效数据，重新检测", Toast.LENGTH_LONG).show();
//								new Thread(new Runnable() {
//
//									@Override
//									public void run() {
//										SystemClock.sleep(500);
//										handler.sendEmptyMessage(100);
//									}
//								}).start();
//								return;
//							} else {
//								strResult = strResult.substring(2, strResult.length());
//							}
//
//							Boolean isNumberResult = isNumeric(strResult);
//							if (isNumberResult) {
//								etDr.setText(strResult);
//								etConcentrate.setText("0.000");
//								saveCheck_ResultData(check_resultTxt);
//								handler.sendEmptyMessage(100);
//								Log.d(TAG, "检测成功");
////									new Thread(new Runnable() {
////
////										@Override
////										public void run() {
////											SystemClock.sleep(500);
////											handler.sendEmptyMessage(100);
////										}
////									}).start();
//								return;
//							}
//							etDr.setText(strResult + "");
//							if ((selectedProject.getA1() != null && selectedProject.getA1().length() > 0)) {
//								double longStyle = Double.parseDouble(X0) * (Math.pow((((Double.parseDouble(A2) - Double.parseDouble(A1)) / (Double
//										.parseDouble(A2) - Double.parseDouble(strResult))) - 1), (1 / Double.parseDouble(P))));
//								Boolean isLongStyleResult = isNumeric(longStyle + "");
//								if (isLongStyleResult) {
//									etConcentrate.setText("0.000");
//								} else {
//									if (longStyle > 0) {
//										DecimalFormat df = new DecimalFormat("#.000");
//										etConcentrate.setText(df.format(longStyle) + "");
//									} else {
//										etConcentrate.setText("0.000");
//									}
//								}
//							}
//							saveCheck_ResultData(check_resultTxt);
//							handler.sendEmptyMessage(100);
//							Log.d(TAG, "检测成功");
////								new Thread(new Runnable() {
////
////									@Override
////									public void run() {
////										SystemClock.sleep(500);
////										handler.sendEmptyMessage(100);
////									}
////								}).start();
//						}
                        }
                    }

                    break;
            }
        }
    };

    /**
     * 计算并显示检测结果
     * @param data
     * 检测值和检测结果组成的长度为2的字符串数组
     */
    private void CalcAndShowTestResult(String[] data){

        etDr.setText(data[0]);
        if("no signal".equals(data[0].toLowerCase())){  //无效的情况
            if(source.equals("1")){  //定量
                etConcentrate.setText("0.000");
            }
            etResult.setText(data[1]);
            handler.sendEmptyMessage(100);//关闭定时器
            return;
        }
        float dr = 0.0f;
        try {
            dr = Float.parseFloat(data[0]);
        }catch (NumberFormatException ex){
            APPUtils.showToast(CheckActivity.this, "数据格式错误，接收数据失败");//几乎不可能异常，除非下位机出现问题
            handler.sendEmptyMessage(100);//关闭定时器
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if(source.equals("1")){  //定量
                double concentrate = 0.0;
                if(!TextUtils.isEmpty(A1) && !TextUtils.isEmpty(A2)	&& !TextUtils.isEmpty(X0) && !TextUtils.isEmpty(P)){

                    concentrate = Double.parseDouble(X0) * (Math.pow((((Double.parseDouble(A2) - Double.parseDouble(A1)) / (Double
                            .parseDouble(A2) - dr)) - 1), (1 / Double.parseDouble(P))));
                    concentrate = concentrate < 0 ? 0.0 : concentrate;  //如果计算得到的浓度值为负数，则令其=0
                    DecimalFormat df = new DecimalFormat("#.000");
                    String s = df.format(concentrate);
                    concentrate = Double.parseDouble(s);
                    etConcentrate.setText(concentrate + "");
                    if(Double.parseDouble(selectedProject.Jcx) > concentrate){
                        etResult.setText("合格");
                    }else{
                        etResult.setText("不合格");
                    }
                }else{  //定量检测中用户对进行检测的项目没有设置四参数的情况，该情况下，检测结果显示下位机收到的阴阳性结果
                    etResult.setText(data[1]);
                }
            }else{  //定性
                etResult.setText(data[1]);
            }
        } catch (NumberFormatException e) {
            APPUtils.showToast(CheckActivity.this, "计算错误");
            e.printStackTrace();
        }
        saveCheck_ResultData(etResult.getText().toString());
        handler.sendEmptyMessage(100);//关闭定时器
    }

    public boolean isNumeric(String str) {

        {
            char c = str.charAt(0);
            if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void ClickBack(View v) {
        this.back();
    }


    // [ScanStart] bytes2HexString byte与16进制字符串的互相转换
    public String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public byte[] str2HexStr(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }

        return str.toString().getBytes();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkactivity_et_SampleTime:
                ShowSampleTimeDialog();
                break;

            default:
                break;
        }

    }

    /**
     * 显示抽样时间dialog
     */
    private void ShowSampleTimeDialog(){

        final Calendar c = Calendar.getInstance();
        // 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
        new DateTimePickerDialog(CheckActivity.this, 0,
                new DateTimePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker startDatePicker,
                                          int year, int monthOfYear,
                                          int dayOfMonth, TimePicker timePicker,
                                          int hour, int minute) {
                        // TODO Auto-generated method stub
                        String textString = GetFormatTime(year, monthOfYear, dayOfMonth, hour, minute, c.get(Calendar.SECOND));
                        et_SampleTime.setText(textString);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private String GetFormatTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute, int second) {

        String textString = String.format("%d-%d-%d %02d:%02d:%02d", year, monthOfYear + 1, dayOfMonth, hour, minute, second);
        return textString;
    }

    private String GetCurrentTime(){

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 可以方便地修改日期格式
        return dateFormat.format(now);

    }
}
