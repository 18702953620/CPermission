package com.ch.cper.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.io.File;

/**
 * 作者： ch
 * 时间： 2019/5/9 0009-下午 2:52
 * 描述：
 * 来源：
 */

public class IntentUtils {

    /**
     * 安装apk
     *
     * @param context
     * @param file
     * @return
     */
    public static Intent getInstallIntent(Context context, File file) {


        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = PermissUtils.getFileUri(context, file);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }


    public static Intent getRequestInstailIntent(Context context) {
        Intent manageIntent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        manageIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return manageIntent;
    }

    /**
     * 设置
     *
     * @param context
     * @return
     */
    public static Intent getSettingIntent(Context context) {
        String MARK = Build.MANUFACTURER.toLowerCase();
        Intent intent;
        if (MARK.contains("huawei")) {
            intent = huaweiApi(context);
        } else if (MARK.contains("xiaomi")) {
            intent = xiaomiApi(context);
        } else if (MARK.contains("oppo")) {
            intent = oppoApi(context);
        } else if (MARK.contains("vivo")) {
            intent = vivoApi(context);
        } else if (MARK.contains("meizu")) {
            intent = meizuApi(context);
        } else {
            intent = defaultApi(context);
        }

        return intent;
    }

    private static Intent defaultApi(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return intent;
    }

    private static Intent huaweiApi(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultApi(context);
    }

    private static Intent xiaomiApi(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", context.getPackageName());
        if (hasActivity(context, intent)) return intent;

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        if (hasActivity(context, intent)) return intent;

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultApi(context);
    }

    private static Intent vivoApi(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packagename", context.getPackageName());
        intent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity");
        if (hasActivity(context, intent)) return intent;

        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultApi(context);
    }

    private static Intent oppoApi(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", context.getPackageName());
        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
        if (hasActivity(context, intent)) return intent;

        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionAppListActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultApi(context);
    }

    private static Intent meizuApi(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.putExtra("packageName", context.getPackageName());
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultApi(context);
    }

    private static boolean hasActivity(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }
}
