package com.ch.cper.request;

import android.content.Context;
import android.content.Intent;

import com.ch.cper.base.Request;
import com.ch.cper.result.PermissHelper;
import com.ch.cper.utils.IntentUtils;

/**
 * 作者： ch
 * 时间： 2019/5/9 0009-下午 2:14
 * 描述：
 * 来源：
 */

public class InstallORequest extends InstallRequest implements Request {

    public InstallORequest(Context context) {
        super(context);
    }

    @Override
    public void start() {
        if (file == null || !file.exists()) {
            callbackFailed();
            return;
        }
        if (canRequestPackageInstalls()) {
            context.startActivity(IntentUtils.getInstallIntent(context, file));
        } else {
            final Intent intent = IntentUtils.getRequestInstailIntent(context);
            PermissHelper.init(context).startActivityForResult(intent, new PermissHelper.CallBack() {
                @Override
                public void onActivityResult(int resultCode, Intent data) {
                    if (canRequestPackageInstalls()) {
                        callbackSucceed();
                        context.startActivity(IntentUtils.getInstallIntent(context, file));
                    } else {
                        callbackFailed();
                    }
                }
            });
        }
    }
}
