package com.seray.stock;

import android.app.AlertDialog;
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
import com.seray.entity.TareItem;
import com.seray.http.UploadDataHttp;
import com.seray.pork.App;
import com.seray.pork.BaseActivity;
import com.seray.pork.R;
import com.seray.pork.dao.OperationLogManager;
import com.seray.pork.dao.ProductsCategoryManager;
import com.seray.pork.dao.ProductsManager;
import com.seray.service.BatteryMsg;
import com.seray.service.BatteryService;
import com.seray.utils.FileHelp;
import com.seray.utils.LibraryUtil;
import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;
import com.seray.view.DeductTareDialog;
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
import java.util.ArrayList;
import java.util.List;

public class OtherWeightActivity extends BaseActivity {

    private ImageView mBatteryIv;
    private TextView tvName, tvWeight, tvTareWeight, tvWeightType;
    private TextView mMaxUnitView, mTimeView;
    private Button peelBt, submitBt;
    private GridView mGridViewPlu;
    private ListView groupListView;
    private String name, weight, source, plu = "", unit, unitPrice;
    private String comeLibraryId, goLibraryId, goLibrary;
    private int number, typeLibrary;
    /**
     * 手动扣重重量
     */
    private float tareFloat = -1.0F;
    private boolean isByWeight = true;
    private JNIScale mScale;
    private boolean flag = true;

    private float currWeight = 0.0f;
    private float lastWeight = 0.0f;
    private float divisionValue = 0.02f;

    private OtherWeightHandler mOtherWeightHandler = new OtherWeightHandler(new WeakReference<>(this));

    private Spinner spinnerCome, spinnerLeave,spinnerWeightType;
    private List<Library> comeLibraryList = new ArrayList<>();
    private List<Library> goLibraryList = new ArrayList<>();

    private List<String> come_data = new ArrayList<>();
    private List<String> go_data = new ArrayList<>();
    private List<String> type_data = new ArrayList<>();
    private ArrayAdapter<String> come_adapter;
    private ArrayAdapter<String> go_adapter;
    private ArrayAdapter<String> type_adapter;
    private CameraPreview mCameraPreview = null;
    private String lastImgName = null;
    private String prevRecordImgName = "";
    private boolean camerIsEnable = true;

    private List<ProductsCategory> categoryList = new ArrayList<>();
    private List<Products> productList;
    CategoryAdapter categoryAdapter;
    ProductsAdapter productsAdapter;
    OperationLogManager logManager = OperationLogManager.getInstance();
    private NumFormatUtil mNumUtil = null;
    LoadingDialog loadingDialog;
    LibraryUtil libraryUtil = new LibraryUtil(this);

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLibrary(MonitorLibraryMessage msg) {
        initLibrary();
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
        setContentView(R.layout.activity_other_weight);
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
        tvName = (TextView) findViewById(R.id.tv_other_weight_name);
        tvWeight = (TextView) findViewById(R.id.tv_other_weight_weight);
        tvTareWeight = (TextView) findViewById(R.id.tv_other_weight_tare_weight);
        tvWeightType = (TextView) findViewById(R.id.tv_other_weight_type);

        peelBt = (Button) findViewById(R.id.bt_other_weight_peel);
        submitBt = (Button) findViewById(R.id.bt_other_weight_ok);

        mGridViewPlu = (GridView) findViewById(R.id.gv_other_weight_plu);
        groupListView = (ListView) findViewById(R.id.lv_other_weight_group);

        groupListView.setItemsCanFocus(true);// 让ListView的item获得焦点
        groupListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// 单选模式
        mGridViewPlu.setItemChecked(1, true);
        mGridViewPlu.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        spinnerCome = (Spinner) findViewById(R.id.spinner_other_weight_come);
        spinnerCome.setGravity(Gravity.CENTER);
        spinnerLeave = (Spinner) findViewById(R.id.spinner_other_weight_leave);
        spinnerLeave.setGravity(Gravity.CENTER);
        spinnerWeightType = (Spinner) findViewById(R.id.spinner_other_weight_type);
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

    private void initListener() {
        peelBt.setOnClickListener(this);
        submitBt.setOnClickListener(this);

        mGridViewPlu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                tvName.setText(productList.get(position).getProductName());
                plu = productList.get(position).getPluCode();
                unitPrice = String.valueOf(productList.get(position).getUnitPrice());
                unit= productList.get(position).getUnit();
                isByWeight = unit.equals("KG") ? false : true;
                toggleIsWeight();
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
                tv.setTextSize(28.0f);    //设置大小
                unit = type_data.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initData() {
        mCameraPreview = CameraPreview.getInstance();
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
        type_data.add("袋");
        type_data.add("箱");
    }

    private void initLibrary() {
        come_data.clear();
        go_data.clear();
        List<LibraryList> libraryList = libraryUtil.libraryJson("Purchase");
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

    private void initAdapter() {

        categoryAdapter = new CategoryAdapter(this, categoryList);
        groupListView.setAdapter(categoryAdapter);
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

    @Override
    protected void onResume() {
        mMaxUnitView.setText(String.valueOf(mScale.getMainUnitFull()) + "kg");
        super.onResume();
    }

    private void initJNI() {
        mScale = JNIScale.getScale();
        divisionValue = mScale.getDivisionValue();
    }

    private void timer() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (flag) {
                    mOtherWeightHandler.sendEmptyMessage(1);
                    mOtherWeightHandler.sendEmptyMessage(5);

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

    /**
     * 判断秤稳定
     */
    private boolean isStable() {
        return mScale.getStabFlag();
    }

    private void weightChangedCyclicity() {
        if (isByWeight) {
            String strNet = mScale.getStringNet().trim();
            float fW = NumFormatUtil.isNumeric(strNet) ? Float.parseFloat(strNet) : 0;
            if (isOL()) {
                tvWeight.setText(strNet);
            } else {
                if (tareFloat > 0) {
                    fW -= tareFloat;
                }
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

    private static class OtherWeightHandler extends Handler {

        WeakReference<OtherWeightActivity> mWeakReference;

        OtherWeightHandler(WeakReference<OtherWeightActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            OtherWeightActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
//                        activity.setTareFloat();
                        activity.weightChangedCyclicity();
                        break;
                    case 5:
                        activity.lightScreenCyclicity();
                        break;
                }
            }
        }
    }

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveProdcts(MonitorProdctsMessage msg) {
        categoryList.clear();
        categoryList = msg.getList();
        categoryAdapter.setNewData(categoryList);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.bt_other_weight_peel:
                deductTareDialog();
                break;
            case R.id.bt_other_weight_ok:
                weight = tvWeight.getText().toString();
                name = tvName.getText().toString();
                if (isOL()) {
                    showMessage("超出秤量程！");
                    return;
                }
                float weightFt = Float.parseFloat(weight);

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
                    if (number <= 0) {
                        showMessage("输入大于0的件数");
                        return;
                    }
                    weight = "0";
                    value = "\n数量： " + number;
                }

                if (TextUtils.isEmpty(name)) {
                    showMessage("请选择品名");
                    return;
                }
                camera();
                showNormalDialog("品名： " + name + value + "\n去向： 从 " + source + " 到 " + goLibrary);

                break;
        }
    }
    //通用接口

    private void submitOut() {
        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiParameter apiParameter = new ApiParameter();
                apiParameter.setDataHelper(submitDataHelper());
                if (typeLibrary == 6) {
                    apiParameter.setDivision(submitDivision());
                }
                ApiResult api = UploadDataHttp.api(typeLibrary, apiParameter);
                if (api.Result) {
                    //    sqlInsert(1, goLibraryId);
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

//    //出分割到鲜品
//    private void submitOutToExcess() {
//        httpQueryThread.submit(new Runnable() {
//            @Override
//            public void run() {
//                ApiResult api = UploadDataHttp.getTakeDivision(comeLibraryId, name, weight);
//                if (api.Result) {
//                    //     sqlInsert(1, "");
//                    loadingDialog.dismissDialogs();
//                    showMessage(api.ResultMessage);
//                } else {
//                    sqlInsert(2, "");
//                    loadingDialog.dismissDialogs();
//                    showMessage(api.ResultMessage);
//                }
//            }
//        });
//    }

    private String submitDivision() {
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

    private String submitDataHelper() {
        JSONObject object = new JSONObject();
        String DataHelper = "";
        try {
            object.put("ItemName", name);
            object.put("Weight", weight);
            object.put("ComeAlibraryName", source);
            object.put("Number", number);
            object.put("PLU", plu);
            object.put("WeightCompany", unit);
            object.put("UnitPrice", unitPrice);
            object.put("ComelibraryId", comeLibraryId);
            object.put("GolibraryId", goLibraryId);
            object.put("GoAlibraryName", goLibrary);
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
                lastWeight = 0.0F;
                currWeight = 0.0F;
                if (mScale.tare()) {
                    float curTare = mScale.getFloatTare();
                    tvTareWeight.setText(NumFormatUtil.df2.format(curTare));
                } else {
                    showMessage("去皮失败");
                }
                return true;
            case KeyEvent.KEYCODE_F2:// 置零
                lastWeight = 0.0F;
                currWeight = 0.0F;
                if (mScale.zero()) {
                    cleanTareFloat();
                    tvTareWeight.setText(R.string.base_weight);
                } else {
                    showMessage("置零失败");
                }
                return true;
            case KeyEvent.KEYCODE_NUMPAD_MULTIPLY: // 计件计重切换
                toggleIsWeight();
                return true;
            case KeyEvent.KEYCODE_NUMPAD_DIVIDE:// 取消
                clearProductInfo();
                loadingDialog.dismissDialogs();
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

    /**
     * 计件与计重判断
     */
    protected void toggleIsWeight() {
        if (isByWeight) {
            cleanTareFloat();
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
        cleanTareFloat();
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

    /*
     *  信息确认提示
     */
    private void showNormalDialog(String weightContent) {
        new PromptDialog(this, R.style.Dialog, weightContent, new PromptDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    mMisc.beep();
                    loadingDialog.showDialog();
                    submitOut();
                    dialog.dismiss();
                } else {
                    mMisc.beep();
                }
            }
        }).setTitle("提示").show();
    }

    private void deductTareDialog() {
        DeductTareDialog dialog = new DeductTareDialog(OtherWeightActivity.this);
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
                    float net = Float.parseFloat(item.getNetStr());
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
                    new AlertDialog.Builder(OtherWeightActivity.this).setTitle(R.string.test_clear_title)
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
        mOtherWeightHandler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(this, BatteryService.class);
        stopService(intent);
        flag = false;
    }
}
