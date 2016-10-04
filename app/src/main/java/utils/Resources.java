package utils;

import android.support.annotation.StringRes;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.srh.birthdayassistant.App;

public class Resources {
    public static String getString(@StringRes int id){
        return App.get().getResources().getString(id);
    }

    public static String getString(@StringRes int id, Object... formatArgs){
        return App.get().getResources().getString(id, formatArgs);
    }

    public static float px2Dp(float px){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, App.get().getResources().getDisplayMetrics());
    }

    public static float dp2Px(float dp){
        DisplayMetrics metrics = App.get().getResources().getDisplayMetrics();
        return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
