package com.seray.pork;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.service.GpPrintService;
import com.lzscale.scalelib.misclib.GpScalePrinter;
import com.seray.cache.AppConfig;
import com.seray.entity.ApiResult;
import com.seray.entity.OrderDetail;
import com.seray.http.UploadDataHttp;
import com.seray.pork.dao.ConfigManager;
import com.seray.pork.dao.OrderDetailManager;
import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;
import com.seray.view.LoadingDialog;
import com.seray.view.PromptDialog;
import com.tscale.scalelib.jniscale.JNIScale;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class OrderWeightActivity extends BaseActivity {
    private TextView nameTv, quantityTv, actualQuantityTv, weightTv, numberTv, floatingTv;
    private Button returnBt, confirmBt, finishBt;
    private int TotalNumber;//需配数量
    private int actualNumber;
    private BigDecimal actualWeight;
    private LinearLayout linearWeight, linearNumber;
    private boolean flag = true;
    private JNIScale mScale;
    private float tareFloat = -1.0F;
    private boolean isByWeight = true;
    private String weightStr = "", orderDetailId, barCode;
    private int number = 0, position, state;
    LoadingDialog loadingDialog;
    NumFormatUtil numUtil;
    OrderDetailManager orderManager = OrderDetailManager.getInstance();

    OrderWeightHandler mOrderWeightHandler = new OrderWeightHandler(new WeakReference<>(this));
    ConfigManager configManager = ConfigManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_weight);
        initView();
        register();
        initJNI();
        initData();
        timer();
    }

    private void initView() {
        loadingDialog = new LoadingDialog(this);
        nameTv = (TextView) findViewById(R.id.tv_order_weight_name);
        quantityTv = (TextView) findViewById(R.id.tv_order_weight_quantity);
        actualQuantityTv = (TextView) findViewById(R.id.tv_order_weight_actual_quantity);
        weightTv = (TextView) findViewById(R.id.tv_order_weight_weight);
        numberTv = (TextView) findViewById(R.id.tv_order_weight_number);
        floatingTv = (TextView) findViewById(R.id.tv_order_weight_floating);
        linearWeight = (LinearLayout) findViewById(R.id.ll_weight);
        linearNumber = (LinearLayout) findViewById(R.id.ll_number);
        returnBt = (Button) findViewById(R.id.bt_order_weight_return);
        confirmBt = (Button) findViewById(R.id.bt_order_weight_confirm);
        finishBt = (Button) findViewById(R.id.bt_order_weight_finish);
        setPieceNum(numberTv);

        returnBt.setOnClickListener(this);
        confirmBt.setOnClickListener(this);
        finishBt.setOnClickListener(this);
    }

    private void initData() {
        numUtil = NumFormatUtil.getInstance();
        Intent intent = getIntent();
        if (intent != null) {
            position = intent.getIntExtra("position", 0);
            OrderDetail detail = (OrderDetail) getIntent().getSerializableExtra("OrderDetail");
            nameTv.setText(detail.getProductName() != null ? detail.getProductName() : "");

            TotalNumber = detail.getNumber();
            state = detail.getState();
            orderDetailId = detail.getOrderDetailId();
            actualWeight = detail.getBigDecimalActualWeight();
            actualNumber = detail.getActualNumber();
            barCode = detail.getBarCode();
            if (TotalNumber > 0) {
                quantityTv.setText(String.valueOf(TotalNumber));
                actualQuantityTv.setText(String.valueOf(actualNumber));
                linearWeight.setVisibility(View.GONE);
                linearNumber.setVisibility(View.VISIBLE);
                floatingTv.setVisibility(View.GONE);
                isByWeight = false;
            } else {
                BigDecimal weight = detail.getBigDecimalWeight();
                BigDecimal weightFloating = weight.multiply(new BigDecimal(configManager.query("floatRange"))).setScale(3, BigDecimal.ROUND_HALF_UP);
                quantityTv.setText(String.valueOf(weight) + "KG");
                actualQuantityTv.setText(String.valueOf(actualWeight));
                floatingTv.setText("浮动范围：" + weight.subtract(weightFloating) + "~" + weight.add(weightFloating));
                linearWeight.setVisibility(View.VISIBLE);
                floatingTv.setVisibility(View.VISIBLE);
                linearNumber.setVisibility(View.GONE);
                isByWeight = true;
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection();
            }
        }).start();
    }

    public void setPieceNum(final TextView testview) {
        testview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isByWeight) {
                    if (s.toString().contains(".")) {
                        s = s.toString().subSequence(0, s.toString().length() - 1);
                        testview.setText(s);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initJNI() {
        mScale = JNIScale.getScale();
    }

    private void timer() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (flag) {
                    mOrderWeightHandler.sendEmptyMessage(1);
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

    /**
     * 重置tareFloat
     */
    void cleanTareFloat() {
        tareFloat = -1.0F;
    }

    private void weightChangedCyclicity() {
        if (isByWeight) {
            String strNet = mScale.getStringNet().trim();
            float tare = tareFloat;
            float fW = NumFormatUtil.isNumeric(strNet) ? Float.parseFloat(strNet) : 0;
            if (isOL()) {
                weightTv.setText(strNet);
            } else {
                if (tareFloat > 0) {
                    fW -= tare;
                }
                weightTv.setText(NumFormatUtil.df3.format(fW));
            }
        }
    }

    private static class OrderWeightHandler extends Handler {

        WeakReference<OrderWeightActivity> mWeakReference;

        OrderWeightHandler(WeakReference<OrderWeightActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            OrderWeightActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.weightChangedCyclicity();
                        break;
                    case 2:
                        activity.updateView();
                        break;
                }
            }
        }
    }

    private void updateView() {
        if (isByWeight) {
            actualQuantityTv.setText(String.valueOf(actualWeight));
        } else {
            actualQuantityTv.setText(String.valueOf(actualNumber));
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bt_order_weight_return:
                returnValue();
                finish();
                break;
            case R.id.bt_order_weight_confirm:
                state = 2;
                setData();
                break;
            case R.id.bt_order_weight_finish:
                state = 1;
                setData();
                break;
        }
    }

    private void setData() {
        if (TotalNumber > 0) {
            if (state == 2) {
                if (numberTv.getText().toString() == null) {
                    number = 0;
                } else {
                    number = Integer.valueOf(numberTv.getText().toString());
                }
                if (number > 0) {
                    showNormalDialog("数量：" + number);
                } else {
                    showMessage("数量不能小于1");
                }
            } else {
                number = 0;
                weightStr = "0";
                showNormalDialog("已配货总数量：" + actualNumber);
            }
        } else {
            if (state == 2) {
                if (weightTv.getText().toString() == null) {
                    weightStr = "0";
                } else {
                    weightStr = weightTv.getText().toString();
                }
                float weightFt = Float.parseFloat(weightStr);
                if (weightFt <= 0) {
                    showMessage("重量需大于0");
                    return;
                }
                showNormalDialog("重量：" + weightStr);
            } else {
                number = 0;
                weightStr = "0";
                showNormalDialog("已配货总重量：" + actualWeight);
            }
        }
    }

    private void saveData() {
        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.updatetOrderActual(orderDetailId, weightStr, String.valueOf(number), String.valueOf(state));
                if (api.Result) {
                    if (state == 1) {
                        returnValue();
                        setPrintData();
                        finish();
                    } else {
                        actualWeight = actualWeight.add(numUtil.getDecimalNet(weightStr));
                        actualNumber += number;
                        mOrderWeightHandler.sendEmptyMessage(2);
                        setPrintData();
                    }
                    loadingDialog.dismissDialog();

                } else {
                    if (api.ResultMessage.equals("失败")) {
                        sqlInsert();
                        if (state == 2) {
                            actualWeight = actualWeight.add(numUtil.getDecimalNet(weightStr));
                            actualNumber += number;
                            mOrderWeightHandler.sendEmptyMessage(2);
                            setPrintData();
                        } else {
                            setPrintData();
                            returnValue();
                            finish();
                        }
                    } else {
                        showMessage(api.ResultMessage);
                    }
                    loadingDialog.dismissDialog();
                }
            }
        });
    }

    private void sqlInsert() {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderDetailId(orderDetailId);
        orderDetail.setActualWeight(actualWeight);
        orderDetail.setActualNumber(actualNumber);
        orderDetail.setState(state);
        orderManager.insertOrderDetail(orderDetail);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mMisc.beep();
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                returnValue();
                finish();
                return true;
            case KeyEvent.KEYCODE_NUMPAD_DOT: // 一键清除
                numberTv.setText("");
                return true;
            case KeyEvent.KEYCODE_F1:// 去皮
                cleanTareFloat();
                if (!mScale.tare()) {
                    showMessage("去皮失败");
                }
                return true;
            case KeyEvent.KEYCODE_F2:// 置零
                if (mScale.zero()) {
                    cleanTareFloat();
                } else {
                    showMessage("置零失败");
                }
                return true;
//            case KeyEvent.KEYCODE_NUMPAD_MULTIPLY: // 计件计重切换
//                toggleIsWeight();
//                return true;
            case KeyEvent.KEYCODE_NUMPAD_0:
                unitValu("0");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_1:
                unitValu("1");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_2:
                unitValu("2");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_3:
                unitValu("3");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_4:
                unitValu("4");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_5:
                unitValu("5");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_6:
                unitValu("6");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_7:
                unitValu("7");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_8:
                unitValu("8");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_9:
                unitValu("9");
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void unitValu(String num) {
        String iszero = numberTv.getText().toString().trim();
        boolean wzero = iszero.equals("0");
        if (wzero) {
            numberTv.setText(num);
        } else {
            if (iszero.length() >= 4)
                return;
            numberTv.setText(iszero + num);
        }
    }

    /*
   *  信息确认提示
   */
    public void showNormalDialog(String weightContent) {

        new PromptDialog(this, R.style.Dialog, weightContent, new PromptDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    loadingDialog.showDialog();
                    mMisc.beep();
                    saveData();
                    dialog.dismiss();
                } else {
                    mMisc.beep();
                }

            }
        }).setTitle("提示").show();
    }

    private void returnValue() {
        Intent intent = new Intent();
        intent.putExtra("actualWeight", String.valueOf(actualWeight));
        intent.putExtra("actualNumber", actualNumber);
        intent.putExtra("position", position);
        intent.putExtra("state", state);
        //通过intent对象返回结果，必须要调用一个setResult方法，
        setResult(2, intent);
    }


    private GpService mGpService = null;

    private PrinterServiceConnection mPrintConnect = null;

    private PortParameters mPortParam = new PortParameters();

    private class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
            initPortParam();
        }
    }

    private BroadcastReceiver PrinterStatusBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (GpCom.ACTION_CONNECT_STATUS.equals(intent.getAction())) {
                int type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0);
                LogUtil.d("TYPE RECEIVE " + type);
                if (type == GpDevice.STATE_CONNECTING) {
                    showMessage("打印机正在连接");
                    mPortParam.setPortOpenState(false);
                } else if (type == GpDevice.STATE_NONE) {
                    showMessage("打印机未连接");
                    mPortParam.setPortOpenState(false);
                    AppConfig.isUseGpPrinter = false;
                } else if (type == GpDevice.STATE_VALID_PRINTER) {
                    showMessage("打印机已连接");
                    mPortParam.setPortOpenState(true);
                    AppConfig.isUseGpPrinter = true;
                }
            }
        }
    };


    private void initPortParam() {
        boolean state = getConnectState();
        mPortParam = new PortParameters();
        mPortParam.setPortOpenState(state);
        getUsbDeviceList();
        connectOrDisConnectToDevice();
    }

    private boolean getConnectState() {
        boolean state = false;
        try {
            if (mGpService.getPrinterConnectStatus(0) == GpDevice.STATE_CONNECTED) {
                state = true;
            }
        } catch (RemoteException e) {
            LogUtil.e(e.getMessage());
        }
        return state;
    }

    boolean checkPortParameters(PortParameters param) {
        boolean rel = false;
        int type = param.getPortType();
        if (type == PortParameters.USB) {
            if (!param.getUsbDeviceName().equals("")) {
                rel = true;
            }
        }
        return rel;
    }

    void connectOrDisConnectToDevice() {
        int rel = 0;
        try {
            if (!mPortParam.getPortOpenState()) {

                if (checkPortParameters(mPortParam)) {
                    mGpService.closePort(0);
                    if (mPortParam.getPortType() == PortParameters.USB) {
                        rel = mGpService.openPort(0, mPortParam.getPortType(), mPortParam.getUsbDeviceName(), 0);
                    }
                    GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
                    if (r != GpCom.ERROR_CODE.SUCCESS) {
                        if (r == GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN) {
                            mPortParam.setPortOpenState(true);
                        } else {
                            showMessage(GpCom.getErrorText(r));
                        }
                    }
                } else {
                    showMessage("端口参数错误");
                }
            } else {
                mGpService.closePort(0);
            }
        } catch (RemoteException e) {
            LogUtil.e(e.getMessage());
        }
    }

    public void getUsbDeviceList() {
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> devices = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = devices.values().iterator();
        int count = devices.size();
        if (count > 0) {
            if (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                String deviceName = device.getDeviceName();
                if (checkUsbDevicePidVid(device)) {
                    mPortParam.setPortType(PortParameters.USB);
                    mPortParam.setIpAddr("");
                    mPortParam.setPortNumber(10000);
                    mPortParam.setBluetoothAddr("");
                    mPortParam.setUsbDeviceName(deviceName);
                }
            }
        } else {
            showMessage("未连接USB打印机");
        }
    }

    boolean checkUsbDevicePidVid(UsbDevice dev) {
        int pid = dev.getProductId();
        int vid = dev.getVendorId();
        boolean rel = false;
        if (vid == 34918 && pid == 256 || vid == 1137 && pid == 85 || vid == 6790 && pid == 30084 || vid == 26728 && pid == 256 || vid == 26728 && pid == 512 || vid == 26728 && pid == 768 || vid == 26728 && pid == 1024 || vid == 26728 && pid == 1280 || vid == 26728 && pid == 1536) {
            rel = true;
        }
        return rel;
    }

    private void connection() {
        mPrintConnect = new PrinterServiceConnection();
        Intent intent = new Intent(this, GpPrintService.class);
        bindService(intent, mPrintConnect, Context.BIND_AUTO_CREATE);
    }

    private PendingIntent pi;

    public void register() {

//        registerReceiver(mPrintReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
        IntentFilter filter = new IntentFilter();
        filter.addAction(GpCom.ACTION_CONNECT_STATUS);
        registerReceiver(PrinterStatusBroadcastReceiver, filter);
        IntentFilter dateFilter = new IntentFilter();
        dateFilter.addAction("com.seray.scale.DATE_CHANGED");
//        registerReceiver(dateReceiver, dateFilter);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction("com.seray.scale.DATE_CHANGED");
        pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 1000 * 60 * 60 * 24, pi);
    }

    /**
     * 设置打印数据
     */
    private void setPrintData() {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProductName(nameTv.getText().toString());
        orderDetail.setAlibraryName("苏州源丰食品有限公司");
        orderDetail.setWeight(state == 1 ? BigDecimal.ZERO : new BigDecimal(weightStr));
        orderDetail.setNumber(state == 1 ? 0 : number);
        orderDetail.setBarCode(barCode);
        orderDetail.setOrderDate(NumFormatUtil.getDateDetail());
        keyEnter(orderDetail);
    }

    /**
     * 准备打印
     */
    private void keyEnter(OrderDetail detail) {
        GpScalePrinter.getInstance(mGpService).printOrder(detail, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false;
        mOrderWeightHandler.removeCallbacksAndMessages("");
        if (mGpService != null) {
            try {
                mGpService.closePort(0);
                if (mPrintConnect != null) {
                    unbindService(mPrintConnect);
                }
            } catch (RemoteException e) {
                LogUtil.e(e.getMessage());
            }
        }
        unregisterReceiver(PrinterStatusBroadcastReceiver);
        AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        am.cancel(pi);
        mPortParam = null;
    }
}
