package co.askseoulites.seoulcityapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ui.ParseLoginBuilder;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.askseoulites.seoulcityapp.MainActivity;
import co.askseoulites.seoulcityapp.R;
import co.askseoulites.seoulcityapp.model.GuiderPagerEnum;
import co.askseoulites.seoulcityapp.model.ModelUtils;

public class LaunchActivity extends AppCompatActivity {

    private static final String LOG = LaunchActivity.class.getSimpleName();
    private static final int LOGIN_REQUEST = 0;
    public static final int LOGIN_REQUEST_FACEBOOK = 1;


    private ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomPagerAdapter(this));

    }

    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            GuiderPagerEnum customPagerEnum = GuiderPagerEnum.values()[position];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(customPagerEnum.getLayoutResId(),
                    collection, false);
            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return GuiderPagerEnum.values().length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            GuiderPagerEnum customPagerEnum = GuiderPagerEnum.values()[position];
            return mContext.getString(customPagerEnum.getTitleResId());
        }

    }

    public void startLogin(View view) {

        Button button = (Button) view;

        String buttonText = (String) button.getText();
        Log.d(LOG,"button text : " + buttonText);
        if(buttonText.equals(getResources().getString(R.string.login))) {
            startParseLogin();

        } else if (buttonText.equals(getResources().getString(R.string.signup))) {
            startParseLogin();

        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            updateUser();
            Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void updateUser() {
        ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();
        currentInstallation.put(ModelUtils.USER, currentUser);
        currentInstallation.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if(e == null)
                    Log.d(LOG,"user installation saved");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG,"result code : " + resultCode + " requestCode : " + requestCode);
        if(resultCode == RESULT_OK) {
            Log.d(LOG, "result ok!");
           /* if(requestCode == LOGIN_REQUEST_FACEBOOK) {
                Log.d(LOG,"result facebook_login!");
                getEmailFromFacebook();
                linkFacebookWithUser();
            }*/
        } else if (resultCode == RESULT_CANCELED) {
            Log.d(LOG,"result cancelled!");

        } else if (resultCode == RESULT_FIRST_USER) {
            Log.d(LOG,"result first_user!");

        }
    }

    private void getEmailFromFacebook() {

       //TODO: Add Parse User link code

        if(ParseUser.getCurrentUser().isNew()) {
            Log.d(LOG,"new user");
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            if (object == null)
                                return;

                            String email = "";
                            email = object.optString("email");
                            Log.d(LOG, "email : " + email);
                            ParseUser.getCurrentUser().setEmail(email);
                            ParseUser.getCurrentUser().saveInBackground();

                            Log.v(LOG, response.toString());
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    private void startParseLogin() {
        ParseLoginBuilder builder = new ParseLoginBuilder(
                LaunchActivity.this);
        Intent parseLoginIntent = builder.setParseLoginEnabled(true)
                .setAppLogo(R.drawable.web_icon_v1)
                .setParseLoginHelpText("Forgot password?")
                .setParseLoginEmailAsUsername(true)
/*                .setFacebookLoginEnabled(true)
                .setFacebookLoginButtonText("Facebook")
                .setFacebookLoginPermissions(Arrays.asList("public_profile", "email", "user_friends"))*/
                .build();
        startActivityForResult(parseLoginIntent, LOGIN_REQUEST);
    }

    private void linkFacebookWithUser() {
        final ParseUser user = ParseUser.getCurrentUser();
        if (user == null)
            return;
        List<String> permissions = new ArrayList<>();
        permissions.add("email");
        permissions.add("user_friends");
        if (!ParseFacebookUtils.isLinked(user)) {
            ParseFacebookUtils.linkWithReadPermissionsInBackground(user, LaunchActivity.this, permissions, new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (ParseFacebookUtils.isLinked(user)) {
                        Log.d("MyApp", "Woohoo, user logged in with Facebook!");
                    }
                }
        });
        }
    }

}
