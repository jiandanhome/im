package com.eju.cy.audiovideo.net;

import android.content.Context;
import android.util.Log;


import com.eju.cy.audiovideo.BuildConfig;
//import com.ihsanbal.logging.Level;
//import com.ihsanbal.logging.LoggingInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    public static final String SERVER_URL = "https://yun.jiandanhome.com/";
    private static RetrofitManager instance;
    private String userID;
    private String userToken;
    private String appType;

    public static RetrofitManager getDefault() {


        if (instance == null) {
            synchronized (RetrofitManager.class) {
                if (instance == null) {
                    instance = new RetrofitManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化网络请求
     *
     * @param userID
     * @param userToken
     * @param appType
     */
    public void init(String userID, String userToken, String appType) {

        this.userID = userID;
        this.userToken = userToken;
        this.appType = appType;

    }

    public String getToken() {

        return this.userToken;
    }

    public String getUserID() {

        return this.userID;
    }

    public String getPlat() {
        return this.appType;
    }


    public AppNetInterface provideClientApi(final Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .client(genericClient(context))
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                .build();
        return retrofit.create(AppNetInterface.class);

    }


    private OkHttpClient genericClient(final Context context) {


        OkHttpClient httpClient = new OkHttpClient.Builder().
                connectTimeout(60, TimeUnit.SECONDS).
                readTimeout(60, TimeUnit.SECONDS).
                writeTimeout(60, TimeUnit.SECONDS).
                addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {



                        Log.w("UploadCard---", "userId" + userID + "\n" + userToken);
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("User-Id", userID)
                                .addHeader("User-Token", userToken)
                                .addHeader("X-REQUESTED-WITH", "json")
                                .addHeader("Http-Plat", appType)
                                .build();
                        return chain.proceed(request);
                    }

                })

//                .addInterceptor(new LoggingInterceptor.Builder()
//                        .loggable(BuildConfig.DEBUG)
//                        .setLevel(Level.BASIC)
//                        .log(Platform.INFO)
//                        .request("Request")
//                        .response("Response")
//                        .build())

                .build();

        return httpClient;
    }



    /*-------------------------*/

    private OkHttpClient genericClient() {


        OkHttpClient httpClient = new OkHttpClient.Builder().
                connectTimeout(60, TimeUnit.SECONDS).
                readTimeout(60, TimeUnit.SECONDS).
                writeTimeout(60, TimeUnit.SECONDS).
                addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {


//                        Log.w("UploadCard---","userId" + userId + "\n" + token);
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("User-Id", userID)
                                .addHeader("User-Token", userToken)
                                .addHeader("X-REQUESTED-WITH", "json")
                                .addHeader("Http-Plat", appType)
                                .build();
                        return chain.proceed(request);
                    }

                })

//                .addInterceptor(new LoggingInterceptor.Builder()
//                        .loggable(BuildConfig.DEBUG)
//                        .setLevel(Level.BASIC)
//                        .log(Platform.INFO)
//                        .request("Request")
//                        .response("Response")
//                        .build())
                .build();

        return httpClient;
    }


    public AppNetInterface provideClientApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .client(genericClient())
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                .build();
        return retrofit.create(AppNetInterface.class);

    }


}

