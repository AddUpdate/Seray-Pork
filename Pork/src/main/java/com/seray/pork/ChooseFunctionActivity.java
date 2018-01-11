package com.seray.pork;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.seray.adapter.ChooseFunctionAdapter;
import com.seray.adapter.ManagementAdapter;
import com.seray.entity.ApiResult;
import com.seray.entity.OrderDetail;
import com.seray.entity.TareItem;
import com.seray.http.UploadDataHttp;
import com.seray.stock.PurchaseActivity;
import com.seray.utils.HttpUtils;
import com.seray.utils.LocalServer;
import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;
import com.seray.view.LoginDialog;
import com.seray.view.PromptDialog;
import com.seray.view.SetTareDialog;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2017/11/17.
 */

public class ChooseFunctionActivity extends BaseActivity {

    Button inputBt, porkBt, separateBt, sortBt, frozenBt, finishBt, excessBt, orderBt;

    private LocalServer mLocalServer = null;
    private GridView mGridViewBtn;
    private List<String> name = new ArrayList<>();
    private List<Integer> mPosition = new ArrayList<>();
    private ChooseFunctionAdapter adapter;
    private String phoneNumber , passWord;
    private ChooseFunctionHandler chooseFunctionHandler = new ChooseFunctionHandler(new
            WeakReference<>(this));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_function);
        initView();
        initData();
        showLogin("登录");
        initListener();
    }

    private void initView() {
        mGridViewBtn = (GridView) findViewById(R.id.gv_choose_function_button);

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

    }

    private void initData() {
        try {
            mLocalServer = new LocalServer();
            mLocalServer.start();
        } catch (IOException e) {
            LogUtil.e(e.getMessage());
        }
        name.add("采购台账录入");
        name.add("白条库");
        name.add("分割房");
        name.add("分拣区");
        name.add("速冻库");
        name.add("成品1、2号库");
        name.add("鲜品库");
        name.add("订单");
        adapter = new ChooseFunctionAdapter(this, name, mPosition);
        mGridViewBtn.setAdapter(adapter);
    }

    private void initListener() {
        mGridViewBtn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                if (mPosition.size()<1){
                    showMessage("您无权限！");
                    return;
                }
                for (int i = 0; i < mPosition.size(); i++) {
                    if (mPosition.get(i) == position) {
                        switch (position) {
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
                }
            }
        });
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

    private void submitLogin( ) {
        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.LoginPost( phoneNumber, passWord);
                if (api.Result) {
                    for (int i = 0; i <api.sourceDetail.length; i++) {
                        mPosition.add(Integer.valueOf(api.sourceDetail[i]));
                    }
                    showMessage("欢迎使用");
                    chooseFunctionHandler.sendEmptyMessage(1);
                } else {
                    showMessage(api.ResultMessage);
                }
            }
        });
    }
    private  class ChooseFunctionHandler extends Handler {

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
        adapter.notifyDataSetChanged();
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
                if (confirm) {
                    mMisc.beep();
                    finish();
                    dialog.dismiss();
                } else
                    mMisc.beep();

            }
        }).setTitle("提示").show();
    }

    public void showLogin(String title) {
        LoginDialog dialog = new LoginDialog(ChooseFunctionActivity.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setTitle(title);
        dialog.setOnPositiveClickListener(R.string.reprint_ok, new LoginDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick(LoginDialog dialog, String tel, String password) {

                if (TextUtils.isEmpty(tel)||TextUtils.isEmpty(password)){
                    return;
                }
                phoneNumber = tel;
                passWord = password;
                submitLogin();
                dialog.dismiss();
            }
        });

    }
}
