package com.tnd.multifuction1.net;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * 网络请求
 */

public interface ApiService {

    //上传数据
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("data/reception/detect")
    Observable<ResponseBody> upload(@Body RequestBody route);

}
