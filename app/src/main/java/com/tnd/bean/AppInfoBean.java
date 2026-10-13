package com.tnd.bean;

import android.graphics.drawable.Drawable;

public class AppInfoBean {
	
	/**
	 * apk文件的路径
	 */
	public String apkPath;
	
	/**
	 * 应用的包名
	 */
	public String packageName;
	
	/**
	 * 应用图标
	 */
	public Drawable appIcon;
	
	/**
	 * 应用的名称
	 */
	public String appName;
	
	/**
	 * 应用程序APK的大小
	 */
	public long appSize;
	
	/**
	 * 是否安装在SD中
	 */
	public boolean isInSd;
	
	/**
	 * 是否是系统应用
	 */
	public boolean isSys;

	@Override
	public String toString() {
		return "AppInfoBean [appIcon=" + appIcon + ", appName=" + appName
				+ ", appSize=" + appSize + ", isInSd=" + isInSd + ", isSys="
				+ isSys + "]";
	}
	
	
	
	
}
