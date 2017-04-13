package com.vitaliikuznetsov.vkt.model;

import android.util.Log;
import android.view.KeyboardShortcutGroup;
import android.view.animation.LayoutAnimationController;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by vitalykuznetsov on 13/04/17.
 */

public class ytLangDeserializer implements JsonDeserializer<ArrayList> {

    private static final String SER_CONTENT_ARRAY = "langs";

    @Override
    public ArrayList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()){
            JsonObject jsonObject = json.getAsJsonObject();
            if (jsonObject.has(SER_CONTENT_ARRAY) && jsonObject.get(SER_CONTENT_ARRAY).isJsonObject()){
                JsonObject langs = jsonObject.getAsJsonObject(SER_CONTENT_ARRAY);
                JSONObject jso = null;
                try {
                    jso = new JSONObject(langs.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Iterator<String> iterator = jso.keys();
                while (iterator.hasNext()){
                    Log.d("ytLangDeserializer", iterator.next());
                }
            }
        }
        return null;
    }

//    public Lang deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException, JSONException {
//        if (json.isJsonObject()){
//            JsonObject jsonObject = json.getAsJsonObject();
//            if (jsonObject.has(SER_CONTENT_ARRAY) && jsonObject.get(SER_CONTENT_ARRAY).isJsonObject()){
//                JsonObject langs = jsonObject.getAsJsonObject(SER_CONTENT_ARRAY);
//                JSONObject jso = null;
//                try {
//                    jso = new JSONObject(langs.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Iterator<String> iterator = jso.keys();
//                while (iterator.hasNext()){
//                    Log.d("ytLangDeserializer", iterator.next());
//                }
//            }
//        }
//        return null;
//    }
}
