package com.urribarri.argenis.spotme;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by argenis on 06/10/2016.
 */

public class ObjTerr {

    ArrayList<ObjDraft> draftList= new ArrayList<ObjDraft>();

    public ObjTerr(ArrayList<ObjDraft> list) {
        draftList = list;
    }

    public ArrayList<ObjDraft> getDraftList() {
        return draftList;
    }

    public ObjDraft getDraft(int i) {
        return this.draftList.get(i);
    }

    public static Boolean draftListValidation(int quantity){
        if (quantity > 0) {return true;}
        else {return false;}
    }
}