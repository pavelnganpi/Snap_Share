package com.paveynganpi.snapshare.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.paveynganpi.snapshare.adapter.UserAdapter;
import com.paveynganpi.snapshare.utils.ParseConstants;
import com.paveynganpi.snapshare.R;

import java.util.List;

/**
 * Created by paveynganpi on 12/31/14.
 */
public class FriendsFragment extends Fragment implements AbsListView.OnScrollListener {//does extend listfragment anymore since we are using a gridview now
    private static final String TAG = FriendsFragment.class.getSimpleName();
    protected List<ParseUser> mFriends;
    //protected ListView mListView;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected GridView mGridView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_grid, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.friendsGrid);

        //since we using gridview now instead of listview, we have to set our own emptyview
        TextView emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

        return rootView;


    }


    @Override
    public void onResume() {

        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();

        getActivity().setProgressBarIndeterminate(true);
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                getActivity().setProgressBarIndeterminate(false);
                if (e == null) {

                    mFriends = friends;

                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {

                        usernames[i] = user.getUsername();
                        i++;
                        Log.d("FRIENDS", user.getUsername());

                    }

                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter) mGridView.getAdapter()).refill(mFriends);
                    }


                } else {

                    Log.e(TAG, e.getMessage().toString());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (scrollState == SCROLL_STATE_IDLE) {
            if (view.getLastVisiblePosition() >= view.getCount() - 1 - 0) {
                //++;
                //load more list items:
                ((UserAdapter) mGridView.getAdapter()).refill(mFriends);
            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
