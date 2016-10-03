package utils;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.widget.ImageView;

import com.srh.birthdayassistant.App;
import com.srh.birthdayassistant.R;

public class ImageViewUtils {
    public static void setImageUri(ImageView iv, Uri imageUri, Drawable defaultImage){
        if(imageUri != null) {
            iv.setImageURI(imageUri);
            iv.setColorFilter(null);
        } else {
            iv.setImageDrawable(defaultImage);
            iv.setColorFilter(ThemeUtils.getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }
    }

    public static Drawable getSvgDrawable(@DrawableRes int svgRes){
        return AppCompatDrawableManager.get().getDrawable(App.get(), svgRes);
    }


    public static Drawable getDrawable(@DrawableRes int resId){
        return ContextCompat.getDrawable(App.get(), resId);
    }
}
