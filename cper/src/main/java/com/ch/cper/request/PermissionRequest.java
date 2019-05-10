package com.ch.cper.request;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.ch.cper.CPermission;
import com.ch.cper.PermissGroup;
import com.ch.cper.utils.PermissUtils;
import com.ch.cper.R;
import com.ch.cper.base.Request;
import com.ch.cper.listener.PermissListener;
import com.ch.cper.result.PermissHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 作者： ch
 * 时间： 2019/5/7 0007-下午 4:52
 * 描述：
 * 来源：
 */

public class PermissionRequest implements Request {

    private List<String> permissions;
    private Context context;
    private PermissListener<String> listener;
    private List<String> sAppPermissions;


    public PermissionRequest(Context context) {
        this.context = context;
    }

    public PermissionRequest permission(String... permissions) {
        PermissUtils.checkPermissions(context, permissions);
        this.permissions = Arrays.asList(permissions);
        return this;
    }

    @Override
    public PermissionRequest listener(PermissListener<String> granted) {
        this.listener = granted;
        return this;
    }


    @Override
    public void start() {
        List<String> permiss = PermissUtils.getDeniedPermissions(context, permissions);

        if (permiss.size() > 0) {
            List<String> rationaleList = PermissUtils.getRationalePermissions(context, permiss);
            if (rationaleList.size() > 0) {
                showSettingDialog();
            } else {
                PermissHelper.init(context).requestPermissions(permiss, new PermissHelper.PermissCallBack() {

                    @Override
                    public void onPermissResult(int resultCode) {
                        List<String> permiss = PermissUtils.getDeniedPermissions(context, permissions);
                        //没有拒绝的权限
                        if (permiss.isEmpty()) {
                            callbackSucceed();
                        } else {
                            callbackFailed(permiss);
                        }
                    }
                });
            }

        } else {
            callbackSucceed();
        }
    }


    private void callbackSucceed() {
        if (listener != null) {
            listener.onGranted(permissions);
        }
    }

    private void callbackFailed(List<String> deniedList) {
        if (PermissUtils.hasAlwaysDeniedPermission(context, permissions)) {
            //如果有禁止的权限
            showSettingDialog();
        }
        if (listener != null) {
            listener.onDenied(deniedList);
        }
    }

    private void showSettingDialog() {
        List<String> permissionNames = PermissGroup.transformText(context, permissions);
        String message = context.getString(R.string.message_permission_always_failed,
                TextUtils.join("\n", permissionNames));
        new AlertDialog.Builder(context).setCancelable(false)
                .setTitle(R.string.tip)
                .setMessage(message)
                .setPositiveButton(R.string.resume, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CPermission.with(context)
                                .setting()
                                .permission(permissions.toArray(new String[permissions.size()]))
                                .listener(listener)
                                .start();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }


}
