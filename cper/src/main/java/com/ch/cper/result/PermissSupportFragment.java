package com.ch.cper.result;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import java.util.List;

/**
 * 作者： ch
 * 时间： 2019/3/7 0007-上午 10:55
 * 描述：
 * 来源：
 */

public class PermissSupportFragment extends Fragment {

    private SparseArray<PermissHelper.CallBack> mCallbacks = new SparseArray<>();
    private SparseArray<PermissHelper.PermissCallBack> mPCallbacks = new SparseArray<>();

    public PermissSupportFragment() {
    }

    public static PermissSupportFragment newInstance() {
        return new PermissSupportFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // configuration change 的时候，fragment 实例不会背重新创建
        setRetainInstance(true);
    }


    public void startActivityForResult(Intent intent, PermissHelper.CallBack callback) {
        int requestCode = getRequestCode();
        mCallbacks.put(requestCode, callback);
        startActivityForResult(intent, requestCode);
    }

    public void requestPermissions(String[] permissions, PermissHelper.PermissCallBack callback) {
        int requestCode = getRequestCode();
        mPCallbacks.put(requestCode, callback);
        requestPermissions(permissions, requestCode);
    }

    public void requestPermissions(List<String> permissions, PermissHelper.PermissCallBack callback) {
        requestPermissions(permissions.toArray(new String[permissions.size()]), callback);
    }


    private int getRequestCode() {
        //code 不能大于65535
        return (int) (System.currentTimeMillis() % 65535);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissHelper.CallBack callback = mCallbacks.get(requestCode);
        mCallbacks.remove(requestCode);
        if (callback != null) {
            callback.onActivityResult(resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissHelper.PermissCallBack callback = mPCallbacks.get(requestCode);
        mPCallbacks.remove(requestCode);
        if (callback != null) {
            callback.onPermissResult(requestCode);
        }

    }
}
