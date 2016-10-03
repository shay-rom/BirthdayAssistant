package utils;

import android.view.View;
import android.widget.TextView;

public class TextViewUtils {
    public static void setTextAndVisibility(TextView tv, String text){
        tv.setText(text);
        tv.setVisibility(StringUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }
}
