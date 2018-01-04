package com.seray.pork;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.seray.adapter.SeparateAdapter;
import com.seray.adapter.SeparateProductsAdapter;
import com.seray.entity.ApiResult;
import com.seray.entity.Library;
import com.seray.entity.LibraryList;
import com.seray.entity.MonitorLibraryMessage;
import com.seray.entity.MonitorProdctsMessage;
import com.seray.entity.OperationLog;
import com.seray.entity.Products;
import com.seray.entity.ProductsCategory;
import com.seray.http.UploadDataHttp;
import com.seray.pork.dao.LibraryManager;
import com.seray.pork.dao.OperationLogManager;
import com.seray.pork.dao.ProductsCategoryManager;
import com.seray.pork.dao.ProductsManager;
import com.seray.utils.LibraryUtil;
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
import java.util.List;

/**
 * 鲜品库（余料）
 */
public class ExcessStockActivity extends BaseActivity {
    private TextView TvName, TvWeight, TvTareWeight,TvWeightType;
    private TextView mMaxUnitView, mTimeView;
    private Spinner spinnerCome, spinnerLeave;
    private List<String> come_data = new ArrayList<>();
    private List<String> go_data = new ArrayList<>();
    private ArrayAdapter<String> come_adapter;
    private ArrayAdapter<String> go_adapter;
    private List<Library> comeLibraryList = new ArrayList<>();
    private List<Library> goLibraryList = new ArrayList<>();

    private Button submitIntoBt, submiitOutBt;
    private GridView mGridViewPlu;
    private ListView groupListView;

    private float tareFloat = -1.0F;
    private boolean isByWeight = true;
    private JNIScale mScale;
    private boolean flag = true;
    private ExcessStockHandler mExcessStockHandler = new ExcessStockHandler(new WeakReference<>(this));

    private List<ProductsCategory> categoryList = new ArrayList<>();
    private List<Products> productList;
    SeparateAdapter separateAdapter;
    SeparateProductsAdapter productsAdapter;

    private String name, plu="", unit, price,weight;
    private int number;
    private String source,comeLibraryId,goLibraryId, goLibrary;

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
        setContentView(R.layout.activity_excess_stock);
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
        TvName = (TextView) findViewById(R.id.tv_excess_stock_name);
        TvWeight = (TextView) findViewById(R.id.tv_excess_stock_weight);
        TvTareWeight = (TextView) findViewById(R.id.tv_excess_stock_tare_weight);
        TvWeightType = (TextView) findViewById(R.id.tv_excess_stock_weight_type);
        submitIntoBt = (Button) findViewById(R.id.bt_excess_stock_into);
        submiitOutBt = (Button) findViewById(R.id.bt_excess_stock_out);

        mGridViewPlu = (GridView) findViewById(R.id.gv_excess_stock_plu);
        groupListView = (ListView) findViewById(R.id.lv_excess_stock_group);

        groupListView.setItemsCanFocus(true);// 让ListView的item获得焦点
        groupListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// 单选模式

        spinnerCome = (Spinner) findViewById(R.id.spinner_excess_stock_come);
        spinnerCome.setGravity(Gravity.CENTER);
        spinnerLeave = (Spinner) findViewById(R.id.spinner_excess_stock_leave);
        spinnerLeave.setGravity(Gravity.CENTER);
        mMaxUnitView = (TextView) findViewById(R.id.maxUnit);
        mTimeView = (TextView) findViewById(R.id.timer);
        mTimeView.setText(NumFormatUtil.getFormatDate());
        setPieceNum(TvWeight);
    }

    public void register() {
        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver, timeFilter);
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

        submitIntoBt.setOnClickListener(this);
        submiitOutBt.setOnClickListener(this);

        mGridViewPlu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                TvName.setText(productList.get(position).getProductName());
                plu= productList.get(position).getProductName();
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
        List<LibraryList> libraryList = libraryUtil.libraryJson("Inventory");
        if (libraryList.size()==0)
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
                    mExcessStockHandler.sendEmptyMessage(1);

                    try {
                        Thread.sleep(100);
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
                TvWeight.setText(NumFormatUtil.df3.format(fW));
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

    private static class ExcessStockHandler extends Handler {

        WeakReference<ExcessStockActivity> mWeakReference;

        ExcessStockHandler(WeakReference<ExcessStockActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ExcessStockActivity activity = mWeakReference.get();
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
            case R.id.bt_excess_stock_into:
                weight= TvWeight.getText().toString();
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
                    weight = "0.000";
                    number = Integer.valueOf(weight);
                    value = "\n数量： " + number;
                }
                    if (!source.equals("鲜品库")&&goLibrary.equals("鲜品库")) {
                        showNormalDialog("品名： " + name +  value+ "\n去向： 从 " + source + " 到 " + goLibrary, 1);
                    }else if (source.equals("鲜品库")&&!goLibrary.equals("鲜品库")){
                        showNormalDialog("品名： " + name + value+ "\n去向： 从 " + source + " 到 " + goLibrary, 2);
                    } else {
                        showMessage("选择正确的来源去向");
                    }

                break;
//            case R.id.bt_excess_stock_out:
//                name = TvName.getText().toString();
//                weight = TvWeight.getText().toString();
//                float weightFt2 = Float.parseFloat(weight);
//                if (!TextUtils.isEmpty(name) && weightFt2 > 0) {
//                    if (source.equals("鲜品库")&&!goLibrary.equals("鲜品库")) {
//                        loadingDialog.showDialog();
//                         submitOut();
//                    } else
//                        showMessage("选择正确的出库名");
//                } else
//                    showMessage("品名不能为空、重量需大于零");
//                break;
        }
    }

    private void submitInto() {
        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getSaveInventory(submitData(), comeLibraryId, goLibraryId, goLibrary);
                if (api.Result) {
                 //   sqlInsert(1);
                    loadingDialog.dismissDialog();
                    showMessage(api.ResultMessage);
                } else {
                    sqlInsert(2);
                    loadingDialog.dismissDialog();
                    showMessage(api.ResultMessage);
                }
            }
        });
    }

    private void submitOut() {
        sqlQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getOutInventory(comeLibraryId, source, goLibraryId, name, weight);
                if (api.Result) {
                    sqlInsert(1);
                    loadingDialog.dismissDialog();
                    showMessage(api.ResultMessage);
                } else {
                    sqlInsert(2);
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
            object.put("UnitPrice", price);
            object.put("Remarks", "");

            Division = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Division;
    }
    private void sqlInsert(int state) {
        //state 1 已回收 2 未回收
        OperationLog log = new OperationLog(comeLibraryId, source,goLibraryId, name, plu, mNumUtil.getDecimalNet(weight), 0, "KG", NumFormatUtil.getDateDetail(), state);
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
//                lastWeight = 0.0F;
//                currWeight = 0.0F;
                if (mScale.tare()) {
                    float curTare = mScale.getFloatTare();
//                    if (CacheHelper.isOpenJin)
//                        curTare *= 2;
                    TvTareWeight.setText(NumFormatUtil.df3.format(curTare));
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
            TvWeightType.setTextColor(Color.RED);
            TvWeight.setText("0");
        } else {
            isByWeight = true;
            TvWeightType.setText("重量   KG");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false;
        unregisterReceiver(timeReceiver);
        EventBus.getDefault().unregister(this);
        mExcessStockHandler.removeCallbacksAndMessages(null);
    }
}