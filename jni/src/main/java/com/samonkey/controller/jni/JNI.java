package com.samonkey.controller.jni;

/**
 * Created on 2017/2/27
 *
 * @author SakerJ
 */

public class JNI {

    // 加载so文件
    static {
        System.loadLibrary("JNI");
    }

    /**
     * 执行系统命令
     *
     * @param length
     * @param strings
     * @return
     */
    public static native int execute(int length, String[] strings);
}
