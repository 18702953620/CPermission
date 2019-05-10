package com.ch.cper.request;

import android.content.Context;
import android.content.Intent;

import com.ch.cper.utils.IntentUtils;
import com.ch.cper.utils.PermissUtils;
import com.ch.cper.base.Request;
import com.ch.cper.listener.PermissListener;
import com.ch.cper.result.PermissHelper;

import java.util.Arrays;
import java.util.List;

/**
 * 作者： ch
 * 时间： 2019/5/8 0008-上午 11:24
 * 描述：
 * 来源：
 */

public class SettingRequest implements Request {

    private Context context;
    private PermissListener<String> listener;
    private List<String> permissions;

    public SettingRequest(Context context) {
        this.context = context;
    }

    @Override
    public SettingRequest listener(PermissListener<String> granted) {
        this.listener = granted;
        return this;
    }


    @Override
    public SettingRequest permission(String... permissions) {
        this.permissions = Arrays.asList(permissions);
        return this;
    }

    @Override
    public void start() {

        Intent intent = IntentUtils.getSettingIntent(context);
        PermissHelper.init(context).startActivityForResult(intent, new PermissHelper.CallBack() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
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

    private void callbackSucceed() {
        if (listener != null) {
            listener.onGranted(permissions);
        }
    }

    private void callbackFailed(List<String> deniedList) {
        if (listener != null) {
            listener.onDenied(deniedList);
        }
    }
}
