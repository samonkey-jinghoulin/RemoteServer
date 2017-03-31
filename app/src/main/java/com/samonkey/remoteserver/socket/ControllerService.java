package com.samonkey.remoteserver.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.samonkey.remoteserver.event.Event;
import com.samonkey.remoteserver.utils.Config;
import com.samonkey.remoteserver.utils.LogUtils;
import com.samonkey.remoteserver.utils.ScreenUtils;
import com.samonkey.remoteserver.view.FloatManager;

import static com.samonkey.remoteserver.utils.Config.TOUCH_DOWN;
import static com.samonkey.remoteserver.utils.Config.TOUCH_UP;

public class ControllerService extends Service {
    private static final String TAG = "ControllerService";

    private UDPBroadcast mUdpBroadcast;
    private int mScreenWidth;
    private int mScreenHeight;

    public ControllerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("ControllerService started");
        mScreenWidth = ScreenUtils.getScreenWidth(this);
        mScreenHeight = ScreenUtils.getScreenHeight(this);
        // test
//        if (!FloatManager.isAdd()) {
//            FloatManager.getInstance().addPointer();
//        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUdpBroadcast = UDPBroadcast.getInstance(this, new UDPCallback() {
            @Override
            public void accept(String data) {
                LogUtils.d(TAG, "accept: " + data);
                if (data == null) {
                    return;
                }
                String flag;
                flag = data.substring(0, 2);
                String actionData = data.substring(2);
                // 如果是切换模式，不再执行动作
                if (Config.CLIENT_MODE.equals(flag)) {
                    switchMode(actionData);
                    return;
                }
                executeAction(flag, actionData);
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mUdpBroadcast.closeServer();
        FloatManager.getInstance().removePointer();
        super.onDestroy();
    }

    private void executeAction(String flag, String actionData) {
        switch (flag) {
            case Config.BACK:
                Event.key(158);
                break;
            case Config.MENU:
                Event.key(139);
                break;
            case Config.HOME:
                Event.key(102);
                break;
            case Config.CENTER:
                Event.key(28);
                break;
            case Config.LEFT:
                Event.key(105);
                break;
            case Config.UP:
                Event.key(103);
                break;
            case Config.RIGHT:
                Event.key(106);
                break;
            case Config.DOWN:
                Event.key(108);
                break;
            case Config.VOLUME_SUB:
                Event.key(114);
                break;
            case Config.VOLUME_ADD:
                Event.key(115);
                break;
//            case Config.CLIENT_VOLUME:// 按小数设置音量
//                OtherUtils.setVolume(ControllerService.this, Float.valueOf(data.split(",")[1]));
//                break;
            case Config.SCROLL:
            case Config.CLICK:
                pointer(flag, actionData);
                break;
            case TOUCH_DOWN:
            case TOUCH_UP:
                touch(flag, actionData);
                break;
            default:
                break;
        }
    }

    /**
     * 切换模式
     *
     * @param mode
     * @return 数据是否被处理
     */
    private boolean switchMode(String mode) {
        switch (mode) {
            case Config.CLIENT_MODE_MOUSE:
                // 添加鼠标悬浮窗
                if (!FloatManager.isAdd()) {
                    FloatManager.getInstance().addPointer();
                }
                FloatManager.getInstance().setVisible(true);
                break;
            case Config.CLIENT_MODE_TRADITIONAL:
            case Config.CLIENT_MODE_TOUCH:
            case Config.CLIENT_MODE_KEYBOARD:
            case Config.CLIENT_MODE_GYROSCOPE:
            case Config.CLIENT_MODE_HANDLE:
            case Config.CLIENT_MODE_VOICE:
                FloatManager.getInstance().setVisible(false);
                break;
            default:// 如果不是以上模式，return false
                return false;
        }
        UDPBroadcast.getInstance().sendBroadcast(Config.SERVER_MODE);
        return true;
    }

    /**
     * 手势模式的事件处理
     *
     * @param data
     */
    private void touch(String flag, String data) {
        int touchId = Integer.valueOf(data.substring(0, 1));
        String[] split = data.substring(1).split(",");
        switch (flag) {
            case Config.TOUCH_DOWN:
                Event.touchDown(touchId, (int) (Float.valueOf(split[0]) * mScreenWidth),
                        (int) (Float.valueOf(split[1]) * mScreenHeight));
                break;
            case Config.TOUCH_UP:
                Event.touchUp(touchId);
                break;
            default:
                break;
        }
    }

    /**
     * 鼠标模式的事件处理
     *
     * @param data
     */
    private void pointer(String flag, String data) {
        String[] split = data.split(",");
        switch (flag) {
            case Config.SCROLL:
                int offsetX = Integer.valueOf(split[0]);
                int offsetY = Integer.valueOf(split[1]);
                FloatManager.getInstance().updatePointer(offsetX * 2, offsetY * 2);
                break;
            case Config.CLICK:
                int x = FloatManager.getInstance().getCenterX();
                int y = FloatManager.getInstance().getCenterY();
                Event.touchDown(0, x, y);
                Event.touchUp(0);
                break;
            default:
                break;
        }
    }
}
