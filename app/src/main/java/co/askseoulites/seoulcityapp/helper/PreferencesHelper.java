/*
 * Copyright 2015 Google Inc.
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
package co.askseoulites.seoulcityapp.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.parse.ParseUser;

/**
 * Easy storage and retrieval of preferences.
 */
public class PreferencesHelper {

    private static final String USER_PREFERENCES = "userPreferences";
    private static final String PREFERENCE_FIRST_NAME = USER_PREFERENCES + ".firstName";
    private static final String PREFERENCE_LAST_INITIAL = USER_PREFERENCES + "lastName";
    private static final String PREFERENCE_TAG = USER_PREFERENCES + ".tag";

    private PreferencesHelper() {
        //no instance
    }


    public static void writeToPreferences(Context context, ParseUser player,int tagPos) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREFERENCE_FIRST_NAME, player.getString("name"));
        editor.putString(PREFERENCE_LAST_INITIAL, player.getString("name"));
        Log.d(USER_PREFERENCES,"tagPos :" + tagPos);
        editor.putInt(PREFERENCE_TAG, tagPos);
        editor.apply();
        editor.commit();
    }


/*    public static Player getPlayer(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        final String firstName = preferences.getString(PREFERENCE_FIRST_NAME, null);
        final String lastInitial = preferences.getString(PREFERENCE_LAST_INITIAL, null);
        final String avatarPreference = preferences.getString(PREFERENCE_TAG, null);
        final Avatar avatar;
        if (null != avatarPreference) {
            avatar = Avatar.valueOf(avatarPreference);
        } else {
            avatar = null;
        }

        if (null == firstName && null == lastInitial && null == avatar) {
            return null;
        }
        return new Player(firstName, lastInitial, avatar);
    }*/

    public static int getTagPosition(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        final int avatarPreference = preferences.getInt(PREFERENCE_TAG, 0);
        return avatarPreference;
    }
    /**
     * Signs out a player by removing all it's data.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     */
    public static void signOut(Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(PREFERENCE_FIRST_NAME);
        editor.remove(PREFERENCE_LAST_INITIAL);
        editor.remove(PREFERENCE_TAG);
        editor.apply();
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.edit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
    }
}
