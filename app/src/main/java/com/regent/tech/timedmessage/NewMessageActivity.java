package com.regent.tech.timedmessage;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class NewMessageActivity extends AppCompatActivity {

    private static final String TAG = NewMessageActivity.class.getSimpleName();
    String phoneNumber;
    String textMessage;
    private EditText mPhoneNumber;
    private EditText mTextMessage;
    private Button mSendNow;
    private Button mSendLater;
    private Button mSearchContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        mPhoneNumber = (EditText) findViewById(R.id.phone_number);
        mTextMessage = (EditText) findViewById(R.id.text_message);

        mSendNow = (Button) findViewById(R.id.send_now);
        mSendNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNow();//TODO
                finish();
            }
        });

        mSendLater = (Button) findViewById(R.id.send_later);
        mSendLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLater();
            }
        });

        mSearchContact = (Button) findViewById(R.id.search_contacts_button);
        mSearchContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Using .ACTION_GET_CONTENT might give a different option
                Intent intent = new Intent(Intent.ACTION_PICK);
                //Using CONTENT_ITEM_TYPE might give a different option also
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 1);
            }
        });

    }


    private void sendNow(){
        Log.d(TAG, "Trying to send message!");
        SmsManager smsManager = SmsManager.getDefault();
        phoneNumber = mPhoneNumber.getText().toString();
        textMessage = mTextMessage.getText().toString();
        if (textMessage.length() > 160){
            ArrayList<String> messageParts = smsManager.divideMessage(textMessage);
            Context curContext = this.getApplicationContext();
            int partsCount = messageParts.size();
            ArrayList<PendingIntent> sentPendings = new ArrayList<PendingIntent>(partsCount);
            ArrayList<PendingIntent> deliveredPendings = new ArrayList<PendingIntent>(partsCount);
            for (int i = 0; i < partsCount; i++){
                PendingIntent sentPending = PendingIntent.getBroadcast(curContext, 0, new Intent("SENT"), 0);
                curContext.registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        switch (getResultCode()){
                            case Activity.RESULT_OK:
                                Toast.makeText(getBaseContext(), "Sent.", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                Toast.makeText(getBaseContext(), "Not Sent: Generic failure.",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                Toast.makeText(getBaseContext(), "Not Sent: No service (possibly, " +
                                        "no SIM-card).", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                Toast.makeText(getBaseContext(), "Not Sent: Null PDU.",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                Toast.makeText(getBaseContext(), "Not Sent: Radio off (possibly, " +
                                        "Airplane mode is enabled in Settings).", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new IntentFilter("SENT"));
                sentPendings.add(sentPending);

                PendingIntent deliveredPending = PendingIntent.getBroadcast(curContext, 0, new Intent("DELIVERED"), 0);
                curContext.registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        switch (getResultCode()){
                            case Activity.RESULT_OK:
                                Toast.makeText(getBaseContext(), "Delivered.", Toast.LENGTH_SHORT).show();
                                break;
                            case Activity.RESULT_CANCELED:
                                Toast.makeText(getBaseContext(), "Not Delivered: Canceled.",
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new IntentFilter("DELIVERED"));
                deliveredPendings.add(deliveredPending);
            }
            smsManager.sendMultipartTextMessage(phoneNumber, null, messageParts, sentPendings, deliveredPendings);
        } else {
            Context curContext = this.getApplicationContext();
            PendingIntent sentPending = PendingIntent.getBroadcast(curContext, 0, new Intent("SENT"), 0);
            curContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()){
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), "Sent.", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(getBaseContext(), "Not Sent: Generic failure.",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Toast.makeText(getBaseContext(), "Not Sent: No service (possibly, " +
                                    "no SIM-card).", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Toast.makeText(getBaseContext(), "Not Sent: Null PDU.",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Toast.makeText(getBaseContext(), "Not Sent: Radio off (possibly, " +
                                    "Airplane mode is enabled in Settings).", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter("SENT"));

            PendingIntent deliveredPending = PendingIntent.getBroadcast(curContext, 0, new Intent("DELIVERED"), 0);
            curContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()){
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), "Delivered.", Toast.LENGTH_SHORT).show();
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(getBaseContext(), "Not Delivered: Canceled.",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter("DELIVERED"));
            smsManager.sendTextMessage(phoneNumber, null, textMessage, sentPending, deliveredPending);
        }


    }

    private void sendLater(){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (data != null){
            Uri uri = data.getData();

            if (uri != null){
                Cursor c = null;
                try {
                    c = getContentResolver().query(uri, new String[]
                            {ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);

                    if (c != null && c.moveToFirst()){
                        String number = c.getString(0);
                        //int type = c.getString(1); Shows the type of number: mobile(2), home(1)
                        mPhoneNumber.setText(number);
                    }
                } finally {
                    if (c != null){
                        c.close();
                    }
                }
            }
        }
    }


}
