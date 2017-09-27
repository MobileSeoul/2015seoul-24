package co.askseoulites.seoulcityapp.activities;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;

import java.io.File;
import java.util.List;

import co.askseoulites.seoulcityapp.R;
import co.askseoulites.seoulcityapp.model.Collection;
import co.askseoulites.seoulcityapp.model.ModelUtils;
import co.askseoulites.seoulcityapp.model.Question;

public class CollectionDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = CollectionDetailActivity.class.getSimpleName();

    private TextView mTitle;
    private TextView mContent;
    private String mLink;
    List<Integer> tags;
    private int mCatNo;
    private String mCategory;
    private ProgressBar mProgress;
    FloatingActionButton fab;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String objectId = getIntent().getStringExtra(ModelUtils.OBJECT_ID);

        mContent = (TextView) findViewById(R.id.cDetailContents);
        mProgress = (ProgressBar) findViewById(R.id.progressCDetail);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        getCollectionFromLocalDB(objectId);

       fab = (FloatingActionButton) findViewById(R.id.fab_collection_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getCollectionFromLocalDB(final String objectId) {
        mProgress.setVisibility(View.VISIBLE);
        Collection.createSingleLocalQuery(objectId).getInBackground(objectId, new GetCallback<Collection>() {
            @Override
            public void done(Collection object, ParseException e) {
                if (e == null && object != null) {
                    setupView(object);
                    Log.d(LOG_TAG, "Got collection from Localdatatore ");

                } else {
                    if (e == null) {
                        getCollectionFromNetwork(objectId);
                    } else {
                        mProgress.setVisibility(View.GONE);
                        Snackbar.make(mContent, getResources().getString(R.string.something_went_wrong),
                                Snackbar.LENGTH_LONG)
                                .setAction("OK", null).show();
                        Log.d(LOG_TAG, "error while fetching collections : " + e.getMessage());
                    }
                }
            }
        });
    }

    private void getCollectionFromNetwork(final String objectId) {

        mProgress.setVisibility(View.VISIBLE);

        Collection.createSingleQuery(objectId).getInBackground(objectId, new GetCallback<Collection>() {
            @Override
            public void done(Collection object, ParseException e) {
                if (e == null && object != null) {
                    setupView(object);
                    Log.d(LOG_TAG, "Got collection from network");

                } else {
                    mProgress.setVisibility(View.GONE);
                    Snackbar.make(mContent, getResources().getString(R.string.something_went_wrong),
                            Snackbar.LENGTH_LONG)
                            .setAction("OK", null).show();
                    Log.d(LOG_TAG, "error while fetching collections : " + e.getMessage());
                }
            }
        });
    }

    private void setupView(final Collection collection) {
        mProgress.setVisibility(View.GONE);
        if(collection == null) {
            return;
        }
        String full_content = collection.getContent() + "/n/n" + "Posted by : "
                + collection.getWriter().getString(ModelUtils.NAME);
        mContent.setText(Html.fromHtml(full_content));
        if(collection.getPhoto() != null) {
            Log.d(LOG_TAG, "getting photo for collection");
            final ParseImageView image = new ParseImageView(CollectionDetailActivity.this);
            image.setParseFile(collection.getPhoto());
            image.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
//                    image.setVisibility(View.VISIBLE);
                    collapsingToolbarLayout.addView(image,0);
                    collapsingToolbarLayout.setScrollBarSize(image.getScrollBarSize());
                }
            });
        }
        else {
            Log.d(LOG_TAG, "photo not available for this collection");
        }

        collapsingToolbarLayout.setTitle(collection.getTitle());

        if(collection.getlink() != null && collection.getlink().length() != 0) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickFab(collection.getlink());
                }
            });
        } else {

            fab.setVisibility(View.INVISIBLE);
        }

    }

    private void onClickFab(String link) {

        if(link == null)
            return;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        if (canResolveIntent(browserIntent)) {
            startActivity(browserIntent);
        }

    }

    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getPackageManager()
                .queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }
}
