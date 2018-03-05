package com.regent.tech.timedmessage;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewMessageActivity extends AppCompatActivity {

    private static final String TAG = NewMessageActivity.class.getSimpleName();

    private EditText mPhoneNumber;
    private EditText mTextMessage;
    private Button mSendNow;
    private Button mSendLater;

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
            }
        });

        mSendLater = (Button) findViewById(R.id.send_later);
        mSendLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLater();
            }
        });

    }

    private String getPhoneNumber(){
        return mPhoneNumber.getText().toString();
    }

    private void sendNow(){
        try {
            Uri uri = Uri.parse("SMS to"+getPhoneNumber());
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
            smsIntent.putExtra("SMS body", mTextMessage.getText().toString());
            startActivity(smsIntent);
        } catch (Exception e){
            Toast.makeText(this, "SMS failed, please try again later", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void sendLater(){

    }

}
