package utils;

import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import com.srh.birthdayassistant.App;

public class ThemeUtils {

    public static int getColor(@ColorRes int colorRes){
        return ContextCompat.getColor(App.get(), colorRes);
    }
}
