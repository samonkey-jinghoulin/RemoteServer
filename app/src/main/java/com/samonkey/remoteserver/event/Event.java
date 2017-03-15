package com.samonkey.remoteserver.event;


import com.samonkey.controller.jni.JNI;
import com.samonkey.remoteserver.utils.LogUtils;

import java.util.Arrays;

/**
 * Created on 2017/2/27
 *
 * @author SakerJ
 */

public class Event {

    // 事件id需适配
    public static int EVENT_KEY_ID = 0;
    public static int EVENT_TOUCH_ID = 2;

    // sendevent命令的常见type
    public static final int EV_SYN = 0;// 同步事件
    public static final int EV_KEY = 1;// 按键事件
    public static final int EV_REL = 2;// 相对坐标
    public static final int EV_ABS = 3;// 绝对坐标

    private static int touchCount;// 同时触控的手指数量

    public static void key(int keyCode) {
        keyDown(keyCode);
        keyUp(keyCode);
    }

    public static void touchDown(int touchId, int x, int y) {
        if (touchCount == 0) {
            start();
        }
        down(touchId, x, y);
        ++touchCount;
    }

    public static void touchUp(int touchId) {
        up(touchId);
        --touchCount;
        if (touchCount == 0) {
            stop();
        }
    }

    // ---------------------------------------------------------------------------------

    /**
     * 按键流程：
     * 1.按下keyCode对应的键
     * 2.同步
     * 3.抬起keyCode对应的键
     * 4.同步
     */

    private static void keyDown(int keyCode) {
        sendEvent(EVENT_KEY_ID, EV_KEY, keyCode, 1);
        sendEvent(EVENT_KEY_ID, EV_SYN, 0, 0);
    }

    private static void keyUp(int keyCode) {
        sendEvent(EVENT_KEY_ID, EV_KEY, keyCode, 0);
        sendEvent(EVENT_KEY_ID, EV_SYN, 0, 0);
    }

    /**
     * 多点触控流程：
     * 1.先调用start()
     * 2.再进行down()和up()的多次调用(多点触控)
     * 3.最后一个动作完成后，调用stop()
     */

    private static void start() {
        sendEvent(EVENT_TOUCH_ID, EV_KEY, 330, 1);// BTN_TOUCH-DOWN
    }

    private static void down(int index, int x, int y) {
        sendEvent(EVENT_TOUCH_ID, EV_ABS, 47, index);// ABS_MT_SLOT 上报触点
//        sendEvent(EVENT_TOUCH_ID, 3, 57, 2002);// ABS_MT_TRACKING_ID
        sendEvent(EVENT_TOUCH_ID, EV_ABS, 48, 40);// ABS_MT_TOUCH_MAJOR
        sendEvent(EVENT_TOUCH_ID, EV_ABS, 53, x);// X
        sendEvent(EVENT_TOUCH_ID, EV_ABS, 54, y);// Y
        sendEvent(EVENT_TOUCH_ID, 0, 0, 0);
    }

    private static void up(int index) {
        sendEvent(EVENT_TOUCH_ID, EV_ABS, 47, index);// ABS_MT_SLOT 上报触点
//        sendEvent(EVENT_TOUCH_ID, 3, 57, -1);// ABS_MT_TRACKING_ID
        sendEvent(EVENT_TOUCH_ID, 0, 0, 0);
    }

    private static void stop() {
        sendEvent(EVENT_TOUCH_ID, EV_KEY, 330, 0);// BTN_TOUCH-UP
        sendEvent(EVENT_TOUCH_ID, 0, 0, 0);
    }

    // ---------------------------------------------------------------------------------

    /**
     * 执行sendevent（格式：sendevent devices type code value）
     * type: 0-EV_SYN, 1-EV_KEY, 3-EV_ABS
     *
     * @param eventId
     * @param type
     * @param code
     * @param value
     * @return
     */
    public static int sendEvent(int eventId, int type, int code, int value) {
        String[] strings = new String[5];
        strings[0] = "sendevent";
        strings[1] = "/dev/input/event" + eventId;
        strings[2] = String.valueOf(type);
        strings[3] = String.valueOf(code);
        strings[4] = String.valueOf(value);

        int result = JNI.execute(5, strings);
        LogUtils.d(Arrays.toString(strings) + "---" + result);
        return result;
    }
}
