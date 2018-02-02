package com.seray.pork;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.seray.adapter.ChooseFunctionAdapter;
import com.seray.entity.ApiResult;
import com.seray.http.UploadDataHttp;
import com.seray.stock.PurchaseActivity;
import com.seray.utils.HttpUtils;
import com.seray.utils.LocalServer;
import com.seray.utils.LogUtil;
import com.seray.view.LoadingDialog;
import com.seray.view.LoginDialog;
import com.seray.view.PromptDialog;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChooseFunctionActivity extends BaseActivity {


    private GridView mGridViewBtn;
    private List<String> name = new ArrayList<>();
    List<String> mName = new ArrayList<>();
    private List<Integer> mPosition = new ArrayList<>();
    private ChooseFunctionAdapter adapter;
    private String phoneNumber, passWord;
    private ChooseFunctionHandler chooseFunctionHandler = new ChooseFunctionHandler(new
            WeakReference<>(this));
    boolean loginFlag = false;
    LoginDialog dialog;
    LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_function);
        initView();
        dialog = new LoginDialog(this);
        initData();
        initAdapter();
        //     showLogin("登录");
        initListener();
        updateAdapter();
    }

    private void initView() {
        mGridViewBtn = (GridView) findViewById(R.id.gv_choose_function_button);
        TextView mIpTextView = (TextView) findViewById(R.id.tv_ip);
        String ipAddress = HttpUtils.getLocalIpStr(getApplication());
        mIpTextView.setText(ipAddress);
    }

    private void initData() {
        mLoadingDialog = new LoadingDialog(this);
        name.add("采购台账录入");
        name.add("白条库");
        name.add("分割房");
        name.add("分拣区");
        name.add("速冻库");
        name.add("成品1、2号库");
        name.add("鲜品库");
        name.add("订单");
        for (int i = 0; i < 8; i++) {
            mPosition.add(i);
        }
    }

    private void initAdapter() {
        adapter = new ChooseFunctionAdapter(this);
        mGridViewBtn.setAdapter(adapter);
    }

    private void initListener() {
        mGridViewBtn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                if (mPosition.size() < 1) {
                    showMessage("您无权限！");
                    return;
                }
                switch (mPosition.get(position)) {
                    case 0:
                        startActivity(PurchaseActivity.class);
                        break;
                    case 1:
                        startActivity(TemporaryLibraryActivity.class);
                        break;
                    case 2:
                        startActivity(SeparateActivity.class);
                        break;
                    case 3:
                        startActivity(SortActivity.class);
                        break;
                    case 4:
                        startActivity(FrozenLibraryActivity.class);
                        break;
                    case 5:
                        startActivity(FinishProductActivity.class);
                        break;
                    case 6:
                        startActivity(ExcessStockActivity.class);
                        break;
                    case 7:
                        startActivity(OrderActivity.class);
                        break;
                }
            }
        });
    }

    private void submitLogin() {

        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.LoginPost(phoneNumber, passWord);
                mLoadingDialog.dismissDialog();
                if (api.Result) {
                    loginFlag = true;
                    mPosition.clear();
                    for (int i = 0; i < api.sourceDetail.length; i++) {
                        mPosition.add(Integer.valueOf(api.sourceDetail[i]));
                    }
                    showMessage("欢迎使用");
                    chooseFunctionHandler.sendEmptyMessage(1);
                } else {
                    loginFlag = false;
                    showMessage(api.ResultMessage);
                }

            }
        });
    }

    private static class ChooseFunctionHandler extends Handler {

        WeakReference<ChooseFunctionActivity> mWeakReference;

        ChooseFunctionHandler(WeakReference<ChooseFunctionActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChooseFunctionActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.updateAdapter();
                        break;
                }
            }
        }
    }

    private void updateAdapter() {
        mName.clear();
        for (int i = 0; i < mPosition.size(); i++) {
            mName.add(name.get(mPosition.get(i)));
        }
        adapter.setNewData(mName, mPosition);
        dialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mMisc.beep();
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;
            case KeyEvent.KEYCODE_MENU:// 桌秤

                startActivity(ManageActivity.class);
                return true;
            case KeyEvent.KEYCODE_MOVE_HOME:// 地秤

                startActivity(ManageActivity.class);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showLogin(String title) {
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.setTitle(title);
        dialog.setOnPositiveClickListener(R.string.reprint_ok, new LoginDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick(String tel, String password) {

                if (TextUtils.isEmpty(tel) || TextUtils.isEmpty(password)) {
                    return;
                }
                mLoadingDialog.showDialog();
                phoneNumber = tel;
                passWord = password;
                submitLogin();
            }
        });

    }
}
