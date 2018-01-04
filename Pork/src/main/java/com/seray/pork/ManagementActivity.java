package com.seray.pork;

import android.app.Dialog;
import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.lidroid.xutils.view.annotation.event.OnClick;
import com.seray.entity.Config;
import com.seray.entity.TareItem;
import com.seray.pork.dao.ConfigManager;
import com.seray.utils.LogUtil;
import com.seray.view.PromptDialog;
import com.seray.view.SetTareDialog;

import java.util.ArrayList;
import java.util.List;

public class ManagementActivity extends BaseActivity {
    private Button outBt, returnBt, tareBt, floatRangeBt;
    ConfigManager configManager = ConfigManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        initView();
    }

    private void initView() {
        outBt = (Button) findViewById(R.id.btn_out);
        returnBt = (Button) findViewById(R.id.btn_return);
        tareBt = (Button) findViewById(R.id.btn_management_tare);
        floatRangeBt = (Button) findViewById(R.id.btn_management_float_range);
        outBt.setOnClickListener(this);
        returnBt.setOnClickListener(this);
        tareBt.setOnClickListener(this);
        floatRangeBt.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_management_tare:
                setTareDialog("设置扣重");
                break;
            case R.id.btn_management_float_range:
                setTareDialog("设置配货浮动率");
                break;
            case R.id.btn_out:
                showNormalDialog();
                break;
            case R.id.btn_return:
                finish();
                break;
        }
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
}
