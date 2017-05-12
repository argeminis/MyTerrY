package com.urribarri.argenis.spotme;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 Created by argenis on 08/09/2016.
*/

public class ObjDraft {

    List<List<LatLng>> userInput= new ArrayList<>();

    public ObjDraft(List<List<LatLng>> input){
        userInput= input;
    }

    public static Boolean draftValidation(int points){
        if (points > 2) {return true;} else {return false;}
    }
}