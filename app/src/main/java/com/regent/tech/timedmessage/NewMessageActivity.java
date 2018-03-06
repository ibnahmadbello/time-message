package com.regent.tech.timedmessage;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
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

    private String getPhoneNumber(){
        return mPhoneNumber.getText().toString();
    }

    private void sendNow(){
        try {
            Uri uri = Uri.parse("SMS to"+getPhoneNumber());
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
            smsIntent.putExtra("SMS body", mTextMessage.getText().toString());
            if (mPhoneNumber.length() == 0){
                Toast.makeText(this, "Please Insert a phone number", Toast.LENGTH_SHORT).show();
            } else {
            startActivity(smsIntent);
            }
        } catch (Exception e){
            Toast.makeText(this, "SMS failed, please try again later", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void sendLater(){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){}

}
