package com.seray.pork;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.seray.adapter.ManagementAdapter;
import com.seray.entity.Config;
import com.seray.entity.TareItem;
import com.seray.pork.dao.ConfigManager;
import com.seray.utils.LogUtil;
import com.seray.view.SetTareDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础配置
 */
public class ConfigActivity extends BaseActivity {
    ConfigManager configManager = ConfigManager.getInstance();
    private GridView mGridViewBtn;
    private ManagementAdapter adapter;
    private List<String> name = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        initView();
        initData();
        initListener();
    }
    private void initView() {
        mGridViewBtn = (GridView) findViewById(R.id.gv_config_button);
    }
    private void initData() {
        name.add("扣重设置");
        name.add("浮动率设置");
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
                    case "返回":
                        finish();
                        break;
                }
            }
        });
    }

    private void setTareDialog(String title) {
        SetTareDialog dialog = new SetTareDialog(ConfigActivity.this);
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
