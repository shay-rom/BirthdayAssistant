package utils;

import android.app.Activity;
import android.app.AlertDialog;

public class DialogUtils {

    public static void showDialog(Activity activity, String title, String msg){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.create().show();
    }
}
