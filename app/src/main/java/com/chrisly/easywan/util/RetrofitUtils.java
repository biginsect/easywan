package com.chrisly.easywan.util;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author big insect
 * @date 2019/6/16.
 */
public final class RetrofitUtils {

    private final static String BASE_URL = "https://www.wanandroid.com";
    private final static int TIME_OUT = 60;
    private Retrofit mRetrofit;

    private RetrofitUtils(){
        init();
    }

    private static class Holder{
        private final static RetrofitUtils INSTANCE = new RetrofitUtils();
    }

    private void init(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public static RetrofitUtils getInstance() {
        return Holder.INSTANCE;
    }

    public <T> T create(Class<T> clazz){
        return mRetrofit.create(clazz);
    }
}
