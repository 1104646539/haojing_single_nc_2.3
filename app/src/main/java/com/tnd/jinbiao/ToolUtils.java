package com.tnd.jinbiao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.friendlyarm.AndroidSDK.HardwareControler;
import com.tnd.jinbiao.activity.CheckSelectProjectActivity;
import com.tnd.jinbiao.activity.SelectActivity;
import com.tnd.jinbiao.kaopiz.kprogresshud.KProgressHUD;
import com.tnd.jinbiao.model.CheckResult;
import com.tnd.jinbiao.model.ResultModel;
import com.tnd.multifuction1.db.DbHelper;
import com.tnd.multifuction1.util.Global;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ToolUtils {
    public final static int upload_success = 500;
    public final static int upload_fail = 501;
    private static final String TAG = "ResultActivity";
    public static String PERSION_AND_COMPANY_DB_NAME = "people_db_name.db";
    public static String PORT_KEY = "port_key";
    public static String BOPING_KEY = "boping_key";
    public static String port = "80";
    public static String boping = "9600";

    public static KProgressHUD kp_hud = null;

    public static void showHUD(Context context, String message) {
        KProgressHUD hud = KProgressHUD.create(context);
        hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        hud.setLabel(message);
        hud.setCancellable(true);
        hud.show();
        kp_hud = hud;
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

    public static void hiddenHUD() {
        kp_hud.dismiss();
    }

    public static Bitmap getPicUrlWithBitmap(Context context, String name) {

        Bitmap mp = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Config.RGB_565;
            options.inJustDecodeBounds = false;
            String path = context.getFilesDir().getPath() + "/" + name;
            mp = BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mp;
    }

    /**
     * 保存bmp图像
     *
     * @param map
     * @param context
     * @return 图像名称
     */
    public static String savePrivateBmp(Bitmap map, Context context) {
        try {
            String name = System.currentTimeMillis() + ".png";
            FileOutputStream out = context.openFileOutput(name, Context.MODE_PRIVATE);
            byte[] bytes = getBitmapByte(map);
            out.write(bytes);
            out.close();
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getBitmapByte(Bitmap map) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        map.compress(Bitmap.CompressFormat.PNG, 20, baos);
        return baos.toByteArray();
    }

    public static void deleteAllFile(Context context, String name) {
        File fi = new File(context.getFilesDir().getPath() + "/" + name);
        if (fi.exists()) {
            fi.delete();
        }
    }

    private static String devName = "/dev/ttyAMA3";
    private static int speed = 9600;
    private static int dataBits = 8;
    private static int stopBits = 1;
    public static int devfd = -1;
    public static int devfdprint = -1;
    private static Handler handler;

    // TODO Auto-generated method stub
    public static void OpenSerialPort(Context context, String message,
                                      int fromActiviy) {

        // TODO Auto-generated method stub
        if (fromActiviy == 5) {
            devName = "/dev/ttyAMA4";
            speed = 9600;
            if (devfdprint == -1) {
                devfdprint = HardwareControler.openSerialPort(devName, speed, dataBits, stopBits);
            }
        } else {
            devName = "/dev/ttyAMA3";
            speed = 115200;
            if (devfd == -1) {
                devfd = HardwareControler.openSerialPort(devName, speed, dataBits, stopBits);
            }
        }
        if (devfd >= 0) {
            if (message.length() > 0) {
                message = message.toString();
                int ret = HardwareControler.write(devfd, message.getBytes());

                if (ret > 0) {

                    if (fromActiviy == 1) {
                        //定量检测
                        Message messageHandler = new Message();
                        messageHandler.what = 101;
                        handler = CheckSelectProjectActivity.mStartActivityHander;
                        handler.sendMessage(messageHandler);
                    } else if (fromActiviy == 2) {
                        //定性检测
                        Message messageHandler = new Message();
                        messageHandler.what = 102;
                        handler = CheckSelectProjectActivity.mStartActivityHander;
                        handler.sendMessage(messageHandler);
                    } else if (fromActiviy == 4) {
                        //样本图像
                        Message messageHandler = new Message();
                        messageHandler.what = 104;
                        handler = SelectActivity.mStartActivityHander;
                        handler.sendMessage(messageHandler);
                    }

                } else {
                    Toast.makeText(context, "Fail to send!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            devfd = -1;
        }
    }

    // long转换为Date类型
    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    } // string类型转换为date类型

    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        try {
            date = formatter.parse(strTime);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
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

    /**
     * 组装打印数据
     *
     * @param model
     * @param context
     * @return
     */
    public static String GetPrintInfo(ResultModel model, Context context) {

        return GetPrintInfo(model, context, "1");
    }

    /**
     * @param model
     * @param context
     * @param testType 检测类型 1为定量，2为定性
     * @return
     */
    public static String GetPrintInfo(ResultModel model, Context context, String testType) {
        StringBuffer sb = new StringBuffer("\n"+"胶体金检测" + "\n");
        sb.append("\n");
        if (model.id != 0) {
            sb.append("检测流水号：");
            sb.append(model.id + "\n");
        }
        sb.append("检测单位：");
        sb.append(model.company_name + "\n");
        sb.append("检 验 员：");
        sb.append(model.persion + "\n");
//		sb.append("试剂厂商：");
//		sb.append(model. + "\n");
        sb.append("样品名称：");
        sb.append(model.sample_name + "\n");
        sb.append("检测项目：");
        sb.append(model.project_name + "\n");

        sb.append("样品类型：");
        sb.append(model.sample_type + "\n");
        sb.append("商品来源：");
        sb.append(model.sample_unit + "\n");

        sb.append("样品编号：");
        sb.append(model.sample_number + "\n");
        sb.append("检 测 限：");
        sb.append(model.xian + model.concentrateUnit + "\n");
//		sb.append("临 界 值：");
//		sb.append(model.lin	+"\n");
        sb.append("检 测 值：");
        sb.append(model.check_value + "\n");

        if (!"2".equals(testType)) {
            sb.append("样品浓度：");
            sb.append(model.style_long + "\n");
        }
        sb.append("检测结果：");
        sb.append(model.check_result + "\n");
        sb.append("检测时间：");
        sb.append(ToolUtils.dateToString(ToolUtils.longToDate(
                model.time, "yyyy-MM-dd HH:mm:ss"),
                "yyyy-MM-dd HH:mm:ss") + "\n");
        sb.append("\n");

        return sb.toString();
    }

    public static String assemblyUploadData(CheckResult result) {
        String ss = "";
        JSONObject json = new JSONObject();
        Log.d(TAG, "assemblyUploadData BASE_URL= " + Global.uploadUrl);
        if (TextUtils.isEmpty(Global.uploadUrl)) {
            return null;
        }

        String jg = result.resultJudge;
        if (!jg.equals("合格") && !jg.equals("不合格")) {
            jg = "合格";
        }
        String weight = result.weight;
//        if (weight.isEmpty()) {
//            weight = "1";
//        }
        weight = "1";
        String jcz = result.testValue;
        if (!isNumericZidai(jcz)) {
            jcz = "0";
        }

        ss = "{" + "cmd:'" + "3" + "'," + "username:'" + Global.TESTING_UNIT_NAME + "'," + "password:'" + Global.TESTING_UNIT_NUMBER + "'," + "items:[{"
                + "spdm:'" + DbHelper.getSampleNumber(result.sampleName) + "" + "'," + "spmc:'" + result.sampleName + "" + "'," + "jcxm:'" + result.projectName + "" + "',"
                + "xmmc:'" + "13" + "" + "'," + "jcz:'" + jcz + "" + "'," + "jcjg:'" + jg + "" + "',"
                + "jcrq:'" + ToolUtils.dateToString(new Date(result.testTime), "yyyy-MM-dd HH:mm:ss") + "" + "'," + "twh:'" + result.twh + "" + "',"
                + "weight:'" + weight + "" + "'," + "cljg:'" + result.resultJudge + ""
                + "'}]" + "}";
//            ss = "{" + "cmd:'" + "3" + "'," + "username:'" + Global.TESTING_UNIT_NUMBER + "'," + "password:'" + Global.TESTING_UNIT_NAME + "'," + "items:[{"
//                    + "spdm:'" + result.sampleNum+ "" + "'," + "spmc:'" + URLEncoder.encode(result.sampleName, "gb2312") + "" + "',"
//                    + "jcxm:'" + URLEncoder.encode(result.projectName, "GB2312") + "" + "',"
//                    + "xmmc:'" + "13" + "" + "'," + "jcz:'" + jcz + "" + "'," + "jcjg:'" + URLEncoder.encode(jg, "gb2312") + "" + "',"
//                    + "jcrq:'" + ToolUtils.dateToString(new Date(result.testTime), "yyyy-MM-dd HH:mm:ss") + "" + "'," + "twh:'" + result.twh + "" + "',"
//                    + "weight:'" + weight + "" + "'," + "cljg:'" + URLEncoder.encode(jg, "gb2312") + ""
//                    + "'}]" + "}";

        Log.d(TAG, "uploadData=" + ss);

        return ss;

    }

    public static boolean isNumericZidai(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    public static String long2String(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }
}
