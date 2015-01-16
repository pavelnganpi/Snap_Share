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
import com.parse.ParseUser;
import com.paveynganpi.snapshare.R;
import com.paveynganpi.snapshare.utils.ParseConstants;

import java.util.Date;
import java.util.List;

/**
 * Created by paveynganpi on 1/5/15.
 */
public class UserAdapter extends ArrayAdapter<ParseUser> {

    protected List<ParseUser> mUsers;
    protected Context mContext;

    public UserAdapter(Context context, List<ParseUser> users){
        super(context, R.layout.message_item,users); //call the super class
        mUsers = users;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
           // holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            convertView.setTag(holder);//makes our listview scroll
        }
        else{

            holder = (ViewHolder)convertView.getTag();//gets the view holder that was already created
            //if tag is no set as above, error will result due to the fact we are trying to retrieve a tag
            //that is no longer available

        }

        ParseUser user = mUsers.get(position);

//        if(user.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
//            holder.iconImageView.setImageResource(R.drawable.ic_picture);
//        }
//        else {
//            holder.iconImageView.setImageResource(R.drawable.ic_video);
//        }

        holder.nameLabel.setText(user.getUsername());

        return convertView;
    }

    public static class ViewHolder{

        //ImageView iconImageView;
        TextView nameLabel;

    }

    //to refill the messageAdapter
    public void refill(List<ParseUser> users){

        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();

    }

}
