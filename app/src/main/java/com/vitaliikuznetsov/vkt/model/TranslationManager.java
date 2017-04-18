package com.vitaliikuznetsov.vkt.model;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.Query;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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

    public static final int NOTIFICATION_GET_LANGUAGES = 1;

    private static final int DEFAULT_CACHE_SIZE = 1024 * 1024 * 10; // байты
    private static final int DEFAULT_CACHE_LIVE = 10; // дни
    private static final String DEFAULT_LANG_CODE = "ru"; // код поддерживаемого языка https://tech.yandex.ru/translate/doc/dg/concepts/api-overview-docpage/#languages

    private static final String API_KEY_YANDEX_TRANSLATOR = "trnsl.1.1.20170411T144731Z.570e044eb78da1d7.78312d6d0d3b918f0444c21a4b00dd42d1b8e23d";

    private static final String API_PARAM_KEY = "key";
    private static final String API_PARAM_UI = "ui";

    private static final String API_URL_YT_BASE = "https://translate.yandex.net/api/v1.5/tr.json/";
    private static final String API_URL_YT_GET_LANGS = "getLangs";

    public static final TranslationManager sharedManager = new TranslationManager();

    private OkHttpClient mHttpClient = new OkHttpClient.Builder()
            .cache(new Cache(ThisApp.sharedApp().getExternalFilesDir(null), DEFAULT_CACHE_SIZE))
            .build();

    private LangDao langDao;
    private Query<Lang> langsQuery;

    private Bus mBus = new Bus(ThreadEnforcer.MAIN);
    private Gson mGson = new GsonBuilder()
            .registerTypeAdapter(Lang.class, new ytLangDeserializer())
            .create();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private TranslationManager() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(ThisApp.sharedApp(), "vkt-db");
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        langDao = daoSession.getLangDao();
    }

    private void postBusEvent(final Event event){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                TranslationManager.this.mBus.post(event);
            }
        });
    }

    public void subscribe(Object object){
        mBus.register(object);
    }

    public void unsubscribe(Object object){
        mBus.unregister(object);
    }

    private ArrayList<Object> modelsOfClass(JsonElement jsonElement, Class modelClass){
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        ArrayList<Object> modelsArray = new ArrayList<Object>();
        for (JsonElement element : jsonArray){
            Object model = mGson.fromJson(element, modelClass);
            modelsArray.add(model);
        }
        return modelsArray;
    }

    public void getSupportedLanguages(){
        langsQuery = langDao.queryBuilder().orderAsc(LangDao.Properties.Code).build();
        List<Lang> langs = langsQuery.list();
        if (langs.size() != 0){
            TranslationManager.this.postBusEvent(Event.successEvent(NOTIFICATION_GET_LANGUAGES, langs));
        }
        else {
            HttpUrl httpUrl = HttpUrl.parse(API_URL_YT_BASE)
                    .newBuilder()
                    .addEncodedPathSegment(API_URL_YT_GET_LANGS)
                    .addQueryParameter(API_PARAM_KEY, API_KEY_YANDEX_TRANSLATOR)
                    .addQueryParameter(API_PARAM_UI, DEFAULT_LANG_CODE)
                    .build();
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
                    TranslationManager.this.postBusEvent(Event.failEvent(NOTIFICATION_GET_LANGUAGES, e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    JsonElement json = new JsonParser().parse(response.body().string());
                    try {
                        JsonElement element = json.getAsJsonObject().getAsJsonObject("langs");
                        JSONObject jso = new JSONObject(element.toString());
                        Iterator<String> iterator = jso.keys();
                        ArrayList out = new ArrayList();
                        while (iterator.hasNext()){
                            String langCode = iterator.next();
                            String langTitle = jso.getString(langCode);
                            Lang lang = new Lang(langCode, langTitle, false, false);
                            if (lang.getCode().equals(Locale.getDefault().getLanguage())) lang.setPreferredTarget(true);
                            if (lang.getCode().equals(Locale.ENGLISH.getLanguage())) lang.setPreferredSource(true);
                            out.add(lang);
                            langDao.insert(lang);
                        }
                        TranslationManager.this.postBusEvent(Event.successEvent(NOTIFICATION_GET_LANGUAGES, out));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void setPreferredSourceLang(Lang lang){
        Lang sourceLang = langDao.queryBuilder().where(LangDao.Properties.PreferredSource.eq(true)).unique();
        if (sourceLang != null){
            if (sourceLang.equals(lang)) return;
            sourceLang.setPreferredSource(false);
            langDao.save(sourceLang);
        }
        lang.setPreferredSource(true);
        langDao.save(lang);
    }

    public Lang getPreferredSourceLang(){
        return langDao.queryBuilder().where(LangDao.Properties.PreferredSource.eq(true)).unique();
    }

    public void setPreferredTargetLang(Lang lang){
        Lang sourceLang = langDao.queryBuilder().where(LangDao.Properties.PreferredTarget.eq(true)).unique();
        if (sourceLang != null){
            if (sourceLang.equals(lang)) return;
            sourceLang.setPreferredTarget(false);
            langDao.save(sourceLang);
        }
        lang.setPreferredTarget(true);
        langDao.save(lang);
    }

    public Lang getPreferredTargetLang(){
        return langDao.queryBuilder().where(LangDao.Properties.PreferredTarget.eq(true)).unique();
    }
}