package co.askseoulites.seoulcityapp;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import net.danlew.android.joda.JodaTimeAndroid;

import java.text.ParseException;

import co.askseoulites.seoulcityapp.activities.LaunchActivity;
import co.askseoulites.seoulcityapp.model.Answer;
import co.askseoulites.seoulcityapp.model.Category;
import co.askseoulites.seoulcityapp.model.Collection;
import co.askseoulites.seoulcityapp.model.Notification;
import co.askseoulites.seoulcityapp.model.Question;
import co.askseoulites.seoulcityapp.model.Tag;

/**
 * Created by hassanabid on 10/20/15.
 */
public class MainApplication extends Application {

    private static final String LOG  = "MainApplication";
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Category.class);
        ParseObject.registerSubclass(Tag.class);
        ParseObject.registerSubclass(Question.class);
        ParseObject.registerSubclass(Answer.class);
        ParseObject.registerSubclass(Collection.class);
        ParseObject.registerSubclass(Notification.class);



        /* Enable Local Datastore. */
        Parse.enableLocalDatastore(this);
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        ParseCrashReporting.enable(this);

        // Add your initialization code here
        Parse.initialize(this);
        ParseFacebookUtils.initialize(this, LaunchActivity.LOGIN_REQUEST_FACEBOOK);

//        ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
        saveInstallation();
        JodaTimeAndroid.init(this);
    }

    private void saveInstallation() {
        ParsePush.subscribeInBackground("global", new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                Log.v(LOG,"push subscribed!");
            }
        });
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
