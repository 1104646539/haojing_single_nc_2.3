package com.tnd.jinbiao.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.tnd.jinbiao.ToolUtils;


public class SharedPreferencesUtil {

	public static String getObjWithkey(Context context, String key) {
		SharedPreferences share = context.getSharedPreferences("value",
				Context.MODE_PRIVATE);

		if (key.equals(ToolUtils.PORT_KEY)) {
			return share.getString(key, ToolUtils.port);
		}

		if (key.equals(ToolUtils.BOPING_KEY)) {
			return share.getString(key, ToolUtils.boping);
		}
		return share.getString(key, "");
	}

	public static Boolean getObjBloWithkey(Context context, String key) {
		SharedPreferences share = context.getSharedPreferences("value",
				Context.MODE_PRIVATE);
		return share.getBoolean(key, false);
	}

	public static void saveObjWithKey(Context context, String key, String value) {
		SharedPreferences share = context.getSharedPreferences("value",
				Context.MODE_PRIVATE);
		Editor edit = share.edit();
		edit.putString(key, value);
		edit.commit();
	}

	public static void saveObjBloWithKey(Context context, String key,
			Boolean value) {
		SharedPreferences share = context.getSharedPreferences("value",
				Context.MODE_PRIVATE);
		Editor edit = share.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	public static void clearKey(Context context, String key) {
		SharedPreferences share = context.getSharedPreferences("value",
				Context.MODE_PRIVATE);
		Editor edit = share.edit();
		edit.remove(key);
		edit.commit();
	}
	public static int getTime(Context context, String key) {
		SharedPreferences share = context.getSharedPreferences("time",
				Context.MODE_PRIVATE);
		return share.getInt(key, 1);
	}
	public static void setTime(Context context, String key,int value) {
		SharedPreferences share = context.getSharedPreferences("time",
				Context.MODE_PRIVATE);
		Editor edit = share.edit();
		edit.putInt(key, value);
		edit.commit();
	}
}
