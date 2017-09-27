/*
* Copyright (C) 2014 The Android Open Source Project
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import co.askseoulites.seoulcityapp.R;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class ProfileTagsAdapter extends BaseAdapter {
    private static final String TAG = "ProfileTagsAdapter";

    private String[] mDataSet;

    private final LayoutInflater mLayoutInflater;

    public ProfileTagsAdapter(Context context,String[] dataset) {
        mLayoutInflater = LayoutInflater.from(context);
        mDataSet = dataset;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(R.layout.tags_row_item, parent, false);
        }
        TextView tag = (TextView) convertView.findViewById(R.id.profileTagsTitle);
        tag.setText(mDataSet[position]);
        return convertView;
    }


    @Override
    public int getCount() {
        return mDataSet.length;
    }

    @Override
    public Object getItem(int position) {
        return mDataSet[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
