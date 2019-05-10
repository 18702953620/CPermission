package com.ch.cper.base;

import android.content.Context;
import android.os.Build;

import com.ch.cper.request.InstallLRequest;
import com.ch.cper.request.InstallORequest;
import com.ch.cper.request.InstallRequest;
import com.ch.cper.request.PermissionRequest;
import com.ch.cper.request.SettingRequest;

/**
 * 作者： ch
 * 时间： 2019/5/8 0008-下午 1:58
 * 描述：
 * 来源：
 */

public class Boot implements Option {
    private Context context;

    public Boot(Context context) {
        this.context = context;
    }

    @Override
    public PermissionRequest permiss() {
        return new PermissionRequest(context);
    }

    @Override
    public SettingRequest setting() {
        return new SettingRequest(context);
    }

    @Override
    public InstallRequest install() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new InstallORequest(context);
        } else {
            return new InstallLRequest(context);
        }
    }
}
