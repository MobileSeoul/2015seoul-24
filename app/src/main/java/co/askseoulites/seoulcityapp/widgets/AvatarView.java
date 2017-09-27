package co.askseoulites.seoulcityapp.widgets;

/**
 * Created by hassanabid on 10/22/15.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;
import co.askseoulites.seoulcityapp.R;


import co.askseoulites.seoulcityapp.helper.ApiLevelHelper;

/**
 * A simple view that wraps an avatar.
 */
public class AvatarView extends ImageView implements Checkable {

    private boolean mChecked;

    public AvatarView(Context context) {
        this(context, null);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setChecked(boolean b) {
        mChecked = b;
        invalidate();
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    /**
     * Set the image for this avatar. Will be used to create a round version of this avatar.
     *
     * @param resId The image's resource id.
     */
    public void setAvatar(@DrawableRes int resId) {
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            setClipToOutline(true);
            setImageResource(resId);
        } else {
            setAvatarPreLollipop(resId);
        }
    }

    private void setAvatarPreLollipop(@DrawableRes int resId) {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), resId,
                getContext().getTheme());
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(),
                bitmapDrawable.getBitmap());
        roundedDrawable.setCircular(true);
        setImageDrawable(roundedDrawable);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (mChecked) {
            Drawable border = ContextCompat.getDrawable(getContext(), R.drawable.selector_avatar);
            border.setBounds(0, 0, getWidth(), getHeight());
            border.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (ApiLevelHelper.isLowerThan(Build.VERSION_CODES.LOLLIPOP)) {
            return;
        }
        if (w > 0 && h > 0) {
            setOutlineProvider(new RoundOutlineProvider(Math.min(w, h)));
        }
    }
}