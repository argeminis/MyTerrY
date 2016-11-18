package com.urribarri.argenis.spotme;

import com.google.gson.Gson;

/**
 * Created by argenis on 17/11/2016.
 */

public class ManagerParser {

    // convert User object user to JSON format
    //com

    public static String JSONType(Object object){
        Gson gson = new Gson();
        String object_json = gson.toJson(object);

        return object_json;

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
}
