package com.tnd.multifuction1.util;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <h3>全局捕获异常</h3>
 * <br>
 * 当程序发生Uncaught异常的时候,有该类来接管程序,并记录错误日志
 */
@SuppressLint("SimpleDateFormat")
public class CrashHandler implements UncaughtExceptionHandler {

    public static String TAG = "MyCrash";
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private static CrashHandler instance = new CrashHandler();
    private Context mContext;

    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final int DEY_TIME = 1000 * 0;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        autoClear(5);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            SystemClock.sleep(DEY_TIME);
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息; 否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null)
            return false;

        try {
            // 使用Toast来显示异常信息
            new Thread() {

                @Override
                public void run() {
                    Looper.prepare();
//                    Toast.makeText(mContext, "很抱歉,程序出现异常,即将重启.",
//                            Toast.LENGTH_LONG).show();

                    Looper.loop();
                }
            }.start();
            // 收集设备参数信息
            collectDeviceInfo(mContext);
            // 保存日志文件
            saveCrashInfoFile(ex);
            SystemClock.sleep(DEY_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName + "";
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     * @throws Exception
     */
    private String saveCrashInfoFile(Throwable ex) throws Exception {
        StringBuffer sb = new StringBuffer();
        try {
            SimpleDateFormat sDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            String date = sDateFormat.format(new java.util.Date());
            sb.append("\r\n" + date + "\n");
            for (Map.Entry<String, String> entry : infos.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append(key + "=" + value + "\n");
            }

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.flush();
            printWriter.close();
            String result = writer.toString();
            sb.append(result);

            String fileName = writeFile(sb.toString());
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
            sb.append("an error occured while writing file...\r\n");
            writeFile(sb.toString());
        }
        return null;
    }

    private String writeFile(String sb) throws Exception {
        String time = formatter.format(new Date());
        String fileName = "crash-" + time + ".log";
        if (FileUtil.hasSdcard()) {
            String path = getGlobalpath();
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            FileOutputStream fos = new FileOutputStream(path + fileName, true);
            fos.write(sb.getBytes());
            fos.flush();
            fos.close();
        }
        return fileName;
    }

    public static String getGlobalpath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "crash" + File.separator;
    }

    public static void setTag(String tag) {
        TAG = tag;
    }

    /**
     * 文件删除
     *
     * @param autoClearDay 文件保存天数
     */
    public void autoClear(final int autoClearDay) {
        FileUtil.delete(getGlobalpath(), new FilenameFilter() {

            @Override
            public boolean accept(File file, String filename) {
                String s = FileUtil.getFileNameWithoutExtension(filename);
                int day = autoClearDay < 0 ? autoClearDay : -1 * autoClearDay;
                String date = "crash-" + DateUtil.getOtherDay(day);
                return date.compareTo(s) >= 0;
            }
        });

    }

    public static final class DateUtil {

        /**
         * yyyy-MM-dd HH:mm:ss字符串
         */
        public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

        /**
         * yyyy-MM-dd字符串
         */
        public static final String DEFAULT_FORMAT_DATE = "yyyy-MM-dd";

        /**
         * HH:mm:ss字符串
         */
        public static final String DEFAULT_FORMAT_TIME = "HH:mm:ss";

        /**
         * yyyy-MM-dd HH:mm:ss格式
         */
        public static final ThreadLocal<SimpleDateFormat> defaultDateTimeFormat = new ThreadLocal<SimpleDateFormat>() {

            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
            }

        };
        /**
         * yyyy-MM-dd格式
         */
        public static final ThreadLocal<SimpleDateFormat> defaultDateFormat = new ThreadLocal<SimpleDateFormat>() {

            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat(DEFAULT_FORMAT_DATE);
            }

        };

        /**
         * 获得几天之前或者几天之后的日期
         *
         * @param diff 差值：正的往后推，负的往前推
         * @return
         */
        public static String getOtherDay(int diff) {
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.add(Calendar.DATE, diff);
            return getDateFormat(mCalendar.getTime());
        }

        public static String getDateFormat(Date date) {
            return dateSimpleFormat(date, defaultDateFormat.get());
        }

        /**
         * 将date转成字符串
         *
         * @param date   Date
         * @param format SimpleDateFormat
         *               <br>
         *               注： SimpleDateFormat为空时，采用默认的yyyy-MM-dd HH:mm:ss格式
         * @return yyyy-MM-dd HH:mm:ss
         */
        public static String dateSimpleFormat(Date date, SimpleDateFormat format) {
            if (format == null)
                format = defaultDateTimeFormat.get();
            return (date == null ? "" : format.format(date));
        }
    }

    /**
     * <h3>File工具类</h3>
     * <p>主要封装了一些对文件读写的操作
     */
    public static final class FileUtil {

        private FileUtil() {
            throw new Error("￣﹏￣");
        }

        /**
         * 分隔符.
         */
        public final static String FILE_EXTENSION_SEPARATOR = ".";

        /**
         * "/"
         */
        public final static String SEP = File.separator;

        /**
         * SD卡根目录
         */
        public static final String SDPATH = Environment
                .getExternalStorageDirectory() + File.separator;

        /**
         * 判断SD卡是否可用
         *
         * @return SD卡可用返回true
         */
        public static boolean hasSdcard() {
            String status = Environment.getExternalStorageState();
            return Environment.MEDIA_MOUNTED.equals(status);
        }

        /**
         * 读取文件的内容
         * <br>
         * 默认utf-8编码
         *
         * @param filePath 文件路径
         * @return 字符串
         * @throws IOException
         */
        public static String readFile(String filePath) throws IOException {
            return readFile(filePath, "utf-8");
        }

        /**
         * 读取文件的内容
         *
         * @param filePath    文件目录
         * @param charsetName 字符编码
         * @return String字符串
         */
        public static String readFile(String filePath, String charsetName)
                throws IOException {
            if (TextUtils.isEmpty(filePath))
                return null;
            if (TextUtils.isEmpty(charsetName))
                charsetName = "utf-8";
            File file = new File(filePath);
            StringBuilder fileContent = new StringBuilder("");
            if (file == null || !file.isFile())
                return null;
            BufferedReader reader = null;
            try {
                InputStreamReader is = new InputStreamReader(new FileInputStream(
                        file), charsetName);
                reader = new BufferedReader(is);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (!fileContent.toString().equals("")) {
                        fileContent.append("\r\n");
                    }
                    fileContent.append(line);
                }
                return fileContent.toString();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * 删除指定目录中特定的文件
         *
         * @param dir
         * @param filter
         */
        public static void delete(String dir, FilenameFilter filter) {
            if (TextUtils.isEmpty(dir))
                return;
            File file = new File(dir);
            if (!file.exists())
                return;
            if (file.isFile())
                file.delete();
            if (!file.isDirectory())
                return;

            File[] lists = null;
            if (filter != null)
                lists = file.listFiles(filter);
            else
                lists = file.listFiles();

            if (lists == null)
                return;
            for (File f : lists) {
                if (f.isFile()) {
                    f.delete();
                }
            }
        }

        /**
         * 获得不带扩展名的文件名称
         *
         * @param filePath 文件路径
         * @return
         */
        public static String getFileNameWithoutExtension(String filePath) {
            if (TextUtils.isEmpty(filePath)) {
                return filePath;
            }
            int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
            int filePosi = filePath.lastIndexOf(File.separator);
            if (filePosi == -1) {
                return (extenPosi == -1 ? filePath : filePath.substring(0,
                        extenPosi));
            }
            if (extenPosi == -1) {
                return filePath.substring(filePosi + 1);
            }
            return (filePosi < extenPosi ? filePath.substring(filePosi + 1,
                    extenPosi) : filePath.substring(filePosi + 1));
        }

    }
}