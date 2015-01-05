package com.paveynganpi.snapshare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by paveynganpi on 12/31/14.
 */
public class InboxFragment extends ListFragment {

    protected List<ParseObject> mMessages; // ,ist of messages returned from the parse backend


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ParseObject message = mMessages.get(position);
                String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);//get the file type
                ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);//gets file from parse
                Uri fileUri = Uri.parse(file.getUrl());//convert url to uri

                if(messageType.equals(ParseConstants.TYPE_IMAGE)){
                //view image

                    Intent intent = new Intent(getActivity(),ViewImageActivity.class);
                    intent.setData(fileUri);
                    startActivity(intent);

                }
                else{
                //view video



                }




            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {

                if(e == null){
                    //success
                    mMessages = messages;

                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {

                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;

                    }

                    MessageAdapter adapter = new MessageAdapter(getListView().getContext(),mMessages);

                    //get reference to the list view

                    setListAdapter(adapter);


                }
                else{


                }


            }
        });


    }



}
