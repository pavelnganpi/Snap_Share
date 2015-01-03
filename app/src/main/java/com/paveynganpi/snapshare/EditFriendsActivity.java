package com.paveynganpi.snapshare;

import android.app.AlertDialog;
import android.app.ListActivity;
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
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditFriendsActivity extends ActionBarActivity  {

    private static final String TAG = EditFriendsActivity.class.getSimpleName();
    protected List<ParseUser> mUsers;
    protected ListView mListView;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_edit_friends);
       // myList=(ListView)findViewById(android.R.id.list);//instantiate mylist
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getListView().setChoiceMode(mListView.CHOICE_MODE_MULTIPLE);

        //whenever an item in the friends list is selected
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(getListView().isItemChecked(position)) {
                    //add friends
                    mFriendsRelation.add(mUsers.get(position));//add the friend at that position

                }
                else{
                    //remove friends
                    mFriendsRelation.remove(mUsers.get(position));

                }
                mCurrentUser.saveInBackground(new SaveCallback() {//save in parse
                    @Override
                    public void done(ParseException e) {

                        if (e != null) {

                            Log.e(TAG, e.getMessage());

                        }

                    }
                });

            }
        });

    }

    //runs each time the activity is running

    @Override
    protected void onResume() {

        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        setSupportProgressBarIndeterminateVisibility(true);




        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                setSupportProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    //success
                    mUsers = users;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers) {

                        usernames[i] = user.getUsername();
                        i++;

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            EditFriendsActivity.this,
                            android.R.layout.simple_list_item_checked,
                            usernames
                    );

                    //get reference to the list view

                    getListView().setAdapter(adapter);
                    addFriendsCheckMark();//makes sure that the user's friends are checked even when we come back
                    //setListAdapter(adapter);

                } else {

                    Log.e(TAG, e.getMessage().toString());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setMessage(e.getMessage());//creates a dialog with this message
                    builder.setTitle(R.string.error_title);
                    builder.setPositiveButton(android.R.string.ok, null);//creates a button to dismiss the dialog

                    AlertDialog dialog = builder.create();//create a dialog
                    dialog.show();//show the dialog

                }

            }
        });

    }

    //gets an instance of the listview, returns a list view which is used to be able to call
    // getListView().setChoiceMode(mListView.CHOICE_MODE_MULTIPLE);
    protected ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(android.R.id.list);
        }
        return mListView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void addFriendsCheckMark(){

        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {

                if(e == null) {
                    //success
                    for (int i = 0; i < mUsers.size(); i++) {

                        ParseUser user = mUsers.get(i);

                        for (ParseUser friend : friends) {

                            if (user.getObjectId().equals(friend.getObjectId())) {

                                getListView().setItemChecked(i, true);

                            }

                        }

                    }
                }
                else{
                    Log.e(TAG,e.getMessage());
                }

            }
        });

    }

}
