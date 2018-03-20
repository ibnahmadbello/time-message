package com.regent.tech.timedmessage;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final  String TAG = MainActivity.class.getSimpleName();

    private static final int TYPE_INCOMING_MESSAGE = 1;
    private FloatingActionButton actionButton;
    private ListView messageList;
    private MessageListAdapter messageListAdapter;
    private CustomHandler customHandler;
    private ArrayList<Message> recordsStored;
    private ArrayList<Message> listInboxMessages;
    private ProgressDialog progressDialogInbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionButton = (FloatingActionButton) findViewById(R.id.fab);
        setupFloatingActionButton();
        initViews();

    }

    private void setupFloatingActionButton(){
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewMessageActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){

        switch (menuItem.getItemId()){
            case R.id.delete_message:
                //Do nothing for now
                return true;
            case R.id.schedule_message:
                //Do nothing for now
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onResume(){
        super.onResume();
        populateMessageList();
    }

    private void initViews(){
        customHandler = new CustomHandler(this);
        progressDialogInbox = new ProgressDialog(this);

        recordsStored = new ArrayList<Message>();
        messageList = (ListView) findViewById(R.id.message_list);
        populateMessageList();
    }

    public void populateMessageList(){
        Log.d(TAG, "About to fetch message.");
        fetchInboxMessages();
        messageListAdapter = new MessageListAdapter(this, R.layout.message_list_item, recordsStored);
        messageList.setAdapter(messageListAdapter);
    }

    private void showProgressDialog(String message){
        progressDialogInbox.setMessage(message);
        progressDialogInbox.setIndeterminate(true);
        progressDialogInbox.setCancelable(true);
        progressDialogInbox.show();
    }

    private void fetchInboxMessages(){
        if (listInboxMessages == null){
            showProgressDialog("Fetching Inbox Messages...");
            startThread();
        } else {
            // messageType = TYPE_INCOMING_MESSAGE
            recordsStored = listInboxMessages;
            messageListAdapter.setArrayList(recordsStored);
        }
    }

    public class FetchMessageThread extends Thread{
        public int tag = -1;

        public FetchMessageThread(int tag){
            this.tag = tag;
        }

        @Override
        public void run(){
            recordsStored = fetchInboxSms(TYPE_INCOMING_MESSAGE);
            listInboxMessages = recordsStored;
            customHandler.sendEmptyMessage(0);
        }
    }

    public ArrayList<Message> fetchInboxSms(int type){
        ArrayList<Message> smsInbox = new ArrayList<Message>();
        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor = this.getContentResolver().query(uriSms, new String[]{"_id", "address",
                "date", "body", "type", "read"}, "type=" + type, null, "date" + " COLLATE LOCALIZED ASC");
        if (cursor != null){
            cursor.moveToLast();
            if (cursor.getCount() > 0){
                do {
                    Message message = new Message();
                    message.messageNumber = cursor.getString(cursor.getColumnIndex("address"));
                    message.messageContent = cursor.getString(cursor.getColumnIndex("body"));
                    smsInbox.add(message);
                } while (cursor.moveToPrevious());
            }
        }
        return smsInbox;
    }

    private FetchMessageThread fetchMessageThread;
    private int currentCount = 0;

    public synchronized void startThread(){
        if (fetchMessageThread == null){
            fetchMessageThread = new FetchMessageThread(currentCount);
            fetchMessageThread.start();
        }
    }

    public synchronized void stopThread(){
        if (fetchMessageThread != null){
            Log.i(TAG, "Stop thread.");
            FetchMessageThread messageThread = fetchMessageThread;
            currentCount = fetchMessageThread.tag == 0 ? 1 : 0;
            fetchMessageThread = null;
            messageThread.interrupt();
        }
    }


    static class CustomHandler extends Handler {
        private final WeakReference<MainActivity> activityHolder;

        CustomHandler(MainActivity inboxListActivity){
            activityHolder = new WeakReference<MainActivity>(inboxListActivity);
        }

        @Override
        public void handleMessage(android.os.Message msg){
            MainActivity inboxListActivity = activityHolder.get();
            if (inboxListActivity.fetchMessageThread != null &&
                    inboxListActivity.currentCount == inboxListActivity.fetchMessageThread.tag){
                Log.i(TAG, "Received results.");
                inboxListActivity.fetchMessageThread = null;
                inboxListActivity.messageListAdapter.setArrayList(inboxListActivity.recordsStored);
                inboxListActivity.progressDialogInbox.dismiss();
            }
        }
    }

    private DialogInterface.OnCancelListener dialogCancelListener = new DialogInterface.OnCancelListener(){
        @Override
        public void onCancel(DialogInterface dialog){
            stopThread();
        }
    };

}
