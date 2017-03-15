package com.samonkey.remoteserver.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.MotionEvent;

import com.samonkey.remoteserver.event.Event;
import com.samonkey.remoteserver.utils.LogUtils;
import com.samonkey.remoteserver.utils.OtherUtils;
import com.samonkey.remoteserver.utils.ScreenUtils;
import com.samonkey.remoteserver.view.FloatManager;

import java.util.ArrayList;
import java.util.List;

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
        mScreenWidth = ScreenUtils.getScreenWidth(this);
        mScreenHeight = ScreenUtils.getScreenHeight(this);
        LogUtils.d("ControllerService started");
        // 指针
//        if (!FloatManager.isAdd()) {
//            FloatManager.getInstance().addPointer();
//        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    private long mLastMillis;
//
//    private boolean ignore() {
//        boolean result = false;
//        long millis = System.currentTimeMillis();
//        if (millis - mLastMillis > 80) {
//            result = true;
//        }
//        mLastMillis = millis;
//        return result;
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUdpBroadcast = UDPBroadcast.getInstance(this, new UDPCallback() {
            @Override
            public void accept(String data) {
                if (data == null) {
                    return;
                }
                int flag;
                try {
                    flag = Integer.valueOf(data.split(",")[0]);
                } catch (Exception e) {
                    return;
                }

                switch (flag) {
                    case Flags.BACK:
                        Event.key(158);
                        break;
                    case Flags.MENU:
                        Event.key(225);
                        break;
                    case Flags.HOME:
                        Event.key(102);
                        break;
                    case Flags.CENTER:
                        Event.key(28);
                        break;
                    case Flags.LEFT:
                        Event.key(105);
                        break;
                    case Flags.UP:
                        Event.key(103);
                        break;
                    case Flags.RIGHT:
                        Event.key(107);
                        break;
                    case Flags.DOWN:
                        Event.key(109);
                        break;
                    case Flags.VOLUME_DOWN:
                        Event.key(114);
                        break;
                    case Flags.VOLUME_UP:
                        Event.key(115);
                        break;
                    case Flags.CLIENT_VOLUME:
                        OtherUtils.setVolume(ControllerService.this, Float.valueOf(data.split(",")[1]));
                        break;
                    case Flags.SCROLL:
                    case Flags.POINTER_CLICK:
                        pointer(data);
                        break;
                    default:
                        break;
                }
//                if (flag.equals(String.valueOf(Flags.BACK))) {// 返回键
//
//                } else if (flag.equals(String.valueOf(Flags.CLIENT_VOLUME))) {// 设置音量
//                }
//                else if (String.valueOf(Flags.CLIENT_PROGRESS).equals(flag)) {// 设置播放进度
//                    if (sPlayerActivity != null) {
//                        sPlayerActivity.mIMediaPlayer.seekTo(Integer.valueOf(data.split(",")[1]) * 1000);
//                    }
//                } else if (String.valueOf(Flags.PLAY_PAUSE).equals(flag)) {// 播放/暂停
//                    if (sPlayerActivity != null) {
//                        sPlayerActivity.playOrPause();
//                    }
//                } else if (String.valueOf(Flags.NEXT).equals(flag)) {// 播放下一个
//                    if (sPlayerActivity != null) {
//                        sPlayerActivity.getNewVideo(VideoPlayerActivity.VIDEO_DOWN);
//                    }
//                } else if (String.valueOf(Flags.PREVIOUS).equals(flag)) {// 播放上一个
//                    if (sPlayerActivity != null) {
//                        sPlayerActivity.getNewVideo(VideoPlayerActivity.VIDEO_UP);
//                    }
//                }
//                else {
//                    switch (MyApplication.sCurActivity.mControllerAction) {
//                        case POINTER:
//                            pointer(data);
//                            break;
//                        case Flags.TOUCH_VIDEO:
//                        case Flags.TOUCH_GAME:
//                            touch(data);
//                            break;
//                        case Flags.ALL:
//                            String[] split = data.split(",");
//                            if (String.valueOf(Flags.ALL_POINTER).equals(split[0])) {
//                                data = data.substring(5);
//                                pointer(data);
//                            } else if (String.valueOf(Flags.ALL_TOUCH).equals(split[0])) {
//                                data = data.substring(5);
//                                touch(data);
//                            }
//                            break;
//                        default:
//                            break;
//                    }

//                }
            }
        });
        // 开启TCP
//        mTcpServer = TCPServer.getInstance(new ServerCallback() {
//
//            @Override
//            public void accept(String data) {
//                if (data == null) {
//                    return;
//                } else if (String.valueOf(BACK).equals(data.split(",")[0])) {//返回键
//                    MyApplication.sCurActivity.finish();
//                    return;
//                }
//                switch (MyApplication.sCurActivity.mControllerAction) {
//                    case POINTER:
//                        pointer(data);
//                        break;
//                    case TOUCH_VIDEO:
//                    case TOUCH_GAME:
//                        touch(data);
//                        break;
//                    case ALL:
//                        String[] split = data.split(",");
//                        if (String.valueOf(ALL_POINTER).equals(split[0])) {
//                            data = data.substring(5);
//                            pointer(data);
//                        } else if (String.valueOf(ALL_TOUCH).equals(split[0])) {
//                            data = data.substring(5);
//                            touch(data);
//                        }
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            @Override
//            public void clientConnected() {
//                mUdpBroadcast.closeServer();
//            }
//        });
//        mTcpServer.startServer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
//        mTcpServer.closeServer();
        mUdpBroadcast.closeServer();
        FloatManager.getInstance().removePointer();
        super.onDestroy();
    }

    /**
     * 生成元素个数为count的，元素值从0递增的整型数组
     *
     * @param count
     * @return
     */
    private int[] intArrayFactory(int count) {
        int[] ints = new int[count];
        for (int i = 0; i < count; i++) {
            ints[i] = i;
        }
        return ints;
    }

    private List<Integer> mFingers = new ArrayList<>();
    private MotionEvent.PointerCoords[] mCoords = new MotionEvent.PointerCoords[2];

    /**
     * 触控模式的事件处理
     *
     * @param data
     */
//    private void touch(String data) {
//        LogUtils.d(TAG, "touch: " + data);
//        final View decorView = MyApplication.sCurActivity.getWindow().getDecorView();
//        final String[] split = data.split(",");
//        if (MyApplication.sCurActivity.mControllerAction == ControllerAction.TOUCH_VIDEO && ignore()) {
//            return;
//        }
//        // 两种处理逻辑：1.TOUCH_GAME；2.TOUCH_VIDEO和ALL模式相同
//        if (MyApplication.sCurActivity.mControllerAction == ControllerAction.TOUCH_GAME) {
//            if (String.valueOf(TOUCH_CLICK).equals(split[0])) {
//                // 多点触控
//                int action = 0x0000;
//                int fingerId = Integer.valueOf(split[1]);
//                int actionArg = Integer.valueOf(split[2]);
//
//                // 不是第一个DOWN和最后一个UP，都要+5成为次要动作(只有第一个down和最后一个up，是主要动作)
//                if (!((actionArg == 0 && mFingers.size() == 0) ||
//                        (actionArg == 1 && mFingers.size() == 1))) {
//                    actionArg += 5;
//                }
//                // 将动作类型合成在action的低8位
//                action = action | actionArg;
//
//                int fingerIndex = mFingers.indexOf(fingerId);
//                if (fingerIndex > -1) {// 如果存在此元素
//                    mFingers.remove(fingerIndex);
//                } else {
//                    // 如果添加元素，最多只有2个
//                    if (mFingers.size() == 2) {
//                        return;
//                    }
//                    mFingers.add(fingerId);
//                    fingerIndex = mFingers.size() - 1;
//                }
//                // 将index合成在action的高8位
//                action = action | (fingerIndex << 8);
//
////                MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(),
////                        SystemClock.uptimeMillis(), action,
////                        Float.valueOf(split[3]) * mScreenWidth, Float.valueOf(split[4]) * mScreenHeight, 0);
//
//                MotionEvent.PointerCoords pointerCoords = new MotionEvent.PointerCoords();
//                pointerCoords.x = Float.valueOf(split[3]) * mScreenWidth;
//                pointerCoords.y = Float.valueOf(split[4]) * mScreenHeight;
//                mCoords[fingerIndex] = pointerCoords;
//                MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), action, fingerIndex + 1,
//                        intArrayFactory(fingerIndex + 1), mCoords, 0, 0, 0, 0, 0, 0, 0);
//                decorView.dispatchTouchEvent(event);
//            }
//        } else if (MyApplication.sCurActivity.mControllerAction == ControllerAction.ALL ||
//                MyApplication.sCurActivity.mControllerAction == ControllerAction.TOUCH_VIDEO) {
//            MyApplication.sHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
//                            MotionEvent.ACTION_DOWN, 0, 0, 0);
//                    decorView.dispatchTouchEvent(event);
//                    event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
//                            MotionEvent.ACTION_MOVE, Integer.valueOf(split[0]) * 3, Integer.valueOf(split[1]) * 3, 0);
//                    decorView.dispatchTouchEvent(event);
//                }
//            });
//        }
//    }

    /**
     * 指针模式的事件处理
     *
     * @param data
     */
    private void pointer(String data) {
        LogUtils.d(TAG, "pointer: ");
        String[] split = data.split(",");
        switch (Integer.valueOf(split[0])) {
            case Flags.SCROLL:
                int offsetX = Integer.valueOf(split[1]);
                int offsetY = Integer.valueOf(split[2]);

                // 指针
                if (!FloatManager.isAdd()) {
                    FloatManager.getInstance().addPointer();
                }
                FloatManager.getInstance().updatePointer(offsetX * 2, offsetY * 2);
                break;
            case Flags.POINTER_CLICK:
//                final View decorView = MyApplication.sCurActivity.getWindow().getDecorView();
                final int x = FloatManager.getInstance().getCenterX();
                final int y = FloatManager.getInstance().getCenterY();
//                MyApplication.sHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        MotionEvent obtain = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
//                                MotionEvent.ACTION_DOWN, x, y, 0);
//                        decorView.dispatchTouchEvent(obtain);
//                        obtain = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
//                                MotionEvent.ACTION_UP, x, y, 0);
//                        decorView.dispatchTouchEvent(obtain);
//                    }
//                });
//                Event.touchDown(0, x, y);
//                Event.touchUp(0);

                // 按键模拟成功，但触摸模拟失败
                Event.touchDown(0, x, y);
                Event.touchUp(0);
                break;
            default:
                break;
        }
    }

//    public static void register(VideoPlayerActivity activity) {
//        sPlayerActivity = activity;
//    }
//
//    public static void unregister(VideoPlayerActivity activity) {
//        if (activity == sPlayerActivity) {
//            sPlayerActivity = null;
//        }
//    }
}
