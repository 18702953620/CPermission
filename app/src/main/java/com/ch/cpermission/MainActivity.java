package com.ch.cpermission;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ch.cper.CPermission;
import com.ch.cper.PermissGroup;
import com.ch.cper.listener.PermissListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements PermissListener<String> {

    @BindView(R.id.btn_read)
    Button btnRead;
    @BindView(R.id.btn_phone)
    Button btnPhone;
    @BindView(R.id.btn_instil)
    Button btnInstil;
    @BindView(R.id.btn_setting)
    Button btnSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


    }

    /**
     * @param s
     */
    public void showtoast(@NonNull String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGranted(List<String> granted) {
        showtoast("权限申请成功");
    }

    @Override
    public void onDenied(List<String> granted) {
        showtoast("权限被拒绝");
    }

    @OnClick({R.id.btn_read, R.id.btn_phone, R.id.btn_instil, R.id.btn_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_read:
                CPermission.with(this)
                        .permiss()
                        .permission(PermissGroup.STORAGE)
                        .listener(this).start();
                break;
            case R.id.btn_phone:
                CPermission.with(this)
                        .permiss()
                        .permission(PermissGroup.CALL_PHONE)
                        .listener(new PermissListener<String>() {
                            @Override
                            public void onGranted(List<String> granted) {
                                showtoast("权限申请成功");
                            }

                            @Override
                            public void onDenied(List<String> granted) {
                                showtoast("权限被拒绝");
                            }
                        }).start();
                break;
            case R.id.btn_instil:
                CPermission.with(this).permiss()
                        .permission(PermissGroup.STORAGE)
                        .listener(new PermissListener<String>() {
                            @Override
                            public void onGranted(List<String> granted) {

                                String file = copyFile();

                                CPermission
                                        .with(MainActivity.this)
                                        .install()
                                        .file(new File(file))
                                        .listener(new PermissListener<String>() {
                                            @Override
                                            public void onGranted(List<String> granted) {
                                                showtoast("安装成功");
                                            }

                                            @Override
                                            public void onDenied(List<String> granted) {
                                                showtoast("安装失败");
                                            }
                                        })
                                        .start();

                            }

                            @Override
                            public void onDenied(List<String> granted) {

                            }
                        }).start();
                break;
            case R.id.btn_setting:

                CPermission.with(this).setting().start();
                break;
        }
    }

    private String copyFile() {
        String dir = "";

        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = getAssets().open("app-debug.apk");

            String state = Environment.getExternalStorageState();
            if (state.equals(Environment.MEDIA_MOUNTED)) {// 检查是否有存储卡

                dir = Environment.getExternalStorageDirectory() + "/ceshi/";
                File dirFile = new File(dir);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }
                File apk = new File(dir + "app-debug.apk");
                if (!apk.exists()) {
                    apk.createNewFile();
                }

                outputStream = new FileOutputStream(apk);

                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = inputStream.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    outputStream.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                outputStream.flush();// 刷新缓冲区
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dir + "app-debug.apk";

    }


    private void aVoid() {
        CPermission.with(this)
                .permiss()
                .permission(PermissGroup.CALL_PHONE)
                .listener(new PermissListener<String>() {
                    @Override
                    public void onGranted(List<String> granted) {
                        showtoast("权限申请成功");
                    }

                    @Override
                    public void onDenied(List<String> granted) {
                        showtoast("权限被拒绝");
                    }
                }).start();
    }
}
