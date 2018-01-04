package com.seray.stock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.seray.entity.ApiResult;
import com.seray.entity.OrderDetail;
import com.seray.entity.PurchaseDetail;
import com.seray.entity.TareItem;
import com.seray.http.UploadDataHttp;
import com.seray.pork.App;
import com.seray.pork.BaseActivity;
import com.seray.pork.R;
import com.seray.pork.dao.PurchaseDetailManager;
import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;
import com.seray.view.DeductTareDialog;
import com.seray.view.LoadingDialog;
import com.seray.view.PromptDialog;
import com.tscale.scalelib.jniscale.JNIScale;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.List;

/**
 * 地磅重量确定
 */
public class InBulkQuantityActivity extends BaseActivity {
    private TextView batchNumberTv, nameTv, weightTv, tareWeightTv, modeTv;
    private EditText numberEt;
    private Button minusPeelBt, confirmBt, finishBt, returnBt;
    private String batchNumber, productName, productId ,mode,weight;
    float weightFt ;
    int numberInt;
    private float tareFloat = -1.0F;
    private JNIScale mScale;
    private int state = 2; //1入库确定完成  2 未确定完
    private boolean timeflag = true;
    private InBulkHandler mInBulkHandler = new InBulkHandler(new WeakReference<>(this));
    NumFormatUtil numUtil;
    LoadingDialog loadingDialog;
    private BigDecimal actualWeight,inputWeight;

    private int actualNumber,inputNumber;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_bulk_quantity);
        initView();
        initListener();
        initData();
        timer();
    }

    private void initView() {
        loadingDialog = new LoadingDialog(this);
        modeTv = (TextView) findViewById(R.id.tv_in_bulk_quantity_mode);
        batchNumberTv = (TextView) findViewById(R.id.tv_in_bulk_batch_number);
        nameTv = (TextView) findViewById(R.id.tv_in_bulk_quantity_name);
        weightTv = (TextView) findViewById(R.id.tv_in_bulk_quantity_weight);
        tareWeightTv = (TextView) findViewById(R.id.tv_in_bulk_quantity_tare_weight);
        numberEt = (EditText) findViewById(R.id.et_in_bulk_quantity_number);
        numberEt.setInputType(InputType.TYPE_NULL);
        numberEt.setCursorVisible(false);
        minusPeelBt = (Button) findViewById(R.id.bt_in_bulk_quantity_peel);
        confirmBt = (Button) findViewById(R.id.bt_in_bulk_quantity_confirm);
        finishBt = (Button) findViewById(R.id.bt_in_bulk_quantity_finish);
        returnBt = (Button) findViewById(R.id.bt_in_bulk_quantity_return);
    }

    private void initListener() {
        minusPeelBt.setOnClickListener(this);
        confirmBt.setOnClickListener(this);
        finishBt.setOnClickListener(this);
        returnBt.setOnClickListener(this);
        numberEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode != KeyEvent.KEYCODE_BACK) {
                        mMisc.beep();
                    }
                    String txt = numberEt.getText().toString();
                    if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent
                            .KEYCODE_NUMPAD_9) {
                        txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;
                    } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent
                            .KEYCODE_9) {
                        txt += keyCode - KeyEvent.KEYCODE_0;
                    } else if (keyCode == KeyEvent.KEYCODE_E) {

                    } else if (keyCode == KeyEvent.KEYCODE_NUM_LOCK) {
                        if (!txt.isEmpty())
                            txt = txt.substring(0, txt.length() - 1);
                    } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
                        txt = "";
                    } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                        InputMethodManager imm = (InputMethodManager) App.getApplication().getSystemService
                                (INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(numberEt.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    } else {
                        return false;
                    }
                    numberEt.setText(txt);
                    return true;
                }
                return true;
            }
        });
    }

    private void initData() {
        numUtil = NumFormatUtil.getInstance();
        mScale = JNIScale.getScale();
        Intent intent = getIntent();
        if (intent != null) {

            PurchaseDetail detail = (PurchaseDetail) getIntent().getSerializableExtra("PurchaseDetail");
            batchNumber = intent.getStringExtra("batchNumber");
            inputWeight = detail.getDecimalQuantity();
            productName = detail.getProductName();
            productId = detail.getProductId();
            mode = detail.getUnit();
            actualWeight = detail.getDecimalActualWeight();
            inputNumber = detail.getNumber();
            actualNumber = detail.getActualNumber();
            position = intent.getIntExtra("position", 0);
            modeTv.setText(mode);
            batchNumberTv.setText(batchNumber);
            nameTv.setText(productName);
        } else {
            minusPeelBt.setBackground(getResources().getDrawable(R.drawable.radio_gray));
            confirmBt.setBackground(getResources().getDrawable(R.drawable.radio_gray));
            finishBt.setBackground(getResources().getDrawable(R.drawable.radio_gray));
            minusPeelBt.setEnabled(false);
            confirmBt.setEnabled(false);
            finishBt.setEnabled(false);
        }
    }

    private void timer() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (timeflag) {
                    mInBulkHandler.sendEmptyMessage(1);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private Boolean isOL() {
        return mScale.getStringNet().contains("OL");
    }

    private void weightChangedCyclicity() {

            String strNet = mScale.getStringNet().trim();
            float fW = NumFormatUtil.isNumeric(strNet) ? Float.parseFloat(strNet) : 0;
            if (isOL()) {
                weightTv.setText(strNet);
            } else {
                if (tareFloat > 0) {
                    fW -= tareFloat;
                    tareWeightTv.setText(NumFormatUtil.df3.format(tareFloat));
                }
                weightTv.setText(NumFormatUtil.df3.format(fW));
            }

    }

    private void updateWeight() {
        weight = weightTv.getText().toString();
        String number = numberEt.getText().toString();
        if (TextUtils.isEmpty(number)) {
            number = "0";
        }
        if (weight.equals("OL")) {
            showMessage("超出秤量程");
            return;
        }
         weightFt = Float.parseFloat(weight);
         numberInt = Integer.parseInt(number);
        if (weightFt < 0) {
            showMessage("重量不能为小于0");
            return;
        }
        if (weightFt == 0 && numberInt < 1 && state == 2) {
            showMessage("确定时重量和计件不符合规范");
            return;
        }
        if (state == 1){
            weightFt = 0;
            numberInt =0;
            if (mode.equals("KG")) {
                showNormalDialog("采购重量："+inputWeight + "KG！\n现已确认重量：" + actualWeight + mode+"!\n确定完成后不能再操作");
            }else {
                showNormalDialog("采购数量："+inputNumber + "KG！\n现已确认重量：" + actualNumber + mode+"！\n确定完成后不能再操作");
            }
        }else {
            if (mode.equals("KG")) {
                showNormalDialog("此次重量为:  " + weightFt + " KG");
            }else {
                showNormalDialog("此次数量为:  " + numberInt );
            }
        }
        //   instance.queryByBatchNumber(batchNumber, productName);
    }

    private void saveData(){

        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.setUpdateActualWeight(productId, weightFt, numberInt, batchNumber, state);
                if (api.Result) {
                    PurchaseDetailManager instance = PurchaseDetailManager.getInstance();
                    instance.updatePurchaseDetail(batchNumber, productName, productId, weightFt, numberInt, state, 1);
                    loadingDialog.dismissDialog();
                    showMessage(api.ResultMessage);
                    if (state == 1) {
                        returnValue();
                        finish();
                    }else {
                        actualWeight = actualWeight.add(numUtil.getDecimalNet(weight));
                        actualNumber += numberInt;
                    }
                } else {
                    state = 2;
                    PurchaseDetailManager instance = PurchaseDetailManager.getInstance();
                    instance.updatePurchaseDetail(batchNumber, productName, productId, weightFt, numberInt, state, 2);
                    loadingDialog.dismissDialog();
                    showMessage(api.ResultMessage);
                }
            }
        });


    }

    @Override
    public void onClick(View view) {
        mMisc.beep();
        switch (view.getId()) {
            case R.id.bt_in_bulk_quantity_peel:
                deductTareDialog();
                clearZero();
                break;
            case R.id.bt_in_bulk_quantity_confirm:
                state = 2;
                updateWeight();
                break;
            case R.id.bt_in_bulk_quantity_finish:
                state = 1;
                updateWeight();
                break;
            case R.id.bt_in_bulk_quantity_return:
                returnValue();
                finish();
                break;
        }
    }

    private void returnValue() {
        Intent intent = new Intent();
        intent.putExtra("actualWeight", String.valueOf(actualWeight));
        intent.putExtra("actualNumber", actualNumber);
        intent.putExtra("state", state); //将计算的值回传回去
        intent.putExtra("position", position);
        //通过intent对象返回结果，必须要调用一个setResult方法，

        setResult(3, intent);
    }


    private static class InBulkHandler extends Handler {

        WeakReference<InBulkQuantityActivity> mWeakReference;

        InBulkHandler(WeakReference<InBulkQuantityActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            InBulkQuantityActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.weightChangedCyclicity();
                        break;
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_F2) {
            mMisc.beep();
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_NUMPAD_DIVIDE:// 取消
                loadingDialog.dismissDialog();
                clearZero();
                return true;
            case KeyEvent.KEYCODE_F2:// 置零
                if (mScale.zero()) {
                    cleanTareFloat();
                    clearZero();
                } else {
                    showMessage("置零失败");
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                returnValue();
                finish();
                return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    private void clearZero() {
        tareWeightTv.setText(R.string.base_weight);
        //    setTareFloat();
    }

    /**
     * 重置tareFloat
     */
    void cleanTareFloat() {
        tareFloat = -1.0F;
    }

    /**
     * 设置手动扣重
     */
    private void setTareFloat(float totalTare) {
        if (totalTare == 0) {
            cleanTareFloat();
        } else {
            tareFloat = totalTare;
        }
    }


    private void deductTareDialog() {
        final DeductTareDialog dialog = new DeductTareDialog(InBulkQuantityActivity.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setTitle("请选择扣重");
        dialog.setOnPositiveClickListener(R.string.reprint_ok, new DeductTareDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick(DeductTareDialog dialog, List<TareItem> data) {
                float totalTare = 0.0F;
                for (TareItem item : data) {
                    int count = item.getCount();
                    if (count == 0)
                        continue;
                    float net =Float.parseFloat(item.getNetStr());
                    totalTare += (net * count);
                }
                setTareFloat(totalTare);
                dialog.dismiss();
            }
        });
        dialog.setOnNegativeClickListener(R.string.reprint_cancel, new DeductTareDialog.OnNegativeClickListener() {
            @Override
            public void onNegativeClick(DeductTareDialog dialog) {
                dialog.dismiss();
            }
        });
    }

    /*
     *  信息确认提示
     */
    public void showNormalDialog(String weightContent) {

        new PromptDialog(this, R.style.Dialog, weightContent, new PromptDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    loadingDialog.showDialog();
                    mMisc.beep();
                    saveData();
                    dialog.dismiss();
                }else {
                    mMisc.beep();
                    state = 2;
                }

            }
        }).setTitle("提示").show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeflag = false;
        mInBulkHandler.removeCallbacksAndMessages(null);
    }
}
