package co.askseoulites.seoulcityapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

import co.askseoulites.seoulcityapp.Constants;
import co.askseoulites.seoulcityapp.R;
import co.askseoulites.seoulcityapp.adapters.QuestionAdapter;
import co.askseoulites.seoulcityapp.fragments.CollectionFragment;
import co.askseoulites.seoulcityapp.model.CategoryUtils;
import co.askseoulites.seoulcityapp.model.Collection;
import co.askseoulites.seoulcityapp.model.ModelUtils;
import co.askseoulites.seoulcityapp.model.Question;

public class SingleCatVPActivity extends AppCompatActivity implements
        CollectionFragment.OnCollectionSelectedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private static final String LOG_TAG = SingleCatVPActivity.class.getSimpleName();
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private RecyclerView mQuestionsView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mCategory;
    private List<Question> mQuestions;
    private List<Collection> mCollectionF;
    private ProgressBar progress;
    private TextView noNetworkText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_cat_vp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCategory = getIntent().getIntExtra(Constants.KEY_CATEGORY_NO,0);
        setTitle(CategoryUtils.getCategoryStr(mCategory, this));
        getCollectionsForCat(mCategory);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_question);
        progress = (ProgressBar) findViewById(R.id.progressQVP);
        noNetworkText = (TextView) findViewById(R.id.no_network_text);
        progress.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SingleCatVPActivity.this,NewQuestionTagActivity.class);
                intent.putExtra(Constants.KEY_CATEGORY_NO,mCategory);
                startActivity(intent);
            }
        });

        mQuestionsView = (RecyclerView) findViewById(R.id.cat_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mQuestionsView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mQuestionsView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        String[] myDataset = getResources().getStringArray(R.array.cat_array);
        getQuestionsForCategory(mCategory);


    }

    private void getQuestionsForCategory(final int catPosition) {

        Question.createLocalQuery(catPosition).findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> questions, ParseException e) {
                if( e == null && questions.size() > 0) {
                    mAdapter = new QuestionAdapter(questions,SingleCatVPActivity.this,mCategory);
                    mQuestionsView.setAdapter(mAdapter);
                } else {
                    if (e == null && questions.size() == 0) {
                        getFreshQuestionsForCategory(catPosition);
                    } else {
                        Snackbar.make(mQuestionsView, getResources().getString(R.string.something_went_wrong),
                                Snackbar.LENGTH_LONG)
                                .setAction("OK", null).show();
                        Log.d(LOG_TAG, "error while fetching questions : " + e.getMessage());

                    }
                }
            }
        });

    }

    private void getFreshQuestionsForCategory(final int cat) {
        Question.createQuery(cat).findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> questions, ParseException e) {
                if (e == null && questions != null && questions.size() > 0) {
                    mAdapter = new QuestionAdapter(questions, SingleCatVPActivity.this,mCategory);
                    mQuestionsView.setAdapter(mAdapter);
                    ParseObject.pinAllInBackground(ModelUtils.QUESTIONS_PIN + cat, questions);
                    Log.d(LOG_TAG, "get fresh questions");
                } else {

                    if( e == null &&  questions != null && questions.size() == 0) {
                        mAdapter = new QuestionAdapter(questions, SingleCatVPActivity.this,mCategory);
                        mQuestionsView.setAdapter(mAdapter);
                        Log.d(LOG_TAG, "get fresh questions - with size 0");
                    } else {
                        Snackbar.make(mQuestionsView, getResources().getString(R.string.something_went_wrong),
                                Snackbar.LENGTH_LONG)
                                .setAction("OK", null).show();
                        Log.d(LOG_TAG, "error while fetching questions : " + e.getMessage());

                    }
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_cat_v, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            progress.setVisibility(View.VISIBLE);
            getFreshCollections(mCategory);
            getFreshQuestionsForCategory(mCategory);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCollectionSelected(int position) {
        Log.d(LOG_TAG,"collection selected at position : " + position);
        Intent intent = new Intent(SingleCatVPActivity.this,CollectionDetailActivity.class);
        if(mCollectionF != null && mCollectionF.size() > 0) {
            intent.putExtra(ModelUtils.OBJECT_ID, mCollectionF.get(position).getObjectId());
            startActivity(intent);
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int mCatPos;
        List<Collection> mCollections;
        public SectionsPagerAdapter(FragmentManager fm, List<Collection> collections) {
            super(fm);
            mCollections = collections;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return CollectionFragment.newInstance(position, mCatPos, mCollections);
        }

        @Override
        public int getCount() {
           if(mCollections != null && mCollections.size() == 0)
               return 1;
            return mCollections.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(mCollections != null && mCollections.size() == 0) {
                return "";
            }
            switch (position) {
                case 0:
                    return mCollections.get(position).getTitle();
                case 1:
                    return mCollections.get(position).getTitle();
                case 2:
                    return mCollections.get(position).getTitle();

            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */


    private void getCollectionsForCat(final int cat) {

        Collection.createLocalQuery(cat).findInBackground(new FindCallback<Collection>() {
            @Override
            public void done(List<Collection> objects, ParseException e) {
                if (e == null && objects != null && objects.size() > 0) {
                    mCollectionF = objects;
                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),objects);
                    // Set up the ViewPager with the sections adapter.
                    mViewPager = (ViewPager) findViewById(R.id.container);
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                    progress.setVisibility(View.GONE);
                    Log.d(LOG_TAG, "retrieved collections for cat " + cat + " from local datastore");

                } else {
                    if (e == null && objects != null && objects.size() == 0) {
                        getFreshCollections( cat);
                    } else {
                        noNetworkText.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                        Snackbar.make(mQuestionsView, getResources().getString(R.string.something_went_wrong),
                                Snackbar.LENGTH_LONG)
                                .setAction("OK", null).show();
                        Log.d(LOG_TAG, "error while fetching collections : " + e.getMessage());

                    }
                }
            }
        });

    }

    private void getFreshCollections(final int cat) {
        progress.setVisibility(View.VISIBLE);
        Collection.createQuery(cat).findInBackground(new FindCallback<Collection>() {
            @Override
            public void done(List<Collection> objects, ParseException e) {
                if( e== null && objects != null && objects.size() > 0) {
                    mCollectionF = objects;
                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),
                            objects);
                    mViewPager = (ViewPager) findViewById(R.id.container);
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                    progress.setVisibility(View.GONE);
                    ParseObject.pinAllInBackground(ModelUtils.COLLECTIONS_PIN + cat, objects);
                    Log.d(LOG_TAG, "pinned collections for cat : " + cat);
                } else {

                    if(e == null && objects != null && objects.size() == 0) {
                        mCollectionF = objects;
                        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),
                                objects);
                        mViewPager = (ViewPager) findViewById(R.id.container);
                        mViewPager.setAdapter(mSectionsPagerAdapter);
                        progress.setVisibility(View.GONE);

                    } else {
                        noNetworkText.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                        Snackbar.make(mQuestionsView, getResources().getString(R.string.something_went_wrong),
                                Snackbar.LENGTH_LONG)
                                .setAction("OK", null).show();
                        Log.d(LOG_TAG, "error while fetching collections : " + e.getMessage());
                    }
                }
            }
        });
    }
}
