package com.vitaliikuznetsov.vkt.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by VItalii on 19/04/2017.
 */

public class ytTranslationDeserializer implements JsonDeserializer<Translation> {

    private static final String SER_CODE = "code";
    private static final String SER_LANG = "lang";
    private static final String SER_TEXT = "text";

    @Override
    public Translation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        return null;
    }
}
