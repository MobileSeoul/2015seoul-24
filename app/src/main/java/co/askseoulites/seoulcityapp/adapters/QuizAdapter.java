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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import co.askseoulites.seoulcityapp.model.Quiz;
import co.askseoulites.seoulcityapp.widgets.AbsQuizView;
import co.askseoulites.seoulcityapp.widgets.FillBlankQuizView;
import co.askseoulites.seoulcityapp.widgets.FillTwoBlanksQuizView;

/**
 * Adapter to display quizzes.
 */
public class QuizAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Quiz> mQuizzes;
    private final int mViewTypeCount;
    private List<String> mQuizTypes;

    public QuizAdapter(Context context) {
        mContext = context;
        mViewTypeCount = calculateViewTypeCount();
        mQuizzes = null;

    }

    private int calculateViewTypeCount() {

        return 1;
    }

    @Override
    public int getCount() {
        return mQuizzes.size();
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return mViewTypeCount;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView instanceof AbsQuizView) {
                return convertView;

        }
        convertView = getViewInternal(0);
        return convertView;
    }

    private AbsQuizView getViewInternal(int quiz) {

        return createViewFor(0);
    }

    private AbsQuizView createViewFor(int quiz) {
        switch (quiz) {
            case 0: // FILL_BLANK
                return new FillBlankQuizView(mContext);
            case 1: //FILL_TWO_BLANKS:
                return new FillTwoBlanksQuizView(mContext);
        }
        throw new UnsupportedOperationException(
                "Quiz of type " + " can not be displayed.");
    }
}
