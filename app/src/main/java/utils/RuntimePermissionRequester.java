package utils;
public interface RuntimePermissionRequester {
    void requestRuntimePermission(AskRunTimePermissionsUtils.Permission permission, BaseActivity.PermissionResult listener);
}
