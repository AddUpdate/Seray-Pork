package com.lzscale.scalelib.misclib;

public class Misc {

    private static Misc mMisc;

    static {
        System.loadLibrary("Misc");
    }

    private Misc() {

    }

    public static Misc newInstance() {
        if (mMisc == null) {
            synchronized (Misc.class) {
                if (mMisc == null) {
                    mMisc = new Misc();
                }
            }
        }
        return mMisc;
    }

    public native void powerOff(); // 控制关机

    public native void startBeep(); // 蜂鸣器开始鸣叫

    public native void stopBeep(); // 蜂鸣器停止鸣叫

    public native void beep(); // 蜂鸣器鸣叫一下，持续时间为100ms

    public native void beepWarn(); // 蜂鸣器鸣叫两声，每次持续时间100ms,间隔50ms

    public native void beepEx(int ms, int spare_ms, int times); // 蜂鸣器鸣叫times次，每次持续时间ms毫秒,
    // 间隔spare_ms毫米。
    // 上面函数，调用不会阻塞主线程，调用者会立即返回。

    public native void openCashBox(); // 钱箱一直处于开启状态，此时去关闭钱箱，是关不掉的

    public native void closeCashBox();// 钱箱可以关闭。

    public native void openCashBoxAutoClose();// 打开钱箱200ms毫秒后，会自动关闭钱箱，此函数不会阻塞主线程。

    public native void tsCalibrate(); // 调用触摸屏校准程序

    public native String system(String cmd);// cmd要执行的命令，返回输出结果

    public native int readBattery();

}
