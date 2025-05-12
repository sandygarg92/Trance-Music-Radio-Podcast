/*
 * Copyright (c) 2017. Radio Polska - All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.
 *
 *         http://radiopolska.com/sourcecode/policy
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.onlineradiofm.trancemusicradio.ypylibs.utils;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author:radiopolska
 * @Skype: 
 * @Mobile : 
 * @Email: baodt@hanet.com
 * @Website: http://hanet.com/
 * @Project: YouTunes
 * Created by radiopolska on 6/2/17.
 */

public class YPYOKHttpClient {

    public static final int CONNECT_TIME_OUT = 10;
    public static final int WRITE_TIME_OUT = 10;
    public static final int READ_TIME_OUT = 30;
    private static final String HEADER_COUNTRY = "X-Country";
    private static final String HEADER_PLATFORM = "X-Platform";
    private static final String ANDROID = "android";

    public static OkHttpClient build() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS);
        builder.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder request = original.newBuilder();
            request.addHeader(HEADER_COUNTRY, getLanCode())
                    .addHeader(HEADER_PLATFORM, ANDROID)
                    .method(original.method(), original.body());
            return chain.proceed(request.build());
        });
//        if(DEBUG){
//            //add log to see what is fuck
//            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message -> YPYLog.e("DCM","========>message="+message));
//            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            builder.addInterceptor(httpLoggingInterceptor);
//        }
        return builder.build();
    }

    public static Response getResponse(String url){
        try {
            OkHttpClient mOkHttpClient = build();
            Request request = new Request.Builder().url(url).build();
            return mOkHttpClient.newCall(request).execute();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private static String getLanCode() {
        try {
            Locale mLocale = Locale.getDefault();
            return mLocale.getLanguage() + "-" + mLocale.getCountry();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "EN";
    }
}
