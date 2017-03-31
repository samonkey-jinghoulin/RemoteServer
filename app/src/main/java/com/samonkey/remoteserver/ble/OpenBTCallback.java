package com.samonkey.remoteserver.ble;

/**
 * Created on 2017/3/20
 *
 * @author saker
 */

public interface OpenBTCallback {
    /**
     * 蓝牙开启成功
     */
    void onSuccess();

    /**
     * 蓝牙开启失败
     */
    void onFailure();
}
