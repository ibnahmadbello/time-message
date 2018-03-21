package com.regent.tech.timedmessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by root on 3/20/18.
 */

public class SmsReceiver extends BroadcastReceiver {

    public static final String TAG = SmsReceiver.class.getSimpleName();

    public SmsReceiver(){}


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        SmsMessage[] messages = null;
        String str = "";

        if (intentExtras != null) {
            /* Get Messages */
            Object[] sms = (Object[]) intentExtras.get("pdus");

            for (int i = 0; i < sms.length; i++){
                /* Parse Messages */
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String phone = smsMessage.getOriginatingAddress();
                String message = smsMessage.getMessageBody();

                Toast.makeText(context, phone + ": " + message, Toast.LENGTH_SHORT).show();

            }
        }
    }
}
