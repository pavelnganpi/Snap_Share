package com.paveynganpi.snapshare.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.twitter.Twitter;
import com.paveynganpi.snapshare.POJO.TwitterFolloweePojo;
import com.paveynganpi.snapshare.POJO.TwitterUserpojo;
import com.paveynganpi.snapshare.R;
import com.paveynganpi.snapshare.SnapShareApplication;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginActivity extends ActionBarActivity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginButton;
    protected TextView mSignUpTextView;
    protected ParseUser mParseCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);//used for adding a spinner to show user time of execution
        setContentView(R.layout.activity_login);

        //hide the action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mSignUpTextView = (TextView) findViewById(R.id.signUpText);

        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });

        //initialize the instance variables with the respective data in the txt fields
        mUsername = (EditText) findViewById(R.id.usernameField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        mLoginButton = (Button) findViewById(R.id.loginButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseTwitterUtils.logIn(LoginActivity.this, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage("Sorry, Please try logging in again");//creates a dialog with this message
                            builder.setTitle("Opps , error loging in with twitter");
                            builder.setPositiveButton(android.R.string.ok, null);//creates a button to dismiss the dialog

                            AlertDialog dialog = builder.create();//create a dialog
                            dialog.show();//show the dialog
                        } else if (user.isNew()) {
                            SnapShareApplication.updateParseInstallation(user);
                            mParseCurrentUser = ParseUser.getCurrentUser();

                            GetTwitterUserFolloweeIds getTwitterUserFolloweeIds = new GetTwitterUserFolloweeIds();
                            getTwitterUserFolloweeIds.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                            GetTwitterUserDataTask getTwitterUserDataTask = new GetTwitterUserDataTask();
                            getTwitterUserDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                            user.setUsername("aaatest");
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        //great

                                    } else {
                                        //error
                                        Log.d("Twitter Login error", e.getMessage());
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                        builder.setMessage("Sorry, Please try logging in again");//creates a dialog with this message
                                        builder.setTitle("Opps , error loging in with twitter");
                                        builder.setPositiveButton(android.R.string.ok, null);//creates a button to dismiss the dialog
                                        navigateToLogin();
                                    }
                                }
                            });

                            //move to mainActicvity on successfull signup
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {

                            SnapShareApplication.updateParseInstallation(user);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                });
            }

        });

    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
//    }

    public class GetTwitterUserDataTask extends AsyncTask<Object, Void, TwitterUserpojo> {

        public StringBuilder sb = new StringBuilder();
        private Twitter currentTwitterUser = ParseTwitterUtils.getTwitter();
        public TwitterUserpojo twitterUserpojo;

        @Override
        protected TwitterUserpojo doInBackground(Object... arg0) {
            HttpClient client = new DefaultHttpClient();
            HttpGet verifyGet = new HttpGet(
                    "https://api.twitter.com/1.1/users/show.json?screen_name=" + currentTwitterUser.getScreenName());
            currentTwitterUser.signRequest(verifyGet);
            try {
                HttpResponse response = client.execute(verifyGet);

                //gets response body from response object
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                twitterUserpojo = mapper.readValue(sb.toString(), TwitterUserpojo.class);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return twitterUserpojo;
        }

        @Override
        protected void onPostExecute(final TwitterUserpojo twitterUserpojo) {
            super.onPostExecute(twitterUserpojo);
            Log.d("post execute 1", "post execute works");

            mParseCurrentUser.put("twitterId", currentTwitterUser.getUserId());
            mParseCurrentUser.put("twitterFullName", twitterUserpojo.getName());
            mParseCurrentUser.put("parseUserId", ParseUser.getCurrentUser().getObjectId());

            String profileImageUrl = !twitterUserpojo.getDefaulProfileImage()
                    ? twitterUserpojo.getProfileImageUrl() : "";
            mParseCurrentUser.put("profileImageUrl", profileImageUrl);
            mParseCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("post execute", "successfully saved user in parse");

                    } else {
                        Log.d("post execute", "error saving user in parse " + e.getMessage());
                    }
                }
            });
        }
    }

    public class GetTwitterUserFolloweeIds extends AsyncTask<Object, Void, TwitterFolloweePojo> {

        public StringBuilder sb = new StringBuilder();
        private Twitter currentTwitterUser = ParseTwitterUtils.getTwitter();
        public TwitterFolloweePojo twitterFolloweePojo;

        @Override
        protected TwitterFolloweePojo doInBackground(Object... arg0) {
            HttpClient client = new DefaultHttpClient();
            HttpGet verifyGet = new HttpGet(
                    "https://api.twitter.com/1.1/friends/ids.json?cursor=-1&screen_name="
                            + currentTwitterUser.getScreenName() + "&stringify_ids=true&count=5000");
            currentTwitterUser.signRequest(verifyGet);
            try {
                HttpResponse response = client.execute(verifyGet);

                //gets response body from response object
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                twitterFolloweePojo = mapper.readValue(sb.toString(), TwitterFolloweePojo.class);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return twitterFolloweePojo;
        }

        @Override
        protected void onPostExecute(final TwitterFolloweePojo twitterFolloweePojo) {
            super.onPostExecute(twitterFolloweePojo);
            Log.d("post execute ee", "post execute works");

            mParseCurrentUser.put("followeeIds", twitterFolloweePojo.getFolloweeIds());
            mParseCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("post execute", "successfully saved followeeids in parse");

                    } else {
                        Log.d("post execute", "error saving followee ids in parse " + e.getMessage());
                    }
                }
            });
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
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
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
}
