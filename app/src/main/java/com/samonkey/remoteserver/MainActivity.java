package com.samonkey.remoteserver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.samonkey.remoteserver.ble.BLEService;
import com.samonkey.remoteserver.event.Event;
import com.samonkey.remoteserver.socket.ControllerService;
import com.samonkey.remoteserver.utils.LogUtils;
import com.tencent.smtt.sdk.WebView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String GAME_URL = "http://bak.samonkey.com/xsaiche/index.html";
    //    private static final String GAME_URL = "http://192.168.1.107/buyu/";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        webView = (WebView) findViewById(R.id.wv_main_show);
//        WebSettings settings = webView.getSettings();
//        settings.setJavaScriptEnabled(true);
//        // 设置可以访问文件
//        settings.setAllowFileAccess(true);
//        // 设置可以使用localStorage
//        settings.setDomStorageEnabled(true);
//
//        // 应用可以有缓存
//        settings.setAppCacheEnabled(true);
//        String appCaceDir = getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();//缓存路径
//        settings.setAppCachePath(appCaceDir);

//        webView.loadUrl(GAME_URL);


        //  test
        startService(new Intent(getApplicationContext(), ControllerService.class));
        startService(new Intent(getApplicationContext(), BLEService.class));
//        OtherUtils.upgradeRootPermission(getPackageName());
    }

    public static void sendCmd() {
        Event.touchDown(0, 100, 120);
        Event.touchUp(0);
    }

    public void onClick(View view) {
//        Event.key(158);
        Toast.makeText(this, "onClick", Toast.LENGTH_SHORT).show();
        LogUtils.e("onClick");
        startService(new Intent(getApplicationContext(), BLEService.class));

    }

    public static Handler sHandler = new Handler();
}
