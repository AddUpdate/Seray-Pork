package com.seray.pork;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.stock.InBulkQuantityActivity;
import com.seray.utils.LogUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by pc on 2017/11/3.
 */

public class BaseActivity extends FragmentActivity implements View.OnClickListener {

    Toast mToast;
    String msg;
    /**
     * 执行http请求
     */
    public static ExecutorService httpQueryThread = Executors.newFixedThreadPool(5);
    /**
     * 执行数据库查询固定数量线程池
     */
    public static ExecutorService sqlQueryThread = Executors.newFixedThreadPool(5);

    public static ExecutorService sqlInsertThread = Executors.newSingleThreadExecutor();

    /**
     * 执行定时任务固定数量线程池
     */
    public static ScheduledExecutorService timerThreads = Executors.newScheduledThreadPool(1);
    /**
     * 蜂鸣器控制器
     */
    public Misc mMisc;
    private Handler mHandler = new Handler();
    Runnable showRun = new Runnable() {
        @Override
        public void run() {
            if (mToast == null) {
                mToast = Toast.makeText(App.getApplication(), msg, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(msg);
            }
            mToast.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMisc = Misc.newInstance();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F3) {
            mMisc.beep();
            shutDown();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            mMisc.beep();
            finish();
        }
        return true;
    }

    /**
     * 显示吐司
     */
    public void showMessage(int msg) {
        try {
            this.showMessage(getResources().getString(msg));
        } catch (Resources.NotFoundException e) {
            LogUtil.w("please check your msg id,make sure it's used string resource id");
        }
    }

    /**
     * 显示吐司
     */
    public void showMessage(final String msg) {
        this.msg = msg;
        mHandler.post(showRun);
    }


    /**
     * 关机操作
     */
    public void shutDown() {
        Intent shutDownIntent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        shutDownIntent.putExtra("android.intent.extra.KEY_CONFIRM", true);
        shutDownIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(shutDownIntent);
    }

    /**
     * 默认模式启动Activity（无对象传递）
     */
    public void startActivity(Class<?> targetActivity) {
        startActivity(getSkipIntent(targetActivity));
    }

    protected Intent getSkipIntent(Class<?> targetActivity) {
        return new Intent(getApplicationContext(), targetActivity);
    }

    @Override
    public void onClick(View v) {
        mMisc.beep();
    }
}
