package com.urribarri.argenis.spotme;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by argenis on 17/11/2016.
 */

public class ManagerParser {

    /** Convert object to JSON */
    public static String JSONType(Object object) {
        Gson gson = new Gson();
        String object_json = gson.toJson(object);

        return object_json;
    }

    /**To rebuild components from JSON */
    public static LatLng LatLngType(double latitude, double longitude){
        LatLng latLng= new LatLng(latitude,longitude);

        return latLng;
    }

    /**To rebuild Terr saved as JSON string */
    public static ObjTerr rebuildTerr(String json_terr) {
        Gson gson = new GsonBuilder().create();
        ObjTerr t = gson.fromJson(json_terr, ObjTerr.class);

        return t;
    }

    // store in SharedPreferences
        /*String id =  "" +  object.getId(); // get storage key
        editor.putString(id, user_json);
        editor.commit();
        */

        // time flies...
        // do the reverse operation
        /*user_json = settings.getString(id, "");
        object  = gson.fromJson(user_json, User.class);*/
    }

