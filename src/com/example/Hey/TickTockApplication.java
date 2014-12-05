package com.example.Hey;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;


/**
 * Created by hello on 09/08/14.
 */
public class TickTockApplication extends Application {

    @Override
    public void onCreate() {
        Parse.initialize(this, "HbuJBqSKSKZpi4sIOQQK1TUXafcY9GC4Dqky7b4F", "9KVyt33BGLSDpusdAvFF7FwmjUQRjw9Wz7597IrV");

        PushService.setDefaultPushCallback(this,MainActivity.class);
        //PushService.setDefaultPushCallback(this,MainActivity.class, R.drawable.icon); - this is used when you set custom icon for push notification.
        ParseInstallation.getCurrentInstallation().saveInBackground();


       //First test  object
       // ParseObject testObject = new ParseObject("TestObject");
       //testObject.put("foo", "bar");
       //testObject.saveInBackground();
    }

    public static void updateParseInstallation(ParseUser user){

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        //save this installation to parse
        installation.saveInBackground();

    }
}
