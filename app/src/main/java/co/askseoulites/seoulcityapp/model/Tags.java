package co.askseoulites.seoulcityapp.model;

import android.support.annotation.DrawableRes;
import co.askseoulites.seoulcityapp.R;

/**
 * Created by hassanabid on 10/25/15.
 */
public enum Tags {
    ONE(R.drawable.avatar_6),
    TWO(R.drawable.avatar_6),
    THREE(R.drawable.avatar_6),
    FOUR(R.drawable.avatar_6),
    FIVE(R.drawable.avatar_6),
    SIX(R.drawable.avatar_6),
    SEVEN(R.drawable.avatar_6),
    EIGHT(R.drawable.avatar_6),
    NINE(R.drawable.avatar_6),
    TEN(R.drawable.avatar_6),
    ELEVEN(R.drawable.avatar_6),
    TWELVE(R.drawable.avatar_6),
    THIRTEEN(R.drawable.avatar_6),
    FOURTEEN(R.drawable.avatar_6),
    FIFTEEN(R.drawable.avatar_6),
    SIXTEEN(R.drawable.avatar_6),
    SEVENTEEN(R.drawable.avatar_6),
    EIGHTEEN(R.drawable.avatar_6),
    NINETEEN(R.drawable.avatar_6),
    TWENTY(R.drawable.avatar_6),
    TWENTYONE(R.drawable.avatar_6),
    TWENTYTWO(R.drawable.avatar_6),
    TWENTYTHREE(R.drawable.avatar_6);


    private static final String TAG = "Tags";

    private final int mResId;

    Tags(@DrawableRes final int resId) {
        mResId = resId;
    }

    @DrawableRes
    public int getDrawableId() {
        return mResId;
    }

    public String getNameForAccessibility() {
        return TAG + " " + ordinal() + 1;
    }
}
