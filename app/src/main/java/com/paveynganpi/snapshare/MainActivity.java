package com.paveynganpi.snapshare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    //request codes for the intent to take a picture. takePhotoIntent
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    protected Uri mMediaUri;


    //listener which runs when any of the items on the alert dialog are clicked. i.e choose picture ...
    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            switch (which){

                case 0://take a pic

                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutPutMediaFileUri(MEDIA_TYPE_IMAGE);
                    if(mMediaUri == null){

                        //error
                        Toast.makeText(MainActivity.this,R.string.error_external_storage,Toast.LENGTH_LONG).show();

                    }
                    else {
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);

                        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                    }

                case 1://take vid
//                    Intent takeVideoIntent = new Intent((MediaStore.ACTION_VIDEO_CAPTURE));
//                    mMediaUri = getOutPutMediaFileUri(MEDIA_TYPE_VIDEO);
//                    if(mMediaUri == null){
//
//                        //error
//                        Toast.makeText(MainActivity.this,R.string.error_external_storage,Toast.LENGTH_LONG).show();
//
//                    }
//                    else {
//                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
//
//                        startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST);
//                        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);//max length of video
//                        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);//low video quality
//                        startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST);
//                    }

                case 2://choose pic

                case 3://choose vid


            }

        }

        private Uri getOutPutMediaFileUri(int mediaType) {
            // To be safe, you should check that the SDCard is mounted
            // using Environment.getExternalStorageState() before doing this.


            if(isExternalStoriageAvailable()){
                //get the URI

                //1. get the external storage
                String appName = MainActivity.this.getString(R.string.app_name);
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        appName);

                //create subdirectory
                if(!mediaStorageDir.exists()){
                    if(!mediaStorageDir.mkdirs()){

                        Log.e(TAG,"Failed to create a directory");
                        return null;
                    }

                }

                //create a file name
                //create a file

                File mediaFile;//create the media file to store our image or video
                Date now = new Date();//create a date which is current
                String timestamp = new SimpleDateFormat("yyyyMMdd__HHmmss", Locale.US).format(now);//now converted to
                //to a string as a to name our media file

                //now get the paths
                String path  = mediaStorageDir.getPath() + File.separator;

                //now depending on media type, give the right extensions
                if(mediaType == MEDIA_TYPE_IMAGE){
                    mediaFile = new File(path + "IMG_"+ timestamp + ".jpg");

                }
                else if(mediaType == MEDIA_TYPE_VIDEO){

                    mediaFile = new File(path + "VID" + timestamp + ".mp4");

                }
                else{
                    return null;
                }
                Log.d(TAG, "FILE "+Uri.fromFile(mediaFile));

                //return the file's uri

                return Uri.fromFile(mediaFile);
            }
            else {

                return null;
            }
        }

        //checks if there is an external storage
        private  boolean isExternalStoriageAvailable(){

            String state = Environment.getExternalStorageState();

            if(state.equals(Environment.MEDIA_MOUNTED)){
                return true;
            }
            else{
                return false;
            }

        }

    };


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        //check if current user is still in session
        ParseUser currentUser = ParseUser.getCurrentUser();

        //if current user is not in session, then take the user to the loginActivity
        if(currentUser == null) {
            navigateToLogin();

        }

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this,getSupportFragmentManager());//added this as the first parameter
                                       //because we added a new parameter of type Context in the SectionsPagerAdapter

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            //success, save to gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(mMediaUri);
            sendBroadcast(mediaScanIntent);

        }

        if(resultCode !=RESULT_CANCELED){
            Toast.makeText(MainActivity.this,R.string.general_error,Toast.LENGTH_LONG).show();

        }

    }

    private void navigateToLogin() {
        //start the loginActivity
        Intent intent = new Intent(this, LoginActivity.class);

        //add the loginActivity to the top of stack, and clear the inbox page
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//add the loginActivity task
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//clear the previous task
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.me, menu);
        return true;
    }

    //used to select any options in the action bar. e.g log out
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();//get the id

        //noinspection SimplifiableIfStatement. logout if item is matches with action_logout
        switch (itemId) {
            case R.id.action_logout:
                ParseUser.logOut();
                //move to login screen
                navigateToLogin();
            case R.id.action_edit_friends:

                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
            case R.id.action_camera:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices,mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


}
//Intent takeVideoIntent = new Intent((MediaStore.ACTION_VIDEO_CAPTURE));
//                    mMediaUri = getOutPutMediaFileUri(MEDIA_TYPE_VIDEO);
//                    if(mMediaUri == null){
//
//                        //error
//                        Toast.makeText(MainActivity.this,R.string.error_external_storage,Toast.LENGTH_LONG).show();
//
//                    }
//                    else {
//                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
//
//                        startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST);
//                        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);//max length of video
//                        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);//low video quality
//                        startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST);
//                    }