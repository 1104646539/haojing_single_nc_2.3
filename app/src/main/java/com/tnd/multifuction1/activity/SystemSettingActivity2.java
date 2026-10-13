package com.tnd.multifuction1.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tnd.multifuction1.R;
import com.tnd.multifuction1.adapter.DialogSelectAdapter;
import com.tnd.multifuction1.dialog.EditDataDialog;
import com.tnd.multifuction1.dialog.EditSampleDialog;
import com.tnd.multifuction1.dialog.EditURLDialog;
import com.tnd.multifuction1.dialog.PrintSettingDialog;
import com.tnd.multifuction1.dialog.UploadingDialog;
import com.tnd.multifuction1.model.CheckResult;
import com.tnd.multifuction1.model.FiltrateModel;
import com.tnd.multifuction1.model.Print;
import com.tnd.multifuction1.model.Project;
import com.tnd.multifuction1.model.SampleName;
import com.tnd.multifuction1.model.VersionInfo;
import com.tnd.multifuction1.resource.SPResource;
import com.tnd.multifuction1.util.APPUtils;
import com.tnd.multifuction1.util.Global;
import com.tnd.multifuction1.util.SPUtils;
import com.tnd.multifuction1.util.SerialUtils;
import com.tnd.multifuction1.util.ToolUtils;
import com.tnd.multifuction1.view.AutoConintaEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 系统设置
 */
public class SystemSettingActivity2 extends Activity implements View.OnClickListener {


    //    private Button btnLocation;
//    private Button btnUploadSetting;
    private Button btnSample;
    private Button btnProject;
    private Button btnDebug;
    private int debugClickCount;
    private Activity act;
    private Button btnIdSetting;
    private Button btnUploadSetting;
    private SharedPreferences sp;
    private EditDataDialog editDataDialog_bc;//被检测单位
    private EditDataDialog editDataDialog_c;//检测单位
    private EditDataDialog editDataDialog_ss;//商品来源
    private EditDataDialog editDataDialog_sn;//样品名称
    private EditDataDialog editDataDialog_i;//检测人员
    private EditURLDialog editURLDialog;
    private EditSampleDialog editSampleDialog;
    private PrintSettingDialog printSettingDialog;
    private List<Print> prints_check;
    private List<Print> prints_data_manager;
    private UploadingDialog uploadingDialog;
    private boolean print_check;//检测是否为单选
    private boolean print_data_manager;//数据管理是否为单选
    private SPUtils spUtils;
    private AutoConintaEditText et_standard_value;
    private AutoConintaEditText et_test_standard;
    private AutoConintaEditText et_card_warm_time;    //农残检测时间
    private AutoConintaEditText et_card_reaction_time;
    private AutoConintaEditText et_fenguang_reaction_time;
    public static final String TAG = "SystemSettingActivity2";
    private TextView order_setting;


    private CheckBox et_print_code;        //是否打印二维码
    private AutoConintaEditText et_asset_name;    //设备名称
    private AutoConintaEditText et_asset_code;     //设备编码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setting2);
        act = this;
        sp = getSharedPreferences(SPResource.FILE_NAME, MODE_PRIVATE);
        spUtils = new SPUtils(sp);
        initData();
        initView();

        completeReceiver = new CompleteReceiver();
        /** register download success broadcast **/
        registerReceiver(completeReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (completeReceiver != null) {
            try {
                unregisterReceiver(completeReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        editDataDialog_bc = new EditDataDialog(this);
        editDataDialog_c = new EditDataDialog(this);
        editDataDialog_ss = new EditDataDialog(this);
        editDataDialog_sn = new EditDataDialog(this);
        editDataDialog_i = new EditDataDialog(this);
        et_standard_value = findViewById(R.id.et_standard_value);
        et_test_standard = findViewById(R.id.et_test_standard);
        et_card_warm_time = findViewById(R.id.et_card_warm_time);
        order_setting = findViewById(R.id.order_setting);
        et_card_reaction_time = findViewById(R.id.et_card_reaction_time);
        et_fenguang_reaction_time = findViewById(R.id.et_fenguang_reaction_time);



        et_asset_name = findViewById(R.id.et_asset_name);     //设备名称
        et_asset_code = findViewById(R.id.et_asset_code);     //设备编码
        et_print_code = findViewById(R.id.et_print_code);     //是否打印二维码
        et_print_code.setOnClickListener(this);                //绑定点击事件
        et_print_code.setChecked(sp.getBoolean("isPrintQrCode",true)); //设置按钮默认选中


        et_card_warm_time.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_card_reaction_time.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_fenguang_reaction_time.setInputType(InputType.TYPE_CLASS_NUMBER);

        et_standard_value.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_test_standard.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);

        et_card_warm_time.setText(Global.cardWarmTime + "s");
        et_card_warm_time.setOnSave(new AutoConintaEditText.OnSave() {
            @Override
            public void Save(String str) {
                if (str.equals("")) {
                    str = "0";
                }
                sp.edit().putString(SPResource.KEY_CARD_WARM_TIME, str).commit();
                Global.cardWarmTime = ToolUtils.replenishInt(str, "s");
                Log.d("initView Save", "str=" + str);
            }
        });

        et_card_reaction_time.setText(Global.cardReactionTime + "s");
        et_card_reaction_time.setOnSave(new AutoConintaEditText.OnSave() {
            @Override
            public void Save(String str) {
                if (str.equals("")) {
                    str = "0";
                }
                sp.edit().putString(SPResource.KEY_CARD_REACTION_TIME, str).commit();
                Global.cardReactionTime = ToolUtils.replenishInt(str, "s");
                Log.d("initView Save", "str=" + str);
            }
        });

        et_fenguang_reaction_time.setText(Global.fenguangReactionTime + "s");
        et_fenguang_reaction_time.setOnSave(new AutoConintaEditText.OnSave() {
            @Override
            public void Save(String str) {
                if (str.equals("")) {
                    str = "0";
                }
                sp.edit().putString(SPResource.KEY_FENGUANG_REACTION_TIME, str).commit();
                Global.fenguangReactionTime = ToolUtils.replenishInt(str, "s");
                Log.d("initView Save", "str=" + str);
            }
        });


        et_standard_value.setText(Global.project.cardXlz + "%");
        et_standard_value.setOnSave(new AutoConintaEditText.OnSave() {
            @Override
            public void Save(String str) {
                Global.project.cardXlz = ToolUtils.replenishInt(str, "%");
                Global.project.saveOrUpdate(Global.project);
                Log.d("initView Save", "str=" + str);
            }
        });
        et_test_standard.setText(Global.project.testStandard);
        et_test_standard.setOnSave(new AutoConintaEditText.OnSave() {
            @Override
            public void Save(String str) {
                Global.project.testStandard = str;
                Global.project.saveOrUpdate(Global.project);
                Log.d("initView Save", "str=" + str);
            }
        });
        et_asset_name.setText(Global.ASSET_NAME);
        et_asset_name.setOnSave(new AutoConintaEditText.OnSave() {
            @Override
            public void Save(String str) {
                if (str.equals("")) {
                    str = "";
                }
                sp.edit().putString(SPResource.ASSET_NAME, str).commit();
                Global.ASSET_NAME = ToolUtils.replenish(str,"");
                Log.d("initView Save", "str=" + str);
            }
        });
        et_asset_code.setText(Global.ASSET_CODE);
        et_asset_code.setOnSave(new AutoConintaEditText.OnSave() {
            @Override
            public void Save(String str) {
                if (str.equals("")) {
                    str = "";
                }
                sp.edit().putString(SPResource.ASSET_CODE, str).commit();
                Global.ASSET_CODE = ToolUtils.replenish(str, "");
                Log.d("initView Save", "str=" + str);
            }
        });


        order_setting.setOnClickListener(this);
    }




    private void initData() {
        print_check = sp.getBoolean(SPResource.KEY_PRINT_CHECK, true);
        print_data_manager = sp.getBoolean(SPResource.KEY_PRINT_DATA_MANAGER, true);
        Log.d("initData", "print_check=" + print_check + "print_data_manager=" + print_data_manager);
        prints_check = spUtils.getDataList(SPResource.KEY_PRINT_CHECK_DATA);
        prints_data_manager = spUtils.getDataList(SPResource.KEY_PRINT_DATA_MANAGER_DATA);
//        if (prints_check != null && prints_check.size() > 0 &&
//                prints_data_manager != null && prints_data_manager.size() > 0) {
//            Log.d("initData", "print_check=" + null
//                    + "print_data_manager=" + null);
//            return;
//        }
//        prints_check = new ArrayList<>();
//        prints_data_manager = new ArrayList<>();
//        Log.d("initData", "prints_check=" + prints_check.toString()
//                + "prints_data_manager=" + prints_data_manager.toString());
//        prints_check.add(new Print("检测时间", true, true, true));
//        prints_check.add(new Print("样品名称", true, true, true));
//        prints_check.add(new Print("抑制率", true, true, true));
//        prints_check.add(new Print("判定结果", true, true, true));
//        prints_check.add(new Print("通道号", true, true, true));
//        prints_check.add(new Print("样品编号", true, false, false));
//        prints_check.add(new Print("被检测单位", true, false, false));
//        prints_check.add(new Print("重量", true, false, false));
//        prints_check.add(new Print("商品来源", true, false, false));
//        prints_check.add(new Print("限量值", true, true, false));
//        prints_check.add(new Print("限量标准", true, true, false));
//
//        prints_data_manager.add(new Print("检测时间", true, true, true));
//        prints_data_manager.add(new Print("样品名称", true, true, true));
//        prints_data_manager.add(new Print("抑制率", true, true, true));
//        prints_data_manager.add(new Print("判定结果", true, true, true));
//        prints_data_manager.add(new Print("通道号", true, true, true));
//        prints_data_manager.add(new Print("被检测单位", true, false, false));
//        prints_data_manager.add(new Print("检测单位", true, true, false));
//        prints_data_manager.add(new Print("检测人员", true, true, false));
//        prints_data_manager.add(new Print("商品来源", true, false, false));
//        prints_data_manager.add(new Print("样品编号", true, false, false));
//        prints_data_manager.add(new Print("重量", true, false, false));
//        prints_data_manager.add(new Print("限量值", true, true, false));
//        prints_data_manager.add(new Print("限量标准", true, true, false));
//
//
//        Log.d("initData", "data b");
//        spUtils.setDataList(SPResource.KEY_PRINT_CHECK_DATA, prints_check);
//        spUtils.setDataList(SPResource.KEY_PRINT_DATA_MANAGER_DATA, prints_data_manager);
//        Log.d("initData", "data a");
    }
    ProgressDialog waitDialog;
    private void showWaitDialog() {
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle("提示");
        waitDialog.setMessage("正在调零，请稍候...");
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.setCancelable(false);
        waitDialog.show();
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_id_setting:
                break;
            case R.id.btn_sample:
                break;
            case R.id.btn_upload_setting:
                break;
            case R.id.btn_project:
                break;
            case R.id.order_setting:
                if (debugClickCount == 0) {
                    debugClickCount++;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            debugClickCount = 0;
                        }
                    }, 2000);
                }
                if (debugClickCount == 5) {
                    debugClickCount = 0;
                    showDebugPwdDialog();
                } else {
                    debugClickCount++;
                }
                break;
            case R.id.et_print_code:
                //给CheckBox设置事件监听
                boolean isChecked=et_print_code.isChecked();
                if(isChecked){
                    //如果是选中状态就把布尔值改为true
                    sp.edit().putBoolean("isPrintQrCode", isChecked).commit();
                    Log.d("onInspector", "是否打印二维码:" + isChecked);
                }else{
                    sp.edit().putBoolean("isPrintQrCode", isChecked).commit();
                    Log.d("onInspector", "是否打印二维码:" + isChecked);

                }
                break;
        }
    }

    /**
     * 被检测单位
     *
     * @param v
     */
    public void onBCheckOrg(View v) {
        editDataDialog_bc.showDialog("被检测单位",
                EditDataDialog.DIALOG_TYPE_BCHEKEORG, null);
    }

    /**
     * 检测单位
     *
     * @param v
     */
    public void onCheckOrg(View v) {

        editDataDialog_c.showDialog("检测单位",
                EditDataDialog.DIALOG_TYPE_CHECKORG, new DialogSelectAdapter.OnItemClick() {
                    @Override
                    public void onItemClick(FiltrateModel data, int oldPosition, int newPosition) {
                        if (data != null) {
                            Global.checkOrg = data.getName();
                        }
                    }
                });
    }

    /**
     * 商品来源
     *
     * @param v
     */
    public void onSampleSource(View v) {
        editDataDialog_ss.showDialog("商品来源",
                EditDataDialog.DIALOG_TYPE_SAMPLESOURCE, null);
    }

    /**
     * 样品名称
     *
     * @param v
     */
    public void onSampleName(View v) {
        editDataDialog_sn.showDialog("样品名称",
                EditDataDialog.DIALOG_TYPE_SAMPLENAME, null);
    }

    /**
     * 检测人员
     *
     * @param v
     */
    public void onInspector(View v) {
        editDataDialog_i.showDialog("检测人员",
                EditDataDialog.DIALOG_TYPE_INSPECTOR, new DialogSelectAdapter.OnItemClick() {
                    @Override
                    public void onItemClick(FiltrateModel data, int oldPosition, int newPosition) {
                        if (data != null) {
                            Global.inspector = data.getName();
                        }
                        Log.d("onInspector", "" + data.getName());
                    }
                });
    }


    /**
     * 商品管理
     *
     * @param v
     */
    public void onSampleSetting(View v) {
        if (editSampleDialog == null) {
            editSampleDialog = new EditSampleDialog(this);
        }
        editSampleDialog.showDilaog(new EditSampleDialog.OnSampleSave() {
            @Override
            public void onSampleSave(String code, String name, String type) {
                new SampleName().save(new SampleName(name,type,code));
                Toast.makeText(SystemSettingActivity2.this, "保存成功", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 检测
     *
     * @param v
     */
    public void onCheck(View v) {
        if (printSettingDialog == null) {
            printSettingDialog = new PrintSettingDialog(this);
        }
        printSettingDialog.showDialog("检测", prints_check, print_check);
        printSettingDialog.setOnPrintDataSave(new PrintSettingDialog.OnPrintDataSave() {
            @Override
            public void OnPrintDataSave(List<Print> prints, boolean isZt) {
                sp.edit().putBoolean(SPResource.KEY_PRINT_CHECK, isZt).commit();
                spUtils.setDataList(SPResource.KEY_PRINT_CHECK_DATA, prints);
                print_check = isZt;
                prints_check.clear();
                prints_check.addAll(prints);
                Log.d("saveData", "isZt=" + isZt + "saveData=" + prints_check.toString() + "prints=" + prints);
            }
        });
    }


    /**
     * 数据管理
     *
     * @param v
     */
    public void onDataManager(View v) {
        if (printSettingDialog == null) {
            printSettingDialog = new PrintSettingDialog(this);
        }
        printSettingDialog.showDialog("数据管理", prints_data_manager, print_data_manager);
        printSettingDialog.setOnPrintDataSave(new PrintSettingDialog.OnPrintDataSave() {
            @Override
            public void OnPrintDataSave(List<Print> prints, boolean isZt) {
                sp.edit().putBoolean(SPResource.KEY_PRINT_DATA_MANAGER, isZt).commit();
                spUtils.setDataList(SPResource.KEY_PRINT_DATA_MANAGER_DATA, prints);
                print_data_manager = isZt;
                prints_data_manager.clear();
                prints_data_manager.addAll(prints);
                Log.d("saveData", "isZt=" + isZt + "saveData=" + prints_check.toString() + "prints=" + prints);
            }
        });
    }
    public void onEditProject(View v){
//        Intent startEditProject= new Intent(this,EditProjectActivity.class);
//        startActivity(startEditProject);
        startActivity(new Intent(this, ProjectActivity.class));
    }
    /**
     * 校准
     *
     * @param v
     */
    public void onCalibration(View v) {
        if (uploadingDialog == null) {
            uploadingDialog = new UploadingDialog(this);
        }
        uploadingDialog.showDialog("校准", "请确认是否进行校准", UploadingDialog.TYPE_SHOW);
        uploadingDialog.setOnConfirmListener(new UploadingDialog.OnConfirmListener() {
            @Override
            public void onConfirmPw(String pw) {
            }

            @Override
            public void onUploading() {
                if (!SerialUtils.COM3_SendData(Global.LightSame)) {
                    APPUtils.showToast(act, "校准失败");
                }else{
                    APPUtils.showToast(act, "校准成功，校准时间约10分钟，请耐心等待所有灯光点亮！",true);
                    showWaitDialog();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (waitDialog!=null){
                                waitDialog.dismiss();
                                APPUtils.showToast(act, "校准成功，校准已结束");
                            }
                        }
                    },1000*60*10);
                }
            }
        });
    }

    /**
     * 进出卡
     *
     * @param v
     */
    public void onOutOrIn(View v) {
        if (!SerialUtils.COM3_SendData(Global.OUT_IN_CARD_INSTRUCTION)) {
            APPUtils.showToast(this, "进出卡发送失败");
        }
    }

    /**
     * 上传设置
     *
     * @param v
     */
    public void onUpLoadingSetting(View v) {
        if (editURLDialog == null) {
            editURLDialog = new EditURLDialog(this);
        }
        editURLDialog.showDilaog(new EditURLDialog.OnUrlSave() {
            @Override
            public void onUrlSave(String url, String user, String pw) {
                Global.uploadUrl = url;
                Global.TESTING_UNIT_NAME = user;
                Global.TESTING_UNIT_NUMBER = pw;
                sp.edit().putString(SPResource.KEY_UPLOAD_URL, Global.uploadUrl).commit();
                sp.edit().putString(SPResource.KEY_UPLOAD_USERNAME, Global.TESTING_UNIT_NAME).commit();
                sp.edit().putString(SPResource.KEY_UPLOAD_PASSWORD, Global.TESTING_UNIT_NUMBER).commit();
                Toast.makeText(SystemSettingActivity2.this, "保存成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static final String SERVER_IP = "http://235i3h6502.iok.la:42915";//更新的网址
    public static final String SERVER_ADDRESS = SERVER_IP + "/apkfile/index.php";//软件更新包版本描述
    //    public static final String SERVER_ADDRESS = "http://www.vigrance.com";//软件更新包版本描述
    public static final String UPDATESOFTADDRESS = SERVER_IP + "/apkfile/update_pakage/app-release.apk";//软件更新包地址
//    public static final String UPDATESOFTADDRESS = "http://p.gdown.baidu.com/675c418a09f6597f1e79fbf40ac2ff07a4dbf2928daa3b765fa010548d01f1f55c45603df73c86d96db2b91a81a1fbc64dd4e14dc8d79310d343b352da26149a5a6fdf80ec29b0413dfd57541fe83eada1f3c50947e464e50a84fc90bc2405b9604da77613c143e4952484069cf5bbdf63554686b189f5fb7e4ea84ca9dad1f184a11d093ef8121b";

    /**
     * 软件升级
     *
     * @param v
     */
    public void onUpdate(View v) {
//        startActivity(new Intent(act, DebugActivity.class));
        showUpdateDialog("软件升级", null, UploadingDialog.TYPE_EDIT);

    }

    private void showUpdateDialog(String title, String msg, int typeEdit) {
        if (uploadingDialog == null) {
            uploadingDialog = new UploadingDialog(this);
        }
        uploadingDialog.showDialog(title, msg, typeEdit);
        uploadingDialog.setOnConfirmListener(new UploadingDialog.OnConfirmListener() {
            @Override
            public void onConfirmPw(String pw) {
                if (pw.equals("TM1111")) {
                    getUpdateMsg();
                } else {
                    Toast.makeText(SystemSettingActivity2.this, "密码错误", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onUploading() {
                downLoad();
            }
        });
    }

    private long downloadId = 0;
    public static final String Save_File = "TND_TM";
    public static final String Save_File_NAME = "TND_TM.apk";

    private void downLoad() {
        File file = Environment.getExternalStoragePublicDirectory(Save_File);
        File ff = new File(file, Save_File_NAME);
        if (ff.exists()) {
            ff.delete();
        }
        DownloadManager downloadManager = (DownloadManager)
                getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request =
                new DownloadManager.Request(Uri.parse(UPDATESOFTADDRESS));
        request.setDestinationInExternalPublicDir(Save_File,
                Save_File_NAME);
        request.setTitle(getResources().getString(R.string.app_name) + "更新下载中");
// request.setDescription("MeiLiShuo desc");
// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
// request.setMimeType("application/cn.trinea.download.file");
        downloadId = downloadManager.enqueue(request);
    }

    class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // get complete download id
            long completeDownloadId = intent.
                    getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            // to do here
            if (downloadId == completeDownloadId) {
                File file = Environment.getExternalStoragePublicDirectory(Save_File);
                String absPath = new File(file, Save_File_NAME).getAbsolutePath();
                install(absPath);
            }
        }
    }

    private void install(String filePath) {
        Log.i(TAG, "开始执行安装: " + filePath);
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.w(TAG, "版本大于 N ，开始使用 fileProvider 进行安装");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(
                    SystemSettingActivity2.this
                    , "com.tnd.multifuction.fileprovider"
                    , apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            Log.w(TAG, "正常进行安装");
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    private CompleteReceiver completeReceiver;

    private void getUpdateMsg() {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("action", "checkNewestVersion")
                .build();
        Request request = new Request.Builder().url(SERVER_ADDRESS)
                .post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SystemSettingActivity2.this,
                                "获取软件升级信息失败", Toast.LENGTH_SHORT).show();
                        Log.d("getUpdateMsg", "onFailure=" + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
//                String result = "[{\"id\":\"1\",\"verName\":\"1.2.1\",\"verCode\":\"19\"}]";
                Log.d("getUpdateMsg", "onSuccess=" + result);
                VersionInfo versionInfo = parseUpdateResult(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (versionInfo != null && versionInfo.getVerCode() != null
                                && !versionInfo.getVerCode().equals("")) {
                            if (Integer.valueOf(versionInfo.getVerCode()) >
                                    ToolUtils.getLocalVersion(SystemSettingActivity2.this)) {

                                showUpdateDialog("软件升级",
                                        "当前版本为" + ToolUtils.getLocalVersionName(SystemSettingActivity2.this) +
                                                ",最新版本为" + versionInfo.getVerName() + ",是否进行升级？"
                                        , UploadingDialog.TYPE_SHOW);
                            } else {
                                showUpdateDialog("软件升级", "当前已经是最新版本",
                                        UploadingDialog.TYPE_SHOW);
                            }
                        } else {
                            Toast.makeText(SystemSettingActivity2.this,
                                    "获取软件升级信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }


        });
    }

    private VersionInfo parseUpdateResult(String result) {
        VersionInfo versionInfo = new VersionInfo();
        if (result == null || result.equals("") || result.equals("[]")
                || result.equals("{}")) {
            return versionInfo;
        }
        try {
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                versionInfo.setId(jsonObject.getString("id"));
                versionInfo.setVerName(jsonObject.getString("verName"));
                versionInfo.setVerCode(jsonObject.getString("verCode"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return versionInfo;
    }

    private void showIdSettingDialog() {

        final EditText et = new EditText(this);
        et.setText(Global.device_id);
        new AlertDialog.Builder(this)
                .setView(et)
                .setMessage("请设置设备ID")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sp.edit().putString(SPResource.KEY_DEVICE_ID, et.getText().toString()).apply();
                        Global.device_id = et.getText().toString();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void showUploadSettingDialog() {

        View inflate = View.inflate(this, R.layout.dialog_upload_setting, null);
        final EditText et = inflate.findViewById(R.id.et_upload_url);
        final RadioButton rbAutoUpload = inflate.findViewById(R.id.rb_auto_upload);
        final RadioButton rbManualUpload = inflate.findViewById(R.id.rb_manual_upload);
        if (Global.uploadModel == 1) {
            rbAutoUpload.setChecked(true);
        } else {
            rbManualUpload.setChecked(true);
        }
        et.setText(Global.uploadUrl);
        new AlertDialog.Builder(this)
                .setView(inflate)
                .setTitle("上传设置")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(SPResource.KEY_UPLOAD_URL, et.getText().toString());
                        if (rbAutoUpload.isChecked()) {
                            editor.putInt(SPResource.KEY_UPLOAD_MODE, 1).apply();
                            Global.uploadModel = 1;
                        } else {
                            editor.putInt(SPResource.KEY_UPLOAD_MODE, 2).apply();
                            Global.uploadModel = 2;
                        }
                        Global.uploadUrl = et.getText().toString();
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .create()
                .show();
    }

    private void showDebugPwdDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText etPwd = new EditText(this);
//        et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etPwd.setHint("请输入密码");
        builder.setView(etPwd);
        builder.setTitle("密码")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pwd = etPwd.getText().toString();
                        if (TextUtils.isEmpty(pwd) || !"TM1111".equals(pwd)) {
                            APPUtils.showToast(act, "密码错误");
                        } else {
                            showDebugInfoDialog();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }

    private void showDebugInfoDialog() {

        View view = View.inflate(act, R.layout.dialog_debug_info, null);
        final EditText etcardWarmTime = view.findViewById(R.id.et_card_test_time);
        final EditText etfenguangReactionTime = view.findViewById(R.id.et_fenguang_test_time);
        final EditText etReactionTime = view.findViewById(R.id.et_card_reaction_time);
        etcardWarmTime.setText(Global.cardWarmTime + "");
        etfenguangReactionTime.setText(Global.fenguangReactionTime + "");
        etReactionTime.setText(Global.cardReactionTime + "");
        Button btnSetDefaultSetting = view.findViewById(R.id.btn_set_default_setting);
        Button btnFit = view.findViewById(R.id.btn_fit);
        btnFit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SerialUtils.COM3_SendData(Global.FIT_LIGHT)) {
                    APPUtils.showToast(act, "校准失败");
                }
            }
        });
        btnSetDefaultSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CheckResult().deleteAll(CheckResult.class);
                new Project().deleteAll(Project.class);
                Global.cardWarmTime = 3 * 60;
                Global.fenguangReactionTime = 3 * 60;
                Global.cardReactionTime = 10 * 60;
            }
        });
        Button btnSave = view.findViewById(R.id.btn_save);
        Button btnOpenDebug = view.findViewById(R.id.btn_open_debug);

        final Dialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.getWindow().setWindowAnimations(R.style.DialogAnim);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etcardWarmTime.getText().toString().trim())
                        || TextUtils.isEmpty(etReactionTime.getText().toString().trim())) {
                    APPUtils.showToast(act, "数据不允许为空");
                    return;
                }
                int cardWarmTime, cardReactionTime, fenguangReactionTime, limitValue;
                try {
                    cardWarmTime = Integer.parseInt(etcardWarmTime.getText().toString().trim());
                    cardReactionTime = Integer.parseInt(etReactionTime.getText().toString().trim());
                    fenguangReactionTime = Integer.parseInt(etfenguangReactionTime.getText().toString().trim());
                    if (cardWarmTime < 30 || cardWarmTime > 180) {
                        APPUtils.showToast(act, "卡片检测时间必须大于30且小于180s");
                        return;
                    }
                    if (fenguangReactionTime < 10 || fenguangReactionTime > 180) {
                        APPUtils.showToast(act, "分光检测时间必须大于10且小于180s");
                        return;
                    }
                    if (cardReactionTime < 10 || cardReactionTime > 600) {
                        APPUtils.showToast(act, "卡片反应时间必须大于10且小于600s");
                        return;
                    }
                    Global.fenguangReactionTime = fenguangReactionTime;
                    Global.cardWarmTime = cardWarmTime;
                    Global.cardReactionTime = cardReactionTime;
                    SharedPreferences.Editor editor = getSharedPreferences(SPResource.FILE_NAME, MODE_PRIVATE).edit();
                    if (Global.DEBUG) {
                        editor.putInt(SPResource.KEY_CARD_REACTION_TIME, cardReactionTime).apply();
                        editor.putInt(SPResource.KEY_CARD_WARM_TIME, cardWarmTime).apply();
                        editor.putInt(SPResource.KEY_FENGUANG_REACTION_TIME, fenguangReactionTime).apply();
                    }

//                    editor.putInt(SPResource.KEY_CARD_TEST_LIMIT_VALUE, Global.limitValue).apply();
                    APPUtils.showToast(act, "保存成功");
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    APPUtils.showToast(act, "请输入整数");
                }
            }
        });
        btnOpenDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, DebugActivity.class));
            }
        });
        dialog.show();

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    public abstract class AutoSaveTextWatcher implements TextWatcher {
        private EditText et;
        private boolean isLimit;
        private String containStr;//要替换补充的 null为不需要

        public AutoSaveTextWatcher(EditText editText, String containStr) {
            this.et = editText;
            this.containStr = containStr;
        }

        public AutoSaveTextWatcher(EditText editText, boolean isLimit, String containStr) {
            this.et = editText;
            this.isLimit = isLimit;
            this.containStr = containStr;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        boolean isDel = false;
        boolean isContainStr = false;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("onTextChanged", "s=" + s.toString() + "start=" + start
                    + "before=" + before + "count=" + count);
//            if (count == 0) {
//                isDel = false;
//                if (start == s.length()) {//删除最后一个字符
//                    if (containStr != null) {
//                        isContainStr = true;
//                    } else {
//                        isContainStr = false;
//                    }
//                }
//            } else {
//                isDel = true;
//            }

        }

        @Override
        public void afterTextChanged(Editable s) {
//            if (s.toString() == null || s.toString().trim().equals("")) {
//                if (isLimit) {
//                    et.removeTextChangedListener(this);
//                    et.setText(ToolUtils.replenish("0", containStr));
//                    et.addTextChangedListener(this);
//                    String str = ToolUtils.replenish("0", containStr);
//                    et.setSelection(str.length());
//                    Save(str);
//                }
// else {
//                    String str = ToolUtils.replenish(s.toString().trim(), containStr);
//                    et.removeTextChangedListener(this);
//                    et.setText(str);
//                    et.addTextChangedListener(this);
//                    Save(str);
//                    et.setSelection(str.length());
//                }
//            } else {
//                String str = ToolUtils.replenish(s.toString().trim(), containStr);
//                et.removeTextChangedListener(this);
//                et.setText(str);
//                et.addTextChangedListener(this);
//                Save(str);
//                et.setSelection(str.length());
//            }
            if (s.toString() == null || s.toString().trim().equals("")) {
                if (isLimit) {
                    String str = "0";
                    et.removeTextChangedListener(this);
                    et.setText(str);
                    et.addTextChangedListener(this);
                    et.setSelection(str.length());
                    Save(str);
                } else {
                    String str = s.toString().trim();
                    et.removeTextChangedListener(this);
                    et.setText(str);
                    et.addTextChangedListener(this);
                    Save(str);
                    et.setSelection(str.length());
                }
            } else {
                String str = s.toString().trim();
                et.removeTextChangedListener(this);
                et.setText(str);
                et.addTextChangedListener(this);
                Save(str);
                et.setSelection(str.length());
            }
            et.setHint(containStr == null ? "" : getCC(containStr));
        }

        abstract void Save(String str);
    }

    public String getCC(String str) {
        String ss = "";
        if (str == null) {
            return ss;
        }
        for (int i = 0; i < str.length(); i++) {
            ss += " ";
        }
        return ss;
    }
}
