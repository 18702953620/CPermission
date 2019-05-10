package com.ch.cper.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.ch.cper.CPermission;
import com.ch.cper.PermissGroup;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 作者： ch
 * 时间： 2019/5/8 0008-下午 1:55
 * 描述：
 * 来源：
 */

public class PermissUtils {

    /**
     * 检查未取得的权限
     *
     * @return
     */
    public static List<String> getDeniedPermissions(Context context, List<String> permissions) {
        List<String> deniedList = new ArrayList<>();

        if (permissions == null || permissions.size() == 0) {
            return deniedList;
        }
        for (String permission : permissions) {
            if (!CPermission.hasPermission(context, permission)) {
                deniedList.add(permission);
            }
        }
        return deniedList;
    }


    /**
     * Get permissions to show rationale.
     */
    public static List<String> getRationalePermissions(Context context, List<String> permissions) {
        List<String> rationaleList = new ArrayList<>(1);
        for (String permission : permissions) {
            if (isShowRationalePermission(context, permission)) {
                rationaleList.add(permission);
            }
        }
        return rationaleList;
    }

    public static boolean isShowRationalePermission(Context context, String permission) {
        if (context instanceof Activity) {
            return isShowRationalePermission((Activity) context, permission);
        } else {
            isShowRationale(context, permission);
        }
        return false;
    }

    public static boolean isShowRationale(Context context, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;

        PackageManager packageManager = context.getPackageManager();
        Class<?> pkManagerClass = packageManager.getClass();
        try {
            Method method = pkManagerClass.getMethod("shouldShowRequestPermissionRationale", String.class);
            if (!method.isAccessible()) method.setAccessible(true);
            return (boolean) method.invoke(packageManager, permission);
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isShowRationalePermission(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;

        return activity.shouldShowRequestPermissionRationale(permission);
    }

    public static boolean hasAlwaysDeniedPermission(Context context, List<String> deniedPermissions) {
        for (String permission : deniedPermissions) {
            if (!isShowRationalePermission(context, permission)) {
                return true;
            }
        }
        return false;
    }

    public static Uri getFileUri(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, getMetaDataFromAuthority(context), file);
        }
        return Uri.fromFile(file);
    }

    /**
     * 获取androidmanifest.xml 中providers 的authority
     *
     * @param context
     * @return
     */
    public static String getMetaDataFromAuthority(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PROVIDERS);
            ProviderInfo[] providers = info.providers;
            if (providers == null || providers.length == 0) {
                return null;
            } else {
                return providers[0].authority;
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Check if the permissions are valid and each permission has been registered in manifest.xml. This method will
     * throw a exception if permissions are invalid or there is any permission which is not registered in manifest.xml.
     *
     * @param permissions permissions which will be checked.
     */
    public static void checkPermissions(Context context, String... permissions) {
        List<String> sAppPermissions = getManifestPermissions(context);

        if (permissions.length == 0) {
            throw new IllegalArgumentException("Please enter at least one permission.");
        }

        for (String p : permissions) {
            if (!sAppPermissions.contains(p)) {
                if (!(PermissGroup.ADD_VOICEMAIL.equals(p) &&
                        sAppPermissions.contains(PermissGroup.ADD_VOICEMAIL_MANIFEST))) {
                    throw new IllegalStateException(
                            String.format("The permission %1$s is not registered in manifest.xml", p));
                }
            }
        }
    }

    /**
     * Get a list of permissions in the manifest.
     */
    private static List<String> getManifestPermissions(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] permissions = packageInfo.requestedPermissions;
            if (permissions == null || permissions.length == 0) {
                throw new IllegalStateException("You did not register any permissions in the manifest.xml.");
            }
            return Collections.unmodifiableList(Arrays.asList(permissions));
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError("Package name cannot be found.");
        }
    }


}
