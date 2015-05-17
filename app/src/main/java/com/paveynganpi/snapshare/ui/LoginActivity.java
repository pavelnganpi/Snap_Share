package com.paveynganpi.snapshare.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.paveynganpi.snapshare.R;
import com.paveynganpi.snapshare.SnapShareApplication;


public class LoginActivity extends ActionBarActivity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginButton;
    protected TextView mSignUpTextView;

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

//        ParseTwitterUtils.logIn(this, new LogInCallback() {
//            @Override
//            public void done(ParseUser user, ParseException err) {
//                if (user == null) {
//                    Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
//                } else if (user.isNew()) {
//                    Log.d("MyApp", "User signed up and logged in through Twitter!");
//                } else {
//                    Log.d("MyApp", "User logged in through Twitter!");
//                }
//            }
//        });

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
                            Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage("Sorry, Please try logging in again");//creates a dialog with this message
                            builder.setTitle("Opps , error loging in with twitter");
                            builder.setPositiveButton(android.R.string.ok, null);//creates a button to dismiss the dialog

                            AlertDialog dialog = builder.create();//create a dialog
                            dialog.show();//show the dialog
                        } else if (user.isNew()) {
                            Log.d("MyApp", "User signed up and logged in through Twitter!");
                            SnapShareApplication.updateParseInstallation(user);
                            user.setUsername(ParseTwitterUtils.getTwitter().getScreenName());
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        //great
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    else{
                                        //error
                                        Log.d("Twitter Login error", e.getMessage());
                                    }
                                }
                            });
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

//                //get their various strings
//                String username = mUsername.getText().toString();
//                String password = mPassword.getText().toString();
//
//                //remove extra white spaces
//                username.trim();
//                password.trim();
//
//                //if user leaves any text fields blank
//                if (username.isEmpty() || password.isEmpty()) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                    builder.setMessage(R.string.login_error_message);//creates a dialog with this message
//                    builder.setTitle(R.string.login_error_title);
//                    builder.setPositiveButton(android.R.string.ok, null);//creates a button to dismiss the dialog
//
//                    AlertDialog dialog = builder.create();//create a dialog
//                    dialog.show();//show the dialog
//                }
//                //the user put in good data, logging in
//                else {
//                    //login
//                    setProgressBarIndeterminateVisibility(true);//show the progress bar
//
//                    ParseUser.logInInBackground(username, password, new LogInCallback() {
//                        @Override
//                        public void done(ParseUser user, ParseException e) {
//                            setProgressBarIndeterminateVisibility(false);//remove progress bar after we have response from parse
//                            if (e == null) {
//                                //success
//
//                                SnapShareApplication.updateParseInstallation(user);
//
//                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//
//                            } else {
//
//                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                                builder.setMessage(R.string.login_error_message);//creates a dialog with this message
//                                builder.setTitle(R.string.login_error_title);
//                                builder.setPositiveButton(android.R.string.ok, null);//creates a button to dismiss the dialog
//
//                                AlertDialog dialog = builder.create();//create a dialog
//                                dialog.show();//show the dialog
//
//                            }
//
//                        }
//                    });
//
//                }
//
//            }
        });

    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
//    }


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
