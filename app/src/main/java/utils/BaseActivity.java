package utils;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity implements RuntimePermissionRequester{
    private SparseArray<List<PermissionResult>> listenersMap = new SparseArray<>();

    public interface PermissionResult{
        void onPermissionGranted();
    }

    @Override
    public void requestRuntimePermission(AskRunTimePermissionsUtils.Permission permission, PermissionResult listener){
        boolean alreadyHasPermission = AskRunTimePermissionsUtils.askPermission(this, permission);
        if(alreadyHasPermission) {
            if(listener != null) {
                listener.onPermissionGranted();
            }
        } else {
            //
            addListener(permission, listener);
        }
    }

    private void addListener(AskRunTimePermissionsUtils.Permission permission, PermissionResult listener){
        List<PermissionResult> permissionListeners = listenersMap.get(permission.requestCode);
        if(permissionListeners == null) {
            permissionListeners = new ArrayList<>(1);
            listenersMap.put(permission.requestCode,permissionListeners);
        }

        permissionListeners.add(listener);
    }

    private void notifyToAllListeners(int requestCode){
        List<PermissionResult> permissionListeners = listenersMap.get(requestCode);
        if(permissionListeners != null) {
            for(PermissionResult listener : permissionListeners) {
                listener.onPermissionGranted();
            }
        }
    }

    private void removeAllListeners(int requestCode){
        List<PermissionResult> permissionListeners = listenersMap.get(requestCode);
        if(permissionListeners != null) {
            permissionListeners.clear();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        notifyToAllListeners(requestCode);
        removeAllListeners(requestCode);
    }
}
