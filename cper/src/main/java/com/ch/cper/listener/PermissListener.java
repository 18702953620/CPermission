package com.ch.cper.listener;

import java.util.List;

/**
 * 作者： ch
 * 时间： 2019/5/7 0007-下午 5:02
 * 描述：
 * 来源：
 */

public interface PermissListener<T> {

    void onGranted(List<T> granted);

    void onDenied(List<T> granted);

}
