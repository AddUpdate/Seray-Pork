package com.seray.pork;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.lidroid.xutils.view.annotation.event.OnClick;
import com.seray.adapter.ManagementAdapter;
import com.seray.cache.CacheHelper;
import com.seray.entity.Config;
import com.seray.entity.LocalFileTag;
import com.seray.entity.TareItem;
import com.seray.http.API;
import com.seray.pork.dao.ConfigManager;
import com.seray.utils.FileHelp;
import com.seray.utils.LogUtil;
import com.seray.view.LoadingDialog;
import com.seray.view.LoginDialog;
import com.seray.view.PromptDialog;
import com.seray.view.SetIpDialog;
import com.seray.view.SetTareDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理中心
 */

public class ManagementActivity extends BaseActivity {
    ConfigManager configManager = ConfigManager.getInstance();
    private GridView mGridViewBtn;
    private ManagementAdapter adapter;
    private List<String> name = new ArrayList<>();
     LoadingDialog mLoadingDialog;
    private  Handler mSettingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case 6:
                    Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    scanIntent.setData(Uri.fromFile(new File(FileHelp.DATA_PIC_DIR)));
                    sendBroadcast(scanIntent);
                    mLoadingDialog.dismissDialogs();// TODO: 2018/1/24 待完善
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        initView();
        initData();
        initListener();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        mGridViewBtn = (GridView) findViewById(R.id.gv_management_button);
    }

    private void initData() {
        mLoadingDialog = new LoadingDialog(this);
        name.add("扣重设置");
        name.add("浮动率设置");
        name.add("IP端口设置");
        name.add("退出应用");
        name.add("清操作数据和图片");
        name.add("返回");
        adapter = new ManagementAdapter(this, name);
        mGridViewBtn.setAdapter(adapter);
    }

    private void initListener() {

        mGridViewBtn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                String mName = name.get(position);
                switch (mName) {
                    case "扣重设置":
                        setTareDialog("扣重设置");
                        break;
                    case "浮动率设置":
                        setTareDialog("配货浮动率设置");
                        break;
                    case "退出应用":
                        showNormalDialog(1);
                        break;
                    case "IP端口设置":
                        setIpDialog("IP端口设置");
                        break;
                    case "清操作数据和图片":
                        showNormalDialog(2);
                        break;
                    case "返回":
                        finish();
                        break;
                }
            }
        });
    }

    /*
     *  信息确认提示
     */
    private void showNormalDialog(final int content) {

        new PromptDialog(this, R.style.Dialog, content == 1?"确定退出应用吗？":getString(R.string.test_clear_msg), new PromptDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    mMisc.beep();
                    switch (content){
                        case 1:
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                            App.getApplication().exit();
                            break;
                        case 2:
                            mLoadingDialog.showDialog();
                            sqlQueryThread.submit(new Runnable() {
                                @Override
                                public void run() {
                                    if (FileHelp.deleteDir(FileHelp.DATA_PIC_DIR)) {
                                        showMessage(R.string.test_clear_show_msg_ok);
                                    } else {
                                        showMessage(R.string.test_clear_show_msg_failed);
                                    }
                                    FileHelp.deleteDir(FileHelp.STOCK_DIR);
                                    mSettingHandler.sendEmptyMessage(6);
                                }
                            });
                            break;
                    }
                    dialog.dismiss();
                } else {
                    mMisc.beep();
                }
            }
        }).setTitle("提示").show();
    }

    private void setTareDialog(String title) {
        SetTareDialog dialog = new SetTareDialog(ManagementActivity.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setTitle(title);
        dialog.setOnPositiveClickListener(R.string.reprint_ok, new SetTareDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick(SetTareDialog dialog, List<TareItem> data) {
                float totalTare = 0.0F;
                //      List<Config> list = new ArrayList<>();
                for (TareItem item : data) {
                    Config config = new Config();
                    switch (item.getType()) {
                        case 0:
                            config.setKey("tareCar");
                            break;
                        case 1:
                            config.setKey("tareSmall");
                            break;
                        case 2:
                            config.setKey("tareMedium");
                            break;
                        case 3:
                            config.setKey("tareBig");
                            break;
                        case 4:
                            config.setKey("floatRange");
                            break;
                    }
                    config.setValue(item.getNetStr());
                    //     list.add(config);
                    LogUtil.e("config", config.toString());
                    configManager.insertConfig(config);
                }
                //         configManager.insertMultProducts(list);
                dialog.dismiss();
            }
        });
        dialog.setOnNegativeClickListener(R.string.reprint_cancel, new SetTareDialog.OnNegativeClickListener() {
            @Override
            public void onNegativeClick(SetTareDialog dialog) {
                dialog.dismiss();
            }
        });
    }

    private void setIpDialog(String title) {
        SetIpDialog setIpDialog = new SetIpDialog(this);
        setIpDialog.setCanceledOnTouchOutside(false);
        setIpDialog.show();
        setIpDialog.setTitle(title);
        setIpDialog.setOnPositiveClickListener(R.string.reprint_ok, new SetIpDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick(SetIpDialog dialog, String Ip) {
                Config config = new Config();
                config.setKey("ip");
                config.setValue(Ip);
                configManager.insertConfig(config);
                API.reset();
                dialog.dismiss();
            }
        });
        setIpDialog.setOnNegativeClickListener(R.string.reprint_cancel, new SetIpDialog.OnNegativeClickListener() {
            @Override
            public void onNegativeClick(SetIpDialog dialog) {
                dialog.dismiss();
            }
        });

    }
}
