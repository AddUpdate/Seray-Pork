package com.seray.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.seray.pork.MainActivity;
import com.seray.pork.StartActivity;


/**
 * 开机广播
 */
public class BootCompletedReceiver extends BroadcastReceiver {

//    private LogManager manager = LogManager.getInstance();
static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
//        LogEntity info = new LogEntity();
//        info.setType(1);
//        info.setContent("开机");
//        info.setCreateAt(NumFormatUtil.getLogCreateAt());
//        info.setRemark("");
//        manager.insert(info);
//        if (intent.getAction().equals(ACTION)) {
//            Intent mainActivityIntent = new Intent(context, StartActivity.class);  // 要启动的Activity
//            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(mainActivityIntent);
//        }
    }
}
