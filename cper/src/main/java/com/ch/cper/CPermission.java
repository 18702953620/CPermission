package com.ch.cper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.ch.cper.base.Boot;

import java.util.Arrays;
import java.util.List;

/**
 * 作者： ch
 * 时间： 2019/5/7 0007-下午 4:45
 * 描述：
 * 来源：
 */

public class CPermission {

    public static Boot with(Context context) {
        return new Boot(context);
    }

    public static boolean hasPermission(Context context, String... permissions) {
        return hasPermission(context, Arrays.asList(permissions));
    }

    public static boolean hasPermission(Context context, List<String> permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            int result = context.checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid());
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


}
