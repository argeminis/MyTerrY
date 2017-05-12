package com.urribarri.argenis.spotme;

import java.util.ArrayList;

// Created by argenis on 06/10/2016

public class ObjTerr {

    ArrayList<ObjDraft> draftList= new ArrayList<ObjDraft>();//variable name used as key in JSON (drafList)

    public ObjTerr(ArrayList<ObjDraft> list) {
        draftList = list;
    }

    public static Boolean draftListValidation(int quantity){
        if (quantity > 0) {return true;}
        else {return false;}
    }

    public ObjDraft getDraft(int i) {
        return this.draftList.get(i);
    }
}