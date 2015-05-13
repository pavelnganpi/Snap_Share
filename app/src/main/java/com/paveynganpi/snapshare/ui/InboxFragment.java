package com.paveynganpi.snapshare.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.paveynganpi.snapshare.R;
import com.paveynganpi.snapshare.adapter.MessageAdapter;
import com.paveynganpi.snapshare.utils.ParseConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paveynganpi on 12/31/14.
 */
public class InboxFragment extends ListFragment {

    protected List<ParseObject> mMessages; // ,ist of messages returned from the parse backend
    protected SwipeRefreshLayout mSwipeRefreshLayout;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.SwipeRefresh1,
                R.color.SwipeRefresh2,
                R.color.SwipeRefresh3,
                R.color.SwipeRefresh4);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ParseObject message = mMessages.get(position);
                String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);//get the file type
                ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);//gets file from parse
                Uri fileUri = Uri.parse(file.getUrl());//convert url to uri

                if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
                    //view image

                    Intent intent = new Intent(getActivity(), ViewImageActivity.class);
                    intent.setData(fileUri);
                    startActivity(intent);

                } else {
                    //view video
                    Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
                    intent.setDataAndType(fileUri, "video/*");
                    startActivity(intent);


                }
                //delete
                List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);
                if (ids.size() == 1) {
                    message.deleteInBackground();


                } else {
                    //remove the recipient and save
                    ids.remove(ParseUser.getCurrentUser().getObjectId());

                    ArrayList<String> idsToRemove = new ArrayList<String>();
                    idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

                    message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
                    message.saveInBackground();


                }


            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveMessages();


    }

    private void retrieveMessages() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {

                //if the user is refreshing the listview, set it to false
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                if (e == null) {
                    //success
                    mMessages = messages;

                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {

                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;

                    }
                    //if messageAdapter does not exist, then create it
                    if (getListView().getAdapter() == null) {
                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                        setListAdapter(adapter);

                    } else {
                        //if it exists, no need to recreate it,
                        //just set the data on the listview
                        ((MessageAdapter) getListView().getAdapter()).refill(mMessages);
                    }

                }


            }
        });
    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrieveMessages();
        }
    };


}
