package com.vitaliikuznetsov.vkt.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by vitalykuznetsov on 13/04/17.
 */

public class TranslationManager {

    private static final int DEFAULT_CACHE_SIZE = 1024 * 1024 * 10; // bytes
    private static final int DEFAULT_CACHE_LIVE = 10; // days

    private static final String API_KEY_YANDEX_TRANSLATOR = "trnsl.1.1.20170411T144731Z.570e044eb78da1d7.78312d6d0d3b918f0444c21a4b00dd42d1b8e23d";
    private static final String API_URL_YANDEX_TRANSLATOR = "https://translate.yandex.net/api/v1.5/tr.json/";

    public static final TranslationManager sharedManager = new TranslationManager();

    private OkHttpClient mHttpClient = new OkHttpClient.Builder()
            .cache(new Cache(ThisApp.sharedApp().getExternalFilesDir(null), DEFAULT_CACHE_SIZE))
            .build();

    private Bus mBus = new Bus();
    private Gson mGson = new GsonBuilder()
            .registerTypeAdapter(Lang.class, new ytLangDeserializer())
            .create();

    public void getSupportedLanguages(){
        HttpUrl httpUrl = HttpUrl.parse("https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=trnsl.1.1.20170411T144731Z.570e044eb78da1d7.78312d6d0d3b918f0444c21a4b00dd42d1b8e23d&ui=ru").newBuilder().build();
        Request request = new Request.Builder()
                .get()
                .url(httpUrl)
                .cacheControl(new CacheControl.Builder()
                           .maxStale(DEFAULT_CACHE_LIVE, TimeUnit.DAYS)
                           .build())
                .build();
        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TranslationManager", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mGson.fromJson(response.body().string(), Lang.class);
//                Log.d("TranslationManager", response.body().string());
            }
        });
    }
}
