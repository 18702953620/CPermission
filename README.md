# CPermission


### 使用方式
* 1.添加引用


* 2.请求权限
```java
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
```
* 3.安装应用（需要读写权限）
```java
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
```
* 4.打开权限设置
```java
CPermission.with(this).setting().start();
```