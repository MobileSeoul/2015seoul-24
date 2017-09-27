package co.askseoulites.seoulcityapp;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by hassanabid on 10/22/15.
 */
public class CategoryAdapter extends BaseAdapter {


    public static final String DRAWABLE = "drawable";
    private static final String ICON_CATEGORY = "icon_category_";
    private final Resources mResources;
    private final LayoutInflater mLayoutInflater;
    private final Activity mActivity;
    private String[] CAT_TITLES;

    public CategoryAdapter(Activity activity, String[] titles) {
        mResources = activity.getResources();
        mActivity = activity;
        CAT_TITLES = titles;
        mLayoutInflater = LayoutInflater.from(activity.getApplicationContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(R.layout.category_grid_item, parent, false);
            convertView.setTag(new CategoryViewHolder((LinearLayout) convertView));
        }
        CategoryViewHolder holder = (CategoryViewHolder) convertView.getTag();
        ImageView icon = holder.icon;
        icon.setImageResource(mThumbIds[position]);
//        holder.icon.setBackgroundColor(mResources.getColor(ColorUtils.getMainIconColor(position)));
        holder.title.setText(CAT_TITLES[position]);
        holder.title.setTextColor(mResources.getColor(ColorUtils.getMaterialText(position)));
        holder.title.setBackgroundColor(mResources.getColor(ColorUtils
                .getMainIconColor(position)));
        return convertView;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
//        updateCategories(mActivity);
    }

    private Integer[] mThumbIds = {
            R.drawable.main_icon_01,
            R.drawable.main_icon_02,
            R.drawable.main_icon_03,
            R.drawable.main_icon_04,
            R.drawable.main_icon_05,
            R.drawable.main_icon_06
    }; // 7


}
