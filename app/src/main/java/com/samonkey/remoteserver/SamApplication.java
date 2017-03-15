package com.samonkey.remoteserver;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.samonkey.remoteserver.utils.LogUtils;
import com.tencent.smtt.sdk.QbSdk;

/**
 * Created on 2017/3/2
 *
 * @author SakerJ
 */

public class SamApplication extends Application {

    private static Context sContext;
    public static Handler sHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

        // 设置是否打印Log
        LogUtils.isDebug = true;
//        LogUtils.isDebug = false;

//        QbSdk.initX5Environment(this, null);
    }

    public static Context getContext() {
        return sContext;
    }
}
