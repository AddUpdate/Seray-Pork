package com.seray.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 关机系统广播
 */
public class ShutdownReceiver extends BroadcastReceiver {

   // private LogManager manager = LogManager.getInstance();

//    private ConfigManager mConManager = ConfigManager.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
//        LogEntity info = new LogEntity();
//        info.setType(2);
//        info.setContent("关机");
//        info.setCreateAt(NumFormatUtil.getLogCreateAt());
//        info.setRemark("");
//        manager.insert(info);
//        mConManager.insert("Date_Id", String.valueOf(CacheHelper.DATE_ID));
    //    SubtotalManager.getInstance().closeConnection();
    }
}
