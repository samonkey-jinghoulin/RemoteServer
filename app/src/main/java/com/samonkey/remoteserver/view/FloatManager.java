package com.samonkey.remoteserver.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.samonkey.remoteserver.R;
import com.samonkey.remoteserver.SamApplication;
import com.samonkey.remoteserver.server.ControllerService;
import com.samonkey.remoteserver.utils.ScreenUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Author SakerJ
 * Date   2016/12/18
 */

public class FloatManager {

    private static FloatManager sManager;
    private WindowManager.LayoutParams mFloatBallParams;
    private ImageView mFloatBall;
    private WindowManager mWindowManager;
    private static boolean sIsAdd;
    private Context mContext;
    private final int mViewSize = 100;

    private FloatManager() {
        init();
    }

    public static FloatManager getInstance() {
        if (sManager == null) {
            sManager = new FloatManager();
        }
        return sManager;
    }

    private void init() {
        mContext = SamApplication.getContext();
        mFloatBall = new ImageView(mContext);
//        mFloatBall.setImageResource(R.drawable.pointer_anim);
//        Drawable drawable = mFloatBall.getDrawable();
//        if (drawable instanceof AnimationDrawable) {
//            AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
//            if (!animationDrawable.isRunning()) {
//                animationDrawable.start();
//            }
//        }
        // test
        mFloatBall.setBackgroundColor(Color.BLUE);
        mFloatBall.setScaleType(ImageView.ScaleType.FIT_XY);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public void addPointer() {
        if (mFloatBallParams == null) {
            mFloatBallParams = new WindowManager.LayoutParams();
            mFloatBallParams.width = mViewSize;
            mFloatBallParams.height = mViewSize;
            mFloatBallParams.gravity = Gravity.TOP | Gravity.LEFT;
            mFloatBallParams.x = 0;
            mFloatBallParams.y = 0;
            mFloatBallParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            mFloatBallParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            mFloatBallParams.format = PixelFormat.RGBA_8888;
        }
        SamApplication.sHandler.post(new Runnable() {
            @Override
            public void run() {
                mWindowManager.addView(mFloatBall, mFloatBallParams);
            }
        });
        sIsAdd = true;
    }

    public void removePointer() {
        mWindowManager.removeViewImmediate(mFloatBall);
        sIsAdd = false;
        SamApplication.sHandler.removeCallbacksAndMessages(null);
    }

    public void updatePointer(int offsetX, int offsetY) {
        int screenWidth = ScreenUtils.getScreenWidth(mContext);
        int screenHeight = ScreenUtils.getScreenHeight(mContext);
        int right = mFloatBallParams.x + mViewSize;
        int bottom = mFloatBallParams.y + mViewSize;
        //X轴越界检查
        if (mFloatBallParams.x + offsetX < 0) {
            offsetX = -mFloatBallParams.x;
        } else if (mFloatBallParams.x + offsetX > screenWidth) {
            offsetX = screenWidth - right;
        }
        //Y轴越界检查
        if (mFloatBallParams.y + offsetY < 0) {
            offsetY = -mFloatBallParams.y;
        } else if (bottom + offsetY > screenHeight) {
            offsetY = screenHeight - bottom;
        }

        mFloatBallParams.x += offsetX;
        mFloatBallParams.y += offsetY;
        SamApplication.sHandler.post(new Runnable() {
            @Override
            public void run() {
                mWindowManager.updateViewLayout(mFloatBall, mFloatBallParams);
            }
        });
    }

    public static boolean isAdd() {
        return sIsAdd;
    }

    public int getCenterX() {
        if (mFloatBallParams == null) {
            return 0;
        }
//        return mFloatBallParams.x + (mViewSize / 2);
        return mFloatBallParams.x;
    }

    public int getCenterY() {
        if (mFloatBallParams == null) {
            return 0;
        }
//        return mFloatBallParams.y + (mViewSize / 2);
        return mFloatBallParams.y;
    }

    /**
     * 设置pointer是否可见
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        if (mFloatBall != null) {
            if (visible) {
                mFloatBall.setVisibility(VISIBLE);
            } else {
                mFloatBall.setVisibility(GONE);
            }
        }
    }
}
