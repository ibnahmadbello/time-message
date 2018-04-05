package com.regent.tech.timedmessage;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by root on 4/5/18.
 */

public class DateDialog extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setTitle(R.string.schedule_date);
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
