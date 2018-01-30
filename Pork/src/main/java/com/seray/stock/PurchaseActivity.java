package com.seray.stock;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.seray.pork.BaseActivity;
import com.seray.pork.R;

public class PurchaseActivity extends BaseActivity {

    private Button mSearchBtn, mInsertBtn, mWeighingBtn,mQRBtn, mUploadBtn;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mMisc.beep();
            finish();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        initView();
        initListener();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.pur_search:
                startActivity(EntrySearchActivity.class);
                break;
            case R.id.pur_insert:
                startActivity(StockActivity.class);
                break;
            case R.id.pur_upload:
                startActivity(ConfirmGoodsActivity.class);
                break;
            case R.id.pur_qr:
                startActivity(InBulkGoodsActivity.class);
                break;
            case R.id.pur_weighing:
                startActivity(OtherWeightActivity.class);
                break;
        }
    }

    private void initView() {
        mSearchBtn = (Button) findViewById(R.id.pur_search);
        mInsertBtn = (Button) findViewById(R.id.pur_insert);
        mQRBtn = (Button) findViewById(R.id.pur_qr);
        mUploadBtn = (Button) findViewById(R.id.pur_upload);
        mWeighingBtn = (Button) findViewById(R.id.pur_weighing);
    }

    private void initListener() {
        mSearchBtn.setOnClickListener(this);
        mInsertBtn.setOnClickListener(this);
        mQRBtn.setOnClickListener(this);
        mUploadBtn.setOnClickListener(this);
        mWeighingBtn.setOnClickListener(this);
    }
}
