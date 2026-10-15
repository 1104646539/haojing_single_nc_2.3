package com.tnd.multifuction1.thread;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tnd.multifuction1.bean.HjData;
import com.tnd.multifuction1.db.DbHelper;
import com.tnd.multifuction1.model.CheckResult;
import com.tnd.multifuction1.net.ApiService;
import com.tnd.multifuction1.net.BaseObserver;
import com.tnd.multifuction1.net.RetrofitHelper;
import com.tnd.multifuction1.net.RxSchedulers;
import com.tnd.multifuction1.util.APPUtils;
import com.tnd.multifuction1.util.Global;
import com.tnd.multifuction1.util.JsonUtil;
import com.tnd.multifuction1.util.ToolUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.tnd.multifuction1.util.ToolUtils.isNumericZidai;

public class UploadThread extends Thread {

    private static final String TAG = "UploadThread";
    private Context context;
    private onUploadListener listener;
    private List<CheckResult> list;
    private List<HjData.Items> arr;

    public UploadThread(Context context, List<CheckResult> list, onUploadListener listener) {

        this.list = list;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public void run() {
        Log.d(TAG, "获取到的list=" + list.size());
        try {
            //斯坦道
            /*OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("name",Global.TESTING_UNIT_NAME)
                    .add("passwd",Global.TESTING_UNIT_NUMBER)
                    .build();
            Request request = new Request.Builder()
                    .url("http://27.154.242.234:9081/main/login/login.do")
                    .post(requestBody)
                    .build();
            Call call = okHttpClient.newCall(request);
            int finalI = 1;
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (listener != null) {
                        listener.onFail(e.getMessage());
                    }
                    Log.d("111111111111", e.toString());
                    APPUtils.showToast((Activity) context, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    if (listener != null) {
                        if (response.code() == 200) {
                            //listener.onSuccess(list, 1, finalI, result);
                            String session = response.headers().get("Set-Cookie");
                            String JSESSIONID = session.substring(0,session.indexOf(";"));
                            try {
                                for (int i = 0; i < list.size(); i++) {
                                    Thread.sleep(300);
                                    String content = ToolUtils.assemblyUploadData(list.get(i));
                                    //String content = "有机氯%23苹果%23<0.1%23mg/kg%23合格%23GB2763-2019%23德州金锣肉制品有限公司%23%23STD-9000%232021-03-11 10:43:42%23e57ae626cc24176f%231.0mg/kg%23123%23%23|";
                                    Log.d("",""+TextUtils.isEmpty(content));
                                    if(!TextUtils.isEmpty(content)){
                                        if (TextUtils.isEmpty(content) && listener != null) {
                                            listener.onFail("设备ID或上传地址为空，请输入后重新上传");
                                            return;

                                        }
                                        Log.d("","String content:"+content);
//                APPUtils.showToast((Activity) context, content);
                                        OkHttpClient okHttpClient = new OkHttpClient();

                                        MediaType FORM_CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
                                        RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, "data=" + content);
//                RequestBody requestBody = new FormBody.Builder()
//                        .add("result", content)
//                        .build();
                                        requestBody.contentType().charset(Charset.forName("gb2312"));
                                        Request request = new Request.Builder().addHeader("cookie", JSESSIONID).url(Global.uploadUrl)
                                                .post(requestBody).build();
                                        Call call2 = okHttpClient.newCall(request);
                                        int finalI = i;
                                        call2.enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                if (listener != null) {
                                                    listener.onFail(e.getMessage());
                                                }
                                                Log.d("111111111111", e.toString());
                                                APPUtils.showToast((Activity) context, e.toString());
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                String result = response.body().string();
                                                if (listener != null) {
                                                    if (result.contains("上传成功")) {
                                                        listener.onSuccess(list, 1, finalI, result);
                                                    } else {
                                                        listener.onFail(result);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }catch (Exception e) {
                                e.printStackTrace();
                                if (listener != null) {
                                    listener.onFail("上传失败2");
                                }
                            }
                        } else {
                            listener.onFail(result);
                        }
                    }
                }
            });*/

            for (int i = 0; i < list.size(); i++) {
                Thread.sleep(300);

                String content = ToolUtils.assemblyUploadData(list.get(i));
                Log.d("",""+TextUtils.isEmpty(content));
                if(!TextUtils.isEmpty(content)){
                    if (TextUtils.isEmpty(content) && listener != null) {
                        listener.onFail("设备ID或上传地址为空，请输入后重新上传");
                        return;

                    }
                    Log.d("","String content:"+content);
//                APPUtils.showToast((Activity) context, content);
                    OkHttpClient okHttpClient = new OkHttpClient();

                    MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json; charset=utf-8");
                    RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE,  content);
//                RequestBody requestBody = new FormBody.Builder()
//                        .add("result", content)
//                        .build();
                    requestBody.contentType().charset(Charset.forName("gb2312"));
                    Request request = new Request.Builder().url(Global.uploadUrl)
                            .post(requestBody).build();
                    Call call = okHttpClient.newCall(request);
                    int finalI = i;
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            if (listener != null) {
                                listener.onFail(e.getMessage());
                            }
                            Log.d("111111111111", e.toString());
                            APPUtils.showToast((Activity) context, e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            Log.d("quanzhou",result);
                            if (listener != null) {
                                if (result.contains("200")) {
                                    listener.onSuccess(list, 1, finalI, result);
                                } else {
                                    listener.onFail(result);
                                }
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFail("上传失败2");
            }
        }

        //浩景
//       for (int i = 0; i < list.size(); i++) {
//            HjData hjData = new HjData();
//            hjData.setCmd("3");
////            hjData.setUsername("zhangsan");
////            hjData.setPassword("123456");
//
//            hjData.setUsername("HaoJing");
//            hjData.setPassword("hj123456");
//
//            HjData.Items items = new HjData.Items();
//            if(!TextUtils.isEmpty(list.get(i).sampleName)){
//                items.setGid(DbHelper.getSampleNumber(list.get(i).sampleName));
//                items.setJcxm(list.get(i).projectName);
//                String jg = list.get(i).resultJudge;
//                if (!jg.equals("合格") && !jg.equals("不合格")) {
//                    jg = "合格";
//                }
//                items.setJcjg(jg);
//                String jcz = list.get(i).testValue;
//                if (!isNumericZidai(jcz)) {
//                    jcz = "0";
//                }
//                items.setJcz(jcz);
//                String weight = list.get(i).weight;
//                if (weight.isEmpty()) {
//                    weight = "1";
//                }
//
//                items.setWeight(weight);
//                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//                items.setJcrq(format.format(new Date(list.get(i).testTime)));
//                arr = new ArrayList<>();
//                arr.add(items);
//                hjData.setItems(arr);
//
//                String json = JsonUtil.beanToJson(hjData);
//                Log.e("1234567",json);
//                ApiService apiService = RetrofitHelper.getInstance().create(ApiService.class);
//                RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),json);
//                apiService.upload(body)
//                        .compose(RxSchedulers.<ResponseBody>applySchedulers())
//                        .subscribe(new BaseObserver<ResponseBody>() {
//
//                            @Override
//                            public void onSuccess(ResponseBody requestBody) throws IOException {
//                                String string = requestBody.string();
//                                Log.d("HJ", string);
//                                if (string.contains("上传成功1条")) {
//                                    APPUtils.showToast((Activity) context,"上传成功");
//                                }
//                            }
//
//                            @Override
//                            public void onFail(Throwable e) {
//                                Log.d("HJ111111", e.toString());
//                                APPUtils.showToast((Activity) context,e.toString());
//                            }
//                        });
//            }
//        }
    }

    public interface onUploadListener {

        void onSuccess(List<CheckResult> list, int returnId, int position, String result);

        void onFail(String failInfo);
    }

}
