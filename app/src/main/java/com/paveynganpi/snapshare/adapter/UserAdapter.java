package com.paveynganpi.snapshare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.paveynganpi.snapshare.R;
import com.squareup.picasso.Picasso;

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
        A layout inflater is an android object that takes in an xml layouts and turns them
        into views in code that we can use
         */
        if(convertView == null) {//to avoid creating a convertview all the time
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
            holder.userImageView = (ImageView) convertView.findViewById(R.id.userImageView);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            holder.checkImageView = (ImageView) convertView.findViewById(R.id.checkImageView);
            convertView.setTag(holder);//makes our listview scroll
        }
        else{

            holder = (ViewHolder)convertView.getTag();//gets the view holder that was already created
            //if tag is no set as above, error will result due to the fact we are trying to retrieve a tag
            //that is no longer available

        }

        ParseUser user = mUsers.get(position);
//        String email = (user.getEmail()==null)?"":user.getEmail().toLowerCase();;
//        if(email.equals("")){
//            holder.userImageView.setImageResource(R.drawable.avatar_empty);
//        }
//        else{
//            String hash = MD5Util.md5Hex(email);
//            String gravatarUrl = "http://www.gravatar.com/avatar/"+hash + "?s=240&d=404";
//            Picasso.with(mContext)
//                    .load(gravatarUrl)
//                    .placeholder(R.drawable.avatar_empty)
//                    .into(holder.userImageView);
//        }

        if(user.get("profileImageUrl")!=null){
            String profileImageUrlNormalSize = user.get("profileImageUrl").toString();
                   String profileImageUrl = profileImageUrlNormalSize.substring(0, profileImageUrlNormalSize.length() - 12) + ".jpeg";
            Picasso.with(mContext)
                    .load(profileImageUrl)
                    .into(holder.userImageView);
        }
        else{
            holder.userImageView.setImageResource(R.drawable.avatar_empty);
        }
        //Log.d("profileImageUrl",user.get)
        holder.nameLabel.setText(user.getUsername());

        //checks if the item at position is checked or not
        //references the view from the parent
        GridView gridView = (GridView)parent;
        if(gridView.isItemChecked(position)){
            holder.checkImageView.setVisibility(View.VISIBLE);
        }
        else{
            holder.checkImageView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public static class ViewHolder{

        ImageView userImageView;
        ImageView checkImageView;
        TextView nameLabel;

    }

    //to refill the UserAdapter
    public void refill(List<ParseUser> users){

        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();

    }

}
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("TwitterUsers");
//        query.whereEqualTo("parseUserId", mUsers.get(position).getObjectId());
//        query.getFirstInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject parseObject, ParseException e) {
//                if (e == null) {
//
//                    //strip off _normal.jpeg to have a high resolution image
//                    String profileImageUrlNormalSize = parseObject.get("profileImageUrl").toString();
//
//                    profileImageUrl = profileImageUrlNormalSize.substring(0, profileImageUrlNormalSize.length() - 12) + ".jpeg";
//                    Picasso.with(mContext)
//                            .load(profileImageUrl)
//                            .into(holder.userImageView);
//                    Log.d("query in user ", profileImageUrl + "username is" + mUsers.get(position).getUsername());
//                } else {
//                    Log.d("error in query", e.getMessage());
//                    holder.userImageView.setImageResource(R.drawable.avatar_empty);
//                }
//            }
//        });
