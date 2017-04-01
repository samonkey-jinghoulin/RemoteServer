package com.samonkey.remoteserver.event;

import android.content.Context;
import android.os.Build;

import com.samonkey.remoteserver.SamApplication;
import com.samonkey.remoteserver.ble.BLEService;
import com.samonkey.remoteserver.socket.UDPBroadcast;
import com.samonkey.remoteserver.utils.Config;
import com.samonkey.remoteserver.utils.LogUtils;
import com.samonkey.remoteserver.utils.ScreenUtils;
import com.samonkey.remoteserver.view.FloatManager;

/**
 * Created on 2017/3/31
 *
 * @author saker
 */

public class DataProcessor {
    private static final String TAG = "DataProcessor";

    private Context mContext;
    private int mScreenWidth;
    private int mScreenHeight;
    public static final int SOCKET_SERVER = 0;
    public static final int BLE_SERVER = 1;
    private int mServerType;

    public DataProcessor(int serverType) {
        mServerType = serverType;
        init();
    }

    private void init() {
        mContext = SamApplication.getContext();
        mScreenWidth = ScreenUtils.getScreenWidth(mContext);
        mScreenHeight = ScreenUtils.getScreenHeight(mContext);
    }

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
            case Config.TOUCH_DOWN:
            case Config.TOUCH_UP:
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
        if (mServerType == SOCKET_SERVER) {
            UDPBroadcast.getInstance().sendBroadcast(Config.SERVER_MODE);
        } else if (mServerType == BLE_SERVER) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                BLEService.write(Config.SERVER_MODE);
            }
        }
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
