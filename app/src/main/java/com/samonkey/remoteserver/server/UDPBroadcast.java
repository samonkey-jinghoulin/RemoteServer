package com.samonkey.remoteserver.server;

import android.content.Context;
import android.text.TextUtils;

import com.samonkey.remoteserver.utils.OtherUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import static com.samonkey.remoteserver.server.Flags.ALL;
import static com.samonkey.remoteserver.server.Flags.CLIENT_FLAG;
import static com.samonkey.remoteserver.server.Flags.CURRENT_PROGRESS;
import static com.samonkey.remoteserver.server.Flags.CURRENT_VOLUME;
import static com.samonkey.remoteserver.server.Flags.POINT;
import static com.samonkey.remoteserver.server.Flags.SERVER_FLAG;
import static com.samonkey.remoteserver.server.Flags.SERVER_STOP;
import static com.samonkey.remoteserver.server.Flags.TOTAL_PROGRESS;
import static com.samonkey.remoteserver.server.Flags.TOUCH_GAME;
import static com.samonkey.remoteserver.server.Flags.TOUCH_VIDEO;

/**
 * Created on 2017/2/27
 *
 * @author SakerJ
 */

public class UDPBroadcast {

    private static final String TAG = "UDPBroadcast";
    private Integer[] mFlags = new Integer[]{CURRENT_PROGRESS, TOTAL_PROGRESS,
            CURRENT_VOLUME, SERVER_FLAG, POINT, TOUCH_GAME,
            TOUCH_VIDEO, ALL};
    private static UDPBroadcast sUDPBroadcast;
    private static final String BROADCAST_IP = "255.255.255.255";// 广播IP
    private static final int PORT = 22225;// 不同的port对应不同的socket发送端和接收端
    private DatagramSocket mSocket;
    private boolean flag;
    private InetAddress mBroadcastAddress;
    private String mIp;//本机IP
    private UDPCallback mUDPCallback;
    private Context mContext;
    public static boolean clientConnect;// 客户端是否在线

    public static UDPBroadcast getInstance(Context context, UDPCallback callback) {
        if (sUDPBroadcast == null) {
            sUDPBroadcast = new UDPBroadcast(context, callback);
        }
        return sUDPBroadcast;
    }

    public static UDPBroadcast getInstance() {
        return sUDPBroadcast;
    }

    private UDPBroadcast(Context context, UDPCallback udpCallback) {
        mContext = context;
        mUDPCallback = udpCallback;
        // 如果有线ip为空，则获取无线ip
        mIp = OtherUtils.getLocalIp();
        if (TextUtils.isEmpty(mIp)) {
            mIp = OtherUtils.getWifiIp(context);
        }

        try {
            mSocket = new DatagramSocket(PORT);
            mBroadcastAddress = InetAddress.getByName(BROADCAST_IP);
            flag = true;
            new Thread(new ReadThread()).start();
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        }
        sendInitMessage();// 启动时先发送一次初始化信息
    }

    public void sendBroadcast(final String message) {
        if (flag) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        byte[] bytes = message.getBytes("UTF-8");
                        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, mBroadcastAddress, PORT);
                        mSocket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public class ReadThread implements Runnable {

        @Override
        public void run() {
            try {
                byte[] b = new byte[1024];
                DatagramPacket packet = new DatagramPacket(b, b.length);
                while (flag) {
                    mSocket.receive(packet);
                    String s = new String(b, 0, packet.getLength(), "UTF-8");
                    String[] split = s.split(",");
                    // 过滤Server自己发送的消息
                    if (Arrays.asList(mFlags).contains(Integer.valueOf(split[0]))) {
                        continue;
                    }
                    // 收到其它设备的广播后，再发送一次初始化信息
                    if ((String.valueOf(CLIENT_FLAG).equals(split[0]) && !split[1].equals(mIp))) {
                        clientConnect = true;
                        sendInitMessage();
//                        MyApplication.sCurActivity.remoteMode();// 当客户端上线，发送当前遥控模式
                        sendBroadcast("3013");
                    } else {
                        if (mUDPCallback != null) {
                            mUDPCallback.accept(s);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeServer() {
        sendBroadcast(String.valueOf(SERVER_STOP));
        if (mSocket != null && mSocket.isConnected()) {
            mSocket.close();
        }
    }

    /**
     * 发送初始化信息
     */
    private void sendInitMessage() {
        sendBroadcast(SERVER_FLAG + "," + mIp + "," + OtherUtils.getVolumePercent(mContext));
    }
}
