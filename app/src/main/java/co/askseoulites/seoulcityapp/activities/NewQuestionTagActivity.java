package co.askseoulites.seoulcityapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.askseoulites.seoulcityapp.Constants;
import co.askseoulites.seoulcityapp.R;
import co.askseoulites.seoulcityapp.adapters.TagsAdapter;
import co.askseoulites.seoulcityapp.helper.PreferencesHelper;
import co.askseoulites.seoulcityapp.model.CategoryUtils;
import co.askseoulites.seoulcityapp.model.Tags;

public class NewQuestionTagActivity extends AppCompatActivity {

    private static final String LOG = NewQuestionTagActivity.class.getSimpleName();
    private static final String KEY_SELECTED_TAG_INDEX = "selected_tag_index";

    private GridView mTagGrid;
    private Tags mSelectedTag = Tags.ONE;
    private View mSelectedTagView;
    private int tagNo = 0;
    ParseUser mPlayer;
    private Integer[] tagsSelected;
    private List<Integer> mTagsList;
    private int mCategory;
    private TextView chooseTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question_tag);
        mPlayer = ParseUser.getCurrentUser();
        initContents();
//        mTagsList = new ArrayList<>();
        mCategory = getIntent().getIntExtra(Constants.KEY_CATEGORY_NO,0);
        if (savedInstanceState != null) {
            int savedAvatarIndex = savedInstanceState.getInt(KEY_SELECTED_TAG_INDEX);
            mSelectedTag = Tags.values()[savedAvatarIndex];
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView next = (TextView) toolbar.findViewById(R.id.nextStepQuestion);
        chooseTag = (TextView) findViewById(R.id.toolbar_choose_avatar);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewQuestionTagActivity.this,NewQuestionActivity.class);
                intent.putExtra(Constants.KEY_TAG_NO, tagNo);
                intent.putExtra(Constants.KEY_CATEGORY_NO,mCategory);
                savePlayer(NewQuestionTagActivity.this);
                /*Log.d(LOG,"mTagsList : " + mTagsList.size());
                tagsSelected = mTagsList.toArray(tagsSelected);*/
                startActivity(intent);
            }
        });
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(android.R.id.content).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                setUpGridView(null);
            }
        });

    }
    private void setUpGridView(View container) {
        mTagGrid = (GridView) findViewById(R.id.tags);
        mTagGrid.setAdapter(new TagsAdapter(this));
        mTagGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedTagView = view;
                mSelectedTag = Tags.values()[position];
                tagNo = position;
                chooseTag.setText(String.format(Locale.US,getResources().getString(R.string.choose_tags),
                        CategoryUtils.getTagTitle(position,NewQuestionTagActivity.this)));

            }
        });
        mTagGrid.setNumColumns(calculateSpanCount());
       /* mTagGrid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        mTagGrid.setMultiChoiceModeListener(new MultiChoiceModeListener());*/
        mTagGrid.setItemChecked(mSelectedTag.ordinal(), true);

    }

    /**
     * Calculates spans for tags dynamically.
     *
     * @return The recommended amount of columns.
     */
    private int calculateSpanCount() {
        int avatarSize = getResources().getDimensionPixelSize(R.dimen.size_fab);
        int avatarPadding = getResources().getDimensionPixelSize(R.dimen.spacing_double);
        int cols = mTagGrid.getWidth() / (avatarSize + avatarPadding);
        Log.d(LOG,"number of cols : " + cols);
        return 3;
    }

    private void initContents() {
        assurePlayerInit();
        if (null != mPlayer) {
            Log.d(LOG,"init | mSelectedTag : " + tagNo);
            mSelectedTag = Tags.values()[tagNo];
        }
    }

    private void assurePlayerInit() {
        if (null != mPlayer) {
            tagNo = PreferencesHelper.getTagPosition(this);
        }
    }

    private void savePlayer(Activity activity) {

        PreferencesHelper.writeToPreferences(activity, mPlayer,tagNo);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt(KEY_SELECTED_TAG_INDEX, mSelectedTag.ordinal());
    }

   /* public class MultiChoiceModeListener implements
            GridView.MultiChoiceModeListener {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position,
                                              long id, boolean checked) {
            int selectCount = mTagGrid.getCheckedItemCount();
            if(checked) {
                mTagsList.add(position);
            } else {
                mTagsList.remove( mTagsList.indexOf(position));
            }
        }

    }*/
}
