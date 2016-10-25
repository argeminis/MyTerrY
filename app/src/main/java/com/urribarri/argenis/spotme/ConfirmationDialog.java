package com.urribarri.argenis.spotme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * Created by argenis on 25/10/2016.
 */

public class ConfirmationDialog {

    public static AlertDialog confirmDialog(final Context context) {
        AlertDialog ad= new AlertDialog.Builder(context)
                .setTitle("Title")
                .setMessage("Question?")
                .setIcon(android.R.drawable.ic_dialog_alert)

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(context,"Something", Toast.LENGTH_SHORT).show();
                    }
                })

                .setNegativeButton(android.R.string.no, null)
                .show();

        return ad;
    }
}
