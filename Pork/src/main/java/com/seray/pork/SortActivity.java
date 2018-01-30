package com.seray.pork;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.service.GpPrintService;
import com.lzscale.scalelib.misclib.GpScalePrinter;
import com.seray.adapter.SeparateAdapter;
import com.seray.adapter.SeparateProductsAdapter;
import com.seray.cache.AppConfig;
import com.seray.entity.ApiResult;
import com.seray.entity.Library;
import com.seray.entity.LibraryList;
import com.seray.entity.MonitorLibraryMessage;
import com.seray.entity.MonitorProdctsMessage;
import com.seray.entity.OperationLog;
import com.seray.entity.OrderDetail;
import com.seray.entity.Products;
import com.seray.entity.ProductsCategory;
import com.seray.http.UploadDataHttp;
import com.seray.inter.BackDisplayBase;
import com.seray.pork.dao.OperationLogManager;
import com.seray.pork.dao.ProductsCategoryManager;
import com.seray.pork.dao.ProductsManager;
import com.seray.service.BatteryMsg;
import com.seray.service.BatteryService;
import com.seray.utils.LibraryUtil;
import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;
import com.seray.view.LoadingDialog;
import com.seray.view.PromptDialog;
import com.tscale.scalelib.jniscale.JNIScale;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 分拣区
 */

public class SortActivity extends BaseActivity {
    private TextView TvName, TvWeight, TvTareWeight, TvWeightType;
    private TextView mMaxUnitView, mTimeView;
    private ImageView mBatteryIv, mPrinterView;

    private Spinner spinnerCome, spinnerLeave;
    private List<String> come_data = new ArrayList<>();
    private List<String> go_data = new ArrayList<>();
    private ArrayAdapter<String> come_adapter;
    private ArrayAdapter<String> go_adapter;
    private List<Library> comeLibraryList = new ArrayList<>();
    private List<Library> goLibraryList = new ArrayList<>();
    private Button submitBt;
    private GridView mGridViewPlu;
    private ListView groupListView;

    private float tareFloat = -1.0F;

    private BackDisplayBase backDisplay = null;
    private JNIScale mScale;
    private boolean flag = true;
    private SortHandler mSortHandler = new SortHandler(new WeakReference<>(this));

    private List<ProductsCategory> categoryList = new ArrayList<>();
    private List<Products> productList;
    SeparateAdapter separateAdapter;
    SeparateProductsAdapter productsAdapter;

    private String name, weight, unit, plu = "",price;
    private int number;
    private String source, goLibrary, comeLibraryId, goLibraryId;

    private boolean isByWeight = true;
    OperationLogManager logManager = OperationLogManager.getInstance();
    private NumFormatUtil mNumUtil = null;
    LoadingDialog loadingDialog;
    LibraryUtil libraryUtil = new LibraryUtil(this);

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLibrary(MonitorLibraryMessage msg) {
        initLibrary();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveProdcts(MonitorProdctsMessage msg) {
        categoryList.clear();
        categoryList = msg.getList();
        separateAdapter.setNewData(categoryList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);
        EventBus.getDefault().register(this);
        initView();
        register();
        initListener();
        initData();
        initAdapter();
        initLibrary();
        initJNI();
        timer();
    }

    private void initView() {
        loadingDialog = new LoadingDialog(this);
        TvWeightType = (TextView) findViewById(R.id.tv_sort_weight_type);
        TvName = (TextView) findViewById(R.id.tv_sort_name);
        TvWeight = (TextView) findViewById(R.id.tv_sort_weight);
        TvTareWeight = (TextView) findViewById(R.id.tv_sort_tare_weight);

        submitBt = (Button) findViewById(R.id.bt_sort_ok);

        mGridViewPlu = (GridView) findViewById(R.id.gv_sort_plu);
        groupListView = (ListView) findViewById(R.id.lv_sort_group);

        groupListView.setItemsCanFocus(true);// 让ListView的item获得焦点
        groupListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// 单选模式
        mGridViewPlu.setItemChecked(1, true);// 让ListView的item获得焦点
        mGridViewPlu.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// 单选模式

        spinnerCome = (Spinner) findViewById(R.id.spinner_sort_come);
        spinnerCome.setGravity(Gravity.CENTER);
        spinnerLeave = (Spinner) findViewById(R.id.spinner_sort_leave);
        spinnerLeave.setGravity(Gravity.CENTER);
        mPrinterView = (ImageView) findViewById(R.id.server_order_count);
        mMaxUnitView = (TextView) findViewById(R.id.maxUnit);
        mBatteryIv = (ImageView) findViewById(R.id.battery);
        mTimeView = (TextView) findViewById(R.id.timer);
        mTimeView.setText(NumFormatUtil.getFormatDate());
        setPieceNum(TvWeight);
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

    /**
     * 时间刷新监听器 每分钟自动监听 注意：API要求必须动态注册
     */
    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                mTimeView.setText(NumFormatUtil.getFormatDate());
            }
        }
    };

    private void initListener() {

        submitBt.setOnClickListener(this);

        mGridViewPlu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                TvName.setText(productList.get(position).getProductName());
                plu = productList.get(position).getPluCode();
                price = String.valueOf(productList.get(position).getUnitPrice());
                int num = productList.get(position).getMeasurementMethod();
                switch (num) {
                    case 1:
                        unit = "KG";
                        break;
                    case 2:
                        unit = "袋";
                        break;
                    case 3:
                        unit = "箱";
                        break;
                    default:
                        unit = "";
                        break;
                }
                isByWeight = unit.equals("KG") ? false : true;
                toggleIsWeight();
            }
        });
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                productList = categoryList.get(position).getProductsList();
                productsAdapter.setNewData(productList);
            }
        });
        spinnerCome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(R.color.white));    //设置颜色
                tv.setTextSize(25.0f);    //设置大小
                tv.setGravity(Gravity.CENTER_HORIZONTAL);   //设置居中
                //   showMessage(data_list.get(position));
                comeLibraryId = comeLibraryList.get(position).getLibraryId();
                source = come_data.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerLeave.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(R.color.white));    //设置颜色
                tv.setTextSize(25.0f);    //设置大小
                tv.setGravity(Gravity.CENTER_HORIZONTAL);   //设置居中
                goLibraryId = goLibraryList.get(position).getLibraryId();
                goLibrary = go_data.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initData() {
        Intent intent = new Intent(this, BatteryService.class);
        startService(intent);
        mNumUtil = NumFormatUtil.getInstance();
        ProductsCategoryManager pCaManager = ProductsCategoryManager.getInstance();
        ProductsManager productsManager = ProductsManager.getInstance();
        List<ProductsCategory> mCategoryList = pCaManager.queryAllProductsCategory();
        for (int i = 0; i < mCategoryList.size(); i++) {
            List<Products> list = productsManager.queryProductsByQueryBuilder(mCategoryList.get(i).getCategoryId());
            ProductsCategory productsCategory = new ProductsCategory(mCategoryList.get(i).getCategoryId(), mCategoryList.get(i).getCategoryName());
            productsCategory.setProductsList(list);
            categoryList.add(productsCategory);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection();
            }
        }).start();
    }

    private void initAdapter() {
        separateAdapter = new SeparateAdapter(this, categoryList);
        groupListView.setAdapter(separateAdapter);
        groupListView.setItemChecked(0, true);
        productsAdapter = new SeparateProductsAdapter(this, productList);
        mGridViewPlu.setAdapter(productsAdapter);
        if (categoryList.size() > 0) {
            productList = categoryList.get(0).getProductsList();
            productsAdapter.setNewData(productList);
        }
    }

    private void initLibrary() {
        come_data.clear();
        go_data.clear();
        List<LibraryList> libraryList = libraryUtil.libraryJson("SortingArea");
        if (libraryList.size() == 0)
            return;
        comeLibraryList = libraryList.get(0).getLibraryList();
        goLibraryList = libraryList.get(1).getLibraryList();
        for (int i = 0; i < comeLibraryList.size(); i++) {
            come_data.add(comeLibraryList.get(i).getLibraryName());
        }
        for (int i = 0; i < goLibraryList.size(); i++) {
            go_data.add(goLibraryList.get(i).getLibraryName());
        }
        come_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, come_data);
        come_adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerCome.setAdapter(come_adapter);
        go_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, go_data);
        go_adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerLeave.setAdapter(go_adapter);
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
                    mSortHandler.sendEmptyMessage(1);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 判断秤稳定
     */
    private boolean isStable() {
        return mScale.getStabFlag();
    }

    private void weightChangedCyclicity() {
        if (isByWeight) {
            String strNet = mScale.getStringNet().trim();
            float tare = tareFloat;
            float fW = NumFormatUtil.isNumeric(strNet) ? Float.parseFloat(strNet) : 0;
            if (isOL()) {
                TvWeight.setText(strNet);
            } else {
                if (tareFloat > 0) {
                    fW -= tare;
                }
                TvWeight.setText(NumFormatUtil.df2.format(fW));
            }
        }
        if (isStable()) {
            //   backDisplay.showIsStable(true);
            findViewById(R.id.stableflag).setVisibility(View.VISIBLE);
        } else {
            //  backDisplay.showIsStable(false);
            findViewById(R.id.stableflag).setVisibility(View.INVISIBLE);
        }
        if (mScale.getZeroFlag()) {
            // backDisplay.showIsZero(true);
            findViewById(R.id.zeroflag).setVisibility(View.VISIBLE);
        } else {
            //    backDisplay.showIsZero(false);
            findViewById(R.id.zeroflag).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 重置tareFloat
     */
    void cleanTareFloat() {
        tareFloat = -1.0F;
    }


    private Boolean isOL() {
        return mScale.getStringNet().contains("OL");
    }

    private static class SortHandler extends Handler {

        WeakReference<SortActivity> mWeakReference;

        SortHandler(WeakReference<SortActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SortActivity activity = mWeakReference.get();
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
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.bt_sort_ok:
                weight = TvWeight.getText().toString();
                name = TvName.getText().toString();
                float weightFt = Float.parseFloat(weight);
                if (TextUtils.isEmpty(name)) {
                    showMessage("请选择品名");
                    return;
                }
                String value;
                if (isByWeight) {
                    if (weightFt <= 0) {
                        showMessage("重量不对");
                        return;
                    }
                    number = 0;
                    value = "\n重量： " + weight;
                } else {
                    number = Integer.valueOf(weight);
                    weight = "0";
                    value = "\n数量： " + number;
                }
                if (goLibrary.equals("分拣区") && !source.equals("分拣区")) {
                    showNormalDialog("品名： " + name + value + "\n去向： 从 " + source + " 到 " + goLibrary, 1);
                } else if (source.equals("分拣区")) {
                    showNormalDialog("品名： " + name + value + "\n去向： 从 " + source + " 到 " + goLibrary, 2);
                } else {
                    showMessage("选择正确的库");
                }
                break;
        }
    }

    private void submitOut() {
        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getTakeSortingArea(name, weight, String.valueOf(number), source, comeLibraryId, goLibraryId);
                if (api.Result) {
                    //   sqlInsert(1, "");
                    loadingDialog.dismissDialog();
                    showMessage(api.ResultMessage);
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setProductName(name);
                    orderDetail.setAlibraryName("苏州源丰食品有限公司");
                    orderDetail.setWeight(new BigDecimal(weight));
                    orderDetail.setNumber(number);
                    orderDetail.setBarCode(api.ResultJsonStr);
                    orderDetail.setOrderDate(NumFormatUtil.getDateDetail());
                    keyEnter(orderDetail);
                } else {
                    sqlInsert(2, "");
                    loadingDialog.dismissDialog();
                    showMessage(api.ResultMessage);
                }
            }
        });
    }

    //采购进分拣
    private void submitInto() {
        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getSaveSortingArea(submitData(), comeLibraryId, goLibraryId);
                if (api.Result) {
                    // sqlInsert(1, goLibraryId);
                    loadingDialog.dismissDialog();
                    showMessage(api.ResultMessage);
                } else {
                    sqlInsert(2, goLibraryId);
                    loadingDialog.dismissDialog();
                    showMessage(api.ResultMessage);
                }
            }
        });
    }

    private String submitData() {
        JSONObject object = new JSONObject();
        String Division = "";
        try {
            object.put("ItemName", name);
            object.put("Weight", weight);
            object.put("Source", source);
            object.put("PLU", plu);
            object.put("WeightCompany", unit);
            object.put("Number", number);
            object.put("UnitPrice", price);
            object.put("Remarks", "");
            Division = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Division;
    }

    private void sqlInsert(int state, String goId) {
        //state 1 已回收 2 未回收     接口只担任出的任务时 goId 去向库id  置为空
        OperationLog log = new OperationLog(comeLibraryId, source, goId, name, plu, mNumUtil.getDecimalNet(weight), 0, "KG", NumFormatUtil.getDateDetail(), "", state);
        logManager.insertOperationLog(log);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            mMisc.beep();
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_NUMPAD_DOT: // 一键清除
                clearEvent();
                return true;
            case KeyEvent.KEYCODE_F1:// 去皮
                cleanTareFloat();
                if (mScale.tare()) {
                    float curTare = mScale.getFloatTare();
                    TvTareWeight.setText(NumFormatUtil.df2.format(curTare));
                } else {
                    showMessage("去皮失败");
                }
                return true;
            case KeyEvent.KEYCODE_F2:// 置零
                if (mScale.zero()) {
                    cleanTareFloat();
//                    lastWeight = 0.0F;
//                    currWeight = 0.0F;
                    TvTareWeight.setText(R.string.base_weight);
                } else {
                    showMessage("置零失败");
                }
                return true;

            case KeyEvent.KEYCODE_NUMPAD_DIVIDE:// 取消
                clearProductInfo();
                loadingDialog.dismissDialog();
                return true;
            case KeyEvent.KEYCODE_NUMPAD_MULTIPLY: // 计件计重切换
                toggleIsWeight();
                return true;
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
        String iszero = TvWeight.getText().toString().trim();
        boolean wzero = iszero.equals("0");
        if (wzero) {
            TvWeight.setText(num);
        } else {
            if (iszero.length() >= 4)
                return;
            TvWeight.setText(iszero + num);
        }
    }

    /**
     * 计件与计重判断
     */
    protected void toggleIsWeight() {
        if (isByWeight) {
            cleanTareFloat();
            isByWeight = false;
            TvWeightType.setText("计件");
            unit = "袋";
            TvWeightType.setTextColor(Color.RED);
            TvWeight.setText("0");
        } else {
            isByWeight = true;
            TvWeightType.setText("重量   KG");
            unit = "KG";
            TvWeightType.setTextColor(Color.WHITE);
        }
    }

    private void clearProductInfo() {
        TvName.setText("");
        cleanTareFloat();
        TvWeightType.setTextColor(Color.WHITE);
        TvWeightType.setText("计重   KG");
        isByWeight = true;
    }

    private void clearEvent() {
        if (!isByWeight)
            TvWeight.setText("0");
    }

    @Override
    protected void onResume() {
        mMaxUnitView.setText(String.valueOf(mScale.getMainUnitFull()) + "kg");
        super.onResume();
    }

    /*
     *  信息确认提示
     */
    private void showNormalDialog(String weightContent, final int flag) {

        new PromptDialog(this, R.style.Dialog, weightContent, new PromptDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    loadingDialog.showDialog();
                    mMisc.beep();
                    switch (flag) {
                        case 1:
                            submitInto();
                            break;
                        case 2:
                            submitOut();
                            break;
                    }
                    dialog.dismiss();
                } else {
                    mMisc.beep();
                }
            }
        }).setTitle("提示").show();
    }

    //接收电量消息 每半小时一次
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveBattery(BatteryMsg msg) {

        if (msg != null) {

            int level = msg.getLevel();

            switch (level) {
                case 4:
                    mBatteryIv.setImageResource(R.drawable.four_electric);
                    break;
                case 3:
                    mBatteryIv.setImageResource(R.drawable.three_electric);
                    break;
                case 2:
                    mBatteryIv.setImageResource(R.drawable.two_electric);
                    break;
                case 1:
                    mBatteryIv.setImageResource(R.drawable.one_electric);
                    break;
                case 0:
                    mBatteryIv.setImageResource(R.drawable.need_charge);
                    new AlertDialog.Builder(SortActivity.this).setTitle(R.string.test_clear_title)
                            .setMessage("电子秤电量即将耗完，请及时连接充电器！")
                            .setPositiveButton(R.string.reprint_ok, null).show();
                    break;
            }
        }
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

//    private BroadcastReceiver mPrintReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(GpCom.ACTION_DEVICE_REAL_STATUS)) {
//                int requestCode = intent.getIntExtra(GpCom.EXTRA_PRINTER_REQUEST_CODE, -1);
//                if (requestCode == 255) {
//                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
//                    String str;
//                    LogUtil.d("BroadcastReceiver RECEIVE " + requestCode + "||||" + status);
//                    if (status == GpCom.STATE_NO_ERR) {
//                        AppConfig.isUseGpPrinter = true;
//                    } else {
//                        str = "打印机 ";
//                        if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
//                            str += "脱机";
//                        }
//                        if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
//                            str += "缺纸";
//                        }
//                        if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
//                            str += "开盖";
//                        }
//                        if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
//                            str += "出错";
//                        }
//                        if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0) {
//                            str += "查询超时";
//                        }
//                        AppConfig.isUseGpPrinter = false;
//                        showMessage(str);
//                    }
//                }
//            }
//        }
//    };

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
                    mPrinterView.setVisibility(View.INVISIBLE);
                    AppConfig.isUseGpPrinter = false;
                } else if (type == GpDevice.STATE_VALID_PRINTER) {
                    showMessage("打印机已连接");
                    mPortParam.setPortOpenState(true);
                    mPrinterView.setVisibility(View.VISIBLE);
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
        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver, timeFilter);

        //  registerReceiver(mPrintReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
        IntentFilter filter = new IntentFilter();
        filter.addAction(GpCom.ACTION_CONNECT_STATUS);
        registerReceiver(PrinterStatusBroadcastReceiver, filter);
        IntentFilter dateFilter = new IntentFilter();
        dateFilter.addAction("com.seray.scale.DATE_CHANGED");
        // registerReceiver(dateReceiver, dateFilter);
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
     * 准备打印
     */
    private void keyEnter(OrderDetail detail) {
        GpScalePrinter.getInstance(mGpService).printOrder(detail, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false;
        unregisterReceiver(timeReceiver);
        EventBus.getDefault().unregister(this);
        mSortHandler.removeCallbacksAndMessages(null);
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
        Intent intent = new Intent(this, BatteryService.class);
        stopService(intent);
        //   unregisterReceiver(mPrintReceiver);
        unregisterReceiver(PrinterStatusBroadcastReceiver);
        AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        am.cancel(pi);
        mPortParam = null;
        // android.os.Process.killProcess(android.os.Process.myPid());
    }
}
