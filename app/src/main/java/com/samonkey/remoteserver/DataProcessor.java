package com.samonkey.remoteserver;

import android.content.Context;

import com.samonkey.remoteserver.utils.ScreenUtils;

/**
 * Created on 2017/3/31
 *
 * @author saker
 */

public class DataProcessor {

    private Context mContext;
    private int mScreenWidth;
    private int mScreenHeight;

    public DataProcessor() {

    }

    private void init() {
        mContext = SamApplication.getContext();
        mScreenWidth = ScreenUtils.getScreenWidth(mContext);
        mScreenHeight = ScreenUtils.getScreenHeight(mContext);
    }

}
