package com.samonkey.remoteserver.socket;

import android.content.Context;
import android.text.TextUtils;

import com.samonkey.remoteserver.utils.Config;
import com.samonkey.remoteserver.utils.LogUtils;
import com.samonkey.remoteserver.utils.OtherUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created on 2017/2/27
 *
 * @author SakerJ
 */

public class UDPBroadcast {

    private static final String TAG = "UDPBroadcast";
    private String[] mFlags = new String[]{Config.SERVER_IP, Config.SERVER_MODE, Config.SERVER_STOP};
    private static UDPBroadcast sUDPBroadcast;
    private static final String BROADCAST_IP = "255.255.255.255";// 广播IP
    private DatagramSocket mSocket;
    private boolean flag;
    private InetAddress mBroadcastAddress;
    private String mIp;//本机IP
    private UDPCallback mUDPCallback;
    private Context mContext;
    public static boolean clientConnect;// 客户端是否在线
    private ExecutorService mExecutorService;

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
        mExecutorService = Executors.newCachedThreadPool();
        // 如果有线ip为空，则获取无线ip
        mIp = OtherUtils.getLocalIp();
        if (TextUtils.isEmpty(mIp)) {
            mIp = OtherUtils.getWifiIp(context);
        }

        try {
            mSocket = new DatagramSocket(Config.PORT);
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
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] bytes = message.getBytes("UTF-8");
                        DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
                                mBroadcastAddress, Config.PORT);
                        mSocket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
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
                    LogUtils.d("receive->" + s);
                    String flag = s.substring(0, 2);
                    // 过滤Server自己发送的消息
                    if (Arrays.asList(mFlags).contains(flag)) {
                        continue;
                    }
                    // 收到客户端的广播后，再发送一次初始化信息
                    if (Config.CLIENT_IP.equals(flag)) {
                        clientConnect = true;
                        sendInitMessage();
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
        sendBroadcast(Config.SERVER_STOP);
        if (mSocket != null && mSocket.isConnected()) {
            mSocket.close();
        }
    }

    /**
     * 发送初始化信息
     */
    private void sendInitMessage() {
//        sendBroadcast(Config.SERVER_IP + mIp + "," + OtherUtils.getVolumePercent(mContext));
        sendBroadcast(Config.SERVER_IP + mIp);
    }
}
