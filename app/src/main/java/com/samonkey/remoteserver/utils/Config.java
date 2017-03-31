package com.samonkey.remoteserver.utils;

/**
 * Created on 2017/2/27
 *
 * @author SakerJ
 */

public interface Config {

    int PORT = 22225;

    // 连接标识
    String SERVER_IP = "00";
    String CLIENT_IP = "01";
    String SERVER_STOP = "04";

    String SERVER_MODE = "02";// 服务端接收反馈
    String CLIENT_MODE = "03";

    // 遥控模式
    String CLIENT_MODE_TRADITIONAL = "0A";// 传统
    String CLIENT_MODE_MOUSE = "0B";// 鼠标
    String CLIENT_MODE_TOUCH = "0C";// 手势
    String CLIENT_MODE_HANDLE = "0D";// 手柄
    String CLIENT_MODE_GYROSCOPE = "0E";// 陀螺仪
    String CLIENT_MODE_KEYBOARD = "0F";// 键盘
    String CLIENT_MODE_VOICE = "0G";// 语音

    // 传统指令
    String BACK = "10";
    String HOME = "11";
    String MENU = "12";
    String LEFT = "13";
    String UP = "14";
    String RIGHT = "15";
    String DOWN = "16";
    String CENTER = "17";
    String VOLUME_ADD = "18";
    String VOLUME_SUB = "19";

    // 鼠标指令
    String SCROLL = "20";
    String CLICK = "21";

    // 触控指令
    String TOUCH_DOWN = "2A";
    String TOUCH_UP = "2B";


//    int SERVER_FLAG = 4000;// 服务器广播的标识，广播规则：标识,IP
//    int CLIENT_FLAG = 4001;// 客户端广播的标识
//    int SERVER_STOP = 3100;// 服务器关闭标识
//    // 客户端发送的播放器控制
//    int PLAY_PAUSE = 3060;
//    int PREVIOUS = 3061;
//    int NEXT = 3062;
//    int LOCAL_VIDEO = 3069;
//    // 发送的进度和音量信息
//    int TOTAL_PROGRESS = 3040;
//    int CURRENT_PROGRESS = 3041;
//    int CLIENT_PROGRESS = 3042;// 客户端请求的进度
//    int CURRENT_VOLUME = 3051;
//    int CLIENT_VOLUME = 3052;// 客户端请求的音量
//    // 客户端控制指令
//    int POINTER_CLICK = 3001;// 指针单击
//    int SCROLL = 3002;// 滑动
//    int BACK = 3003;// 返回
//    int MENU = 3004;// 菜单键
//    int HOME = 3005;// HOME键
//    int LEFT = 3006;// 左
//    int UP = 3007;// 上
//    int RIGHT = 3008;// 右
//    int DOWN = 3009;// 下
//    int CENTER = 3000;// 确认键
//    int VOLUME_DOWN = 3021;// 音量-
//    int VOLUME_UP = 3022;// 音量+
//    int TOUCH_CLICK = 3020;// 游戏点击
//    // 向客户端发送的遥控模式
//    int POINT = 3011;
//    int TOUCH_VIDEO = 3012;
//    int TOUCH_GAME = 3013;
//    int ALL = 3014;
//    // 双模式下，从客户端接收的事件标识
//    int ALL_POINTER = 3030;// 双模式下的指针事件
//    int ALL_TOUCH = 3031;// 双模式下的触控事件
//
//    // client发来的遥控模式
//    int CLIENT_MODE_TRADITIONAL = 1;// 传统
//    int CLIENT_MODE_MOUSE = 2;// 鼠标
//    int CLIENT_MODE_GESTURE = 3;// 手势
//    int CLIENT_MODE_HANDLE = 4;// 手柄
//    int CLIENT_MODE_GYROSCOPE = 5;// 陀螺仪
//    int CLIENT_MODE_KEYBOARD = 6;// 键盘
//    int CLIENT_MODE_VOICE = 7;// 语音
}
