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

public class NewMessageActivity extends AppCompatActivity {

    private static final String TAG = NewMessageActivity.class.getSimpleName();
    String phoneNumber;
    String textMessage;
    PendingIntent pSent;
    PendingIntent pDelivered;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
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
                //Using .ACTION_PICK might give a different option
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //Using CONTENT_TYPE might give a different option also
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
            }
        });

    }


    private void sendNow(){
        Log.d(TAG, "Trying to send message!");
        phoneNumber = mPhoneNumber.getText().toString();
        textMessage = mTextMessage.getText().toString();
        pSent = PendingIntent.getBroadcast(this, 0, new Intent("SMS Sent!"), 0);
        pDelivered = PendingIntent.getBroadcast(this, 0, new Intent("SMS Delivered"), 0);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, textMessage, pSent, pDelivered);

        Log.i(TAG, "message sent");
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
                        String number = c.getString(1);
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

    @Override
    public void onResume(){
        super.onResume();
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS has been sent", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS has been canceled", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic Failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio Off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS Delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS Not Delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        registerReceiver(smsSentReceiver, new IntentFilter("SMS SENT"));
        registerReceiver(smsDeliveredReceiver, new IntentFilter("SMS DELIVERED"));

    }

}
