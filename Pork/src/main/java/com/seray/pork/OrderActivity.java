package com.seray.pork;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.service.GpPrintService;
import com.lzscale.scalelib.misclib.GpScalePrinter;
import com.seray.cache.AppConfig;
import com.seray.entity.OrderDetail;
import com.seray.entity.OrderSortCodeMessage;
import com.seray.frag.CompleteCarFrag;
import com.seray.frag.OrderCustomerFrag;
import com.seray.frag.OrderFinishFrag;
import com.seray.frag.OrderProductsFrag;
import com.seray.frag.ProductsSortFrag;
import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class OrderActivity extends BaseActivity {
    RadioGroup radioGroup;
    RadioButton rbCustomer, rbProducts, rbFinish, rbProductsSort, rbCompleteCar, lastSelected;
    private static final String TAG_ORDER_CUSTOMER = "customer";
    private static final String TAG_ORDER_PRODUCTS = "products";
    private static final String TAG_ORDER_FINISH = "finish";
    private static final String TAG_PRODUCTS_SORT = "productsSort";
    private static final String TAG_COMPLETE_CAR = "completeCar";
    private Fragment customerFrag, productsFrag, finishFrag, productsSortFrag, completeCarFrag, currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();
        init();
       register();
    }

    private void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.radio_gr);
        rbCustomer = (RadioButton) findViewById(R.id.order_wait_customer_rb);
        rbProducts = (RadioButton) findViewById(R.id.order_wait_products_rb);
        rbFinish = (RadioButton) findViewById(R.id.order_finish_rb);
        rbProductsSort = (RadioButton) findViewById(R.id.order_products_sort_rb);
        rbCompleteCar = (RadioButton) findViewById(R.id.order_complete_car_rb);
        lastSelected = rbCustomer;
    }

    public void init() {
        customerFrag = new OrderCustomerFrag();
        productsFrag = new OrderProductsFrag();
        productsSortFrag = new ProductsSortFrag();
        finishFrag = new OrderFinishFrag();
        completeCarFrag = new CompleteCarFrag();
        currentFragment = customerFrag;
        initFrag();
        rbCustomer.setOnClickListener(this);
        rbProducts.setOnClickListener(this);
        rbFinish.setOnClickListener(this);
        rbProductsSort.setOnClickListener(this);
        rbCompleteCar.setOnClickListener(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection();
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.order_wait_customer_rb:
                if (rbCustomer.isChecked()) {
                    setFragment(customerFrag, TAG_ORDER_CUSTOMER);
                }
                changedSelectedView(rbCustomer);
                break;
            case R.id.order_wait_products_rb:
                setFragment(productsFrag, TAG_ORDER_PRODUCTS);
                changedSelectedView(rbProducts);
                break;
            case R.id.order_products_sort_rb:
                setFragment(productsSortFrag, TAG_PRODUCTS_SORT);
                changedSelectedView(rbProductsSort);
                break;
            case R.id.order_finish_rb:
                setFragment(finishFrag, TAG_ORDER_FINISH);
                changedSelectedView(rbFinish);
                break;
            case R.id.order_complete_car_rb:
                setFragment(completeCarFrag, TAG_COMPLETE_CAR);
                changedSelectedView(rbCompleteCar);
                break;
        }
    }

    private void changedSelectedView(View v) {
        if (lastSelected != null) {
            lastSelected.setBackground(getResources().getDrawable(R.drawable.shapeone));
        }
        lastSelected = (RadioButton) v;
        lastSelected.setBackground(getResources().getDrawable(R.drawable.radio_selected));
    }

    public void initFrag() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.mian_fram, customerFrag, TAG_ORDER_CUSTOMER).commitAllowingStateLoss();
    }

    public void setFragment(Fragment fragment, String tag) {
        if (fragment == currentFragment) {
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!fragment.isAdded()) {
            fragmentTransaction.hide(currentFragment).add(R.id.mian_fram, fragment, tag).commitAllowingStateLoss();
        } else {
            fragmentTransaction.hide(currentFragment).show(fragment).commitAllowingStateLoss();
        }
        currentFragment = fragment;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int code = event.getKeyCode();
        if (code == KeyEvent.KEYCODE_BACK || code == KeyEvent.KEYCODE_F3) {
            return super.dispatchKeyEvent(event);
        }
        return true;
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
        // registerReceiver(mPrintReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
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
        pi = PendingIntent.getBroadcast(this, 0,
                intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 1000 * 60 * 60 * 24, pi);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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