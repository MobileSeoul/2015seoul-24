package co.askseoulites.seoulcityapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterViewAnimator;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Locale;

import co.askseoulites.seoulcityapp.R;
import co.askseoulites.seoulcityapp.adapters.AnswerAdapter;
import co.askseoulites.seoulcityapp.adapters.QuizAdapter;
import co.askseoulites.seoulcityapp.model.Answer;
import co.askseoulites.seoulcityapp.model.Category;
import co.askseoulites.seoulcityapp.model.CategoryUtils;
import co.askseoulites.seoulcityapp.model.ModelUtils;
import co.askseoulites.seoulcityapp.model.Question;
import co.askseoulites.seoulcityapp.widgets.AvatarView;

public class QuestionDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = QuestionDetailActivity.class.getSimpleName();
    private static final String KEY_USER_INPUT = "USER_INPUT";
    private TextView mProgressText;
    private int mQuizSize = 10;
    private ProgressBar mProgressBar;
    private Category mCategory;
    private Question mQuestion;
    private AdapterViewAnimator mQuizView;
    private AnswerAdapter mAnswerAdapter;
    private QuizAdapter mQuizAdapter;
    /*      private SolvedStateListener mSolvedStateListener; */
    private TextView questiontitle;
    private TextView questionContent;
    private FloatingActionButton fab;
    private EditText answerEditText;
    private TextView showAnswers;
    private List<Answer> answers;
    private boolean isShowingAnswers;
    private String mHashTag;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout toolbarLayout = (AppBarLayout)
                findViewById(R.id.app_bar_question);
        questiontitle = (TextView) findViewById(R.id.qTitleDetail);
        questionContent = (TextView) findViewById(R.id.qContentDetail);
        answerEditText = (EditText) findViewById(R.id.answer_edit_text);
        showAnswers = (TextView) findViewById(R.id.show_answers);
        answerEditText.addTextChangedListener(new AnswerTextWatch());

        String objectId = getIntent().getStringExtra(ModelUtils.OBJECT_ID);
        findSingleLocalQuestion(objectId);

    }

    private void setQuestionView(Question q) {

        if(q == null)
            return;
        questiontitle.setText(q.getTitle());
        questionContent.setText(q.getContent());
    }


    /**
     * Convenience method to hide the keyboard.
     *
     * @param view A view in the hierarchy.
     */
    protected void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = getInputMethodManager();
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private InputMethodManager getInputMethodManager() {
        return (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @SuppressWarnings("ConstantConditions")
    private void setAvatarDrawable(AvatarView avatarView) {
        avatarView.setAvatar(R.drawable.avatar_2_raster);
        ViewCompat.animate(avatarView)
                .setInterpolator(new FastOutLinearInInterpolator())
                .setStartDelay(500)
                .scaleX(1)
                .scaleY(1)
                .start();
    }

    private void initProgressToolbar() {
        mProgressText = (TextView) findViewById(R.id.progress_text);
        mProgressBar = ((ProgressBar) findViewById(R.id.progress));
        mProgressBar.setMax(mQuizSize);
        @SuppressWarnings("ConstantConditions")
        final ListView answerListView = (ListView) findViewById(R.id.scorecard);
        setProgressBar(mQuestion.getAnswersCount());
        showAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowingAnswers) {
                    showAnswers(answerListView);
                } else {
                    hideAnswers(answerListView);
                }
            }
        });
    }

    public void showAnswers(ListView list) {
        /*
         mQuizView.setVisibility(View.GONE); */
        isShowingAnswers = true;
        getAnswerAdapter(list);

    }
    private void getAnswerAdapter(final ListView answerListView) {
        Answer.createQuery(mQuestion).findInBackground(new FindCallback<Answer>() {
            @Override
            public void done(final List<Answer> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    Log.d(LOG_TAG, "answers : " + objects.size());
                    mAnswerAdapter = new AnswerAdapter(QuestionDetailActivity.this, objects);
                    answerListView.setAdapter(mAnswerAdapter);
                    answerListView.setVisibility(View.VISIBLE);
                    answerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final Answer answer = objects.get(position);
                            new BottomSheet.Builder(QuestionDetailActivity.this, R.style.BottomSheet_StyleDialog).
                                    title(getResources().getString(R.string.answer_popup_title)).
                                    sheet(R.menu.answer_popup_menu).
                                    listener(new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                            /*    case R.id.location:
                                                    Snackbar.make(answerListView, getResources().
                                                                    getString(R.string.no_answer_available),
                                                            Snackbar.LENGTH_SHORT)
                                                            .setAction("OK", null).show();
                                                    break;*/
                                                case R.id.accept_answer:
                                                    if (answer.getStatus())
                                                        return;
                                                    mProgressBar.setVisibility(View.VISIBLE);
                                                    answer.setStatus(true);
                                                    answer.pinInBackground(ModelUtils.ANSWERS_PIN
                                                            + mQuestion.getObjectId(), new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                answer.saveInBackground(new SaveCallback() {
                                                                    @Override
                                                                    public void done(ParseException e) {
                                                                        if (e == null) {
                                                                            Log.d(LOG_TAG, "answer status changed on server");
                                                                        } else {
                                                                            Log.d(LOG_TAG, "exception while changing answer status on server" +
                                                                                    e.getMessage());
                                                                            mProgressBar.setVisibility(View.GONE);
                                                                            return;
                                                                        }
                                                                    }
                                                                });
                                                                mAnswerAdapter.notifyDataSetChanged();
                                                                mProgressBar.setVisibility(View.GONE);
                                                                Log.d(LOG_TAG, "answer status changed to true");

                                                            } else {
                                                                Log.d(LOG_TAG, "exception while changing answer status " +
                                                                        e.getMessage());

                                                            }
                                                        }
                                                    });
                                                    break;
                                                case R.id.share:
                                                    shareAnswer(answer);
                                                    break;
                                            }
                                        }
                                    }).show();
                        }
                    });
                    questiontitle.setVisibility(View.GONE);
                    questionContent.setVisibility(View.GONE);
                    answerEditText.setVisibility(View.GONE);
                    showAnswers.setText(getResources().getString(R.string.hide_answers));
                    ParseUser.pinAllInBackground(ModelUtils.ANSWERS_PIN + mQuestion.getObjectId(), objects);

                } else {
                    if (e == null) {
                        isShowingAnswers = false;
                        hideAnswers(answerListView);
                        Snackbar.make(answerListView, getResources().getString(R.string.no_answer_available), Snackbar.LENGTH_LONG)
                                .setAction("OK", null).show();
                    } else {
                        Log.d(LOG_TAG, "exception while fetching answers : " + e.getMessage());
                    }
                }
            }
        });

    }


    private void hideAnswers(ListView listView) {

        listView.setVisibility(View.GONE);
        questiontitle.setVisibility(View.VISIBLE);
        questionContent.setVisibility(View.VISIBLE);
        answerEditText.setVisibility(View.VISIBLE);
        showAnswers.setText(getResources().getString(R.string.show_answers));
        isShowingAnswers = false;
    }
    private void setProgressBar(int currentQuizPosition) {

        mProgressText
                .setText(getString(R.string.quiz_of_quizzes, currentQuizPosition, mQuizSize));
        mProgressBar.setProgress(currentQuizPosition);
    }

    private void saveAnswer(String content, final View view) {
        final Answer answer = new Answer();
        answer.setContent(content);
        answer.setQuestion(mQuestion);
        answer.setWriter(ParseUser.getCurrentUser());
        answer.setStatus(false);
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setWriteAccess(ParseUser.getCurrentUser(), true);
        acl.setWriteAccess(mQuestion.getWriter(),true);
        acl.setRoleWriteAccess("Moderators", true);
        acl.setRoleWriteAccess("Administrator", true);
        answer.setACL(acl);
        answer.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Snackbar.make(view, getResources().getString(R.string._answer_submitted), Snackbar.LENGTH_LONG)
                        .setAction("OK", null).show();
                answerEditText.setText("");
                answer.pinInBackground(ModelUtils.ANSWERS_PIN + mQuestion.getObjectId());
            }
        });

    }

    private void decideOnViewToDisplay() {
        final boolean isSolved = mQuestion.isSolved();
        if (isSolved) {
            showAnswers(null);
        } else {
            mQuizView.setAdapter(getQuizAdapter());
        }
    }

    private QuizAdapter getQuizAdapter() {
   /*     if (null == mQuizAdapter) {
            mQuizAdapter = new QuizAdapter(this), mCategory);
        }*/
        return mQuizAdapter;
    }

    private void shareAnswer(Answer answer) {
        startActivity(Intent.createChooser(
                createShareIntent(R.string.share_template, mQuestion.getTitle() + "- " +
                                mQuestion.getContent(),
                        mHashTag,
                        mQuestion.getlink() != null ? mQuestion.getlink() : "",answer.getContent()),
                getString(R.string.title_share)));
    }

    public Intent createShareIntent(int messageTemplateResId, String title, String hashtags,
                                    String url,String answer) {
        String photoUrl = mQuestion.getPhoto() != null ?
                mQuestion.getPhoto().getUrl(): "";
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setStream(Uri.parse(photoUrl))
                .setText(getString(messageTemplateResId,
                        title, hashtags, " " + url , answer));
        return builder.getIntent();
    }

    private void getHastTag(Question q) {

        if(q.getTags() != null && q.getTags().size() != 0) {
            mHashTag = CategoryUtils.getTagTitle(q.getTags().get(0),this);
        }
    }

    private void findSingleLocalQuestion(final String objectId) {

        Question.createSingleLocalQuery(objectId).getInBackground(objectId, new GetCallback<Question>() {
            @Override
            public void done(Question object, ParseException e) {
                if(e==null && object != null) {
                    mQuestion = object;
                    updateQuestionView(object,toolbar);
                    Log.d(LOG_TAG,"fetched question from localdatastore");
                } else {
                    findSingleQuestion(objectId);
                }

            }
        });
    }

    private void findSingleQuestion(String objectId) {
        Question.createSingleLocalQuery(objectId).getInBackground(objectId, new GetCallback<Question>() {
            @Override
            public void done(Question object, ParseException e) {
                if(e==null && object != null) {
                    mQuestion = object;
                    updateQuestionView(object,toolbar);
                    Log.d(LOG_TAG, "fetched question from parse");
                } else {
                    Snackbar.make(questiontitle, getResources().getString(R.string.something_went_wrong),
                            Snackbar.LENGTH_LONG)
                            .setAction("OK", null).show();
                    Log.d(LOG_TAG, "fetched question error : " + e.getMessage());
                }


            }
        });
    }

    private void updateQuestionView(Question q, Toolbar toolbar) {
        getHastTag(mQuestion);
        setQuestionView(mQuestion);
        setSupportActionBar(toolbar);
        String name = mQuestion.getWriter().getString(ModelUtils.NAME);
        if(name != null)
            setTitle(String.format(Locale.US,getResources().getString(R.string.posted_by),name));
        else
            setTitle("");

        fab = (FloatingActionButton) findViewById(R.id.fab_question_detail);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(questiontitle);
                saveAnswer(answerEditText.getText().toString(), view);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AvatarView avatarView = (AvatarView) findViewById(R.id.avatar_q_detail);
        setAvatarDrawable(avatarView);
        isShowingAnswers = false;
        initProgressToolbar();
    }


    private class AnswerTextWatch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if(count == 0)
                fab.setVisibility(View.INVISIBLE);
            else
                fab.setVisibility(View.VISIBLE);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(count > 0) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.INVISIBLE);

            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
