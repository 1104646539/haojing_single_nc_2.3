package com.tnd.multifuction1.net;

import com.google.gson.GsonBuilder;
import com.tnd.multifuction1.MyApplication;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zsl on 2020/9/21.
 * author hhq
 */

public class RetrofitHelper {

    private Retrofit retrofit;

    private RetrofitHelper(){

        Cache cache = new Cache(new File(MyApplication.getInstance().getCacheDir(), "HttpCache"),
                1024 * 1024 * 100);

        OkHttpClient client = new OkHttpClient.Builder().
                connectTimeout(60, TimeUnit.SECONDS).
                readTimeout(60, TimeUnit.SECONDS).
                writeTimeout(60, TimeUnit.SECONDS).build();
        GsonBuilder gsonBuilder = new GsonBuilder();
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder().cache(cache)
                .retryOnConnectionFailure(true)
                .addInterceptor(RetrofitConfig.sLoggingInterceptor)
                .addInterceptor(RetrofitConfig.sRewriteCacheControlInterceptor)
                .addNetworkInterceptor(RetrofitConfig.sRewriteCacheControlInterceptor)
                ;

        retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.baseUrl)
                .addConverterFactory(GsonConverterFactory.create(
                        gsonBuilder.serializeNulls()
                                .registerTypeAdapter(Double.class,new DoubleDefaultAdapter())
                                .registerTypeAdapter(double.class,new DoubleDefaultAdapter())
                                .create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

    }



    public static RetrofitHelper getInstance(){
        return RetrofitHelperHolder.retrofitHelper;
    }

    //静态内部类
    public static class RetrofitHelperHolder{
        private static final RetrofitHelper retrofitHelper = new RetrofitHelper();
    }

    public <T> T create(Class<T> service){
        return retrofit.create(service);
    }
}
