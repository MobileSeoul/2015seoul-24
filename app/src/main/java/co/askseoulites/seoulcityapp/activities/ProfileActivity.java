package co.askseoulites.seoulcityapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.askseoulites.seoulcityapp.R;
import co.askseoulites.seoulcityapp.adapters.ProfileTagsAdapter;
import co.askseoulites.seoulcityapp.helper.UserUtils;
import co.askseoulites.seoulcityapp.model.ModelUtils;

public class ProfileActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return ProfileFirstFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ProfileFirstFragment extends Fragment {

        private static final String LOG_TAG = ProfileFirstFragment.class.getSimpleName();
        private static final int SPAN_COUNT = 4;
        private GridView mProfileTagsView;
        protected ProfileTagsAdapter mAdapter;
        protected String[] mDataset;
        ParseUser mCurrentUser;


        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ProfileFirstFragment newInstance(int sectionNumber) {
            ProfileFirstFragment fragment = new ProfileFirstFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ProfileFirstFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
            /* Get current User */
            mCurrentUser = ParseUser.getCurrentUser();
            setUserProfile(mCurrentUser,rootView);

            return rootView;
        }

        private void setUserProfile(ParseUser user, View rootView) {

            int number = getArguments().getInt(ARG_SECTION_NUMBER);
            TextView nameView = (TextView) rootView.findViewById(R.id.user_name);
            String name = mCurrentUser.getString(ModelUtils.NAME);
            nameView.setText(String.format(Locale.US,getString(R.string.name_format), name));
            mProfileTagsView = (GridView) rootView.findViewById(R.id.profileTagsRV);
            setupUserTags(mCurrentUser,rootView);
            Switch privacySwitch = (Switch) rootView.findViewById(R.id.profilePrivacy);
            handlePofilePrivacy(privacySwitch);
            setupUserProfileLevel(mCurrentUser,rootView);
            setupUserStats(mCurrentUser, rootView);

        }

        private void setupUserProfileLevel(ParseUser user,View rootView) {

            TextView lowLevelView = (TextView) rootView.findViewById(R.id.lowLevel);
            ProgressBar userLevelProgress = (ProgressBar) rootView.findViewById(R.id.progress_user);
            userLevelProgress.setMax(UserUtils.MAX_POINTS);
            int level = UserUtils.getLevel(user.getInt(ModelUtils.POINTS));
            userLevelProgress.setProgress(level);
            lowLevelView.setText(String.format(Locale.US,getActivity().getResources()
                    .getString(R.string.user_level_low),level));
        }

        private void setupUserStats(ParseUser user,View rootView) {


            TextView followersCount = (TextView) rootView.findViewById(R.id.followersCount);
            TextView followers = (TextView) rootView.findViewById(R.id.followersView);

            TextView followingsCount = (TextView) rootView.findViewById(R.id.followingCount);
            TextView following = (TextView) rootView.findViewById(R.id.followingView);

            TextView qCount = (TextView) rootView.findViewById(R.id.questionsCount);
            TextView questions = (TextView) rootView.findViewById(R.id.questionsView);

            TextView aCount = (TextView) rootView.findViewById(R.id.answersCount);
            TextView answers = (TextView) rootView.findViewById(R.id.answersView);

            followersCount.setText(String.valueOf(user.getInt(ModelUtils.USER_FOLLOWERS)));
            followingsCount.setText(String.valueOf(user.getInt(ModelUtils.USER_FOLLOWINGS)));
            qCount.setText(String.valueOf(user.getInt(ModelUtils.USER_QUESTIONS)));
            aCount.setText(String.valueOf(user.getInt(ModelUtils.USER_ANSWERS)));


        }


        private void handlePofilePrivacy(Switch mSwitch) {
            if(mCurrentUser.getBoolean(ModelUtils.PUBLIC)) {
                mSwitch.setChecked(true);
            } else {
                mSwitch.setChecked(false);
            }
            mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mCurrentUser.put(ModelUtils.PUBLIC, true);
                        mCurrentUser.saveInBackground();
                    } else {
                        mCurrentUser.put(ModelUtils.PUBLIC, false);
                        mCurrentUser.saveInBackground();
                    }
                }
            });
        }
        private void setupUserTags(ParseUser user, final View rootView){
            String[] dataset = {"Korean","K-POP","HANBOK","LEARN","BIGBANG","FOOD","KIMCHI","K-DRAMA"};
            final List<String> userTags = user.getList(ModelUtils.USER_TAGS);
            if( userTags!= null && userTags.size() > 0) {
                dataset = new String[userTags.size()];
                dataset = userTags.toArray(dataset);
                mAdapter = new ProfileTagsAdapter(getActivity(), dataset);
                mProfileTagsView.setAdapter(mAdapter);
            } else {
                mAdapter = new ProfileTagsAdapter(getActivity(), dataset);
                mProfileTagsView.setAdapter(mAdapter);
            }


            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_add_user_tag);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();*/
                    tagDialog = createTagDialog(new EditText(getActivity()),userTags,rootView);
                    tagDialog.show();

                }
            });
        }

        private AlertDialog tagDialog;
        private String newTag = "";
        private EditText inputTag;
        private AlertDialog createTagDialog(final EditText input, final List<String> userTags,final View root) {

            // 1. Instantiate an AlertDialog.Builder with its constructor
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        final AlertDialog alertd = builder.create();

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage(getResources().getString(R.string.tag_example))
                    .setTitle(getResources().
                            getString(R.string.insert_tag_message))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Editable value = input.getText();
                            newTag = value.toString();
                            if(TextUtils.isEmpty(newTag)) {
                                Snackbar.make(mProfileTagsView, getActivity().getResources().
                                        getString(R.string.enter_some_text), Snackbar.LENGTH_LONG)
                                        .setAction("OK", null).show();
                            } else {
                                    if(userTags != null) {
                                        Log.d(LOG_TAG,"adding new tag : " + newTag);
                                        userTags.add(newTag);
                                        mCurrentUser.addUnique(ModelUtils.USER_TAGS, newTag);
                                        mCurrentUser.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e == null) {
                                                    Snackbar.make(mProfileTagsView, getActivity().getResources().
                                                            getString(R.string.tag_added),
                                                            Snackbar.LENGTH_LONG)
                                                            .setAction("OK", null).show();
                                                    setupUserTags(mCurrentUser,root);
                                                } else {
                                                    Log.d(LOG_TAG,"error in adding tag " + e.getMessage());
                                                }
                                            }
                                        });
                                    }
                            }
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    tagDialog.dismiss();
                }
            });
            if(input.getParent() == null) {
                Log.d(LOG_TAG, "input.getParent() == null");
                if(!newTag.isEmpty())
                input.setText(newTag);
                builder.setView(input);
            } else {
                Log.d(LOG_TAG,"input.getParent() != null");
                inputTag = null;
                inputTag = new EditText(getActivity());
                if(!newTag.isEmpty())
                    inputTag.setText(newTag);
                builder.setView(inputTag);
            }

            // 3. Get the AlertDialog from create()
            return builder.create();
        }

    }


}
