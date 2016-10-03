package utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.srh.birthdayassistant.App;

import java.io.IOException;

public class BitmapUtils {

    public static Bitmap getBitmapFromUri(Uri imageUri){
        try {
            return MediaStore.Images.Media.getBitmap(App.get().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
