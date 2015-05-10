package com.paveynganpi.snapshare.ui;

import android.app.AlertDialog;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.paveynganpi.snapshare.adapter.UserAdapter;
import com.paveynganpi.snapshare.utils.FileHelper;
import com.paveynganpi.snapshare.utils.ParseConstants;
import com.paveynganpi.snapshare.R;

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
    protected GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid);

        //get the uri data from the MainActivity
        mMedialUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

        mGridView = (GridView) findViewById(R.id.friendsGrid);
        //when an item on the list is clicked, set the button to be visible
        mGridView.setOnItemClickListener(mOnItemClickListener);

        TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

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
                if (e == null) {

                    mFriends = friends;

                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {

                        usernames[i] = user.getUsername();
                        i++;

                    }

                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(RecipientsActivity.this, mFriends);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter) mGridView.getAdapter()).refill(mFriends);
                    }

                } else {

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

                if (message == null) {
                    //error
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getString(R.string.error_selecting_file))
                            .setTitle(getString(R.string.error_selecting_file_title));
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    send(message);
                    finish();//move out of this activity, and next in stack comes up
                }
                return true;
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    protected ParseObject createMessages() {

        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientId());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMedialUri);

        if (fileBytes == null) {
            return null;
        } else {
            if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);

            }

            String fileName = FileHelper.getFileName(this, mMedialUri, mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);
            return message;
        }

    }

    protected ArrayList<String> getRecipientId() {

        ArrayList<String> recipientIds = new ArrayList<String>();

        for (int i = 0; i < mGridView.getCount(); i++) {

            if (mGridView.isItemChecked(i)) {
                recipientIds.add(mFriends.get(i).getObjectId());
            }

        }
        return recipientIds;

    }

    protected void send(ParseObject message) {

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {
                    //success
                    Toast.makeText(RecipientsActivity.this, getString(R.string.success_message), Toast.LENGTH_LONG).show();

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(getString(R.string.error_sending_message))
                            .setTitle(getString(R.string.error_selecting_file_title))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);
            int count = mGridView.getCheckedItemCount();
            if (count >= 1) {
                //Log.d(TAG,"count "+count);
                mSendMenuItem.setVisible(true);
            } else {
                mSendMenuItem.setVisible(false);
            }

            if (mGridView.isItemChecked(position)) {
                //add recipient
                checkImageView.setVisibility(View.VISIBLE);

            } else {
                //remove recipient
                checkImageView.setVisibility(View.INVISIBLE);

            }
        }
    };

}
