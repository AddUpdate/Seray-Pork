package com.seray.pork;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.seray.cache.ScanGunKeyEventHelper;
import com.seray.utils.FileHelp;
import com.camera.simplewebcam.CameraPreview;
import com.seray.entity.ApiResult;
import com.seray.entity.Library;
import com.seray.entity.LibraryList;
import com.seray.entity.MonitorLibraryMessage;
import com.seray.entity.OperationLog;
import com.seray.http.UploadDataHttp;
import com.seray.pork.dao.OperationLogManager;

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
 * 速冻库
 */
public class FrozenLibraryActivity extends BaseActivity implements ScanGunKeyEventHelper.OnScanSuccessListener{
    private TextView tareWeightTv, weightTv, nameTv, standardTv, numberTv;
    private TextView mMaxUnitView, mTimeView;
    private EditText numberEt, codeEt;
    private Button intoBt, outBt;
    private List<String> come_data = new ArrayList<>();
    private List<String> go_data = new ArrayList<>();
    private ArrayAdapter<String> come_adapter;
    private ArrayAdapter<String> go_adapter;
    private List<Library> comeLibraryList = new ArrayList<>();
    private List<Library> goLibraryList = new ArrayList<>();
    private Spinner comeSpinner, leaveSpinner;
    private JNIScale mScale;
    private boolean flag = true;
    private FrozenHandler mFrozenHandler = new FrozenHandler(new WeakReference<>(this));
    private String comeLibraryId;
    private String comeLibrary;
    private String goLibraryId;
    private String goLibrary;
    private String codeData, weightData, numberData = "", name, standard, plu = "",picture;
    LibraryUtil libraryUtil = new LibraryUtil(this);
    private CameraPreview mCameraPreview = null;
    private String lastImgName = null;
    private String prevRecordImgName = null;
    private boolean camerIsEnable = true;
    OperationLogManager logManager = OperationLogManager.getInstance();
    LoadingDialog loadingDialog;
    ScanGunKeyEventHelper scanGunKeyEventHelper = null;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLibrary(MonitorLibraryMessage msg) {
        initLibrary();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frozen_library);
        EventBus.getDefault().register(this);
        initView();
        initJNI();
        initListener();
        initLibrary();
        register();
        // timer();
    }

    private void initJNI() {
        mScale = JNIScale.getScale();
    }

    private void initView() {
        mCameraPreview = CameraPreview.getInstance();
        loadingDialog = new LoadingDialog(this);
        nameTv = (TextView) findViewById(R.id.tv_frozen_library_name);
        standardTv = (TextView) findViewById(R.id.tv_frozen_library_standard);
        numberTv = (TextView) findViewById(R.id.tv_frozen_library_number);
        codeEt = (EditText) findViewById(R.id.et_frozen_library_code);
        codeEt.setInputType(InputType.TYPE_NULL);
        tareWeightTv = (TextView) findViewById(R.id.tv_frozen_library_tare_weight);
        weightTv = (TextView) findViewById(R.id.tv_frozen_library_weight);
        numberEt = (EditText) findViewById(R.id.et_frozen_library_number);
        numberEt.setInputType(InputType.TYPE_NULL);
        numberEt.setCursorVisible(false);
        intoBt = (Button) findViewById(R.id.bt_frozen_library_into);
        outBt = (Button) findViewById(R.id.bt_frozen_library_out);
        comeSpinner = (Spinner) findViewById(R.id.spinner_frozen_come);
        leaveSpinner = (Spinner) findViewById(R.id.spinner_frozen_leave);

        mMaxUnitView = (TextView) findViewById(R.id.maxUnit);
        mTimeView = (TextView) findViewById(R.id.timer);
        mTimeView.setText(NumFormatUtil.getFormatDate());
    }

    private void initLibrary() {
        come_data.clear();
        go_data.clear();
        List<LibraryList> libraryList = libraryUtil.libraryJson("InventoryTwo");
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
        comeSpinner.setAdapter(come_adapter);
        go_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, go_data);
        go_adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        leaveSpinner.setAdapter(go_adapter);
    }

    private void initListener() {
        scanGunKeyEventHelper = new ScanGunKeyEventHelper(this);
        intoBt.setOnClickListener(this);
        outBt.setOnClickListener(this);

        comeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                tv.setTextColor(getResources().getColor(R.color.white));    //设置颜色
                tv.setTextSize(25.0f);    //设置大小
                tv.setGravity(Gravity.CENTER_HORIZONTAL);   //设置居中
                comeLibraryId = comeLibraryList.get(position).getLibraryId();
                comeLibrary = comeLibraryList.get(position).getLibraryName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        leaveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
//        codeEt.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                        codeData = codeEt.getText().toString();
//                        if (!TextUtils.isEmpty(codeData))
//                            getBarCode();
//                        return true;
//                    } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
//                        codeEt.setText("");
//                    } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DIVIDE) {
//                        clearEvent();
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
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

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.bt_frozen_library_into:
                //    weightData = weightTv.getText().toString();
                //   numberData = numberEt.getText().toString();
                camera();
                if (!TextUtils.isEmpty(codeData)) {
                    //    if (comeLibrary.equals("采购")||comeLibrary.equals("分拣区")||comeLibrary.equals("速冻库")){
                    showNormalDialog("序号：" + codeData + "\n品名：" + name + "\n规格：" + standard);
                    //    }else {
                    //出库
                    //  }
                } else {
                    showMessage("信息不完整");
                }
                break;
        }
    }

    private void submitIntoLibrary() {

        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("getPirture-----",getCameraPic());
                ApiResult api = UploadDataHttp.getSaveFreeze(codeData, comeLibraryId, comeLibrary, goLibraryId, goLibrary,getCameraPic()==null?"":getCameraPic());
                showMessage(api.ResultMessage);
                if (api.Result) {
                    mFrozenHandler.sendEmptyMessage(3);
                    sqlInsert(1);
                    loadingDialog.dismissDialog();
                } else {
                    sqlInsert(2);
                    loadingDialog.dismissDialog();
                }
            }
        });
    }

    private void sqlInsert(int state) {
        //state 1 已回收 2 未回收
        OperationLog log = new OperationLog(comeLibraryId, comeLibrary, goLibraryId, name, plu, new BigDecimal(weightData), numberData.equals("") ? 0 : Integer.valueOf(numberData), standard, NumFormatUtil.getDateDetail(),getCameraPic(), state);
        logManager.insertOperationLog(log);
    }

    private void getBarCode() {
        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getBarCodeContent(codeData);
                if (api.Result) {
                    JSONObject obj;
                    try {
                        obj = new JSONObject(api.ResultJsonStr);
                        JSONObject object = obj.getJSONObject("Result");
                        name = object.getString("ItemName");
                        standard = object.getString("Weight");//需要一份对应品名的规格表
                        weightData = object.getString("Weight");
                        numberData = object.getString("Number");
                        plu = object.getString("PLU");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mFrozenHandler.sendEmptyMessage(2);
                } else {
                    showMessage(api.ResultMessage);
                }
            }
        });
    }

    private String submitData() {
        JSONObject object = new JSONObject();
        String Division = "";
        try {
            object.put("Weight", weightData);
            object.put("Number", numberData);
            Division = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Division;
    }

    @Override
    protected void onResume() {
        mMaxUnitView.setText(String.valueOf(mScale.getMainUnitFull()) + "kg");
        super.onResume();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            mMisc.beep();
        }
        switch (keyCode) {

            case KeyEvent.KEYCODE_F1:// 去皮
                if (mScale.tare()) {
                    float curTare = mScale.getFloatTare();
                    tareWeightTv.setText(NumFormatUtil.df3.format(curTare));
                } else {
                    showMessage("去皮失败");
                }
                return true;
            case KeyEvent.KEYCODE_F2:// 置零
                if (mScale.zero()) {
                    tareWeightTv.setText(R.string.base_weight);
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int code = event.getKeyCode();
        if (code == KeyEvent.KEYCODE_BACK || code == KeyEvent.KEYCODE_F3) {
            return super.dispatchKeyEvent(event);
        } else {
            scanGunKeyEventHelper.analysisKeyEvent(event);
        }
        return true;
    }

    @Override
    public void onScanSuccess(String barcode) {
        codeData = barcode;
        LogUtil.e("codeEt", codeData);
        codeEt.setText(codeData);
        if (!TextUtils.isEmpty(codeData))
            getBarCode();
    }


    private static class FrozenHandler extends Handler {

        WeakReference<FrozenLibraryActivity> mWeakReference;

        FrozenHandler(WeakReference<FrozenLibraryActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FrozenLibraryActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
//                        activity.setTareFloat();
                        activity.weightChangedCyclicity();
                        break;
                    case 2:
//                        activity.setTareFloat();
                        activity.setViewData();
                        break;
                    case 3:
//                        activity.setTareFloat();
                        activity.clearEvent();
                        break;
                }
            }
        }
    }

    private Boolean isOL() {
        return mScale.getStringNet().contains("OL");
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
                weightTv.setText(strNet);
            } else {
                weightTv.setText(NumFormatUtil.df3.format(fW));
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

    private void setViewData() {
        nameTv.setText(name);
        standardTv.setText(standard);
        numberTv.setText(numberData);
    }

    private void clearEvent() {
        nameTv.setText("");
        standardTv.setText("");
        numberTv.setText("");
        codeEt.setText("");
        numberEt.setText("");
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

    /*
   *  信息确认提示
   */
    private void showNormalDialog(String weightContent) {

        new PromptDialog(this, R.style.Dialog, weightContent, new PromptDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    loadingDialog.showDialog();
                    mMisc.beep();
                    submitIntoLibrary();
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
        unregisterReceiver(timeReceiver);
        EventBus.getDefault().unregister(this);
        mFrozenHandler.removeCallbacksAndMessages(null);
        scanGunKeyEventHelper.onDestroy();
    }
}
