package com.paveynganpi.snapshare;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class LoginActivity extends ActionBarActivity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginButton;
    protected TextView mSignUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignUpTextView = (TextView) findViewById(R.id.signUpText);

        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);

            }
        });

        //initialize the instance variables with the respective data in the txt fields
        mUsername = (EditText)findViewById(R.id.usernameField);
        mPassword = (EditText)findViewById(R.id.passwordField);
        mLoginButton = (Button)findViewById(R.id.loginButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get thier various strings
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                //remove extra white spaces
                username.trim();
                password.trim();

                //if user lives any text fields blanck
                if(username.isEmpty() || password.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(R.string.login_error_message);//creates a dialog with this message
                    builder.setTitle(R.string.login_error_title);
                    builder.setPositiveButton(android.R.string.ok,null);//creates a button to dismiss the dialog

                    AlertDialog dialog = builder.create();//create a dialog
                    dialog.show();//show the dialog
                }
                //the user put in good data, logging in
                else{

                   ParseUser.logInInBackground(username, password, new LogInCallback() {
                       @Override
                       public void done(ParseUser user, ParseException e) {

                           if(e == null){
                               //success

                               Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                               startActivity(intent);

                           }
                           else{

                               AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                               builder.setMessage(R.string.login_error_message);//creates a dialog with this message
                               builder.setTitle(R.string.login_error_title);
                               builder.setPositiveButton(android.R.string.ok,null);//creates a button to dismiss the dialog

                               AlertDialog dialog = builder.create();//create a dialog
                               dialog.show();//show the dialog

                           }

                       }
                   });

                }

            }
        });



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
