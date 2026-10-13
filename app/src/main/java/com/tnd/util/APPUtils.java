package com.tnd.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.SystemClock;
import android.widget.Toast;


import com.tnd.bean.AppInfoBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class APPUtils {

	public static int getVersionCode(Context context) {
		int versionCode = 0;
		
		//拿到管理类之类这样的代码
		/*
		 * SMSManager.getDefaul()
		 * windowManager 可以拿到和屏幕相关的信息
		 * TelephoneManager 电话相关的
		 * NotificationManager 拿到和通知相关的
		 * PackageManager 和包相关的
		 */
		
		PackageManager packageManager = context.getPackageManager();
		// 拿到包的一些信息
		try {
			// 标记写一个0代表所有信息都要，javabean对象
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			// 拿到版本号和版本名称
			versionCode = packageInfo.versionCode;

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionCode;
	}

	public static String getVersionName(Context context) {
		String versionName = null;
		
		PackageManager packageManager = context.getPackageManager();
		// 拿到包的一些信息
		try {
			// 标记写一个0代表所有信息都要
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			
			
			// 拿到版本号和版本名称
			versionName = packageInfo.versionName;

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionName;
	}
	
	public static boolean isSDCardEnable(){
		
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	
	public static void showToast(final Activity activity,final String content){
		activity.runOnUiThread(new Runnable() {
			
			@SuppressLint("WrongConstant")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(activity, content, 0).show();
			}
		});
	}
	
	/**
	 * 判断指定名称的服务是否运行
	 * @param act
	 * @param serviceName
	 * @return 
	 */
	public static boolean isServiceRunning(Activity act,String serviceName){
		
		/**
		 * 手机中的任务管理器，管理手机所有的正在运行的信息，如：activity,service,内存使用状态，等，都由ActivityManager 来管理
		 */
		ActivityManager am = (ActivityManager) act.getSystemService(Context.ACTIVITY_SERVICE);
		
		// 获得正在运行的服务的信息
		List<RunningServiceInfo> runningServices = am.getRunningServices(100); // 如果手机运行20个服务，返回的集合 size = 20 ，如果手机运行 200个服务，返回的集合 size = 100
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			// 正在运行的服务的类名
			String className = runningServiceInfo.service.getClassName();
//			System.out.println("className::"+className);
			if(className.equals(serviceName)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 获得手机安装的所有的应用程序信息
	 * @param ctx
	 * @return
	 */
	public static List<AppInfoBean> getAllAppInfo(Context ctx){
	
		List<AppInfoBean> allApplist = new ArrayList<AppInfoBean>();
		
		// 获得包管理器，管理手机上所有的APK安装包
		PackageManager pm = ctx.getPackageManager();
		
		// 获得安装的所有的包的信息
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		for (PackageInfo packageInfo : installedPackages) {
			
			AppInfoBean appInfo = new AppInfoBean();
			
			allApplist.add(appInfo);
			
			// 设置包名
			appInfo.packageName = packageInfo.packageName;
			
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			// 获得图标
			Drawable appIcon = applicationInfo.loadIcon(pm);
			appInfo.appIcon = appIcon;
			// 获得名称
			CharSequence appName = applicationInfo.loadLabel(pm);
			appInfo.appName = appName.toString();
			
			// APK文件的路径 
			String apkPath = applicationInfo.sourceDir;
//			System.out.println(appName+" : "+apkPath);
			appInfo.apkPath = apkPath;
			
			// 获得apk文件大小
			appInfo.appSize = new File(apkPath).length();
			
			// 判断是否是系统应用
			if(apkPath.startsWith("/data")){ // 用户应用
				appInfo.isSys = false;
			}else{
				appInfo.isSys = true;
			}
			
			// applicationInfo.flags 与特定的FLAG值按位相与 ，如果不等于0，说明批配成功，
			// 当前应用拥有该FLAG表示的属性
			if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)!=0){
//				System.out.println(appName +"用flag 判断是系统应用");
			}else{
//				System.out.println(appName +"用flag 判断是用户应用");
			}
			
			if((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0){
				// 是安装在SD卡中
				appInfo.isInSd = true;
			}else{
				// 在内部存储中
				appInfo.isInSd = false;
			}
		}
		
		SystemClock.sleep(1000); // 休眠一会儿，让看见效果
		
		return allApplist;
	}
	
	
	
	
}
