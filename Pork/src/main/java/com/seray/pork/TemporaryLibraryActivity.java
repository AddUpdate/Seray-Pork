package com.seray.pork;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 白条库
 */

public class TemporaryLibraryActivity extends BaseActivity {

    private JNIScale mScale;
    private TextView tvWeight, tvName, TvTareWeight;
    private TextView mMaxUnitView, mTimeView;
    private Button initLibraryBt;
    private Spinner spinnerCome, spinnerLeave;
    private List<String> come_data = new ArrayList<>();
    private List<String> go_data = new ArrayList<>();
    private ArrayAdapter<String> come_adapter;
    private ArrayAdapter<String> go_adapter;
    private List<Library> comeLibraryList = new ArrayList<>();
    private List<Library> goLibraryList = new ArrayList<>();

    private List<ProductsCategory> categoryList = new ArrayList<>();
    private List<Products> productList;

    private GridView mGridViewPlu;
    private ListView groupListView;

    private String comeLibraryId;
    private String goLibraryId;
    private String goLibrary;
    private String name = "";
    private String plu = "";
    private String weight = "";
    private String source;
    private boolean flag = true;
    private TemporaryHandler mTemporaryHandler = new TemporaryHandler(new WeakReference<>(this));

    SeparateAdapter separateAdapter;
    SeparateProductsAdapter productsAdapter;

    OperationLogManager logManager = OperationLogManager.getInstance();
    private NumFormatUtil mNumUtil = null;
    LoadingDialog loadingDialog;
    LibraryUtil libraryUtil = new LibraryUtil(this);

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLibrary(MonitorLibraryMessage msg) {
//        libraryList.clear();
//        libraryList = msg.getList();
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
        setContentView(R.layout.activity_temporary_library);
        EventBus.getDefault().register(this);
        initView();
        initJNI();
        initData();
        register();
        initAdapter();
        initListener();
        initLibrary();
        timer();
    }

    private void initView() {
        loadingDialog = new LoadingDialog(this);
        tvWeight = (TextView) findViewById(R.id.tv_temporary_weight);
        tvName = (TextView) findViewById(R.id.tv_temporary_name);
        TvTareWeight = (TextView) findViewById(R.id.tv_temporary_tare_weight);
        initLibraryBt = (Button) findViewById(R.id.bt_temporary_into_libraries);
        mGridViewPlu = (GridView) findViewById(R.id.gv_temporary_plu);
        groupListView = (ListView) findViewById(R.id.lv_temporary_group);
        groupListView.setItemsCanFocus(true);// 让ListView的item获得焦点
        groupListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// 单选模式
        mGridViewPlu.setItemChecked(1, true);
        mGridViewPlu.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        spinnerCome = (Spinner) findViewById(R.id.spinner_temporary_come);
        spinnerCome.setGravity(Gravity.CENTER);
        spinnerLeave = (Spinner) findViewById(R.id.spinner_temporary_leave);
        spinnerLeave.setGravity(Gravity.CENTER);

        mMaxUnitView = (TextView) findViewById(R.id.maxUnit);
        mTimeView = (TextView) findViewById(R.id.timer);
        mTimeView.setText(NumFormatUtil.getFormatDate());

    }

    private void initData() {
        mNumUtil = NumFormatUtil.getInstance();
        ProductsCategoryManager pCaManager = ProductsCategoryManager.getInstance();
        ProductsManager productsManager = ProductsManager.getInstance();
        List<ProductsCategory> mCategoryList = pCaManager.queryAllProductsCategory();
        for (int i = 0; i < mCategoryList.size(); i++) {
            List<Products> list = productsManager.queryProductsByQueryBuilder(mCategoryList.get(i).getCategoryId());
            ProductsCategory productsCategory = new ProductsCategory(mCategoryList.get(i).getCategoryId(), mCategoryList.get(i).getCategoryName());
            LogUtil.e("productsCategory",productsCategory.toString());
            productsCategory.setProductsList(list);
            categoryList.add(productsCategory);
        }
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
        List<LibraryList> libraryList = libraryUtil.libraryJson("Loulibrary");
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

    private void initListener() {
        initLibraryBt.setOnClickListener(this);
        mGridViewPlu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                tvName.setText(productList.get(position).getProductName());
                plu = productList.get(position).getPluCode();
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
                source = comeLibraryList.get(position).getLibraryName();
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

    private void initJNI() {
        mScale = JNIScale.getScale();
    }

    /**
     * 判断秤稳定
     */
    private boolean isStable() {
        return mScale.getStabFlag();
    }

    //定时器
    private void timer() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (flag) {
                    mTemporaryHandler.sendEmptyMessage(1);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private static class TemporaryHandler extends Handler {

        WeakReference<TemporaryLibraryActivity> mWeakReference;

        TemporaryHandler(WeakReference<TemporaryLibraryActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TemporaryLibraryActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.weightChangedCyclicity();
                        break;
                }
            }
        }
    }

    private Boolean isOL() {
        return mScale.getStringNet().contains("OL");
    }

    private void weightChangedCyclicity() {
        if (true) {
            String strNet = mScale.getStringNet().trim();
            float fW = NumFormatUtil.isNumeric(strNet) ? Float.parseFloat(strNet) : 0;
            if (isOL()) {
                tvWeight.setText(strNet);
            } else {
                tvWeight.setText(NumFormatUtil.df3.format(fW));
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

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.bt_temporary_into_libraries:
                name = tvName.getText().toString();
                weight = tvWeight.getText().toString();
                if (!TextUtils.isEmpty(name) && new BigDecimal(weight).compareTo(new BigDecimal(0)) > 0) {
                    if (source.equals("采购") && goLibrary.equals("分割房")) {
                        showNormalDialog("品名： "+name+"\n重量： "+weight+"\n去向： 从 "+source+ " 到 "+goLibrary, 1);
                    } else if (source.equals("采购") && goLibrary.equals("白条库")) {
                        showNormalDialog("品名： "+name+"\n重量： "+weight+"\n去向： 从 "+source+ " 到 "+goLibrary, 2);
                    } else if (source.equals("白条库") && goLibrary.equals("鲜品库")) {
                        showNormalDialog("品名： "+name+"\n重量： "+weight+"\n去向： 从 "+source+ " 到 "+goLibrary, 3);
                    } else if (source.equals("白条库") && goLibrary.equals("分割房")) {
                        showNormalDialog("品名： "+name+"\n重量： "+weight+"\n去向： 从 "+source+ " 到 "+goLibrary, 4);
                    } else {
                        showMessage("请选择正确的来源去向");
                    }
                } else {
                    showMessage("重量或品名不符合规范");
                }
                break;
//            case R.id.bt_temporary_out_libraries:
//                name = tvName.getText().toString();
//                weight = tvWeight.getText().toString();
//
//                if (!TextUtils.isEmpty(name) && new BigDecimal(weight).compareTo(new BigDecimal(0)) > 0) {
//                    if (source.equals("白条库") && goLibrary.equals("鲜品库")) {
//                        loadingDialog.showDialog();
//                        submitTakeLibrary();
//                    } else if (source.equals("白条库") && goLibrary.equals("分割房")) {
//                        loadingDialog.showDialog();
//                        submitOutLibrary();
//                    } else {
//                        showMessage("请选择正确的来源去向");
//                    }
//                } else
//                    showMessage("重量或品名不符合规范");
        }

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

    @Override
    protected void onResume() {
        mMaxUnitView.setText(String.valueOf(mScale.getMainUnitFull()) + "kg");
        super.onResume();
    }

    /**
     * 采购进白条库
     */
    private void submitIntoLibrary() {
        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getSaveLoulibrary(submitData(), comeLibraryId, goLibraryId);
                if (api.Result) {
                    sqlInsert(1, goLibraryId);
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

    /*
     * 采购进分割库
     */
    private void submitIntoDivision() {
        sqlQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getSaveDivision(submitData(), comeLibraryId, goLibraryId);
                if (api.Result) {
                    sqlInsert(1, goLibraryId);
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

    /*
     * 出白条库 到分割
     */
    private void submitOutLibrary() {
        sqlQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getOutLoulibrary(source, comeLibraryId, goLibraryId, weight, name);
                if (api.Result) {
                    sqlInsert(1, goLibraryId);
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

    /*
     * 出白条库
     */
    private void submitTakeLibrary() {
        sqlQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getTakeLoulibrary(comeLibraryId, weight, name);
                if (api.Result) {
                    sqlInsert(1, "");
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

    private String submitData() {
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

    private void clearEvent() {
        tvName.setText("");
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
                if (mScale.tare()) {
                    float curTare = mScale.getFloatTare();
                    TvTareWeight.setText(NumFormatUtil.df3.format(curTare));
                } else {
                    showMessage("去皮失败");
                }
                return true;
            case KeyEvent.KEYCODE_F2:// 置零
                if (mScale.zero()) {
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
                            submitIntoDivision();
                            break;
                        case 2:
                            submitIntoLibrary();
                            break;
                        case 3:
                            submitTakeLibrary();
                            break;
                        case 4:
                            submitOutLibrary();
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
        EventBus.getDefault().unregister(this);
        unregisterReceiver(timeReceiver);
        mTemporaryHandler.removeCallbacksAndMessages(null);
        flag = false;
    }
}
