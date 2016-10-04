package utils;

import android.support.annotation.StringRes;

import com.srh.birthdayassistant.App;

public class Resources {
    public static String getString(@StringRes int id){
        return App.get().getResources().getString(id);
    }

    public static String getString(@StringRes int id, Object... formatArgs){
        return App.get().getResources().getString(id, formatArgs);
    }
}
