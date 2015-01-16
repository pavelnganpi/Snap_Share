package com.paveynganpi.snapshare.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.paveynganpi.snapshare.R;
import com.paveynganpi.snapshare.utils.ParseConstants;

import java.util.Date;
import java.util.List;

/**
 * Created by paveynganpi on 1/5/15.
 */
public class UserAdapter extends ArrayAdapter {

    protected List<ParseObject> mMessages;
    protected Context mContext;

    public UserAdapter(Context context, List<ParseObject> messages){
        super(context, R.layout.message_item,messages); //call the super class
        mMessages = messages;
        mContext = context;

    }

    //inflate the view contentView from the layout fule and return it to the list
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        /*
        A layout inflater is an androinf object that takes in an xml layouts and turns them
        into views in code that we can use
         */
        if(convertView == null) {//to avoid creating a converview all the time
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            holder.timeLabel = (TextView)convertView.findViewById(R.id.timeLabel);
            convertView.setTag(holder);//makes our listview scroll
        }
        else{

            holder = (ViewHolder)convertView.getTag();//gets the view holder that was already created
            //if tag is no set as above, error will result due to the fact we are trying to retrieve a tag
            //that is no longer available

        }

        ParseObject message = mMessages.get(position);


        Date createdAt = message.getCreatedAt();//get the date the message was created from parse backend
        long now = new Date().getTime();//get current date
        String convertedDate = DateUtils.getRelativeTimeSpanString(
                createdAt.getTime(),now,DateUtils.SECOND_IN_MILLIS).toString();
        holder.timeLabel.setText(convertedDate); //sets the converted date into the message_item.xml view


        if(message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
            holder.iconImageView.setImageResource(R.drawable.ic_picture);
        }
        else {
            holder.iconImageView.setImageResource(R.drawable.ic_video);
        }
        holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));

        return convertView;
    }

    public static class ViewHolder{

        ImageView iconImageView;
        TextView nameLabel;
        TextView timeLabel;

    }

    //to refill the messageAdapter
    public void refill(List<ParseObject> messages){

        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();

    }

}
