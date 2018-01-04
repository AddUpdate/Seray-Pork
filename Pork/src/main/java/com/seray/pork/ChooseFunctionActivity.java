package com.seray.pork;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.seray.stock.PurchaseActivity;
import com.seray.utils.HttpUtils;
import com.seray.utils.LocalServer;
import com.seray.utils.LogUtil;
import com.seray.view.PromptDialog;

import java.io.IOException;

/**
 * Created by pc on 2017/11/17.
 */

public class ChooseFunctionActivity extends BaseActivity {

    Button inputBt, porkBt, separateBt, sortBt, frozenBt, finishBt, excessBt, orderBt;

    private LocalServer mLocalServer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_function);
        initView();
        initData();
    }

    private void initView() {
        inputBt = (Button) findViewById(R.id.bt_choose_input);
        porkBt = (Button) findViewById(R.id.bt_choose_pork);
        separateBt = (Button) findViewById(R.id.bt_choose_separate);
        sortBt = (Button) findViewById(R.id.bt_choose_sort);
        frozenBt = (Button) findViewById(R.id.bt_frozen_library);
        finishBt = (Button) findViewById(R.id.bt_finish_product_library);
        excessBt = (Button) findViewById(R.id.bt_excess_stock_library);
        orderBt = (Button) findViewById(R.id.bt_order);
        TextView mIpTextView = (TextView) findViewById(R.id.tv_ip);
        String ipAddress = HttpUtils.getLocalIpStr(getApplication());
        mIpTextView.setText(ipAddress);

        inputBt.setOnClickListener(this);
        porkBt.setOnClickListener(this);
        separateBt.setOnClickListener(this);
        sortBt.setOnClickListener(this);
        frozenBt.setOnClickListener(this);
        finishBt.setOnClickListener(this);
        excessBt.setOnClickListener(this);
        orderBt.setOnClickListener(this);
    }

    private void initData() {
        try {
            mLocalServer = new LocalServer();
            mLocalServer.start();
        } catch (IOException e) {
            LogUtil.e(e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.bt_choose_input:
                startActivity(PurchaseActivity.class);
                break;
            case R.id.bt_choose_pork:
                startActivity(TemporaryLibraryActivity.class);
                break;
            case R.id.bt_choose_separate:
                startActivity(SeparateActivity.class);
                break;
            case R.id.bt_choose_sort:
                startActivity(SortActivity.class);
                break;
            case R.id.bt_frozen_library:
                startActivity(FrozenLibraryActivity.class);
                break;
            case R.id.bt_finish_product_library:
                startActivity(FinishProductActivity.class);
                break;
            case R.id.bt_excess_stock_library:
                startActivity(ExcessStockActivity.class);
                break;
            case R.id.bt_order:
                startActivity(OrderActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocalServer != null && mLocalServer.wasStarted())
            mLocalServer.stop();
        mLocalServer = null;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //    showNormalDialog();
                return true;
            case KeyEvent.KEYCODE_MENU:// 桌秤
                mMisc.beep();
                startActivity(ManageActivity.class);
                return true;
            case KeyEvent.KEYCODE_MOVE_HOME:// 地秤
                mMisc.beep();
                startActivity(ManageActivity.class);
                return true;
          //  default:
            //    return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
   *  信息确认提示
   */
    public void showNormalDialog() {
        new PromptDialog(this, R.style.Dialog, "确定退出程序吗？", new PromptDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    mMisc.beep();
                    finish();
                    dialog.dismiss();
                }else
                    mMisc.beep();

            }
        }).setTitle("提示").show();
}
}
