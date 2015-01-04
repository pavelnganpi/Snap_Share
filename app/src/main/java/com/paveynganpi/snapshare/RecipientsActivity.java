package com.paveynganpi.snapshare;

import android.app.AlertDialog;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class RecipientsActivity extends ActionBarActivity {

    private static final String TAG = RecipientsActivity.class.getSimpleName();
    protected List<ParseUser> mFriends;
    protected ListView mListView;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected MenuItem mSendMenuItem;
    protected Uri mMedialUri;
    protected String mFileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);
        getListView().setChoiceMode(mListView.CHOICE_MODE_MULTIPLE);

        //get the uri data from the MainActivity
        mMedialUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

        //when an item on the list is clicked, set the button to be visible
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int count = getListView().getCheckedItemCount();
                if(count>=1){
                    Log.d(TAG,"count "+count);
                    mSendMenuItem.setVisible(true);
                }
                else{
                    mSendMenuItem.setVisible(false);
                }
            }
        });

    }

    @Override
    public void onResume() {

        super.onResume();

        //supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();



        //setSupportProgressBarIndeterminate(true);
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                //setSupportProgressBarIndeterminate(false);
                if(e == null){

                    mFriends = friends;

                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {

                        usernames[i] = user.getUsername();
                        i++;

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            getListView().getContext(),
                            android.R.layout.simple_list_item_checked,
                            usernames
                    );
                    //get reference to the list view

                    getListView().setAdapter(adapter);
                    //setListAdapter(adapter);

                }
                else{

                    Log.e(TAG, e.getMessage().toString());
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(e.getMessage());//creates a dialog with this message
                    builder.setTitle(R.string.error_title);
                    builder.setPositiveButton(android.R.string.ok, null);//creates a button to dismiss the dialog

                    AlertDialog dialog = builder.create();//create a dialog
                    dialog.show();//show the dialog

                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mSendMenuItem = menu.getItem(0);//get the menu Object at position 0;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_send:
                ParseObject message = createMessages();
                //send(message);
                return true;
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    //gets an instance of the listview, returns a list view which is used to be able to call
    // getListView().setChoiceMode(mListView.CHOICE_MODE_MULTIPLE);
    protected ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(android.R.id.list);
        }
        return mListView;
    }

    protected ParseObject createMessages(){

        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME,ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS,getRecipientId());
        message.put(ParseConstants.KEY_FILE_TYPE,mFileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this,mMedialUri);

        if(fileBytes == null){
            return null;
        }
        else {
            if (mFileType == ParseConstants.TYPE_IMAGE) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);

            }

            String fileName = FileHelper.getFileName(this,mMedialUri,mFileType);
            ParseFile file = new ParseFile(fileName,fileBytes);
            message.put(ParseConstants.KEY_FILE,file);
            return message;
        }

    }

    protected ArrayList<String> getRecipientId(){

        ArrayList<String> recipientIds = new ArrayList<String>();

        for(int i =0; i< getListView().getCount();i++){

            if(getListView().isItemChecked(i)){
                recipientIds.add(mFriends.get(i).getObjectId());
            }

        }
        return recipientIds;

    }

}
