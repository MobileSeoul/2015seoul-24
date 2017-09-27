package co.askseoulites.seoulcityapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

import co.askseoulites.seoulcityapp.R;
import co.askseoulites.seoulcityapp.activities.QuestionDetailActivity;
import co.askseoulites.seoulcityapp.model.ModelUtils;
import co.askseoulites.seoulcityapp.model.Question;
import co.askseoulites.seoulcityapp.viewholders.QViewHolder;

/**
 * Created by hassanabid on 10/24/15.
 */
public class QuestionAdapter extends RecyclerView.Adapter<QViewHolder> {

    private static final String LOG_TAG = QuestionAdapter.class.getSimpleName();
    private List<Question> mDataset;
    private Context mContext;
    private TextView voteTextView;
    private int mCatPos;


    // Provide a suitable constructor (depends on the kind of dataset)
    public QuestionAdapter(List<Question> myDataset, Context context, int cat) {
        mDataset = myDataset;
        mContext = context;
        mCatPos = cat;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public QViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_question_list_item, parent, false);
        TextView title = (TextView) v.findViewById(R.id.qtitle);
        TextView time = (TextView) v.findViewById(R.id.time_view);
        ImageView tagIcon = (ImageView) v.findViewById(R.id.tag_icon);
        TextView writer = (TextView) v.findViewById(R.id.writer_name);
        voteTextView = (TextView) v.findViewById(R.id.questionVotes);
        ImageView voteIcon = (ImageView) v.findViewById(R.id.questionVoteIcon);

        // set the view's size, margins, paddings and layout parameters
        QViewHolder vh = new QViewHolder(v,title,time,tagIcon,voteTextView,writer,voteIcon, new QViewHolder.QuestionViewHolderClicks() {
            @Override
            public void onClickQuestion(View caller, final int position) {
                if(mDataset != null & mDataset.size() != 0) {
                    if( caller instanceof TextView) {
                        Log.d(LOG_TAG, "question title clicked");
                        Intent intent = new Intent(mContext, QuestionDetailActivity.class);
                        intent.putExtra(ModelUtils.OBJECT_ID, (String) caller.getTag());
                        mContext.startActivity(intent);
                    } else if (caller instanceof ImageView) {
                        int voteCount =  Integer.valueOf(voteTextView.getText().toString());
                        ParseUser cUser = ParseUser.getCurrentUser();
                        final Question q = mDataset.get(position);
                        if (q.getVoters() != null & !q.getVoters().contains(cUser)) {
                            int newVoteCount = voteCount + 1;
                            voteTextView.setText(String.valueOf(newVoteCount));
                            q.increment(ModelUtils.VOTES);
                            q.addUnique(ModelUtils.VOTERS,cUser);
                            q.pinInBackground(ModelUtils.QUESTIONS_PIN + mCatPos, new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    q.saveInBackground();
                                    notifyDataSetChanged();
                                    Log.d(LOG_TAG, "updated vote count and object");
                                }
                            });
                            Log.d(LOG_TAG, "up vote icon clicked ; " + voteCount);
                        }
                    }
                }
            }
        });
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(QViewHolder holder, int position) {

        if(mDataset != null & mDataset.size() == 0 ) {
            holder.mTimeView.setVisibility(View.INVISIBLE);
            holder.voteCount.setVisibility(View.INVISIBLE);
            holder.writerName.setVisibility(View.INVISIBLE);
            holder.mTitleView.setText(mContext.getResources().getString(R.string.no_questions_posted));
            holder.voteIconView.setVisibility(View.INVISIBLE);
            holder.tagIconView.setVisibility(View.INVISIBLE);
        } else {
            Question q = mDataset.get(position);
            PrettyTime p = new PrettyTime();
            Date now = new Date(System.currentTimeMillis());
            long diff = now.getTime() - q.getCreatedAt().getTime();
            holder.mTitleView.setText(q.getTitle());
            holder.mTitleView.setTag(q.getObjectId());
            holder.mTimeView.setText(p.format(new Date(System.currentTimeMillis() - diff)));
            if(q.getVoters()!= null && q.getVoters().size() != 0) {
                holder.voteCount.setText(String.valueOf(q.getVoters().size()));
            } else {
                holder.voteCount.setText("0");
            }
            holder.writerName.setText(q.getWriter().getString(ModelUtils.NAME));
        }

    }

    private void update() {
        notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(mDataset != null && mDataset.size() == 0)
            return  1;
        return mDataset.size();
    }
}
