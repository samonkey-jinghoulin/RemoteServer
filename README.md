# RemoteServer
系统遥控服务端

使用环境：
-----

 - Android 4.0.3系统及以上的设备
 - 如需使用蓝牙BLE，必须运行在Android 4.3系统及以上且具有BLE功能的设备上
 - 如需开机自启动，必须将apk包放入Android的/system/app中，并将so文件放入/system/lib或lib64中。
 - root权限，否则无法对/system文件夹进行操作

模拟事件的实现原理
----

本项目采用JNI调用Android系统的inputevent事件。
格式：sendevent devices {type} {code} {value}

其他实现方式的缺点：

 - adb命令-延迟高达1s
 - Instrumentation-只能用于当前程序
 - 调用隐藏系统API IWindowManager-兼容差且实现复杂

