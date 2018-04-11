package com.seray.pork;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import com.seray.adapter.ChooseFunctionAdapter;
import com.seray.card.RFIDPay;
import com.seray.card.ReturnValue;
import com.seray.card.ReturnValueCallback;
import com.seray.entity.ApiResult;
import com.seray.entity.Config;
import com.seray.http.UploadDataHttp;
import com.seray.pork.dao.ConfigManager;
import com.seray.stock.PurchaseActivity;
import com.seray.utils.HttpUtils;
import com.seray.utils.LogUtil;
import com.seray.view.LoadingDialog;
import com.seray.view.LoginDialog;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChooseFunctionActivity extends BaseActivity {

    private GridView mGridViewBtn;
    private TextView operatorTv, loginTv, hintTv,loginTvEt;
    private List<String> name = new ArrayList<>();
    List<String> mName = new ArrayList<>();
    private List<Integer> mPosition = new ArrayList<>();
    private ChooseFunctionAdapter adapter;
    private String phoneNumber, passWord, cardId;
    private ChooseFunctionHandler chooseFunctionHandler = new ChooseFunctionHandler(new
            WeakReference<>(this));
    LoginDialog dialog;
      LoadingDialog mLoadingDialog;
    ConfigManager configManager = ConfigManager.getInstance();
    RFIDPay read = new RFIDPay();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_function);
        initView();
        dialog = new LoginDialog(this);
        initData();
        initAdapter();
        initListener();
        read.readCardNumber(callback);
    }

    private void initView() {
        mGridViewBtn = (GridView) findViewById(R.id.gv_choose_function_button);
        TextView mIpTextView = (TextView) findViewById(R.id.tv_ip);
        operatorTv = (TextView) findViewById(R.id.tv_choose_function_operator);
        loginTv = (TextView) findViewById(R.id.tv_choose_function_login);
        hintTv = (TextView) findViewById(R.id.tv_choose_function_hint);
        loginTvEt = (TextView) findViewById(R.id.tv_choose_function_login_et);
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
    }

    private void initAdapter() {
        adapter = new ChooseFunctionAdapter(this);
        mGridViewBtn.setAdapter(adapter);
    }

    private void initListener() {
        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                hintTv.setText("请刷卡~");
                read.readCardNumber(callback);
            }
        });
        loginTvEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                read.cancelReadCard(callback);
                hintTv.setText("已取消~");
                showLogin("登录");
            }
        });
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
                ApiResult api = UploadDataHttp.LoginPost(cardId);
                if (api.Result) {
                    mPosition.clear();
                    for (int i = 0; i < api.sourceDetail.length; i++) {
                        mPosition.add(Integer.valueOf(api.sourceDetail[i]));
                    }
                    Config config = new Config();
                    config.setKey("operator");
                    config.setValue(api.ResultJsonStr);
                    configManager.insertConfig(config);
                    showMessage("欢迎使用");
                    chooseFunctionHandler.sendEmptyMessage(1);
                } else {
                    showMessage(api.ResultMessage);
                    chooseFunctionHandler.sendEmptyMessage(3);
                }
            }
        });
    }

    private void submitLoginNumber() {
        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.LoginNumberPost(phoneNumber,passWord);
                if (api.Result) {
                    mPosition.clear();
                    for (int i = 0; i < api.sourceDetail.length; i++) {
                        mPosition.add(Integer.valueOf(api.sourceDetail[i]));
                    }
                    Config config = new Config();
                    config.setKey("operator");
                    config.setValue(api.ResultJsonStr);
                    configManager.insertConfig(config);
                    showMessage("欢迎使用");
                    chooseFunctionHandler.sendEmptyMessage(1);
                    mLoadingDialog.dismissDialogs();
                } else {
                    mLoadingDialog.dismissDialogs();
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
                    case 2:
                        activity.hintTv.setText("读卡中~");
                        break;
                    case 3:
                        activity.hintTv.setText("请重新刷卡~");
                        activity.read.readCardNumber(activity.callback);
                        break;
                }
            }
        }
    }

    private void updateAdapter() {
        loginTv.setVisibility(View.GONE);
        hintTv.setVisibility(View.GONE);
        loginTvEt.setVisibility(View.GONE);
        operatorTv.setText(String.format("操作员:%s", configManager.query("operator")));
        mName.clear();
        for (int i = 0; i < mPosition.size(); i++) {
            mName.add(name.get(mPosition.get(i)));
        }
        adapter.setNewData(mName, mPosition);
        dialog.dismiss();

    }

    ReturnValueCallback callback = new ReturnValueCallback() {
        @Override
        public void run(ReturnValue result) {
            boolean isSuccess = result.getIsSuccess();
            if (isSuccess) {
                cardId = result.getTag().toString();
                chooseFunctionHandler.sendEmptyMessage(2);
                LogUtil.d("读取卡内编号成功", result.getTag().toString());
                submitLogin();
            } else {
                String message = result.getTag().toString();
                if(message.equals("读取设置自动读卡模式超时")){
                    message = "刷卡超时";
                }
                hintTv.setText(message);
                LogUtil.d("读取卡内编号失败", result.getTag().toString());
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mMisc.beep();
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (callback != null) {
                    LogUtil.d("callback", "取消读卡");
                    read.cancelReadCard(callback);
                }
                finish();
                return true;
            case KeyEvent.KEYCODE_MENU:// 桌秤
            case KeyEvent.KEYCODE_MOVE_HOME:// 地秤
                startActivity(ManageActivity.class);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_DIVIDE:// 取消
                if (callback != null) {
                    LogUtil.d("callback", "取消读卡");
                    read.cancelReadCard(callback);
                }
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
                submitLoginNumber();
            }
        });
    }
}
