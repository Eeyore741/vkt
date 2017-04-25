package com.vitaliikuznetsov.vkt.model;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.vitaliikuznetsov.vkt.ThisApp;

import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.database.Database;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by vitalykuznetsov on 13/04/17.
 */

public class TranslationManager {

    // константы идентификации броадкаст событий
    public static final int NOTIFICATION_GET_LANGUAGES = 1;
    public static final int NOTIFICATION_TRANSLATE = 2;
    public static final int NOTIFICATION_SELECT_TRANSLATIONS = 3;
    public static final int NOTIFICATION_DELETE_TRANSLATION = 4;
    public static final int NOTIFICATION_UPDATE_TRANSLATION = 5;
    public static final int NOTIFICATION_SELECT_FAVORITE_TRANSLATIONS = 6;
    public static final int NOTIFICATION_DELETE_HISTORY = 7;
    public static final int NOTIFICATION_DELETE_FAVORITES = 8;

    private static final String DEFAULT_LANG_CODE = "ru"; // код поддерживаемого языка https://tech.yandex.ru/translate/doc/dg/concepts/api-overview-docpage/#languages

    private static final String API_KEY_YANDEX_TRANSLATOR = "trnsl.1.1.20170411T144731Z.570e044eb78da1d7.78312d6d0d3b918f0444c21a4b00dd42d1b8e23d";
    private static final String API_KEY_FORMAT_PLAIN = "plain";
    private static final String API_KEY_LANGS = "langs";
    private static final String API_KEY_LANG_CODE = "lang";

    private static final String API_SER_TEXT = "text";

    private static final String API_PARAM_KEY = "key";
    private static final String API_PARAM_UI = "ui";
    private static final String API_PARAM_TEXT = "text";
    private static final String API_PARAM_LANGUAGE = "lang";
    private static final String API_PARAM_FORMAT = "format";

    private static final String API_URL_YT_BASE = "https://translate.yandex.net/api/v1.5/tr.json/";
    private static final String API_URL_YT_GET_LANGS = "getLangs";
    private static final String API_URL_YT_TRANSLATE = "translate";

    public static final TranslationManager sharedManager = new TranslationManager();

    private OkHttpClient mHttpClient = new OkHttpClient.Builder().build();

    private LangDao langDao;
    private TranslationDao translationDao;

    private Bus mBus = new Bus(ThreadEnforcer.MAIN);
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private AsyncTask<Void, Void, Void> selectTranslationsTask;
    private AsyncTask<Void, Void, Void> selectFavoriteTranslationsTask;

    // инициализация обьектов синхранизации с базой данных
    private TranslationManager() {

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(ThisApp.sharedApp(), "vkt-db");
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();

        this.langDao = daoSession.getLangDao();
        this.translationDao = daoSession.getTranslationDao();
    }

    // подписать обьект на бродкаст события класса Event
    public void subscribe(Object object){
        mBus.register(object);
    }

    // отменить подписку на бродкаст
    public void unsubscribe(Object object){
        mBus.unregister(object);
    }

    // отправить событие в Main Thread
    private void postBusEvent(final Event event){

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                TranslationManager.this.mBus.post(event);
            }
        });
    }

    // загрузка поддерживаемых язков
    public void getSupportedLanguages(){

        final List<Lang> langs = langDao.queryBuilder().orderAsc(LangDao.Properties.Code).build().list();

        if (langs.size() != 0)

            TranslationManager.this.postBusEvent(Event.successEvent(NOTIFICATION_GET_LANGUAGES, langs));
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
                    .build();

            mHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    TranslationManager.sharedManager.postBusEvent(Event.failEvent(NOTIFICATION_GET_LANGUAGES, e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    JsonElement json = new JsonParser().parse(response.body().string());

                    try {

                        JsonElement element = json.getAsJsonObject().getAsJsonObject(API_KEY_LANGS);
                        JSONObject jso = new JSONObject(element.toString());
                        Iterator<String> iterator = jso.keys();
                        ArrayList<Lang> out = new ArrayList<Lang>();

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

                        TranslationManager.sharedManager.postBusEvent(Event.failEvent(NOTIFICATION_GET_LANGUAGES, e.getMessage()));
                    }
                }
            });
        }
    }

    // загрузка перевода строки
    public void getTranslation(final String text, final Lang sourceLang, final Lang targetLang){

        int hash = Translation.hash(sourceLang.getCode(), targetLang.getCode(), text);
        Translation translation = translationDao.queryBuilder().where(TranslationDao.Properties.Hash.eq(hash)).unique();

        if (translation != null){

            TranslationManager.sharedManager.postBusEvent(Event.successEvent(NOTIFICATION_TRANSLATE, translation));
        }
        else {

            HttpUrl httpUrl = HttpUrl.parse(API_URL_YT_BASE)
                    .newBuilder()
                    .addEncodedPathSegment(API_URL_YT_TRANSLATE)
                    .build();

            RequestBody requestBody = new FormBody.Builder()
                    .addEncoded(API_PARAM_KEY, API_KEY_YANDEX_TRANSLATOR)
                    .addEncoded(API_PARAM_FORMAT, API_KEY_FORMAT_PLAIN)
                    .addEncoded(API_PARAM_TEXT, text)
                    .addEncoded(API_PARAM_LANGUAGE, sourceLang.getCode() + "-" + targetLang.getCode())
                    .build();

            Request request = new Request.Builder().post(requestBody).url(httpUrl).build();

            mHttpClient.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {

                    TranslationManager.sharedManager.postBusEvent(Event.failEvent(NOTIFICATION_TRANSLATE, e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String body = response.body().string();
                    JsonElement jsonElement = new JsonParser().parse(body);

                    if (jsonElement.isJsonObject()){

                        JsonObject jsonObject = jsonElement.getAsJsonObject();

                        if (jsonObject.has(API_SER_TEXT)
                                && jsonObject.get(API_SER_TEXT).isJsonArray()){

                            String langCodeString = jsonObject.getAsJsonPrimitive(API_KEY_LANG_CODE).getAsString();
                            JsonArray translationsArray = jsonObject.getAsJsonArray(API_SER_TEXT);

                            if (translationsArray.size() > 0){

                                String translationString = translationsArray.get(0).getAsString();
                                Translation translation = new Translation(langCodeString, text);
                                translation.setTranslation(translationString);
                                TranslationManager.this.translationDao.insert(translation);
                                TranslationManager.sharedManager.postBusEvent(Event.successEvent(NOTIFICATION_TRANSLATE, translation));
                            }
                        }
                    }
                }
            });
        }
    }

    // выставить предпочитаеммый язык источника
    public void setPreferredSourceLang(Lang lang){

        Lang sourceLang = langDao.queryBuilder()
                .where(LangDao.Properties.PreferredSource.eq(true))
                .unique();

        if (sourceLang != null){

            if (sourceLang.equals(lang)) return;
            sourceLang.setPreferredSource(false);
            langDao.save(sourceLang);
        }
        lang.setPreferredSource(true);
        langDao.save(lang);
    }

    // получить предпочитаеммый язык источника
    public Lang getPreferredSourceLang(){

        return langDao.queryBuilder().where(LangDao.Properties.PreferredSource.eq(true)).unique();
    }

    // выставить предпочитаемый язык направления перевода
    public void setPreferredTargetLang(Lang lang){

        Lang sourceLang = langDao.queryBuilder()
                .where(LangDao.Properties.PreferredTarget.eq(true))
                .unique();

        if (sourceLang != null){

            if (sourceLang.equals(lang)) return;
            sourceLang.setPreferredTarget(false);
            langDao.save(sourceLang);
        }
        lang.setPreferredTarget(true);
        langDao.save(lang);
    }

    // получить предпочитаемый язык направления перевода
    public Lang getPreferredTargetLang(){

        return langDao.queryBuilder().where(LangDao.Properties.PreferredTarget.eq(true)).unique();
    }

    // поиск обьектов перевода по содержанию строки в тесте перевода или источнике в бекграунд таске
    public void selectTranslationsWithString(String search){

        final String queryString = "%" + search + "%";

        if (selectTranslationsTask != null) selectTranslationsTask.cancel(true);
        selectTranslationsTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                List<Translation> translations = translationDao.queryBuilder()
                        .whereOr(TranslationDao.Properties.Text.like(queryString), TranslationDao.Properties.Translation.like(queryString))
                        .orderDesc(TranslationDao.Properties.Id)
                        .build()
                        .list();
                TranslationManager.sharedManager.postBusEvent(Event.successEvent(NOTIFICATION_SELECT_TRANSLATIONS, translations));
                return null;
            }
        };
        selectTranslationsTask.execute();
    }

    // поиск обьектов перевода по содержанию строки в тесте перевода или источнике в бекграунд таске с меткой избранного
    public void selectFavoriteTranslationsWithString(String search){

        final String queryString = "%" + search + "%";
        if (selectFavoriteTranslationsTask != null) selectFavoriteTranslationsTask.cancel(true);

        selectFavoriteTranslationsTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                List<Translation> translations = translationDao.queryBuilder()
                        .where(TranslationDao.Properties.Favorite.eq(true))
                        .whereOr(TranslationDao.Properties.Text.like(queryString), TranslationDao.Properties.Translation.like(queryString))
                        .orderDesc(TranslationDao.Properties.Id)
                        .build()
                        .list();

                TranslationManager.sharedManager.postBusEvent(Event.successEvent(NOTIFICATION_SELECT_FAVORITE_TRANSLATIONS, translations));
                return null;
            }
        };
        selectFavoriteTranslationsTask.execute();
    }

    // удалить обьект перевода
    public void deleteTranslation(final Translation translation){

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                TranslationManager.this.translationDao.delete(translation);
                TranslationManager.sharedManager.postBusEvent(Event.successEvent(NOTIFICATION_DELETE_TRANSLATION, translation));
                return null;
            }
        }
        .execute();
    }

    // обновить поле избранного в обьекте перевода
    public void updateTranslationFavorite(Translation translation, boolean favorite){

        translation.setFavorite(favorite);
        translationDao.save(translation);
        postBusEvent(Event.successEvent(NOTIFICATION_UPDATE_TRANSLATION, translation));
    }

    // удалить обьекты перевода кроме избранных
    public void deleteAllHistory(){

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                TranslationManager.this.translationDao.queryBuilder()
                        .where(TranslationDao.Properties.Favorite.eq(false))
                        .buildDelete()
                        .executeDeleteWithoutDetachingEntities();

                TranslationManager.sharedManager.postBusEvent(Event.successEvent(NOTIFICATION_DELETE_HISTORY, null));
                return null;
            }
        }
        .execute();
    }

    // удалить избранные обьекты перевода
    public void deleteAllFavorites(){

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                TranslationManager.this.translationDao.queryBuilder()
                        .where(TranslationDao.Properties.Favorite.eq(true))
                        .buildDelete()
                        .executeDeleteWithoutDetachingEntities();

                TranslationManager.sharedManager.postBusEvent(Event.successEvent(NOTIFICATION_DELETE_FAVORITES, null));
                return null;
            }
        }.execute();
    }
}