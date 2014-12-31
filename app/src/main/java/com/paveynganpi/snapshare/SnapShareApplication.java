package com.paveynganpi.snapshare;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by paveynganpi on 12/30/14.
 */
public class SnapShareApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate(); //doing since overing from the parent, so we want o inherit from the base class too
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "LzRx4mxemNXRqfz8gdcdhaKy4xFJmAfRIISIYexa", "gzDVpVNlGUgR8SnHUDiNEOrQtDIFGv2cvFTkBpio");

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();


    }
}
