package com.seray.pork;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.seray.entity.Config;
import com.seray.entity.TareItem;
import com.seray.pork.dao.ConfigManager;
import com.seray.utils.LogUtil;

public class StartActivity extends BaseActivity {

   ConfigManager configManager = ConfigManager.getInstance();

    Handler handler = new Handler();
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(StartActivity.this, ChooseFunctionActivity.class);
            startActivity(intent);
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setConfig();

    }
    private void setConfig(){
        for (int i = 0; i < 5; i++) {
            Config config = new Config();
            switch (i) {
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
            config.setValue("0");
            configManager.insertConfig(config);
        }

        handler.postDelayed(r,2000);
    }
}
