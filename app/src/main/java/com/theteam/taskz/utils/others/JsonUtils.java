package com.theteam.taskz.utils.others;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class JsonUtils {

    public static HashMap<String,Object> convertToHashMap(JSONObject jsonObject) throws JSONException {
        final HashMap<String,Object> hash = new HashMap<>();

        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            String key = it.next();
            hash.put(key, jsonObject.get(key));
        }

        return hash;
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
