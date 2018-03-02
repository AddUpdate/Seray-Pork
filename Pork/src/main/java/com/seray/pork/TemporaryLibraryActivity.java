package com.seray.pork;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.camera.simplewebcam.CameraPreview;
import com.seray.adapter.CategoryAdapter;
import com.seray.adapter.ProductsAdapter;
import com.seray.entity.ApiParameter;
import com.seray.entity.ApiResult;
import com.seray.entity.Library;
import com.seray.entity.LibraryList;
import com.seray.entity.MonitorLibraryMessage;
import com.seray.entity.MonitorProdctsMessage;
import com.seray.entity.OperationLog;
import com.seray.entity.Products;
import com.seray.entity.ProductsCategory;
import com.seray.http.API;
import com.seray.http.UploadDataHttp;
import com.seray.pork.dao.OperationLogManager;
import com.seray.pork.dao.ProductsCategoryManager;
import com.seray.pork.dao.ProductsManager;
import com.seray.service.BatteryMsg;
import com.seray.service.BatteryService;
import com.seray.utils.FileHelp;
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

import java.io.File;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 白条库
 */

public class TemporaryLibraryActivity extends BaseActivity {
    private ImageView mBatteryIv;
    private JNIScale mScale;
    private float currWeight = 0.0f;
    private float lastWeight = 0.0f;
    private float divisionValue = 0.02f;

    private TextView tvWeight, tvName, tvTareWeight,tvWeightType;
    private TextView mMaxUnitView, mTimeView;
    private Button initLibraryBt;
    private Spinner spinnerCome, spinnerLeave,spinnerWeightType;
    private List<String> come_data = new ArrayList<>();
    private List<String> go_data = new ArrayList<>();
    private List<String> type_data = new ArrayList<>();
    private ArrayAdapter<String> come_adapter;
    private ArrayAdapter<String> go_adapter;
    private ArrayAdapter<String> type_adapter;
    private List<Library> comeLibraryList = new ArrayList<>();
    private List<Library> goLibraryList = new ArrayList<>();

    private List<ProductsCategory> categoryList = new ArrayList<>();
    private List<Products> productList;

    private GridView mGridViewPlu;
    private ListView groupListView;

    private String comeLibraryId, goLibraryId, goLibrary, name = "", plu = "", weight = "", source, unit = "KG", unitPrice;
    private int typeLibrary, number;
    private boolean flag = true;
    private boolean isByWeight = true;
    private TemporaryHandler mTemporaryHandler = new TemporaryHandler(new WeakReference<>(this));

    CategoryAdapter separateAdapter;
    ProductsAdapter productsAdapter;

    private CameraPreview mCameraPreview = null;
    private String lastImgName = null;
    private String prevRecordImgName = "";
    private boolean camerIsEnable = true;

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

    private void lightScreenCyclicity() {
        float w = mScale.getFloatNet();
        if (isOL() || Math.abs(w - lastWeight) > divisionValue) {
            App.getApplication().openScreen();
        }
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
        tvTareWeight = (TextView) findViewById(R.id.tv_temporary_tare_weight);
        tvWeightType = (TextView) findViewById(R.id.tv_temporary_weight_type);
        spinnerWeightType = (Spinner) findViewById(R.id.spinner_temporary_weight_type);
        initLibraryBt = (Button) findViewById(R.id.bt_temporary_into_libraries);
        mGridViewPlu = (GridView) findViewById(R.id.gv_temporary_plu);
        groupListView = (ListView) findViewById(R.id.lv_temporary_group);
        groupListView.setItemsCanFocus(true);// 让ListView的item获得焦点
        groupListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// 单选模式
        mGridViewPlu.setItemChecked(1, true);
        mGridViewPlu.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mBatteryIv = (ImageView) findViewById(R.id.battery);

        spinnerCome = (Spinner) findViewById(R.id.spinner_temporary_come);
        spinnerCome.setGravity(Gravity.CENTER);
        spinnerLeave = (Spinner) findViewById(R.id.spinner_temporary_leave);
        spinnerLeave.setGravity(Gravity.CENTER);

        mMaxUnitView = (TextView) findViewById(R.id.maxUnit);
        mTimeView = (TextView) findViewById(R.id.timer);
        mTimeView.setText(NumFormatUtil.getFormatDate());
        setPieceNum(tvWeight);
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

    private void initData() {
        Intent intent = new Intent(this, BatteryService.class);
        startService(intent);
        mCameraPreview = CameraPreview.getInstance();
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
        type_data.add("袋");
        type_data.add("箱");
    }

    private void initAdapter() {

        separateAdapter = new CategoryAdapter(this, categoryList);
        groupListView.setAdapter(separateAdapter);
        groupListView.setItemChecked(0, true);
        productsAdapter = new ProductsAdapter(this, productList);
        mGridViewPlu.setAdapter(productsAdapter);
        if (categoryList.size() > 0) {
            productList = categoryList.get(0).getProductsList();
            productsAdapter.setNewData(productList);
        }
        type_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type_data);
        type_adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerWeightType.setAdapter(type_adapter);
    }

    private void initLibrary() {
        come_data.clear();
        go_data.clear();
        List<LibraryList> libraryList = libraryUtil.libraryJson("Loulibrary");
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

    private void initListener() {
        initLibraryBt.setOnClickListener(this);
        mGridViewPlu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                tvName.setText(productList.get(position).getProductName());
                plu = productList.get(position).getPluCode();
                unitPrice = String.valueOf(productList.get(position).getUnitPrice());
                unit = productList.get(position).getUnit();
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
                source = comeLibraryList.get(position).getLibraryName();
                typeLibrary = comeLibraryList.get(position).getType();
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
       spinnerWeightType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               TextView tv = (TextView) view;
               tv.setTextColor(getResources().getColor(R.color.red));    //设置颜色
               tv.setTextSize(30.0f);    //设置大小
               unit = type_data.get(position);
           }
           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });
    }

    private void initJNI() {
        mScale = JNIScale.getScale();
        divisionValue = mScale.getDivisionValue();
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
                    mTemporaryHandler.sendEmptyMessage(5);

                    currWeight = mScale.getFloatNet();

                    if (isStable()) {
                        lastWeight = currWeight;
                    }
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
                    case 5:
                        activity.lightScreenCyclicity();
                        break;
                }
            }
        }
    }

    private Boolean isOL() {
        return mScale.getStringNet().contains("OL");
    }

    private void weightChangedCyclicity() {
        if (isByWeight) {
            String strNet = mScale.getStringNet().trim();
            float fW = NumFormatUtil.isNumeric(strNet) ? Float.parseFloat(strNet) : 0;
            if (isOL()) {
                tvWeight.setText(strNet);
            } else {
                tvWeight.setText(NumFormatUtil.df2.format(fW));
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
                if (isOL()) {
                    showMessage("超出秤量程！");
                    return;
                }
                camera();
                if (!TextUtils.isEmpty(name)) {
                    String value;
                    if (isByWeight) {
                        if (new BigDecimal(weight).compareTo(new BigDecimal(0)) <= 0) {
                            showMessage("重量不对");
                            return;
                        }
                        number = 0;
                        value = "\n重量： " + weight;
                    } else {
                        number = Integer.valueOf(weight);
                        if (number <= 0) {
                            showMessage("数量需大于0！");
                            return;
                        }
                        weight = "0.00";
                        value = "\n数量： " + number;
                    }
                    showNormalDialog("品名： " + name + value + "\n去向： 从 " + source + " 到 " + goLibrary, 4);
                } else {
                    showMessage("重量或品名不符合规范");
                }
                break;
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

    private void camera() {
        //生成本地设置  是否开启拍照
        if (true) {
            if (mCameraPreview != null && camerIsEnable) {
                camerIsEnable = false;
                try {
                    String dir = FileHelp.getImgDir(), currImgName = FileHelp
                            .getImgName();
                    currImgName = mCameraPreview.taskPic(currImgName);
                    if (lastImgName != null) {
                        File file = new File(dir + lastImgName);
                        if (file.exists())
                            file.delete();
                    }
                    lastImgName = currImgName;
                    prevRecordImgName = lastImgName;
                } catch (Exception e) {
                    prevRecordImgName = "";
                }
                camerIsEnable = true;
            }
        }
    }

    /**
     * 获取拍照图片
     */
    String getCameraPic() {
        // 如果当前关闭静默拍照
//        if (!CacheHelper.isOpenCamera) {
//            return null;
//        }
        // 当前记录没有触发拍照，并且是计重（非计件）模式
        if (lastImgName == null)
            return prevRecordImgName;
        return FileHelp.encodeLibraryImg(lastImgName);
    }

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
                    //  sqlInsert(1, goLibraryId);
                    loadingDialog.dismissDialogs();
                    showMessage(api.ResultMessage);
                } else {
                    sqlInsert(2, goLibraryId);
                    loadingDialog.dismissDialogs();
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
                    //   sqlInsert(1, goLibraryId);
                    loadingDialog.dismissDialogs();
                    showMessage(api.ResultMessage);
                } else {
                    sqlInsert(2, goLibraryId);
                    loadingDialog.dismissDialogs();
                    showMessage(api.ResultMessage);
                }
            }
        });
    }

    /*
     * 出白条库
     */
    private void submitOutLibrary() {
        sqlQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiParameter apiParameter = new ApiParameter();
                if (typeLibrary == 6) {
                    apiParameter.setDivision(submitOutData());
                }
                apiParameter.setDataHelper(submitData());
                ApiResult api = UploadDataHttp.api(typeLibrary, apiParameter);
                if (api.Result) {
                    //     sqlInsert(1, goLibraryId);
                    loadingDialog.dismissDialogs();
                    showMessage(api.ResultMessage);
                } else {
                    sqlInsert(2, goLibraryId);
                    loadingDialog.dismissDialogs();
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
                    //      sqlInsert(1, "");
                    loadingDialog.dismissDialogs();
                    showMessage(api.ResultMessage);
                } else {
                    sqlInsert(2, "");
                    loadingDialog.dismissDialogs();
                    showMessage(api.ResultMessage);
                }
            }
        });
    }

    private String submitOutData() {
        JSONObject object = new JSONObject();
        String Division = "";
        try {
            object.put("PLU", plu);
            object.put("WeightCompany", unit);
            object.put("UnitPrice", unitPrice);
            Division = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Division;
    }

    private String submitData() {
        JSONObject object = new JSONObject();
        String DataHelper = "";
        try {
            object.put("ItemName", name);
            object.put("Weight", weight);
            object.put("Number", number);
            object.put("ComelibraryId", comeLibraryId);
            object.put("GolibraryId", goLibraryId);
            object.put("ComeAlibraryName", source);
            object.put("GoAlibraryName", goLibrary);
            object.put("PLU", plu);
            object.put("WeightCompany", unit);
            object.put("UnitPrice", unitPrice);
            object.put("Picture", getCameraPic());
            DataHelper = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return DataHelper;
    }

    private void sqlInsert(int state, String goId) {
        //state 1 已回收 2 未回收     接口只担任出的任务时 goId 去向库id  置为空
        OperationLog log = new OperationLog(comeLibraryId, source, goId, name, plu, mNumUtil.getDecimalNet(weight), 0, "KG", NumFormatUtil.getDateDetail(), "", state);
        logManager.insertOperationLog(log);
    }

    /**
     * 计件与计重判断
     */
    protected void toggleIsWeight() {
        if (isByWeight) {
            isByWeight = false;
            tvWeightType.setVisibility(View.GONE);
            spinnerWeightType.setVisibility(View.VISIBLE);
            if (unit.equals("KG")){
                unit="袋";
            }
            for (int i = 0; i < type_data.size(); i++) {
                if (unit.equals(type_data.get(i))) {
                    spinnerWeightType.setSelection(i, true);
                    break;
                }
            }
            tvWeight.setText("0");
        } else {
            tvWeightType.setVisibility(View.VISIBLE);
            spinnerWeightType.setVisibility(View.GONE);
            isByWeight = true;
            unit = "KG";
        }
    }

    private void clearProductInfo() {
        tvName.setText("");
        tvWeightType.setVisibility(View.VISIBLE);
        spinnerWeightType.setVisibility(View.GONE);
        unit = "KG";
        isByWeight = true;
    }

    private void clearEvent() {
        if (!isByWeight)
            tvWeight.setText("0");
    }

    private void unitValu(String num) {
        String iszero = tvWeight.getText().toString().trim();
        boolean wzero = iszero.equals("0");
        if (wzero) {
            tvWeight.setText(num);
        } else {
            if (iszero.length() >= 4)
                return;
            tvWeight.setText(iszero + num);
        }
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
                lastWeight = 0.0f;
                currWeight = 0.0f;
                if (mScale.tare()) {
                    float curTare = mScale.getFloatTare();
                    tvTareWeight.setText(NumFormatUtil.df2.format(curTare));
                } else {
                    showMessage("去皮失败");
                }
                return true;
            case KeyEvent.KEYCODE_F2:// 置零
                if (mScale.zero()) {
                    lastWeight = 0.0f;
                    currWeight = 0.0f;
                    tvTareWeight.setText(R.string.base_weight);
                } else {
                    showMessage("置零失败");
                }
                return true;
            case KeyEvent.KEYCODE_NUMPAD_DIVIDE:// 取消
                clearProductInfo();
                loadingDialog.dismissDialogs();
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
                    new AlertDialog.Builder(TemporaryLibraryActivity.this).setTitle(R.string.test_clear_title)
                            .setMessage("电子秤电量即将耗完，请及时连接充电器！")
                            .setPositiveButton(R.string.reprint_ok, null).show();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(timeReceiver);
        mTemporaryHandler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(this, BatteryService.class);
        stopService(intent);
        flag = false;
    }
}
