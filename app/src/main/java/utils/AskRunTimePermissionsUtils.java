package utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;

import com.srh.birthdayassistant.App;
import com.srh.birthdayassistant.R;

public class AskRunTimePermissionsUtils {
    private static SparseArray<Permission> requestCodeToPermissionMap = new SparseArray<>();

    static {
        for(Permission permission : Permission.values()) {
            requestCodeToPermissionMap.put(permission.requestCode, permission);
        }
    }

    public static Permission getPermissionEnum(int requestCode){
        return requestCodeToPermissionMap.get(requestCode);
    }

    public enum Permission{
        ReadContacts(Manifest.permission.READ_CONTACTS, 0),
        WriteContacts(Manifest.permission.WRITE_CONTACTS, 1);

        public final String permissionName;
        public final int requestCode;

        Permission(String permissionName, int requestCode) {
            this.permissionName = permissionName;
            this.requestCode = requestCode;
        }


    }

    public static boolean hasPermission(Permission permission){
        return ContextCompat.checkSelfPermission(App.get(),
                permission.permissionName)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     *
     * @param activity - responsible to handle the onRequestPermissionsResult function
     * @param permission - which permission we want to ask
     * @return true if we already has the permission
     */
    public static boolean askPermission(Activity activity, Permission permission){
        // Here, thisActivity is the current activity
        if (hasPermission(permission)) {
            return true;
        } else {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permission.permissionName)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                DialogUtils.showDialog(activity, App.getStringRes(R.string.dialog_read_contacts_permission_title),App.getStringRes(R.string.dialog_read_contacts_permission_msg));

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{permission.permissionName},
                        permission.requestCode);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

            return false;
        }
    }
}
