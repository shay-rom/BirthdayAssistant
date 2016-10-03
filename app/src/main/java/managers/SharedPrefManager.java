package managers;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.srh.birthdayassistant.App;

public class SharedPrefManager {
    private static SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(App.get());
    public static void put(String key, String value){
        sharedPrefs.edit().putString(key, value).apply();
    }

    public static String get(String key, String defaultVal){
        return sharedPrefs.getString(key, defaultVal);
    }

}
