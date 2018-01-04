package com.seray.pork;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.seray.utils.LogUtil;
import com.tscale.scalelib.jniscale.JNIScale;

import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity {
    private JNIScale mScale;
    private float divisionValue = 0.02f;
    private float currWeight = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initJNI();
        timer();
    }

    private void initView() {
        findViewById(R.id.tv_main_hello).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OperationActivity.class));
            }
        });
    }
    /**
     * 判断秤稳定
     */
    private boolean isStable() {
        return mScale.getStabFlag();
    }

    private void initJNI() {

                mScale = JNIScale.getScale();
                divisionValue = mScale.getDivisionValue();
    }
    // 定时器
    private void timer() {
        Runnable timerRun = new Runnable() {
            @Override
            public void run() {
                if (isStable()) {
                    currWeight = mScale.getFloatNet();
                    LogUtil.e("scales", currWeight + "");
                }
            }
        };
        timerThreads.scheduleAtFixedRate(timerRun, 1500, 50, TimeUnit.MILLISECONDS);
    }
}
