package com.urribarri.argenis.spotme;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by argenis on 17/11/2016.
 */

public class ManagerError {

    //com
    public static void log(String label, String value){
        Log.i("*****SPOTME*****", label+" ||----->> "+value);
    }

    public static void errorOnDraftQuantity(Context context){
        Toast.makeText(context,
                "There are no valid drafts",
                Toast.LENGTH_SHORT).show();
    }

    public static void errorOnDraftPoints(Context context){
        Toast.makeText(context,
                "Please set at least 3 points",
                Toast.LENGTH_SHORT).show();
    }
}
