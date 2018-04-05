package com.regent.tech.timedmessage;

import android.app.Dialog;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by root on 4/5/18.
 */

public class DatePickerFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.schedule_date)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(true)
                .create();
    }

    public void show(FragmentManager manager, String tag) {
    }
}
