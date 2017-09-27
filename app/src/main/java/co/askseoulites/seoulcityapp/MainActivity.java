package co.askseoulites.seoulcityapp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogOutCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import co.askseoulites.seoulcityapp.activities.LaunchActivity;
import co.askseoulites.seoulcityapp.activities.NotificationActivity;
import co.askseoulites.seoulcityapp.activities.NotificationActivityNew;
import co.askseoulites.seoulcityapp.activities.ProfileActivity;
import co.askseoulites.seoulcityapp.activities.SingleCatTabActivity;
import co.askseoulites.seoulcityapp.activities.SingleCatVPActivity;
import co.askseoulites.seoulcityapp.activities.SingleCategoryActivity;
import co.askseoulites.seoulcityapp.model.Collection;
import co.askseoulites.seoulcityapp.model.ModelUtils;
import co.askseoulites.seoulcityapp.model.Notification;
import co.askseoulites.seoulcityapp.widgets.AvatarView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG = MainActivity.class.getSimpleName();
    private static final int LOGIN_REQUEST = 0;

    private ParseUser currentUser;
    private  Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final GridView gridview = (GridView) findViewById(R.id.catGrid);
        gridview.setVerticalSpacing(1);
        gridview.setHorizontalSpacing(1);
        CategoryAdapter mCategoryAdapter = new CategoryAdapter(this,
                getResources().getStringArray(R.array.cat_array));
        gridview.setAdapter(mCategoryAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 5) {
                    Intent intent = new Intent(MainActivity.this, SingleCatVPActivity.class);
                    intent.putExtra(Constants.KEY_CATEGORY_NO, position);
                    startActivity(intent);
                } else {
                    showAboutUs();
                }
            }
        });
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/




            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        getKeyHash();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_notification) {
            startActivity(new Intent(MainActivity.this, NotificationActivityNew.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        /*if(id == R.id.nav_gallery) {
            // Collection
        }
        else  if (id == R.id.nav_slideshow) {
            // Question
        } else*/ if (id == R.id.nav_share) {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    Intent intent = new Intent(MainActivity.this, LaunchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

        } else if (id == R.id.nav_send) {
            showAboutUs();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = ParseUser.getCurrentUser();
    }


    private void getKeyHash() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "co.askseoulites.seoulcityapp",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    private void setUpToolbar() {

        ParseUser user = ParseUser.getCurrentUser();
        if(user == null) {
            finish();
            return;
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView scoreView = (TextView) findViewById(R.id.score);
        int points = user.getInt(ModelUtils.POINTS);
        scoreView.setText(String.format(Locale.US,getResources().getString(R.string.user_pts),points));
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final AvatarView avatarView = (AvatarView) toolbar.findViewById(R.id.avatar);
        avatarView.setAvatar(R.drawable.avatar_6);
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });
        String name = "";
        if(user != null) {
            name = user.getString("name");
        }
        ((TextView) toolbar.findViewById(R.id.title)).setText(name);
    }

    private void showAboutUs() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(Constants.ABOUT_US_LINK));
        if (canResolveIntent(i)) {
            startActivity(i);
        }
    }

    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getPackageManager()
                .queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

}
