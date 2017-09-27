package co.askseoulites.seoulcityapp;

import com.parse.ui.ParseLoginDispatchActivity;

/**
 * Created by hassanabid on 10/21/15.
 */
public class LoginDispatchActivity extends ParseLoginDispatchActivity {
    @Override
    protected Class<?> getTargetClass() {
        return MainActivity.class;
    }
}
