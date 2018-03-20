package com.regent.tech.timedmessage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by root on 3/20/18.
 */

public class MessageListAdapter extends ArrayAdapter<Message> {

    private Context ctx;
    public ArrayList<Message> messageArrayList;
    public static boolean isDialogOpen = true;

    public MessageListAdapter(Context context, int textViewResourcesID, ArrayList<Message> messageArrayList){
        super(context, textViewResourcesID);
        this.messageArrayList = messageArrayList;
        this.ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Holder holder;
        View convertView1 = convertView;
        LinearLayout layout;
        if (convertView1 == null){
            holder = new Holder();
            LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView1 = vi.inflate(R.layout.message_list_item, null);
            holder.messageTo = (TextView) convertView1.findViewById(R.id.message_number);
            holder.messageContent = (TextView) convertView1.findViewById(R.id.message_detail);
            convertView1.setTag(holder);
        } else {
            holder = (Holder) convertView1.getTag();
        }
        Message message = getItem(position);
        holder.messageTo.setText(message.messageNumber + " : ");
        holder.messageContent.setText(message.messageContent);
        return convertView1;
    }

    @Override
    public int getCount(){
        return messageArrayList.size();
    }

    @Override
    public Message getItem(int position){
        return messageArrayList.get(position);
    }

    public void setArrayList(ArrayList<Message> messageArrayList){
        this.messageArrayList = messageArrayList;
        notifyDataSetChanged();
    }

    private class Holder{
        public TextView messageTo, messageContent;
    }

}
