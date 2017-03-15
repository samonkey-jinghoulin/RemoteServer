package com.samonkey.remoteserver.server;

/**
 * Created on 2017/2/27
 *
 * @author SakerJ
 */

public interface Flags {
    int SERVER_FLAG = 4000;// 服务器广播的标识，广播规则：标识,IP
    int CLIENT_FLAG = 4001;// 客户端广播的标识
    int SERVER_STOP = 3100;// 服务器关闭标识
    // 客户端发送的播放器控制
    int PLAY_PAUSE = 3060;
    int PREVIOUS = 3061;
    int NEXT = 3062;
    int LOCAL_VIDEO = 3069;
    // 发送的进度和音量信息
    int TOTAL_PROGRESS = 3040;
    int CURRENT_PROGRESS = 3041;
    int CLIENT_PROGRESS = 3042;// 客户端请求的进度
    int CURRENT_VOLUME = 3051;
    int CLIENT_VOLUME = 3052;// 客户端请求的音量
    // 客户端控制指令
    int POINTER_CLICK = 3001;// 指针单击
    int SCROLL = 3002;// 滑动
    int BACK = 3003;// 返回
    int MENU = 3004;// 菜单键
    int HOME = 3005;// HOME键
    int LEFT = 3006;// 左
    int UP = 3007;// 上
    int RIGHT = 3008;// 右
    int DOWN = 3009;// 下
    int CENTER = 3000;// 确认键
    int VOLUME_DOWN = 3021;// 音量-
    int VOLUME_UP = 3022;// 音量+
    int TOUCH_CLICK = 3020;// 游戏点击
    // 向客户端发送的遥控模式
    int POINT = 3011;
    int TOUCH_VIDEO = 3012;
    int TOUCH_GAME = 3013;
    int ALL = 3014;
    // 双模式下，从客户端接收的事件标识
    int ALL_POINTER = 3030;// 双模式下的指针事件
    int ALL_TOUCH = 3031;// 双模式下的触控事件
}
