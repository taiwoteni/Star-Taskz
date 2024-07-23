package com.theteam.taskz.utils.others;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class JsonUtils {

    public static HashMap<String,Object> convertToHashMap(JSONObject jsonObject){
        final Type hashType = new TypeToken<HashMap<String,Object>>(){}.getType();

        return new Gson().fromJson(jsonObject.toString(), hashType);
    }
    public static HashMap<String,Object> convertToHashMap(String src){
        final Type hashType = new TypeToken<HashMap<String,Object>>(){}.getType();

        return new Gson().fromJson(src, hashType);
    }

    public static JSONObject convertToJsonObject(HashMap<String,Object> map){
        JSONObject object = new JSONObject();
        map.forEach((string, o) -> {
            try {
                object.put(string,o);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        return object;
    }

    public static String prettyPrint(String src){
        final String string = src.replaceAll("\\\\", "");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Object obj = gson.fromJson(string, Object.class);
        return gson.toJson(obj);
    }
    public static String prettyPrintHash(HashMap<String,Object> src){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(src);
    }
    public static String prettyPrintArray(ArrayList<HashMap<String,Object>> src){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(src);
    }
}
