package com.theteam.taskz.utils.others;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static String prettyPrint(String string){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Object obj = gson.fromJson(string, Object.class);
        return gson.toJson(obj);
    }
}
