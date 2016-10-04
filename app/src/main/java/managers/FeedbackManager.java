package managers;

import android.widget.Toast;

import com.srh.birthdayassistant.App;
import com.srh.birthdayassistant.BuildConfig;

public class FeedbackManager {

    public static void showToast(final String text){
        if(App.get().isOnMainThread()) {
            Toast.makeText(App.get(), text, Toast.LENGTH_SHORT).show();
        } else {
            App.get().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(App.get(), text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void debug(final String text){
        if (BuildConfig.DEBUG) {
            showToast(text);
        }
    }

}
