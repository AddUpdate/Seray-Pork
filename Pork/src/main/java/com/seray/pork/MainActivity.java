package com.seray.pork;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.seray.adapter.MainAdapter;
import com.seray.service.BatteryMsg;
import com.seray.service.BatteryService;
import com.seray.utils.LocalServer;
import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;
import com.tscale.scalelib.jniscale.JNIScale;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity {

    private JNIScale mScale;
    private float currWeight = 0.0f;
    private float lastWeight = 0.0f;
    private float divisionValue = 0.02f;
    private boolean flag = true;

    private MainHandler mMainHandler = new MainHandler(new WeakReference<>(this));
    private TextView tvWeight, tvTareWeight;
    private List<String> listPlu = new ArrayList<>();
    ;
    private GridView gridView;
    private TextView mMaxUnitView, mTimeView;
    private ImageView mBatteryIv;
    MainAdapter adapter;
    private AlertDialog rebootDialog;
    private LocalServer mLocalServer = null;

    private void lightScreenCyclicity() {
        float w = mScale.getFloatNet();
        if (isOL() || Math.abs(w - lastWeight) > divisionValue) {
            App.getApplication().openScreen();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initJNI();
        initData();
        initListener();
        register();
        timer();
    }

    private void initView() {
        tvWeight = (TextView) findViewById(R.id.tv_main_weight);
        tvTareWeight = (TextView) findViewById(R.id.tv_main_tare_weight);
        gridView = (GridView) findViewById(R.id.gv_main_plu);
        mMaxUnitView = (TextView) findViewById(R.id.maxUnit);
        mBatteryIv = (ImageView) findViewById(R.id.battery);
        mTimeView = (TextView) findViewById(R.id.timer);
        mTimeView.setText(NumFormatUtil.getFormatDate());
        TextView versionView = (TextView) findViewById(R.id.version);
        versionView.setText("版本V" + App.VersionName);
    }

    private void initData() {
        try {
            if (mLocalServer == null) {
                mLocalServer = new LocalServer();
                mLocalServer.start();
            }
        } catch (IOException e) {
            LogUtil.e(e.getMessage());
        }
        Intent intent = new Intent(this, BatteryService.class);
        startService(intent);
        listPlu.add("1");
        listPlu.add("2");
        listPlu.add("3");
        listPlu.add("菜单");
        listPlu.add("4");
        listPlu.add("5");
        listPlu.add("6");
        listPlu.add("设置");
        listPlu.add("7");
        listPlu.add("8");
        listPlu.add("9");
        listPlu.add("重启");
        listPlu.add("0");
        listPlu.add(".");
        listPlu.add("CE");
        listPlu.add("其他");
        adapter = new MainAdapter(this, listPlu);
        gridView.setAdapter(adapter);
    }

    private Boolean isOL() {
        return mScale.getStringNet().contains("OL");
    }

    private void weightChangedCyclicity() {
        String strNet = mScale.getStringNet().trim();
        float fW = NumFormatUtil.isNumeric(strNet) ? Float.parseFloat(strNet) : 0;
        if (isOL()) {
            tvWeight.setText(strNet);
        } else {
            tvWeight.setText(NumFormatUtil.df2.format(fW));
        }
        if (isStable()) {
            //   backDisplay.showIsStable(true);
            findViewById(R.id.stableflag).setVisibility(View.VISIBLE);
        } else {
            //  backDisplay.showIsStable(false);
            findViewById(R.id.stableflag).setVisibility(View.INVISIBLE);
        }
        if (mScale.getZeroFlag()) {
            // backDisplay.showIsZero(true);
            findViewById(R.id.zeroflag).setVisibility(View.VISIBLE);
        } else {
            //    backDisplay.showIsZero(false);
            findViewById(R.id.zeroflag).setVisibility(View.INVISIBLE);
        }
    }

    private void initJNI() {
        mScale = JNIScale.getScale();
        divisionValue = mScale.getDivisionValue();
    }

    /**
     * 判断秤稳定
     */
    private boolean isStable() {
        return mScale.getStabFlag();
    }

    //定时器
    private void timer() {
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                while (flag) {
//                    mMainHandler.sendEmptyMessage(1);
//                    mMainHandler.sendEmptyMessage(5);
//                    currWeight = mScale.getFloatNet();
//                    if (isStable()) {
//                        lastWeight = currWeight;
//                    }
//                    try {
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
        Runnable timerRun = new Runnable() {
            @Override
            public void run() {
                if (!flag)
                    return;
                mMainHandler.sendEmptyMessage(1);
                mMainHandler.sendEmptyMessage(5);
                currWeight = mScale.getFloatNet();
                if (isStable()) {
                    lastWeight = currWeight;
                }

            }
        };
        timerThreads.scheduleAtFixedRate(timerRun, 1500, 50, TimeUnit.MILLISECONDS);
    }

    private void initListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                if (position != 3 && position != 7 && position != 11 && position != 15)
                    return;
                switch (position) {
                    case 3:
                        startActivity(ChooseFunctionActivity.class);
                        break;
                    case 7:
                        startActivity(ManageActivity.class);
                        break;
                    case 11:
                        openRebootDialog();
                        break;
                    case 15:
                        //startActivity(QueryOrderActivity.class);
                        break;
                }
            }
        });
    }

    private static class MainHandler extends Handler {

        WeakReference<MainActivity> mWeakReference;

        MainHandler(WeakReference<MainActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.weightChangedCyclicity();
                        break;
                    case 5:
                        activity.lightScreenCyclicity();
                        break;
                }
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            mMisc.beep();
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //    showNormalDialog();
                return true;
            case KeyEvent.KEYCODE_MENU:// 桌秤
            case KeyEvent.KEYCODE_MOVE_HOME:// 地秤
                startActivity(ChooseFunctionActivity.class);
                return true;
            case KeyEvent.KEYCODE_F1:// 去皮
                lastWeight = 0.0F;
                currWeight = 0.0F;
                if (mScale.tare()) {
                    float curTare = mScale.getFloatTare();
                    tvTareWeight.setText(NumFormatUtil.df2.format(curTare));
                } else {
                    showMessage("去皮失败");
                }
                return true;
            case KeyEvent.KEYCODE_F2:// 置零
                lastWeight = 0.0F;
                currWeight = 0.0F;
                if (mScale.zero()) {
                    tvTareWeight.setText(R.string.base_weight);
                } else {
                    showMessage("置零失败");
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void register() {
        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver, timeFilter);
    }

    /**
     * 时间刷新监听器 每分钟自动监听 注意：API要求必须动态注册
     */
    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                mTimeView.setText(NumFormatUtil.getFormatDate());
            }
        }
    };

    //接收电量消息 每半小时一次
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveBattery(BatteryMsg msg) {

        if (msg != null) {

            int level = msg.getLevel();

            switch (level) {
                case 4:
                    mBatteryIv.setImageResource(R.drawable.four_electric);
                    break;
                case 3:
                    mBatteryIv.setImageResource(R.drawable.three_electric);
                    break;
                case 2:
                    mBatteryIv.setImageResource(R.drawable.two_electric);
                    break;
                case 1:
                    mBatteryIv.setImageResource(R.drawable.one_electric);
                    break;
                case 0:
                    mBatteryIv.setImageResource(R.drawable.need_charge);
                    new AlertDialog.Builder(MainActivity.this).setTitle(R.string.test_clear_title)
                            .setMessage("电子秤电量即将耗完，请及时连接充电器！")
                            .setPositiveButton(R.string.reprint_ok, null).show();
                    break;
            }
        }
    }

    @Override
    protected void onRestart() {
        flag = true;
        super.onRestart();
    }

    @Override
    protected void onResume() {
        mMaxUnitView.setText(String.valueOf(mScale.getMainUnitFull()) + "kg");
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        flag = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerThreads.shutdownNow();
        unregisterReceiver(timeReceiver);
        Intent intent = new Intent(this, BatteryService.class);
        stopService(intent);
        mMainHandler.removeCallbacksAndMessages(null);
        if (mLocalServer != null && mLocalServer.wasStarted())
            mLocalServer.stop();
        mLocalServer = null;
        flag = false;
    }

    private void openRebootDialog() {
        if (rebootDialog == null) {
            rebootDialog = new AlertDialog.Builder(MainActivity.this)
                    .setMessage(R.string.operation_reboot)
                    .setTitle(R.string.test_clear_title)
                    .setPositiveButton(R.string.reprint_ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            App.getApplication().rebootApp();
                        }
                    }).setNegativeButton(R.string.reprint_cancel, new DialogInterface
                            .OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
        }
        rebootDialog.setCanceledOnTouchOutside(false);
        rebootDialog.show();
    }
}
