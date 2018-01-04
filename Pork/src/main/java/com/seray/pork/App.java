package com.seray.pork;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;

import com.seray.cache.CacheHelper;
import com.seray.utils.LogUtil;

import java.util.LinkedList;
import java.util.List;

public class App extends Application {

    /**
     * 缓存已开启的窗口
     */
    public static List<Activity> activityList = new LinkedList<>();
    public static App myApplication = null;

    /**
     * 应用程序的VersionName
     */
    public static String VersionName = "";

    private PowerManager.WakeLock mWakeLock;

    public static App getApplication() {
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        VersionName = getVersionName();
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.SCREEN_DIM_WAKE_LOCK, "WakeAndLock");
    }

    /**
     * 当前应用程序的VersionName
     *
     * @return versionName字符串
     */
    protected String getVersionName() {
        return getPackageInfo().versionName;
    }

    protected PackageInfo getPackageInfo() {
        PackageInfo pi;
        try {
            PackageManager pm = myApplication.getPackageManager();
            pi = pm.getPackageInfo(myApplication.getPackageName(), PackageManager
                    .GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
        return null;
    }

    /**
     * 设置系统的待机时间
     */
    protected boolean setScreenOffTime(int paramInt) {
        int timeOff = getScreenOffTime();
        if (timeOff == paramInt) {
            LogUtil.d("the screenOffTime you set same as original screenOffTime");
            return false;
        }
        try {
            return Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT,
                    paramInt);
        } catch (Exception localException) {
            LogUtil.e(localException.getMessage());
            return false;
        }
    }

    /**
     * 获取系统当前休眠时间
     */
    protected int getScreenOffTime() {
        int screenOffTime = 0;
        try {
            screenOffTime = Settings.System.getInt(getContentResolver(), Settings.System
                    .SCREEN_OFF_TIMEOUT);
            LogUtil.d("current screenOffTime is " + screenOffTime);
        } catch (Exception localException) {
            LogUtil.e(localException.getMessage());
        }
        return screenOffTime;
    }

    /**
     * 点亮屏幕
     */
    public void openScreen() {
        mWakeLock.acquire();
        mWakeLock.release();
    }

    /**
     * 添加已开启的窗口至缓存集合
     */
    public void addActivity(Activity activity) {
        if (activityList.contains(activity)) {
            return;
        }
        activityList.add(activity);
    }

    /**
     * 移除已关闭的窗口
     */
    public void removeActivity(Activity activity) {
        if (!activityList.isEmpty() && activityList.contains(activity)) {
            activityList.remove(activity);
        }
    }

    /**
     * 退出当前应用
     */
    public void exit() {
     //   CacheHelper.saveDataIdToDb();// TODO: 2017/11/28
        try {
            for (Activity activity : activityList) {
                activity.finish();
            }
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        } finally {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }, 500);

        }
    }

    /**
     * 重启当前应用
     */
    public void rebootApp() {
        //   CacheHelper.saveDataIdToDb();// TODO: 2017/11/28
        try {
            for (Activity activity : activityList) {
                activity.finish();
            }
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        } finally {
            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(LaunchIntent);
        }
    }
}
