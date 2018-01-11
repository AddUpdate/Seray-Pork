package com.seray.pork;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
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
import com.seray.entity.Config;
import com.seray.entity.TareItem;
import com.seray.http.API;
import com.seray.pork.dao.ConfigManager;
import com.seray.utils.LogUtil;
import com.seray.view.PromptDialog;
import com.seray.view.SetIpDialog;
import com.seray.view.SetTareDialog;

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
        name.add("扣重设置");
        name.add("浮动率设置");
        name.add("IP端口设置");
        name.add("退出应用");
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
                        showNormalDialog();
                        break;
                    case "IP端口设置":
                        setIpDialog("IP端口设置");
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
    private void showNormalDialog() {

        new PromptDialog(this, R.style.Dialog, "确定退出应用", new PromptDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    mMisc.beep();
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                    App.getApplication().exit();
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
