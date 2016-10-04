package utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.v4.content.FileProvider;

import com.srh.birthdayassistant.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import managers.FeedbackManager;

public class BitmapUtils {

    public static Bitmap getBitmapFromUri(Uri imageUri){
        try {
            return MediaStore.Images.Media.getBitmap(App.get().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String saveBitmapToInternalStorageCache(Bitmap bitmap, String imgName){
        // save bitmap to cache directory
        String fullPath = null;
        try {

            File cachePath = new File(App.get().getCacheDir(), "images");
            if(cachePath.exists() || cachePath.mkdirs()) {// don't forget to make the directory
                fullPath = cachePath + "/" + imgName + ".png";
                FileOutputStream stream = new FileOutputStream(fullPath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.flush();
                stream.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullPath;
    }

    public static void shareBitmap(Activity activity, Bitmap bitmap, String title){
        String fullPath = saveBitmapToInternalStorageCache(bitmap, "image");
        if(fullPath == null) {
            FeedbackManager.showToast("Fail to share image");
            return;
        }

        File newFile = new File(fullPath);
        Uri contentUri = FileProvider.getUriForFile(App.get(), App.get().getPackageName() + ".fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, App.get().getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.setType("image/png");
            activity.startActivity(Intent.createChooser(shareIntent, title ));
        }
    }

    public static Bitmap getBitmap(@DrawableRes int resId){
        return BitmapFactory.decodeResource(App.get().getResources(), resId);
    }
}
