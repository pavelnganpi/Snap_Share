package com.paveynganpi.snapshare;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.paveynganpi.snapshare.utils.ParseConstants;

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
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    //references current user to parse so as to be able to send
    //push notifications
    public static void updateParseInstallation(ParseUser user){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID,user.getObjectId());
        installation.saveInBackground();
    }
}
