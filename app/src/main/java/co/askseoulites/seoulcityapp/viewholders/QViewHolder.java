package co.askseoulites.seoulcityapp.viewholders;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by hassanabid on 10/25/15.
 */
// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public class QViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String LOG_TAG = QViewHolder.class.getSimpleName();
    public TextView mTitleView;
    public TextView mTimeView;
    public ImageView tagIconView;
    public TextView writerName;
    public TextView voteCount;
    public ImageView voteIconView;

    public QuestionViewHolderClicks mListener;

    public QViewHolder(View v,TextView title,TextView time,ImageView tagIcon
                       ,TextView vote,TextView writer,ImageView voteIcon, QuestionViewHolderClicks listener) {
        super(v);
        mListener = listener;
        mTitleView = title;
        mTimeView = time;
        tagIconView = tagIcon;
        voteCount = vote;
        writerName = writer;
        voteIconView = voteIcon;
        voteIconView.setOnClickListener(this);
        mTitleView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d(LOG_TAG,"adapter position : " + getAdapterPosition() + " layout pos: "
                + getLayoutPosition());
        mListener.onClickQuestion(v,getAdapterPosition());
    }

    public static interface QuestionViewHolderClicks {
        public void onClickQuestion(View caller, int position);
    }
}