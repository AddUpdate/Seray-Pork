package com.seray.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lzscale.scalelib.misclib.Misc;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BatteryService extends Service {

    private ScheduledExecutorService batteryThread = null;

    private Misc mMisc;

    private BatteryRunnable mRunnable = null;

    @Override
    public void onCreate() {
        super.onCreate();
        batteryThread = Executors.newScheduledThreadPool(1);
        mRunnable = new BatteryRunnable();
        mMisc = Misc.newInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        batteryThread.scheduleAtFixedRate(mRunnable, 20, 30 * 60 , TimeUnit.SECONDS);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        batteryThread.shutdownNow();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class BatteryRunnable implements Runnable {

        @Override
        public void run() {
            int time = 0;
            int battery = 0;
            while (time < 3) {
                time++;
                if (mMisc != null)
                    battery = mMisc.readBattery();
            }

            int level = 0;

            if (battery >= 1065) {
                level = 4;
            } else if (battery < 1065 && battery >= 1035) {
                level = 3;
            } else if (battery < 1035 && battery >= 1000) {
                level = 2;
            } else if (battery < 1000 && battery >= 965) {
                level = 1;
            } else if (battery < 965) {
                level = 0;
            }
            EventBus.getDefault().post(new BatteryMsg(level, battery, null));
        }
    }
}
