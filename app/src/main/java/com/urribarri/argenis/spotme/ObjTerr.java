package com.urribarri.argenis.spotme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by argenis on 06/10/2016.
 */

public class ObjTerr {

    ArrayList<ObjDraft> draftList= new ArrayList<ObjDraft>();

    public ObjTerr(ArrayList<ObjDraft> draftList) {
        this.draftList = draftList;
    }

    public ArrayList<ObjDraft> getDraftList() {
        return draftList;
    }

    public ObjDraft getDraft(int i) {
        return this.draftList.get(i);
    }

    public static ObjTerr onedraftList(ArrayList<LatLng> input){

        if((ObjDraft.draftValidation(input.size()))==true){
            //TODO: Alert dialog: There's only 1 component. Are you sure?
            ObjDraft draft= new ObjDraft(input);

            ArrayList<ObjDraft> onedraftlist= new ArrayList<ObjDraft>();
            onedraftlist.add(draft);

            ObjTerr terr= new ObjTerr(onedraftlist);

            return terr;

        } else {
            return null;
        }
    }

    private static boolean onedraftListConfirmDialog(Context context){
        Boolean confirmation= new Boolean(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

        return confirmation;
    }
}