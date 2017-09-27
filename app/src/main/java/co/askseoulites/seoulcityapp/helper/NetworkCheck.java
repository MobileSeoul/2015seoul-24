package co.askseoulites.seoulcityapp.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by hassankcdh on 2/8/15.
 */
public class NetworkCheck {
    
    public NetworkCheck() {
        
        
    }
    
    public static boolean isNetworkAvailable(Context context) {


        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
       return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }
}
