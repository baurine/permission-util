package com.baurine.permissionutil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baurine on 2/10/17.
 */

public class PermissionUtil {

    public interface ReqPermissionCallback {
        void onResult(boolean success);
    }

    private static class PermissionReq {
        final Activity activity;
        final String permission;
        final int reqCode;
        final CharSequence reqReason;
        final CharSequence rejectedMsg;
        final ReqPermissionCallback callback;

        PermissionReq(Activity activity,
                      String permission,
                      int reqCode,
                      CharSequence reqReason,
                      CharSequence rejectedMsg,
                      ReqPermissionCallback callback) {
            this.activity = activity;
            this.permission = permission;
            this.reqCode = reqCode;
            this.reqReason = reqReason;
            this.rejectedMsg = rejectedMsg;
            this.callback = callback;
        }
    }

    private static List<PermissionReq> permissionReqList = new ArrayList<>();

    public static boolean hasPermission(Activity activity, String permission) {
        return ActivityCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void checkPermission(Activity activity,
                                       String permission,
                                       int reqCode,
                                       CharSequence reqReason,
                                       CharSequence rejectedMsg,
                                       final ReqPermissionCallback callback) {
        if (hasPermission(activity, permission)) {
            // we shouldn't callback directly, it will mix the sync and async logic
            // if you want to check permission sync, you should manually call hasPermission() method

            // callback.onResult(true);
            activity.getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    callback.onResult(true);
                }
            });
        } else {
            boolean shouldShowReqReason = ActivityCompat
                    .shouldShowRequestPermissionRationale(activity, permission);
            PermissionReq req = new PermissionReq(
                    activity, permission, reqCode, reqReason, rejectedMsg, callback);
            if (shouldShowReqReason) {
                showReqReason(req);
            } else {
                reqPermission(req);
            }
        }
    }

    private static void showReqReason(final PermissionReq req) {
        new AlertDialog.Builder(req.activity)
                .setCancelable(false)
                .setMessage(req.reqReason)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reqPermission(req);
                    }
                })
                .show();
    }

    private static void reqPermission(PermissionReq req) {
        permissionReqList.add(req);
        ActivityCompat.requestPermissions(req.activity, new String[]{req.permission}, req.reqCode);
    }

    private static void showRejectedMsg(final PermissionReq req) {
        new AlertDialog.Builder(req.activity)
                .setCancelable(false)
                .setMessage(req.rejectedMsg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        req.callback.onResult(false);
                        permissionReqList.remove(req);
                    }
                })
                .setNegativeButton(R.string.change_setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openAppDetailSetting(req);
                    }
                })
                .show();
    }

    private static void openAppDetailSetting(PermissionReq req) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", req.activity.getPackageName(), null);
        intent.setData(uri);
        req.activity.startActivityForResult(intent, req.reqCode);
    }

    // Because this lib only supports request one permission at one time,
    // so this method called 'onRequestPermissionResult',
    // not 'onRequestPermissionsResult'
    public static void onRequestPermissionResult(Activity activity,
                                                 int requestCode,
                                                 String[] permissions,
                                                 int[] grantResults) {
        PermissionReq targetReq = null;
        for (PermissionReq req : permissionReqList) {
            if (req.activity.equals(activity)
                    && req.reqCode == requestCode
                    && req.permission.equals(permissions[0])) {
                targetReq = req;
                break;
            }
        }
        if (targetReq != null) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                targetReq.callback.onResult(true);
                permissionReqList.remove(targetReq);
            } else {
                if (TextUtils.isEmpty(targetReq.rejectedMsg)) {
                    targetReq.callback.onResult(false);
                    permissionReqList.remove(targetReq);
                } else {
                    showRejectedMsg(targetReq);
                }
            }
        }
    }

    public static void onActivityResult(Activity activity,
                                        int reqCode) {
        PermissionReq targetReq = null;
        for (PermissionReq req : permissionReqList) {
            if (req.activity.equals(activity)
                    && req.reqCode == reqCode) {
                targetReq = req;
                break;
            }
        }
        if (targetReq != null) {
            if (hasPermission(targetReq.activity, targetReq.permission)) {
                targetReq.callback.onResult(true);
            } else {
                targetReq.callback.onResult(false);
            }
            permissionReqList.remove(targetReq);
        }
    }
}
