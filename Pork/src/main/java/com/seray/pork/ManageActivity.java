package com.seray.pork;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

public class ManageActivity extends BaseActivity {
    private Button operationBtn,backBtn, rebootBtn,managementBtn;
    private AlertDialog rebootDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        initView();
        initListener();
    }

    private void initView() {
        operationBtn = (Button) findViewById(R.id.bt_manage_operation);
        backBtn = (Button) findViewById(R.id.back_to_main);
        rebootBtn = (Button) findViewById(R.id.reboot);
        managementBtn = (Button) findViewById(R.id.management);
    }

    private void initListener() {
        operationBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        rebootBtn.setOnClickListener(this);
        managementBtn.setOnClickListener(this);
    }

    public void wifiSetting(View view){
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.bt_manage_operation:
                startActivity(OperationActivity.class);
                break;
            case R.id.back_to_main:
                finish();
                break;
            case R.id.reboot:
                openRebootDialog();
                break;
            case R.id.management:
                startActivity(ManagementActivity.class);
                break;
        }
    }


    private void openRebootDialog() {
        if (rebootDialog == null) {
            rebootDialog = new AlertDialog.Builder(ManageActivity.this)
                    .setMessage(R.string.operation_reboot)
                    .setTitle(R.string.test_clear_title)
                    .setPositiveButton(R.string.reprint_ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            App.getApplication().rebootApp();
                        }
                    }).setNegativeButton(R.string.reprint_cancel, new DialogInterface
                            .OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
        }
        rebootDialog.setCanceledOnTouchOutside(false);
        rebootDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rebootDialog != null) {
            if (rebootDialog.isShowing())
                rebootDialog.dismiss();
            rebootDialog = null;
        }

    }


}