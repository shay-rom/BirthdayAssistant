package utils;

import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

public class TextViewUtils {
    public static void setTextAndVisibility(TextView tv, @StringRes int id){
        setTextAndVisibility(tv, id == 0 ? null : Resources.getString(id));
    }

    public static void setTextAndVisibility(TextView tv, String text){
        tv.setText(text);
        tv.setVisibility(StringUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }
}
