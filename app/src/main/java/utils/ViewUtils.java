package utils;

import android.view.HapticFeedbackConstants;
import android.view.View;

public class ViewUtils {
    public static void vibrate(View v){
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }
}
