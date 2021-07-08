package com.regent.tech.timedmessage;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class NewMessageActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    private static final String TAG = NewMessageActivity.class.getSimpleName();
    private long mDueDate = Long.MAX_VALUE;

    String phoneNumber;
    String textMessage;
    private EditText mPhoneNumber;
    private EditText mTextMessage;
    private TextView timeTextView, dateTextView;
    private View mSelectDate, mSelectTime;
    private Button mSendNow;
    private Button mSendLater;
    private Button mSearchContact;
    private DateDialog dateDialog;
    Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        mPhoneNumber = (EditText) findViewById(R.id.phone_number);
        mTextMessage = (EditText) findViewById(R.id.text_message);
        timeTextView = (TextView) findViewById(R.id.send_time);
        dateTextView = (TextView) findViewById(R.id.send_date);
        mSelectDate = findViewById(R.id.select_date);
        mSelectTime = findViewById(R.id.select_time);
        mSendNow = (Button) findViewById(R.id.send_now);
        mSendLater = (Button) findViewById(R.id.send_later);
        mSearchContact = (Button) findViewById(R.id.search_contacts_button);
        dateDialog = new DateDialog();

        mSelectDate.setOnClickListener(this);
        mSelectTime.setOnClickListener(this);
        mSendNow.setOnClickListener(this);
        mSendLater.setOnClickListener(this);
        mSearchContact.setOnClickListener(this);

    }

    public void showDateTimePicker(){
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.set(year, month, dayOfMonth);
                new TimePickerDialog(NewMessageActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        Log.i(TAG, "THe chosen one " + date.getTime());
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show();
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

//    private void sendLater(View view){
//        FragmentManager manager = getFragmentManager();
//        DatePickerFragment dialog = new DatePickerFragment();
//        dialog.show(manager, DatePickerFragment.DIALOG_DATE);
//    }

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


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Date todayDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        setDateSelection(calendar.getTimeInMillis());
//        Date setDate = new Date(year, month, dayOfMonth);
//        if (setDate.before(todayDate)){
//            Toast.makeText(this, "Date Already pass!", Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            DialogFragment timeFragment = new TimePickerFragment();
//            timeFragment.show(getSupportFragmentManager(), TAG);
//        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.search_contacts_button:
                //Using .ACTION_GET_CONTENT might give a different option
                Intent intent = new Intent(Intent.ACTION_PICK);
                //Using CONTENT_ITEM_TYPE might give a different option also
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 1);
                break;
            case R.id.send_later:
//                DialogFragment dateFragment = new DatePickerFragment();
//                dateFragment.show(getSupportFragmentManager(), TAG);
//                showDateTimePicker();
//                sendLater();
//                dateDialog.show(getSupportFragmentManager(), TAG);
                break;
            case R.id.send_now:
                sendNow();//TODO
                finish();
                break;
            case R.id.select_date:
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), TAG);
                break;
            case R.id.select_time:
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getSupportFragmentManager(), TAG);
                break;
            default:
                break;
        }
    }

    // Manage the selected date value
    public void setDateSelection(long selectedDateStamp){
        mDueDate = selectedDateStamp;
        updateDateDisplay();
    }

    public long getDateSelection(){
        return mDueDate;
    }

    private void updateDateDisplay() {
        if (getDateSelection() == Long.MAX_VALUE){
            dateTextView.setText(R.string.not_set);
        } else {
            CharSequence formatted = DateUtils.getRelativeTimeSpanString(this, mDueDate);
            dateTextView.setText(formatted);
        }
    }




}
