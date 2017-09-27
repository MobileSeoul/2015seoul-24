package co.askseoulites.seoulcityapp.helper;

/**
 * Created by hassanabid on 10/22/15.
 */
import android.os.Build;

/**
 * Encapsulates checking api levels.
 */
public class ApiLevelHelper {

    /**
     * Checks if the current api level is at least the provided value.
     *
     * @param apiLevel One of the values within {@link Build.VERSION_CODES}.
     * @return <code>true</code> if the calling version is at least <code>apiLevel</code>.
     * Else <code>false</code> is returned.
     */
    public static boolean isAtLeast(int apiLevel) {
        return Build.VERSION.SDK_INT >= apiLevel;
    }

    /**
     * Checks if the current api level is at lower than the provided value.
     *
     * @param apiLevel One of the values within {@link Build.VERSION_CODES}.
     * @return <code>true</code> if the calling version is lower than <code>apiLevel</code>.
     * Else <code>false</code> is returned.
     */
    public static boolean isLowerThan(int apiLevel) {
        return Build.VERSION.SDK_INT < apiLevel;
    }
}