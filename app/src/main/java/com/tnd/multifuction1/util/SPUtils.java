package com.tnd.multifuction1.util;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.tnd.multifuction1.model.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SPUtils {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static final String TAG = "SPUtils";

    public SPUtils(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void setDataList(String listtag, List<Print> datalist) {
        if (null == datalist || datalist.size() == 0)
            return;
        Log.d(TAG, "setDataList=0");
        Gson gson = new Gson();
        //转换成json数据，再保存
        editor = preferences.edit();
        Log.d(TAG, "setDataList=1");
        String Json = toJson(datalist);
        Log.d(TAG, "setDataList=2 Json=" + Json);
        editor.putString(listtag, Json);
        Log.d(TAG, "setDataList=3");
        editor.apply();
        Log.d(TAG, "setDataList=4");
    }

    private String toJson(List<Print> datalist) {
        String str = "[";
        for (int i = 0; i < datalist.size(); i++) {
            str += "{" +
                    "\"" + "p_name" + "\"" + ":" + "\"" + datalist.get(i).p_name + "\"" + "," +
                    "\"" + "isMerge" + "\"" + ":" + datalist.get(i).isMerge + "," +
                    "\"" + "isMultiple" + "\"" + ":" + datalist.get(i).isMultiple + "," +
                    "\"" + "isRequired" + "\"" + ":" + datalist.get(i).isRequired + "," +
                    "\"" + "isSelectMultiple" + "\"" + ":" + datalist.get(i).isSelectMultiple + "," +
                    "\"" + "isSelectMerge" + "\"" + ":" + datalist.get(i).isSelectMerge +
                    "}";
            if (i < datalist.size() - 1) {
                str += ",";
            }
        }
        str += "]";
        return str;
    }

    public List<Print> getDataList(String tag) {
        List<Print> datalist = new ArrayList<>();
        String Json = preferences.getString(tag, null);
        if (null == Json) {
            return null;
        }
        Log.d(TAG, "Json=" + Json);
//        Gson gson = new Gson();
//        datalist = gson.fromJson(Json, new TypeToken<List<Print>>() {
//        }.getType());
        try {
            JSONArray jsonElements = new JSONArray(Json);
            for (int i = 0; i < jsonElements.length(); i++) {
                JSONObject jsonObject = jsonElements.getJSONObject(i);
                Print print = new Print(jsonObject.getString("p_name"),
                        jsonObject.getBoolean("isMultiple"),
                        jsonObject.getBoolean("isMerge"),
                        jsonObject.getBoolean("isRequired"));
                print.isSelectMultiple = jsonObject.getBoolean("isSelectMultiple");
                print.isSelectMerge = jsonObject.getBoolean("isSelectMerge");
                datalist.add(print);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return datalist;
    }
}
