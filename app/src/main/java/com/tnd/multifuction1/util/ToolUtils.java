package com.tnd.multifuction1.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.DatePicker;

import com.tnd.multifuction1.db.DbHelper;
import com.tnd.multifuction1.model.BCheckOrg;
import com.tnd.multifuction1.model.CheckOrg;
import com.tnd.multifuction1.model.CheckResult;
import com.tnd.multifuction1.model.Print;
import com.tnd.multifuction1.model.Project;
import com.tnd.multifuction1.model.SampleSource;
import com.tnd.multifuction1.resource.SPResource;

import org.json.JSONArray;
import org.json.JSONObject;

public class ToolUtils {
    public final static int upload_success = 500;
    public final static int upload_fail = 501;
    public final static int countdown_finish = 502;
    public final static int compare_fail = 503;
    public final static int test_fail = 504;
    public final static int update_countdown = 505;
    public final static int compare_success = 506;
    public final static int test_success = 507;
    public final static int testing = 508;
    private static final String TAG = "ToolUtils";

    // long转换为Date类型
    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    /**
     * @param strTime
     * @param formatType
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        try {
            date = formatter.parse(strTime);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    // date类型转换为String类型
    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    public static String long2String(long time, String pattern) {

        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    public static int[] byte2Int(byte[] data) {

        int[] result = new int[data.length / 2];

        int value;
        int extra = 0;
        if (data.length % 2 != 0) {
            extra = 2;
        }
        for (int i = 0; i < data.length - extra; i += 2) {
            int int1 = (data[i] & 0xff) << 8;
            value = int1 + (data[i + 1] & 0xff);
            result[i / 2] = value;
        }

        return result;
    }

    public static String getSPValue(String key, Context context) {

        try {
            SharedPreferences sp = context.getSharedPreferences(SPResource.FILE_NAME, Context.MODE_PRIVATE);
            String value = null;
            value = sp.getString(key, "");

            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }


    /**
     * 获取wifi网络连接状态
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {

        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据输入流返回一个字符串
     *
     * @param is
     * @return
     * @throws Exception
     */
    public static String getStringFromInputStream(InputStream is) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len = -1;
        while ((len = is.read(buff)) != -1) {
            baos.write(buff, 0, len);
        }
        is.close();
        String html = baos.toString();
        baos.close();

        return html;
    }

    public static byte[] assemblePrintData(CheckResult result) {

        return assemblePrintData(Collections.singletonList(result));
    }


    /**
     * 卡片检测打印格式
     *
     * @param
     * @return
     */
    public static byte[] assemblePrintCheck(List<CheckResult> resultList,
                                            Context context) {
        if (context == null) {
            return null;
        }
        SharedPreferences sp = context.getSharedPreferences(SPResource.FILE_NAME, Context.MODE_PRIVATE);
        SPUtils spUtils = new SPUtils(sp);
        List<Print> prints = spUtils.getDataList(SPResource.KEY_PRINT_CHECK_DATA);
        boolean isZt = sp.getBoolean(SPResource.KEY_PRINT_CHECK, true);

        return assemblePrintCheck(resultList, isZt, prints);
    }

    public static byte[] assemblePrintCheck(List<CheckResult> resultList,
                                            boolean isZT, List<Print> prints) {
        Log.d("assemblePrintData", "isZT=" + isZT + "resultList="
                + resultList + "prints=" + prints);
        StringBuilder sb = new StringBuilder("\n\n\n");
        boolean isPrint_sampleNum = false,
                isPrint_weight = false,
                isPrint_checkp = false,
                isPrint_checkedOrganization = false,
                isPrint_bcheckedOrganization = false,
                isPrint_sampleSource = false,
                isPrint_standardValue = false,
                isPrint_testStandard = false;

        if (isZT) {//逐条打印
            for (int i = 0; i < prints.size(); i++) {
                Print print = prints.get(i);
                if (print.isSelectMultiple || print.isRequired) {
                    if (print.p_name.equals("样品编号")) {
                        isPrint_sampleNum = true;//样品编号
                    } else if (print.p_name.equals("被检测单位")) {
                        isPrint_bcheckedOrganization = true;//被检测单位
                    } else if (print.p_name.equals("重量")) {
                        isPrint_weight = true;//重量
                    } else if (print.p_name.equals("商品来源")) {
                        isPrint_sampleSource = true;//商品来源
                    } else if (print.p_name.equals("限量标准")) {
                        isPrint_standardValue = true;//限量值
                    } else if (print.p_name.equals("限量标准")) {
                        isPrint_testStandard = true;//限量标准
                    } else if (print.p_name.equals("检测人员")) {
                        isPrint_checkp = true;//检测人员
                    } else if (print.p_name.equals("检测单位")) {
                        isPrint_checkedOrganization = true;//检测单位
                    }
                }
            }
            sb.append("\n\n\n");
            for (int i = 0; i < resultList.size(); i++) {
                CheckResult result = resultList.get(i);
                Log.d(TAG, "result=" + result.toString());
                sb.append("\n\n\n");
                if (isPrint_sampleNum) {
                    sb.append("样品编号:");
                    sb.append(result.sampleNum + "\n");
                }
                sb.append("检测时间:");
                sb.append(ToolUtils.long2String(result.testTime,
                        "yyyy-MM-dd HH:mm:ss") + "\n");
                sb.append("样品名称:");
                sb.append(result.sampleName + "\n");
                if (isPrint_weight) {
                    sb.append("重量/Kg:");
                    sb.append(result.weight + "\n");
                }
                sb.append("检测项目:"+ "\n");
                sb.append(result.projectName + "\n");
                if ("有机磷和氨基甲酸酯类农药".equals(result.projectName)) {
                    sb.append("抑制率:");
                } else {
                    sb.append("检测值:");
                }
                sb.append(result.testValue + "\n");

                if (isPrint_standardValue) {
                    sb.append("限量标准:");
                    sb.append(result.xlz + "\n");
                }
                sb.append("判定结果:");
                sb.append(result.resultJudge + "\n");

                if (isPrint_bcheckedOrganization) {
                    sb.append("被检单位:");
                    sb.append(result.bcheckedOrganization + "\n");
                }
                if (isPrint_sampleSource) {
                    sb.append("商品来源:");
                    sb.append(result.sampleSource + "\n");
                }
                if (isPrint_checkedOrganization) {
                    sb.append("检测单位:");
                    sb.append(result.checkedOrganization + "\n");
                }
                if (isPrint_checkp) {
                    sb.append("检测人员:");
                    sb.append(result.checker + "\n");
                }
                sb.append("通道号:");
                sb.append(result.channel + "\n");
                sb.append("\n\n");

//                if (isPrint_testStandard) {
//                    sb.append("限量标准:");
//                    sb.append(result.testStandard + "\n");
//                }

            }
            sb.append("\n\n\n");
        } else {//合并
            for (int i = 0; i < prints.size(); i++) {
                Print print = prints.get(i);
                if (print.isSelectMerge || print.isRequired) {
                    if (print.p_name.equals("限量标准")) {
                        isPrint_standardValue = true;//限量标准
                    } else if (print.p_name.equals("限量标准")) {
                        isPrint_testStandard = true;//限量标准
                    } else if (print.p_name.equals("检测人员")) {
                        isPrint_checkp = true;//检测人员
                    } else if (print.p_name.equals("检测单位")) {
                        isPrint_checkedOrganization = true;//检测单位
                    }
                }
            }
            sb.append("\n\n\n");
            sb.append(merge("检测", 15) + " "
                    + ToolUtils.long2String(resultList.get(0).testTime,
                    "yyyy-MM-dd") + "\n\n");
            if (resultList.get(0).projectName.equals("有机磷和氨基甲酸酯类农药")) {
                sb.append("通道号  " + "" + "" + "抑制率 " + "" + "判定结果 " +
                        "样品名称 " + "" + "\n");
            } else {
                sb.append("通道号  " + "" + "" + "检测值 " + "" + "判定结果 " +
                        "样品名称 " + "\n");
            }
            for (int i = 0; i < resultList.size(); i++) {
                CheckResult result = resultList.get(i);
//                sb.append("通道号:");
                sb.append(merge(result.channel, 8));

//                sb.append("检测值:");
                sb.append(merge(result.testValue, 8));

//                sb.append("判定结果:");
                sb.append(merge(result.resultJudge, 8));

                //                sb.append("样品名称:");
                sb.append(result.sampleName);

                sb.append("\n\n");
            }
            if (isPrint_standardValue) {
                sb.append("限量标准:" + "");
                sb.append(resultList.get(0).xlz + "\n");
            }
//            if (isPrint_testStandard) {
//                sb.append("限量标准:");
//                sb.append(resultList.get(0).testStandard + "\n");
//            }
            sb.append("\n\n\n");
        }
        return sb.toString().getBytes(Charset.forName("GBK"));
    }


    public static void printData(List<CheckResult> resultList, Context context) {

        SharedPreferences sp = context.getSharedPreferences(SPResource.FILE_NAME, Context.MODE_PRIVATE);
        SPUtils spUtils = new SPUtils(sp);
        List<Print> prints = spUtils.getDataList(SPResource.KEY_PRINT_CHECK_DATA);
        boolean isZt = sp.getBoolean(SPResource.KEY_PRINT_CHECK, true);
        boolean isPrintQrCode= sp.getBoolean("isPrintQrCode",true);
        boolean isPrint_sampleNum = false,
                isPrint_weight = false,
                isPrint_checkp = false,
                isPrint_checkedOrganization = false,
                isPrint_bcheckedOrganization = false,
                isPrint_sampleSource = false,
                isPrint_standardValue = false,
                isPrint_testStandard = false;

        if (isZt) {//逐条打印
            for (int i = 0; i < prints.size(); i++) {
                Print print = prints.get(i);
                if (print.isSelectMultiple || print.isRequired) {
                    if (print.p_name.equals("样品编号")) {
                        isPrint_sampleNum = true;//样品编号
                    } else if (print.p_name.equals("被检测单位")) {
                        isPrint_bcheckedOrganization = true;//被检测单位
                    } else if (print.p_name.equals("重量")) {
                        isPrint_weight = true;//重量
                    } else if (print.p_name.equals("商品来源")) {
                        isPrint_sampleSource = true;//商品来源
                    } else if (print.p_name.equals("限量标准")) {
                        isPrint_standardValue = true;//限量值
                    } else if (print.p_name.equals("限量标准")) {
                        isPrint_testStandard = true;//限量标准
                    } else if (print.p_name.equals("检测人员")) {
                        isPrint_checkp = true;//检测人员
                    } else if (print.p_name.equals("检测单位")) {
                        isPrint_checkedOrganization = true;//检测单位
                    }
                }
            }
            if(isPrintQrCode){
                for (int i = 0; i < resultList.size(); i++) {

                    CheckResult result = resultList.get(i);
                    Log.d(TAG, "result=" + result.toString());


                    SerialUtils.COM4_SendData(BrightCommandM.t1b63(0));
                    SerialUtils.COM4_SendData(BrightCommandM.t1b61(0));
                    SerialUtils.COM4_SendData("\n".getBytes());

                    SerialUtils.COM4_SendData(("检测时间:").getBytes(Charset.forName("GB2312")));
                    SerialUtils.COM4_SendData((ToolUtils.long2String(result.testTime,
                            "yyyy-MM-dd HH:mm:ss") + "\n").getBytes(Charset.forName("GB2312")));

                    SerialUtils.COM4_SendData(("样品名称:").getBytes(Charset.forName("GB2312")));
                    SerialUtils.COM4_SendData((result.sampleName + "\n").getBytes(Charset.forName("GB2312")));
                    if("有机磷和氨基甲酸酯类农药".equals(result.projectName)){
                        SerialUtils.COM4_SendData(("检测项目:" + "\n").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.projectName + "\n").getBytes(Charset.forName("GB2312")));
                    }else{
                        SerialUtils.COM4_SendData(("检测项目:").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.projectName + "\n").getBytes(Charset.forName("GB2312")));
                    }
                    List<Project> projectList =new ArrayList<>();
                    Project projectBean = new Project();
                    projectList = projectBean.findAll();
                    String unit="";
                    for(int j =0 ; j<projectList.size(); j++){
                        if(result.projectName.equals(projectList.get(j).projectName)){
                            unit=projectList.get(j).unit;
                            break;
                        }
                    }
                    if ("有机磷和氨基甲酸酯类农药".equals(result.projectName)) {

                        SerialUtils.COM4_SendData(("抑制率:").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.testValue + "\n").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData(("单位:").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData(("%" + "\n").getBytes(Charset.forName("GB2312")));
                    } else {

                        SerialUtils.COM4_SendData(("检测值:").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.testValue + "\n").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData(("单位:").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((unit + "\n").getBytes(Charset.forName("GB2312")));
                    }

                    if (isPrint_standardValue) {
                        SerialUtils.COM4_SendData(("限量标准:").getBytes(Charset.forName("GB2312")));
                        if (result.xlz.contains("-1")) {

                            SerialUtils.COM4_SendData(("无标准\n").getBytes(Charset.forName("GB2312")));
                        } else if (result.xlz.equals("0.000")){

                            SerialUtils.COM4_SendData(("0\n").getBytes(Charset.forName("GB2312")));
                        } else {

                            SerialUtils.COM4_SendData((result.xlz + "\n").getBytes(Charset.forName("GB2312")));
                        }
                    }

                    if (isPrint_weight) {

                        SerialUtils.COM4_SendData(("重量/Kg:").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.weight + "\n").getBytes(Charset.forName("GB2312")));

                    }

                    if (isPrint_sampleNum) {
                        SerialUtils.COM4_SendData(("样品编号:").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.sampleNum + "\n").getBytes(Charset.forName("GB2312")));
                    }

                    if (isPrint_bcheckedOrganization) {
                        SerialUtils.COM4_SendData(("被检单位:").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.bcheckedOrganization + "\n").getBytes(Charset.forName("GB2312")));
                    }

                    if (isPrint_sampleSource) {
                        String sampleSource = result.sampleSource;
                        if (sampleSource.length() > 10) {
                            String substring1 = sampleSource.substring(10);
                            String substring2 = sampleSource.substring(0, 10);

                            SerialUtils.COM4_SendData((substring1 + "\n").getBytes(Charset.forName("GB2312")));
                            SerialUtils.COM4_SendData(("商品来源:" + substring2 + "\n").getBytes(Charset.forName("GB2312")));

                        } else {
                            SerialUtils.COM4_SendData(("商品来源:" + result.sampleSource + "\n").getBytes(Charset.forName("GB2312")));
                        }
                    }

                    SerialUtils.COM4_SendData(("判定结果:").getBytes(Charset.forName("GB2312")));

                    SerialUtils.COM4_SendData((result.resultJudge + "\n").getBytes(Charset.forName("GB2312")));

                    if (isPrint_checkedOrganization) {

                        SerialUtils.COM4_SendData("检测单位:".getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.checkedOrganization + "\n").getBytes(Charset.forName("GB2312")));
                    }

                    if (isPrint_checkp) {
                        SerialUtils.COM4_SendData("检测人员:".getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.checker + "\n").getBytes(Charset.forName("GB2312")));
                    }

                    SerialUtils.COM4_SendData("通道号:".getBytes(Charset.forName("GB2312")));
                    SerialUtils.COM4_SendData((result.channel + "\n").getBytes(Charset.forName("GB2312")));



                        // 增加打印二维码
                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                        SerialUtils.COM4_SendData(BrightCommandM.t0A());
                        SerialUtils.COM4_SendData(BrightCommandM.t1b61(1));
                        SerialUtils.COM4_SendData(BrightCommandM.t1d77(2));
                        SerialUtils.COM4_SendData(BrightCommandM.t1d28Qr(getData(result, context)));
                        SerialUtils.COM4_SendData(BrightCommandM.t1d77(2));
                        SerialUtils.COM4_SendData(BrightCommandM.t1b61(1));
                    /*SerialUtils.COM4_SendData(BrightCommandM.t0A());
                    SerialUtils.COM4_SendData(BrightCommandM.t0A());
                    SerialUtils.COM4_SendData(BrightCommandM.t0A());*/
                    if (i < resultList.size()-1) {
                        SerialUtils.COM4_SendData("\n\n".getBytes());
                    }else if(i == resultList.size()-1){
                        SerialUtils.COM4_SendData("\n\n\n\n".getBytes());
                    }
                }
            }else{
                for (int i = 0; i < resultList.size(); i++) {

                    CheckResult result = resultList.get(i);
                    Log.d(TAG, "result=" + result.toString());

                    SerialUtils.COM4_SendData(BrightCommandM.t1b63(0));
                    SerialUtils.COM4_SendData(BrightCommandM.t1b61(0));
                    SerialUtils.COM4_SendData("\n".getBytes());

                    SerialUtils.COM4_SendData(("检测时间:").getBytes(Charset.forName("GB2312")));
                    SerialUtils.COM4_SendData((ToolUtils.long2String(result.testTime,
                            "yyyy-MM-dd HH:mm:ss") + "\n").getBytes(Charset.forName("GB2312")));

                    SerialUtils.COM4_SendData(("样品名称:").getBytes(Charset.forName("GB2312")));
                    SerialUtils.COM4_SendData((result.sampleName + "\n").getBytes(Charset.forName("GB2312")));
                    if("有机磷和氨基甲酸酯类农药".equals(result.projectName)){
                        SerialUtils.COM4_SendData(("检测项目:" + "\n").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.projectName + "\n").getBytes(Charset.forName("GB2312")));
                    }else{
                        SerialUtils.COM4_SendData(("检测项目:").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.projectName + "\n").getBytes(Charset.forName("GB2312")));
                    }
                    List<Project> projectList =new ArrayList<>();
                    Project projectBean = new Project();
                    projectList = projectBean.findAll();
                    String unit="";
                    for(int j =0 ; j<projectList.size(); j++){
                        if(result.projectName.equals(projectList.get(j).projectName)){
                            unit=projectList.get(j).unit;
                            break;
                        }
                    }
                    if ("有机磷和氨基甲酸酯类农药".equals(result.projectName)) {

                        SerialUtils.COM4_SendData(("抑制率:").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.testValue + "\n").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData(("单位:").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData(("%" + "\n").getBytes(Charset.forName("GB2312")));
                    } else {

                        SerialUtils.COM4_SendData(("检测值:").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.testValue + "\n").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData(("单位:").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((unit + "\n").getBytes(Charset.forName("GB2312")));
                    }

                    if (isPrint_standardValue) {
                        SerialUtils.COM4_SendData(("限量标准:").getBytes(Charset.forName("GB2312")));
                        if (result.xlz.contains("-1")) {

                            SerialUtils.COM4_SendData(("无标准\n").getBytes(Charset.forName("GB2312")));
                        } else if (result.xlz.equals("0.000")){

                            SerialUtils.COM4_SendData(("0\n").getBytes(Charset.forName("GB2312")));
                        } else {

                            SerialUtils.COM4_SendData((result.xlz + "\n").getBytes(Charset.forName("GB2312")));
                        }
                    }

                    if (isPrint_weight) {

                        SerialUtils.COM4_SendData(("重量/Kg:").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.weight + "\n").getBytes(Charset.forName("GB2312")));

                    }

                    if (isPrint_sampleNum) {
                        SerialUtils.COM4_SendData(("样品编号:").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.sampleNum + "\n").getBytes(Charset.forName("GB2312")));
                    }

                    if (isPrint_bcheckedOrganization) {
                        SerialUtils.COM4_SendData(("被检单位:").getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.bcheckedOrganization + "\n").getBytes(Charset.forName("GB2312")));
                    }

                    if (isPrint_sampleSource) {
                        String sampleSource = result.sampleSource;
                        if (sampleSource.length() > 10) {
                            String substring1 = sampleSource.substring(10);
                            String substring2 = sampleSource.substring(0, 10);

                            SerialUtils.COM4_SendData((substring1 + "\n").getBytes(Charset.forName("GB2312")));
                            SerialUtils.COM4_SendData(("商品来源:" + substring2 + "\n").getBytes(Charset.forName("GB2312")));

                        } else {
                            SerialUtils.COM4_SendData(("商品来源:" + result.sampleSource + "\n").getBytes(Charset.forName("GB2312")));
                        }
                    }

                    SerialUtils.COM4_SendData(("判定结果:").getBytes(Charset.forName("GB2312")));

                    SerialUtils.COM4_SendData((result.resultJudge + "\n").getBytes(Charset.forName("GB2312")));

                    if (isPrint_checkedOrganization) {

                        SerialUtils.COM4_SendData("检测单位:".getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.checkedOrganization + "\n").getBytes(Charset.forName("GB2312")));
                    }

                    if (isPrint_checkp) {
                        SerialUtils.COM4_SendData("检测人员:".getBytes(Charset.forName("GB2312")));
                        SerialUtils.COM4_SendData((result.checker + "\n").getBytes(Charset.forName("GB2312")));
                    }

                    SerialUtils.COM4_SendData("通道号:".getBytes(Charset.forName("GB2312")));
                    SerialUtils.COM4_SendData((result.channel + "\n").getBytes(Charset.forName("GB2312")));

                    if (i < resultList.size()-1) {
                        SerialUtils.COM4_SendData("\n\n".getBytes());
                    }else if(i == resultList.size()-1){
                        SerialUtils.COM4_SendData("\n\n\n\n".getBytes());
                    }

                }
            }
            /*SerialUtils.COM4_SendData("\n".getBytes());*/


        } else {//合并
            for (int i = 0; i < prints.size(); i++) {
                Print print = prints.get(i);
                if (print.isSelectMerge || print.isRequired) {
                    if (print.p_name.equals("限量标准")) {
                        isPrint_standardValue = true;//限量标准
                    } else if (print.p_name.equals("限量标准")) {
                        isPrint_testStandard = true;//限量标准
                    } else if (print.p_name.equals("检测人员")) {
                        isPrint_checkp = true;//检测人员
                    } else if (print.p_name.equals("检测单位")) {
                        isPrint_checkedOrganization = true;//检测单位
                    }
                }
            }
            /*StringBuilder sb = new StringBuilder();

            sb.append(merge("检测项目：", 15) + " "
                    + resultList.get(0).projectName + "\n");
            sb.append(merge("检测时间：", 15) + " "
                    + ToolUtils.long2String(resultList.get(0).testTime,
                    "yyyy-MM-dd") + "\n");

            if (resultList.get(0).projectName.equals("农药残留")) {
                sb.append("通道号  " + "" + "" + "样品名称 " + "" + "判定结果 " +
                        "抑制率 " + "" + "\n");
            } else {
                sb.append("通道号  " + "" + "" + "样品名称 " + "" + "判定结果 " +
                        "检测值 " + "\n");
            }

            for (int i = 0; i < resultList.size(); i++) {
                CheckResult result = resultList.get(i);
//                sb.append("通道号:");
                sb.append(merge(result.channel, 8));

//               sb.append("样品名称:");
                sb.append(result.sampleName);

//                sb.append("检测值:");
                sb.append(merge(result.testValue, 8));

//                sb.append("判定结果:");
                sb.append(merge(result.resultJudge, 8));

                sb.append("\n\n");
            }
            if (isPrint_standardValue) {
                sb.append("限量标准:" + "");
                sb.append(resultList.get(0).xlz + "\n");
            }
//            if (isPrint_testStandard) {
//                sb.append("限量标准:");
//                sb.append(resultList.get(0).testStandard + "\n");
//            }

//            sb.append("\n\n\n");
*/
            SerialUtils.COM4_SendData(BrightCommandM.t1b63(0));
            SerialUtils.COM4_SendData(BrightCommandM.t1b61(0));
            SerialUtils.COM4_SendData("\n".getBytes());
            if("有机磷和氨基甲酸酯类农药".equals(resultList.get(0).projectName)){
                SerialUtils.COM4_SendData(("检测项目："+"\n").getBytes(Charset.forName("GB2312")));
                SerialUtils.COM4_SendData((resultList.get(0).projectName + "\n").getBytes(Charset.forName("GB2312")));
            }else{
                SerialUtils.COM4_SendData((merge("检测项目："+"\n", 8).getBytes(Charset.forName("GB2312"))));
                SerialUtils.COM4_SendData((resultList.get(0).projectName + "\n").getBytes(Charset.forName("GB2312")));
            }
            SerialUtils.COM4_SendData((merge("检测时间：", 15).getBytes(Charset.forName("GB2312"))));
            SerialUtils.COM4_SendData(ToolUtils.long2String(resultList.get(0).testTime, "yyyy-MM-dd").getBytes(Charset.forName("GB2312")));
            SerialUtils.COM4_SendData("\n".getBytes());
            if(resultList.get(0).projectName.equals("有机磷和氨基甲酸酯类农药")){
                SerialUtils.COM4_SendData((merge("通道号  " + "" + "" + "样品名称 " + "" + "抑制率 " +
                        "判定结果 " + "" + "\n", 15).getBytes(Charset.forName("GB2312"))));
            }else{
                SerialUtils.COM4_SendData((merge("通道号  " + "" + "" + "样品名称 " + "" + "检测值 " +
                        "判定结果 " + "\n", 15).getBytes(Charset.forName("GB2312"))));
            }
            for (int i = 0; i < resultList.size(); i++) {
                CheckResult result = resultList.get(i);
                SerialUtils.COM4_SendData(merge(result.channel,4).getBytes(Charset.forName("GB2312")));
                SerialUtils.COM4_SendData(merge(result.sampleName,10).getBytes(Charset.forName("GB2312")));
                SerialUtils.COM4_SendData(merge(result.testValue,8).getBytes(Charset.forName("GB2312")));
                SerialUtils.COM4_SendData(merge(result.resultJudge,8).getBytes(Charset.forName("GB2312")));
                SerialUtils.COM4_SendData("\n".getBytes());
            }

            /*if(isPrintQrCode){
                // 增加打印二维码
                SerialUtils.COM4_SendData(BrightCommandM.t0A());
                SerialUtils.COM4_SendData(BrightCommandM.t0A());
                SerialUtils.COM4_SendData(BrightCommandM.t1b61(1));
                SerialUtils.COM4_SendData(BrightCommandM.t1d77(2));
                SerialUtils.COM4_SendData(BrightCommandM.t1d28Qr(getData(result, context)));
                SerialUtils.COM4_SendData(BrightCommandM.t1d77(2));
                SerialUtils.COM4_SendData(BrightCommandM.t1b61(1));

                    *//*SerialUtils.COM4_SendData(BrightCommandM.t0A());
                    SerialUtils.COM4_SendData(BrightCommandM.t0A());
                    SerialUtils.COM4_SendData(BrightCommandM.t0A());*//*
            }*/


            SerialUtils.COM4_SendData("\n\n\n\n".getBytes());

        }
    }

    private static String getData(CheckResult result, Context context) {
        StringBuilder sb = new StringBuilder();
        if("有机磷和氨基甲酸酯类农药".equals(result.projectName)){
            sb.append("检测项目：");
            sb.append("\n" + result.projectName);
        }else{
            sb.append("检测项目：");
            sb.append(result.projectName);
        }
        sb.append("\n检测时间：" + ToolUtils.long2String(result.testTime,
                "yyyy-MM-dd HH:mm:ss") );
        sb.append("\n检测通道：" + result.channel);
        sb.append("\n样品名称：" + result.sampleName);

        sb.append("\n重量：" + result.weight + " kg");
        sb.append("\n样品编码：" + result.sampleNum);
        sb.append("\n检测单位：" + result.checkedOrganization);
        sb.append("\n被检单位：" + result.bcheckedOrganization);
        sb.append("\n商品来源:" + result.sampleSource);
        sb.append("\n检测人员：" + result.checker);
        if ("有机磷和氨基甲酸酯类农药".equals(result.projectName)) {
            sb.append("\n对照值：" + PreferencesUtils.getString(context, "Ac"));
            sb.append("\n抑制率：" + result.testValue);
        } else {
            sb.append("\n样品检测值：" + result.testValue);
        }
        sb.append("\n单位：" + result.unit);

        sb.append("\n结果判定：" + result.resultJudge);
        Log.d("resultPrint", sb.toString());
        return sb.toString();
    }

    public static byte[] assemblePrintData(List<CheckResult> resultList,
                                           Context context) {
        if (context == null) {
            return null;
        }
        SharedPreferences sp = context.getSharedPreferences(SPResource.FILE_NAME, Context.MODE_PRIVATE);
        SPUtils spUtils = new SPUtils(sp);
        List<Print> prints = spUtils.getDataList(SPResource.KEY_PRINT_DATA_MANAGER_DATA);
        boolean isZt = sp.getBoolean(SPResource.KEY_PRINT_DATA_MANAGER, true);

        return assemblePrintData(resultList, true, prints);
    }

    public static byte[] assemblePrintData(List<CheckResult> resultList,
                                           boolean isZT, List<Print> prints) {
//        StringBuilder sb = new StringBuilder("\n\n\n农药残留检测报告单\n\n\n");
        Log.d("assemblePrintData", "isZT=" + isZT + "resultList="
                + resultList + "prints=" + prints);
        StringBuilder sb = new StringBuilder("\n\n\n");
        boolean isPrint_sampleNum = false,
                isPrint_weight = false,
                isPrint_checkp = false,
                isPrint_checkedOrganization = false,
                isPrint_bcheckedOrganization = false,
                isPrint_sampleSource = false,
                isPrint_standardValue = false,
                isPrint_testStandard = false;

        if (isZT) {//逐条打印
            for (int i = 0; i < prints.size(); i++) {
                Print print = prints.get(i);
                if (print.isSelectMultiple || print.isRequired) {
                    if (print.p_name.equals("样品编号")) {
                        isPrint_sampleNum = true;//样品编号
                    } else if (print.p_name.equals("被检测单位")) {
                        isPrint_bcheckedOrganization = true;//被检测单位
                    } else if (print.p_name.equals("重量")) {
                        isPrint_weight = true;//重量
                    } else if (print.p_name.equals("商品来源")) {
                        isPrint_sampleSource = true;//商品来源
                    } else if (print.p_name.equals("限量标准")) {
                        isPrint_standardValue = true;//限量标准
                    } else if (print.p_name.equals("限量标准")) {
                        isPrint_testStandard = true;//限量标准
                    } else if (print.p_name.equals("检测人员")) {
                        isPrint_checkp = true;//检测人员
                    } else if (print.p_name.equals("检测单位")) {
                        isPrint_checkedOrganization = true;//检测单位
                    }
                }
            }
            for (int i = 0; i < resultList.size(); i++) {
                CheckResult result = resultList.get(i);
                sb.append("\n\n\n");

                if (isPrint_sampleNum) {
                    sb.append("样品编号:");
                    sb.append(result.sampleNum + "\n");
                }

                sb.append("检测时间:");
                sb.append(ToolUtils.long2String(result.testTime,
                        "yyyy-MM-dd HH:mm:ss") + "\n");

                sb.append("样品名称:");
                sb.append(result.sampleName + "\n");
                if (isPrint_weight) {
                    sb.append("重量/Kg:");
                    sb.append(result.weight + "\n");
                }
                if("有机磷和氨基甲酸酯类农药".equals(result.projectName)){
                    sb.append("检测项目:"+ "\n");
                    sb.append(result.projectName + "\n");
                }else{
                    sb.append("检测项目:");
                    sb.append(result.projectName + "\n");
                }
                if (result.projectName.equals("有机磷和氨基甲酸酯类农药")) {
                    sb.append("抑制率:");
                } else {
                    sb.append("检测值:");
                }
                sb.append(result.testValue + "\n");
                sb.append("单位:");
                sb.append(result.unit + "\n");
                if (isPrint_standardValue) {
                    sb.append("限量标准:");
                    sb.append(resultList.get(0).xlz + "\n");
                }
                if (isPrint_testStandard) {
                    sb.append("限量标准:");
                    sb.append(result.testStandard + "\n");
                }
                sb.append("判定结果:");
                sb.append(result.resultJudge + "\n");
                if (isPrint_bcheckedOrganization) {
                    sb.append("被检单位:");
                    sb.append(result.bcheckedOrganization + "\n");
                }
                if (isPrint_sampleSource) {
                    sb.append("商品来源:");
                    sb.append(result.sampleSource + "\n");
                }
                if (isPrint_checkedOrganization) {
                    sb.append("检测单位:");
                    sb.append(result.checkedOrganization + "\n");
                }
                if (isPrint_checkp) {
                    sb.append("检测人员:");
                    sb.append(result.checker + "\n");
                }

                sb.append("通道号:");
                sb.append(result.channel + "\n");
                sb.append("\n\n");
            }
            sb.append("\n\n\n");
        } else {//合并
            for (int i = 0; i < prints.size(); i++) {
                Print print = prints.get(i);
                if (print.isSelectMerge || print.isRequired) {
                    if (print.p_name.equals("限量标准")) {
                        isPrint_standardValue = true;//限量标准
                    } else if (print.p_name.equals("限量标准")) {
                        isPrint_testStandard = true;//限量标准
                    } else if (print.p_name.equals("检测人员")) {
                        isPrint_checkp = true;//检测人员
                    } else if (print.p_name.equals("检测单位")) {
                        isPrint_checkedOrganization = true;//检测单位
                    }
                }
            }
            sb.append("\n\n\n");
            sb.append(merge("检测", 15) + " "
                    + ToolUtils.long2String(resultList.get(0).testTime,
                    "yyyy-MM-dd") + "\n\n");
            if (resultList.get(0).projectName.equals("有机磷和氨基甲酸酯类农药")) {
                sb.append("通道号  " + "" + "" + "抑制率 " + "" + "判定结果 " +
                        "样品名称 " + "\n");
            } else {
                sb.append("通道号  " + "" + "" + "检测值 " + "" + "判定结果 " +
                        "样品名称 " + "\n");
            }
            for (int i = 0; i < resultList.size(); i++) {
                CheckResult result = resultList.get(i);

//                sb.append("通道号:");
                sb.append(merge(result.channel, 8));

//                sb.append("检测值:");
                sb.append(merge(result.testValue, 8));


//                sb.append("判定结果:");
                sb.append(merge(result.resultJudge, 8));

                sb.append(result.sampleName);

                sb.append("\n\n");
            }
            if (isPrint_checkp) {
                sb.append("检测人员:");
                sb.append(resultList.get(0).checker + "\n");
            }
            if (isPrint_checkedOrganization) {
                sb.append("检测单位:");
                sb.append(resultList.get(0).checkedOrganization + "\n");
            }
//            if (isPrint_standardValue) {
//                sb.append("限量标准:" + "");
//                sb.append(resultList.get(0).xlz + "\n");
//            }
            if (isPrint_testStandard) {
                sb.append("限量标准:");
                sb.append(resultList.get(0).testStandard + "\n");
            }
            sb.append("\n\n\n");
        }
        return sb.toString().getBytes(Charset.forName("GBK"));
    }

    /**
     * 获取数据长度
     *
     * @param msg
     * @return
     */
    @SuppressLint("NewApi")
    private static int getBytesLength(String msg) {
        if (msg == null) return "".getBytes(Charset.forName("GBK")).length;
        return msg.getBytes(Charset.forName("GBK")).length;
    }

    private static String merge(String channel, int length) {
        int cl = getBytesLength(channel);
        String str = "";
        if (channel == null) return str;
        str = channel;
        if (cl < length) {
            for (int i = 0; i < length - cl; i++) {
                str += " ";
            }
        }
        return str;
    }

    public static byte[] assemblePrintData(List<CheckResult> resultList) {
        StringBuilder sb = new StringBuilder("\n\n\n检测报告单\n\n\n");
        for (int i = 0; i < resultList.size(); i++) {
            CheckResult result = resultList.get(i);
            if("有机磷和氨基甲酸酯类农药".equals(result.projectName)){
                sb.append("检测项目:"+ "\n");
                sb.append(result.projectName + "\n");
            }else{
                sb.append("检测项目:");
                sb.append(result.projectName + "\n");
            }
            sb.append("检测通道:");
            sb.append(result.channel + "\n");
            sb.append("检测人员:");
            sb.append(result.checker + "\n");
            sb.append("临界值:");
            sb.append(result.xlz + "\n");
            sb.append("检测值:");
            sb.append(result.testValue + "\n");
            sb.append("单位:");
            sb.append(result.unit + "\n");
            sb.append("检测结果:");
            sb.append(result.resultJudge + "\n");
            sb.append("被检单位:");
            sb.append(result.checkedOrganization + "\n");
            sb.append("样品名称:");
            sb.append(result.sampleName + "\n");
            sb.append("样本编号:");
            sb.append(result.sampleNum + "\n");
            sb.append("商品来源:");
            sb.append(result.sampleSource + "\n");
            sb.append("限量标准:");
            sb.append(result.testStandard + "\n");
            sb.append("检测时间:");
            sb.append(ToolUtils.long2String(result.testTime, "yyyy-MM-dd HH:mm:ss") + "\n");
            sb.append("\n\n\n");
        }
        sb.append("\n\n\n");

        return sb.toString().getBytes(Charset.forName("GBK"));
    }

    //data={"id":733,"device_id":"8f7a9e45-f90f-4f52-bbfb-15159543e832",
    // "product_num":"716","item_weight":0,"item_id":1,"product_name":"",
    // "company_bjdw":"","data_value":12,"data_conclusion":"","data_dispose":"",
    // "create_time":"2017/5/25 9:59:01","data_image":"","channel":0}
    public static String assemblyUploadData(CheckResult result) {
        String ss = "";
        JSONObject json = new JSONObject();
        Log.d(TAG, "assemblyUploadData uploadUrl= " + Global.uploadUrl);
        if (TextUtils.isEmpty(Global.uploadUrl)) {
            return null;
        }
        try {
            String jg = result.resultJudge;
            int jcjg = 0;
            if (jg.equals("阴性")) {
                jg = "合格";
            } else if (jg.equals("阳性")){
                jg = "不合格";
            }else{
                if (!jg.equals("合格") && !jg.equals("不合格")) {
                    jg = "合格";
                }
            }
            if(jg.equals("合格")){
                jcjg = 1;
            }else{
                jcjg = 0;
            }
            String weight = result.weight;
            if (weight.isEmpty()) {
                weight = "1";
            }
            String jcz = result.testValue;
            /*if (!isNumericZidai(jcz)) {
                jcz = "0";
            }*/
            String cmd = tokenTest.generate("username="+Global.TESTING_UNIT_NAME+"&password="+Global.TESTING_UNIT_NUMBER);

            JSONArray jsonArray = new JSONArray();
            JSONObject item = new JSONObject();
            item.put("stall",result.sampleSource);
            item.put("alias",result.sampleName);
            item.put("jcxm",result.projectName);
            item.put("jcz",jcz);
            item.put("jcjg",jcjg);//0=阳性；1=阴性
            item.put("jcrq",ToolUtils.long2String(result.testTime, "yyyy-MM-dd HH:mm:ss"));
            item.put("weight",Double.parseDouble(weight));
            item.put("jcfs",2);//1=试剂条检测；2=快检仪检测；9=其他检测方式
            jsonArray.put(item);
            json.put("username", Global.TESTING_UNIT_NAME);
            json.put("cmd",cmd);
            json.put("deviceName", Global.ASSET_NAME);
            json.put("deviceCode", Global.ASSET_CODE);
            json.put("items", jsonArray);

            ss = json.toString();

            Log.d(TAG, "uploadData=" + ss);

            return ss;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
//    public static String assemblyUploadData(CheckResult result) {
//        String ss = "";
//        JSONObject json = new JSONObject();
//        Log.d(TAG, "assemblyUploadData uploadUrl= " + Global.uploadUrl);
//        if (TextUtils.isEmpty(Global.uploadUrl)) {
//            return null;
//        }
////        try {
////            json.put("count", "1");
////            json.put("jczbh", Global.TESTING_UNIT_NUMBER);
////            json.put("jcdw", Global.TESTING_UNIT_NAME);
////            JSONObject detail = new JSONObject();
////            detail.put("rwbh", "");
////            detail.put("bjdw", result.bcheckedOrganization + "");
////            detail.put("jcxm", result.projectName + "");
////            detail.put("jcdt", ToolUtils.dateToString(new Date(result.testTime),"yyyy-MM-dd HH:mm:ss")+ "");
////            detail.put("jcz", result.testValue + "");
////            detail.put("szdw", "%");
//////            detail.put("jgpd", result.resultJudge + "合格");
////            detail.put("jgpd",  result.resultJudge+"");
////            detail.put("ybbh", result.sampleNum + "");
////            detail.put("ybmc", result.sampleName + "");
////            detail.put("ybcd", result.sampleSource + "");
////            detail.put("xlbz", result.testStandard + "");
////            detail.put("sbbh", Global.device_id + "");
////            detail.put("xlz", result.xlz + "");
////            JSONArray jsonArray = new JSONArray();
////            jsonArray.put(detail);
////            json.put("details", jsonArray);
////            ss =  json.toString();
////            Log.d(TAG, "uploadData=" + ss);
////            return ss;
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//        try {
////            json.put("cmd", "3");
////            json.put("username", Global.TESTING_UNIT_NUMBER);
////            json.put("password", Global.TESTING_UNIT_NAME);
////            JSONObject detail = new JSONObject();
////            detail.put("spdm", result.id);
////            detail.put("spmc", result.sampleName + "");
////            detail.put("jcxm", result.projectName + "");
////            detail.put("xmmc", result.projectName);
////            detail.put("jcz", result.testValue + "");
////            detail.put("jcjg", result.resultJudge);
//////            detail.put("jgpd", result.resultJudge + "合格");
////            detail.put("jcrq", ToolUtils.dateToString(new Date(result.testTime), "yyyy-MM-dd HH:mm:ss") + "");
////            detail.put("twh", result.sampleNum + "");
////            detail.put("weight", result.weight + "");
////            detail.put("cljg", result.resultJudge + "");
////            JSONArray jsonArray = new JSONArray();
////            jsonArray.put(detail);
////            json.put("items", jsonArray);
////            ss = json.toString();
////            Log.d(TAG, "uploadData=" + ss);
//
//            String jg = result.resultJudge;
//            if (!jg.equals("合格") && !jg.equals("不合格")) {
//                jg = "合格";
//            }
//            if (jg.equals("阴性")) {
//                jg = "合格";
//            } else if (jg.equals("阳性")){
//                jg = "不合格";
//            }
//
//            String bjdw = result.bcheckedOrganization == null ? "" : result.bcheckedOrganization;
//            String ybcd = result.sampleSource == null ? "" : result.sampleSource;
//            String weight = result.weight == null ? "" : result.weight;
//            String jcz = result.testValue;
//            if (!isNumericZidai(jcz)) {
//                jcz = "0";
//            }
//
//            String jcz1 = isNumeric(result.testValue);
//
////data=有机氯%23苹果%23<0.1%23mg/kg%23合格%23GB2763-2019%23德州金锣肉制品有限公司%23%23STD-9000%232021-03-11 10:43:42%23e57ae626cc24176f%231.0mg/kg%23123%23%23|
////            ss=result.projectName+"%23"+result.sampleName+"%23"+jcz1+"%23"+"%25"+"%23"+jg+"%23"+result.testStandard+"%23"+bcheckedOrganization+"%23"+checkedOrganization+"%23"+Global.ASSET_NAME+"%23"+ToolUtils.dateToString(new Date(result.testTime), "yyyy-MM-dd HH:mm:ss")+"%23"+Global.ASSET_CODE+"%23"+"50"+"%23"+result.sampleNum+"%23"+sampleID+"%23"+"|";
//            ss = "{" + "cmd:'" + "3" + "'," + "username:'" + Global.TESTING_UNIT_NAME + "'," + "password:'" + Global.TESTING_UNIT_NUMBER + "'," + "items:[{"
//                    + "spdm:'" + DbHelper.getSampleNumber(result.sampleName).replace(" ", "") + "" + "'," + "spmc:'" + result.sampleName + "" + "'," + "jcxm:'" + result.projectName + "" + "',"
//                    + "xmmc:'" + "13" + "" + "'," + "jcz:'" + jcz + "" + "'," + "jcjg:'" + jg + "" + "',"
//                    + "jcrq:'" + ToolUtils.dateToString(new Date(result.testTime), "yyyy-MM-dd HH:mm:ss") + "" + "'," + "twh:'" + (result.twh == null ? "" : result.twh) + "" + "',"
//                    + "bjdw:'" + bjdw + "'," + "ybcd:'" + ybcd + "'," + "weight:'" + weight + "'," + "cljg:'" + jg + ""
//                    + "'}]" + "}";
////            ss = "{" + "cmd:'" + "3" + "'," + "username:'" + Global.TESTING_UNIT_NUMBER + "'," + "password:'" + Global.TESTING_UNIT_NAME + "'," + "items:[{"
////                    + "spdm:'" + result.sampleNum+ "" + "'," + "spmc:'" + URLEncoder.encode(result.sampleName, "gb2312") + "" + "',"
////                    + "jcxm:'" + URLEncoder.encode(result.projectName, "GB2312") + "" + "',"
////                    + "xmmc:'" + "13" + "" + "'," + "jcz:'" + jcz + "" + "'," + "jcjg:'" + URLEncoder.encode(jg, "gb2312") + "" + "',"
////                    + "jcrq:'" + ToolUtils.dateToString(new Date(result.testTime), "yyyy-MM-dd HH:mm:ss") + "" + "'," + "twh:'" + result.twh + "" + "',"
////                    + "weight:'" + weight + "" + "'," + "cljg:'" + URLEncoder.encode(jg, "gb2312") + ""
////                    + "'}]" + "}";
//
//            Log.d(TAG, "uploadData=" + ss);
//
//            return ss;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    public static boolean isNumericZidai(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String isNumeric(String str) {
        String s = "";
        for(int i=0;i<str.length();i++) {
            if ((str.charAt(i) >= 48 && str.charAt(i) <= 57) || str.charAt(i) == 46) {
                s += str.charAt(i);
            }
        }
        return s;
    }


    private static String getID(String id) {
        if (id == null || id.isEmpty()) {
            return "0000000";
        }
        if (id.length() == 7) {
            return id;
        }
        for (int i = 0; i < 7 - id.length(); i++) {
            id = "0" + id;
        }
        return id;
    }

//	public static String getUploadData(CheckResult result) {
//
//
//		JSONObject json = new JSONObject();
//		try {
//			json.put("count", "1");
//			json.put("jczbh", Global.check_state_no);
//			json.put("jcdw", Global.check_department);
//			JSONArray array = new JSONArray();
//            JSONObject subJson = new JSONObject();
//            array.put(subJson);
//            subJson.put("rwbh", "04");  //任务编号，可以为空
//            subJson.put("bjdw", result.checkedOrganization);  //被检单位
//            subJson.put("jcxm", result.projectName);  //检测项目  "农药残留"
//            subJson.put("jcdt", ToolUtils.long2String(result.testTime, "yyyy-MM-dd HH:mm:ss"));  //检测时间
//
//            subJson.put("jcz", result.testValue.replace("%", ""));   //检测数值
//            subJson.put("szdw", "%");  //检测数值单位
//            subJson.put("jgpd", result.resultJudge);  //结果判定
//
//            subJson.put("ybbh", result.sample.sampleNo);  //样本编号
//            subJson.put("ybmc", result.sample.sampleName);  //样品名称
//            subJson.put("ybcd", result.sampleSource);  //商品来源
//
//            subJson.put("xlbz", result.testStandard);  //限量标准
//            subJson.put("sbbh", "XH0001");  //设备型号
//            subJson.put("xlz", result.xlz);  //国家规定的限量标准
//			json.put("details", array);
////			Log.i(TAG, "json=====" + json.toString());
//		} catch (JSONException e) {
//			e.printStackTrace();
//
//		}
//
//		return "data=" + json.toString();
//	}

    public static float computeGrayValue(int color) {

        int R = Color.red(color);
        int G = Color.green(color);
        int B = Color.blue(color);
        //(color.R * 19595 + color.G * 38469 + color.B * 7472) >> 16
        return (R * 19595 + G * 38469 + B * 7472) >> 16;
    }

    public static double computeGrayValue(int r, int g, int b) {

        return (r * 19595 + g * 38469 + b * 7472) >> 16;
    }

    public static long getTimeFromDataPicker(DatePicker datePicker, boolean isStartTime) {

        Calendar calendar = Calendar.getInstance();
        if (isStartTime) {
            calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);
        } else {
            calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 23, 59, 59);
        }
        return calendar.getTimeInMillis();
    }

    public static String assemblyNullUploadData() {

        JSONObject json = new JSONObject();
        try {
            json.put("id", 0);
            json.put("device_id", Global.device_id);//"fa4418dba5110622"
            json.put("product_num", "");
            json.put("item_weight", 0);
            json.put("item_name", "");
            json.put("product_name", "");
            json.put("company_bjdw", "");
            json.put("data_value", "");
            json.put("data_conclusion", "");
            json.put("data_dispose", "");
            json.put("create_time", "");
            json.put("data_image", "");
            json.put("channel", "");

            return "data=" + json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void connect2Server() {

        try {
            String content = ToolUtils.assemblyNullUploadData();
            if (TextUtils.isEmpty(content)) return;
            // 1. 写一个Url
            URL url = new URL(Global.uploadUrl + "?" + content);
            // 2. 通过Url打开一个连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 3. 设置请求方法和参数
            conn.setRequestMethod("GET");// 默认的请求方式
            conn.setConnectTimeout(3000);
            // 4. 拿到返回状态
            int code = conn.getResponseCode();

            // - 2xxx 请求成功 3xxx缓存 4xxx资源错误 5xxx服务器错误
            String result = null;
            if (code == 200) {
                // 5. 获取服务器返回的二进制输入流
                InputStream is = conn.getInputStream();
                result = ToolUtils.getStringFromInputStream(is);
            }
            if (Global.DEBUG) Log.i("网络连接", "result = " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 匹配补充
     *
     * @param str
     * @param containStr
     * @return
     */
    public static String replenish(String str, String containStr) {
        if (containStr == null) {
            return str;
        }
        if (str == null) {
            return "" + containStr;
        }
        if (str.contains(containStr)) {
            str = str.replace(containStr, "");
        }
        str += containStr;
        return str;

    }

    public static int replenishInt(String str, String s) {
        int i = 0;
        if (s == null) {
            return i;
        }
        if (str == null) {
            return i;
        }
        if (str.contains(s)) {
            str = str.replace(s, "");
        }
        if (str.equals("")) {
            return i;
        }
        try {
            i = new Integer(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;

    }

    public static int getLocalVersion(Context ctx) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
//            LogUtil.d("TAG", "本软件的版本号。。" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
//            LogUtil.d("TAG", "本软件的版本号。。" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }
}
