package com.seray.pork;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.seray.entity.TareItem;
import com.seray.http.UploadDataHttp;
import com.seray.pork.dao.OperationLogManager;
import com.seray.pork.dao.ProductsCategoryManager;
import com.seray.pork.dao.ProductsManager;
import com.seray.service.BatteryMsg;
import com.seray.service.BatteryService;
import com.seray.utils.LibraryUtil;
import com.seray.view.DeductTareDialog;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 分割区
 */

public class SeparateActivity extends BaseActivity {

    private ImageView mBatteryIv;
    private TextView TvName, TvWeight, TvTareWeight;
    private TextView mMaxUnitView, mTimeView;
    private Button peelBt, submitBt;
    private GridView mGridViewPlu;
    private ListView groupListView;
    private String name, weight, source, plu = "",weightCompany;
    private String comeLibraryId;
    private String goLibraryId;
    private String goLibrary;

    /**
     * 手动扣重重量
     */
    private float tareFloat = -1.0F;

    private JNIScale mScale;
    private boolean flag = true;
    private SeparateHandler mSeparateHandler = new SeparateHandler(new WeakReference<>(this));

    private Spinner spinnerCome, spinnerLeave;
    private List<Library> comeLibraryList = new ArrayList<>();
    private List<Library> goLibraryList = new ArrayList<>();

    private List<String> come_data = new ArrayList<>();
    private List<String> go_data = new ArrayList<>();
    private ArrayAdapter<String> come_adapter;
    private ArrayAdapter<String> go_adapter;


    private List<ProductsCategory> categoryList = new ArrayList<>();
    private List<Products> productList;
    SeparateAdapter separateAdapter;
    SeparateProductsAdapter productsAdapter;
    OperationLogManager logManager = OperationLogManager.getInstance();
    private NumFormatUtil mNumUtil = null;
    LoadingDialog loadingDialog;
    LibraryUtil libraryUtil = new LibraryUtil(this);

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLibrary(MonitorLibraryMessage msg) {
        initLibrary();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_separate);
        EventBus.getDefault().register(this);
        initView();
        initListener();
        initData();
        initAdapter();
        initLibrary();
        register();
        initJNI();
        timer();
    }

    private void initView() {
        loadingDialog = new LoadingDialog(this);
        mBatteryIv = (ImageView) findViewById(R.id.battery);
        TvName = (TextView) findViewById(R.id.tv_separate_name);
        TvWeight = (TextView) findViewById(R.id.tv_separate_weight);
        TvTareWeight = (TextView) findViewById(R.id.tv_separate_tare_weight);

        peelBt = (Button) findViewById(R.id.bt_separate_peel);
        submitBt = (Button) findViewById(R.id.bt_separate_ok);

        mGridViewPlu = (GridView) findViewById(R.id.gv_separate_plu);
        groupListView = (ListView) findViewById(R.id.lv_separate_group);

        groupListView.setItemsCanFocus(true);// 让ListView的item获得焦点
        groupListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// 单选模式
        mGridViewPlu.setItemChecked(1, true);
        mGridViewPlu.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        spinnerCome = (Spinner) findViewById(R.id.spinner_separate_come);
        spinnerCome.setGravity(Gravity.CENTER);
        spinnerLeave = (Spinner) findViewById(R.id.spinner_separate_leave);
        spinnerLeave.setGravity(Gravity.CENTER);

        mMaxUnitView = (TextView) findViewById(R.id.maxUnit);
        mTimeView = (TextView) findViewById(R.id.timer);
        mTimeView.setText(NumFormatUtil.getFormatDate());
    }

    private void initListener() {
        peelBt.setOnClickListener(this);
        submitBt.setOnClickListener(this);

        mGridViewPlu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                TvName.setText(productList.get(position).getProductName());
                plu = productList.get(position).getPluCode();
            }
        });
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                //  productsCategory = categoryList.get(position);
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
                source = come_data.get(position);
                comeLibraryId = comeLibraryList.get(position).getLibraryId();
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
                goLibrary = goLibraryList.get(position).getLibraryName();
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
        //  comeLibraryList = manager.queryAllLibrary();
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

    private void initLibrary() {
        come_data.clear();
        go_data.clear();
        List<LibraryList> libraryList = libraryUtil.libraryJson("Division");
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

    @Override
    protected void onResume() {
        mMaxUnitView.setText(String.valueOf(mScale.getMainUnitFull()) + "kg");
        super.onResume();
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
                    mSeparateHandler.sendEmptyMessage(1);
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
        if (true) {
            String strNet = mScale.getStringNet().trim();
            float fW = NumFormatUtil.isNumeric(strNet) ? Float.parseFloat(strNet) : 0;
            if (isOL()) {
                TvWeight.setText(strNet);
            } else {
                if (tareFloat > 0) {
                    fW -= tareFloat;
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

    private Boolean isOL() {
        return mScale.getStringNet().contains("OL");
    }

    private static class SeparateHandler extends Handler {

        WeakReference<SeparateActivity> mWeakReference;

        SeparateHandler(WeakReference<SeparateActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SeparateActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
//                        activity.setTareFloat();
                        activity.weightChangedCyclicity();
                        break;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveProdcts(MonitorProdctsMessage msg) {
        categoryList.clear();
        categoryList = msg.getList();
        separateAdapter.setNewData(categoryList);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.bt_separate_peel:
                deductTareDialog();
                break;
            case R.id.bt_separate_ok:
                weight = TvWeight.getText().toString();
                name = TvName.getText().toString();
                float weightFt = Float.parseFloat(weight);

                if (!TextUtils.isEmpty(name) && weightFt > 0)
                    if (goLibrary.equals("分拣区")) {
                        showNormalDialog("品名： " + name + "\n重量： " + weight + "\n去向： 从 " + source + " 到 " + goLibrary, 1);
                    } else if (goLibrary.equals("鲜品库")) {
                        showNormalDialog("品名： " + name + "\n重量： " + weight + "\n去向： 从 " + source + " 到 " + goLibrary, 2);
                    } else {
                        showMessage("选择正确的去向地");
                    }
                else {
                    showMessage("品名不能为空、重量需大于零");
                }
                break;
        }
    }

    //出分割到分拣
    private void submitOut() {
        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getOutDivision(submitOutData(), source, comeLibraryId, goLibraryId);
                if (api.Result) {
                //    sqlInsert(1, goLibraryId);
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

    //出分割到鲜品
    private void submitOutToExcess() {
        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getTakeDivision(comeLibraryId, name, weight);
                if (api.Result) {
               //     sqlInsert(1, "");
                    loadingDialog.dismissDialog();
                    showMessage(api.ResultMessage);
                } else {
                    sqlInsert(2, "");
                    loadingDialog.dismissDialog();
                    showMessage(api.ResultMessage);
                }
            }
        });
    }

    private String submitOutData() {
        JSONObject object = new JSONObject();
        String Division = "";
        try {
            object.put("ItemName", name);
            object.put("Weight", weight);
            object.put("Source", source);
            object.put("PLU", plu);
            object.put("WeightCompany", "KG");
            object.put("UnitPrice", "0");
            object.put("Remarks", "");
            Division = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Division;
    }

    private void sqlInsert(int state, String goId) {
        //state 1 已回收 2 未回收     接口只担任出的任务时 goId 去向库id  置为空
        OperationLog log = new OperationLog(comeLibraryId, source, goId, name, plu, mNumUtil.getDecimalNet(weight), 0, "KG", NumFormatUtil.getDateDetail(), "",state);
        logManager.insertOperationLog(log);
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
                    TvTareWeight.setText(R.string.base_weight);
                } else {
                    showMessage("置零失败");
                }
                return true;

            case KeyEvent.KEYCODE_NUMPAD_DIVIDE:// 取消
                loadingDialog.dismissDialog();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void clearEvent() {
        TvName.setText("");
        cleanTareFloat();
    }

       /*
        *  信息确认提示
        */
    private void showNormalDialog(String weightContent, final int flag) {
        new PromptDialog(this, R.style.Dialog, weightContent, new PromptDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    mMisc.beep();
                    loadingDialog.showDialog();
                    switch (flag) {
                        case 1:
                            submitOut();
                            break;
                        case 2:
                            submitOutToExcess();
                            break;
                    }
                    dialog.dismiss();
                } else {
                    mMisc.beep();
                }
            }
        }).setTitle("提示").show();
    }

    private void deductTareDialog() {
        DeductTareDialog dialog = new DeductTareDialog(SeparateActivity.this);
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
                    new AlertDialog.Builder(SeparateActivity.this).setTitle(R.string.test_clear_title)
                            .setMessage("电子秤电量即将耗完，请及时连接充电器！")
                            .setPositiveButton(R.string.reprint_ok, null).show();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timeReceiver);
        EventBus.getDefault().unregister(this);
        mSeparateHandler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(this, BatteryService.class);
        stopService(intent);
        flag = false;
    }

}
