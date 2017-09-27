package co.askseoulites.seoulcityapp.model;

import android.content.Context;

import co.askseoulites.seoulcityapp.R;

/**
 * Created by hassanabid on 10/26/15.
 */
public class CategoryUtils  {

    public static String getCategoryStr(int pos, Context context) {

        String[] catArray = context.getResources().getStringArray(R.array.cat_array);
        return catArray[pos];
    }

    public static String getTagTitle(int pos, Context context) {
        // 23 total
        String[] catArray = context.getResources().getStringArray(R.array.tag_array);
        return catArray[pos];
    }
}
