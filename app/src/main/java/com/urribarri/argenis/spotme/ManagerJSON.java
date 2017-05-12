package com.urribarri.argenis.spotme;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;

/**
 * 12/05/2017.
 */

public class ManagerJSON {

    public static JSONObject objectToJsonObject(Object object) throws JSONException {
        Gson gson= new Gson();
        String json_string= gson.toJson(object);

        return new JSONObject(json_string);
    }

    public static void writetofile(Object object, String path, String filename) throws IOException {
        String path_to_file= "D:\\"+filename+".json";
        Gson gson= new Gson();
        gson.toJson(object, new FileWriter(path_to_file));
    }
}