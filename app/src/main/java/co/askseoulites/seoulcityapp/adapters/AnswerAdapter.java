/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.askseoulites.seoulcityapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import co.askseoulites.seoulcityapp.R;

import java.util.List;

import co.askseoulites.seoulcityapp.model.Answer;
import co.askseoulites.seoulcityapp.model.ModelUtils;

/**
 * Adapter for displaying score cards.
 */
public class AnswerAdapter extends BaseAdapter {

    private static final String LOG_TAG = AnswerAdapter.class.getSimpleName();
    private final int count;
    private final List<Answer> mAnswers;

    private Drawable mSuccessIcon;
    private Drawable mFailedIcon;
    private Context mContext;

    public AnswerAdapter(Context context, List<Answer> answers) {
        mAnswers = answers;
        count = mAnswers.size();
        mContext = context;
    }

    @Override
    public int getCount() {
        return mAnswers.size();
    }

    @Override
    public Answer getItem(int position) {
        return mAnswers.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position > count || position < 0) {
            return AbsListView.INVALID_POSITION;
        }
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = createView(parent);
        }

        final Answer answer = getItem(position);
        Log.d(LOG_TAG,"answer : " + answer.getObjectId());
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.mQuizView.setText(answer.getContent());
        viewHolder.mAnswerView.setText(answer.getWriter().getString(ModelUtils.NAME));
        setSolvedStateForQuiz(viewHolder.mSolvedState, answer.getStatus());
        return convertView;
    }

    private void setSolvedStateForQuiz(ImageView solvedState, boolean solved) {
//        final Context context = solvedState.getContext();
        final Drawable tintedImage;
        if (solved) {
            tintedImage = getSuccessIcon(mContext);
        } else {
            tintedImage = getFailedIcon(mContext);
        }
        solvedState.setImageDrawable(tintedImage);
    }

    private Drawable getSuccessIcon(Context context) {
        if (null == mSuccessIcon) {
            mSuccessIcon = loadAndTint(context, R.drawable.ic_tick, R.color.theme_green_primary);
        }
        return mSuccessIcon;
    }

    private Drawable getFailedIcon(Context context) {
        if (null == mFailedIcon) {
            mFailedIcon = loadAndTint(context, R.drawable.ic_cross, R.color.theme_red_primary);
        }
        return mFailedIcon;
    }

    /**
     * Convenience method to aid tintint of vector drawables at runtime.
     *
     * @param context The {@link Context} for this app.
     * @param drawableId The id of the drawable to load.
     * @param tintColor The tint to apply.
     * @return The tinted drawable.
     */
    private Drawable loadAndTint(Context context, @DrawableRes int drawableId,
                                 @ColorRes int tintColor) {
        Drawable imageDrawable = ContextCompat.getDrawable(context, drawableId);
        if (imageDrawable == null) {
            throw new IllegalArgumentException("The drawable with id " + drawableId
                    + " does not exist");
        }
        DrawableCompat.setTint(imageDrawable, tintColor);
        return imageDrawable;
    }

    private View createView(ViewGroup parent) {
        View convertView;
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup scorecardItem = (ViewGroup) inflater.inflate(
                R.layout.item_scorecard, parent, false);
        convertView = scorecardItem;
        ViewHolder holder = new ViewHolder(scorecardItem);
        convertView.setTag(holder);
        return convertView;
    }

    private class ViewHolder {

        final TextView mAnswerView;
        final TextView mQuizView;
        final ImageView mSolvedState;

        public ViewHolder(ViewGroup scorecardItem) {
            mQuizView = (TextView) scorecardItem.findViewById(R.id.quiz);
            mAnswerView = (TextView) scorecardItem.findViewById(R.id.answer);
            mSolvedState = (ImageView) scorecardItem.findViewById(R.id.solved_state);
        }

    }
}
