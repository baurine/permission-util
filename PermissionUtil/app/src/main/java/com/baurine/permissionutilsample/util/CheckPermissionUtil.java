package com.baurine.permissionutilsample.util;

import android.Manifest;
import android.app.Activity;

import com.baurine.permissionutil.PermissionUtil;
import com.baurine.permissionutilsample.R;

/**
 * Created by baurine on 2/10/17.
 */

public class CheckPermissionUtil {

    private static final int LOCATION_PERMISSION_REQ_CODE = 200;
    private static final int WRITE_SD_REQ_CODE = 201;

    public static void checkLocation(Activity activity,
                                     PermissionUtil.ReqPermissionCallback callback) {
        PermissionUtil.checkPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_PERMISSION_REQ_CODE,
                activity.getText(R.string.location_req_reason),
                activity.getText(R.string.location_reject_msg),
                callback);
    }

    public static void checkWriteSd(Activity activity,
                                    PermissionUtil.ReqPermissionCallback callback) {
        PermissionUtil.checkPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                WRITE_SD_REQ_CODE,
                "We need write external storage permission to save your location to file",
                "We can't save your location to file without storage permission",
                callback);
    }
}
