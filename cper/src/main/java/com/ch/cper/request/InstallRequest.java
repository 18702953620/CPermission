package com.ch.cper.request;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.ch.cper.PermissGroup;
import com.ch.cper.base.Request;
import com.ch.cper.listener.PermissListener;
import com.ch.cper.utils.PermissUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 作者： ch
 * 时间： 2019/5/9 0009-下午 2:14
 * 描述：
 * 来源：
 */

public class InstallRequest implements Request {
    private static final int MODE_ASK = 4;
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_REQUEST_INSTALL_PACKAGES = "OP_REQUEST_INSTALL_PACKAGES";
    public Context context;
    public File file;
    private PermissListener<String> listener;

    public InstallRequest(Context context) {
        this.context = context;
    }


    @Override
    public InstallRequest listener(PermissListener<String> granted) {
        this.listener = granted;
        return this;
    }

    @Override
    public InstallRequest permission(String... permissions) {
        return this;
    }

    public InstallRequest file(File file) {
        PermissUtils.checkPermissions(context, PermissGroup.REQUEST_INSTALL_PACKAGES);
        this.file = file;
        return this;
    }

    @Override
    public void start() {

    }

    public void callbackSucceed() {
        if (listener != null) {
            listener.onGranted(null);
        }
    }

    public void callbackFailed() {
        if (listener != null) {
            listener.onDenied(null);
        }
    }


    public boolean canRequestPackageInstalls() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (getTargetSdkVersion(context) < Build.VERSION_CODES.O) {
                return reflectionOps(OP_REQUEST_INSTALL_PACKAGES);
            }
            return getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    private int getTargetSdkVersion(Context context) {
        return context.getApplicationInfo().targetSdkVersion;
    }

    private String getPackageName() {
        return context.getApplicationContext().getPackageName();
    }

    private PackageManager getPackageManager() {
        return context.getPackageManager();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean reflectionOps(String opFieldName) {
        int uid = context.getApplicationInfo().uid;
        try {
            Class<AppOpsManager> appOpsClass = AppOpsManager.class;
            Method method = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
            Field opField = appOpsClass.getDeclaredField(opFieldName);
            int opValue = (int) opField.get(Integer.class);
            int result = (int) method.invoke(getAppOpsManager(), opValue, uid, getPackageName());
            return result == AppOpsManager.MODE_ALLOWED || result == MODE_ASK;
        } catch (Throwable e) {
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private AppOpsManager getAppOpsManager() {
        return (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
    }
}
