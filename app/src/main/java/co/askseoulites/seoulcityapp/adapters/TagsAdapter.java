package co.askseoulites.seoulcityapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import co.askseoulites.seoulcityapp.model.CategoryUtils;
import co.askseoulites.seoulcityapp.model.Tags;
import co.askseoulites.seoulcityapp.widgets.AvatarView;
import co.askseoulites.seoulcityapp.R;

/**
 * Created by hassanabid on 10/25/15.
 */
public class TagsAdapter extends BaseAdapter{
    private static final Tags[] mTags = Tags.values();

    private final LayoutInflater mLayoutInflater;
    private Context mContext;

    public TagsAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(R.layout.tags_row_item, parent, false);
        }
        setAvatar((TextView) convertView.findViewById(R.id.profileTagsTitle), mTags[position], position);
        return convertView;
    }

    private void setAvatar(TextView mIcon, Tags tag,int position) {
//        mIcon.setImageResource(tag.getDrawableId());
        mIcon.setText(CategoryUtils.getTagTitle(position,mContext));
        mIcon.setContentDescription(tag.getNameForAccessibility());
    }

    @Override
    public int getCount() {
        return mTags.length;
    }

    @Override
    public Object getItem(int position) {
        return mTags[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
