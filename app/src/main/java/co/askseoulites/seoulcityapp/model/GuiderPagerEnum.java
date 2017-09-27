package co.askseoulites.seoulcityapp.model;

/**
 * Created by hassanabid on 10/23/15.
 */
import co.askseoulites.seoulcityapp.R;

public enum GuiderPagerEnum {

    RED(R.string.app_name, R.layout.image_1),
    BLUE(R.string.about_us, R.layout.image_1),
    ORANGE(R.string.seoul_life, R.layout.image_1);

    private int mTitleResId;
    private int mLayoutResId;

    GuiderPagerEnum(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }
}
