package com.seray.stock;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.camera.simplewebcam.CameraPreview;
import com.seray.entity.ApiResult;
import com.seray.entity.OperationLog;
import com.seray.entity.PurchaseDetail;
import com.seray.http.UploadDataHttp;
import com.seray.pork.App;
import com.seray.pork.BaseActivity;
import com.seray.pork.R;
import com.seray.pork.dao.OperationLogManager;
import com.seray.pork.dao.PurchaseDetailManager;
import com.seray.utils.FileHelp;
import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;
import com.seray.view.LoadingDialog;
import com.seray.view.PromptDialog;
import com.tscale.scalelib.jniscale.JNIScale;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * 确认实际重量入库 批次号列表 详情
 */

public class ConfirmGoodsDetailActivity extends BaseActivity {

    private TextView TvBatchNumber, TvName, TvWeight, TvTareWeight;
    private Button BtContinue, BtSeparate, BtSubmit, BtReturn;
    private ListView detailListView;
    ConfirmGoodsDetailAdapter adapter = null;
    //  private List<PurchaseDetail> returnList;// 接口返回值
    private List<PurchaseDetail> detailList = new ArrayList<>();
    private String batchNumber, productName, productId, recordWeight;
    private BigDecimal actualWeight;
    private String strWeight = "0";
    private String plu;
    private int state;
    private int actualNumber;
    private int position;
    private int goId = 1;//1 白条库  2 分割房
    private String goLibraryId = "";
    LoadingDialog mLoadingDialog;
    private boolean timeflag = true;
    private boolean weightFlag = false;

    private JNIScale mScale;
    private float currWeight = 0.0f;
    private float lastWeight = 0.0f;
    private float divisionValue = 0.02f;

    private CameraPreview mCameraPreview = null;
    private String lastImgName = null;
    private String prevRecordImgName = "";
    private boolean camerIsEnable = true;
    private ConfirmGoodsDetailHandler mHandler = new ConfirmGoodsDetailHandler(new
            WeakReference<>(this));
    private NumFormatUtil numUtil;
    OperationLogManager logManager = OperationLogManager.getInstance();

    private void lightScreenCyclicity() {
        float w = mScale.getFloatNet();
        if (isOL() || Math.abs(w - lastWeight) > divisionValue) {
            App.getApplication().openScreen();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_goods_detail);
        initView();
        initListener();
        initData();
        initAdapter();
        timer();
    }

    private void initView() {
        TvWeight = (TextView) findViewById(R.id.confirm_goods_detail_weight);
        TvTareWeight = (TextView) findViewById(R.id.confirm_goods_detail_tare_weight);
        TvBatchNumber = (TextView) findViewById(R.id.confirm_goods_detail_batch_number);
        TvName = (TextView) findViewById(R.id.confirm_goods_detail_batch_name);
        detailListView = (ListView) findViewById(R.id.lv_confirm_goods_detail);
        BtContinue = (Button) findViewById(R.id.confirm_goods_detail_continue);
        BtSeparate = (Button) findViewById(R.id.confirm_goods_detail_separate);
        BtSubmit = (Button) findViewById(R.id.confirm_goods_detail_ok);
        BtReturn = (Button) findViewById(R.id.confirm_goods_detail_return);

    }

    private void initListener() {
        BtContinue.setOnClickListener(this);
        BtSeparate.setOnClickListener(this);
        BtSubmit.setOnClickListener(this);
        BtReturn.setOnClickListener(this);
    }

    private Boolean isOL() {
        return mScale.getStringNet().contains("OL");
    }

    private void initData() {
        mScale = JNIScale.getScale();
        divisionValue = mScale.getDivisionValue();

        mCameraPreview = CameraPreview.getInstance();
        numUtil = NumFormatUtil.getInstance();
        mLoadingDialog = new LoadingDialog(this);
        Intent intent = getIntent();
        if (intent != null) {
            batchNumber = intent.getStringExtra("batchNumber");
            productName = intent.getStringExtra("productName");
            productId = intent.getStringExtra("productId");
            actualWeight = numUtil.getDecimalNet(intent.getStringExtra("actualWeight"));
            recordWeight = intent.getStringExtra("weight");
            actualNumber = intent.getIntExtra("actualNumber", 0);
            plu = intent.getStringExtra("plu");
            position = intent.getIntExtra("position", 0);
            TvBatchNumber.setText("单号:" + batchNumber);
            TvName.setText("采购重量:" + recordWeight);
        }
    }

    private void initAdapter() {
        if (adapter == null)
            adapter = new ConfirmGoodsDetailAdapter(this, detailList);
        detailListView.setAdapter(adapter);
    }

    private void updateAdapter() {
        PurchaseDetail detail = new PurchaseDetail();
        detail.setWeightId(detailList.size() + 1);
        detail.setProductName(productName);
        detail.setQuantity(new BigDecimal(strWeight));
        detail.setSubmit(1);
        adapter.addData(detail);
    }

    private void timer() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (timeflag) {
                    mHandler.sendEmptyMessage(1);
                    mHandler.sendEmptyMessage(5);

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

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.confirm_goods_detail_continue:
                goId = 1;
                weightFlag = true;
                break;
            case R.id.confirm_goods_detail_separate:
                goId = 2;
                weightFlag = true;
                break;
            case R.id.confirm_goods_detail_ok:
                strWeight = getString(R.string.base_weight);
                state = 1;
                showNormalDialog("采购重量：" + recordWeight + "KG！\n现已确认重量：" + actualWeight + "KG！\n确定完成后不能再操作");
                break;
            case R.id.confirm_goods_detail_return:
                returnValue();
                finish();
                break;
        }
    }

    /**
     * 判断秤稳定
     */
    private boolean isStable() {
        return mScale.getStabFlag();
    }

    private void addNewWeight() {
        String strNet = mScale.getStringNet().trim();
        float fW = NumFormatUtil.isNumeric(strNet) ? Float.parseFloat(strNet) : 0;

        if (isOL()) {
            showMessage(strNet);
        } else {
            TvWeight.setText(NumFormatUtil.df2.format(fW));
            if (fW > 0.1f && isStable()) {
                if (weightFlag) {
                    camera();
                    strWeight = strNet;
                    weightFlag = false;
                    state = 2;
                    if (goId == 1) {
                        goLibraryId = "";
                        showNormalDialog("此次重量为:  " + strWeight + " KG" + "\t去向白条库");
                    } else {
                        goLibraryId = "77536151-E389-46EB-957A-BFB7B3BB8639";
                        showNormalDialog("此次重量为:  " + strWeight + " KG" + "\t去向分割房");
                    }
                }
            }
        }
    }

    private static class ConfirmGoodsDetailHandler extends Handler {

        WeakReference<ConfirmGoodsDetailActivity> mWeakReference;

        ConfirmGoodsDetailHandler(WeakReference<ConfirmGoodsDetailActivity> weakReference) {
            mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ConfirmGoodsDetailActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        activity.updateAdapter();
                        break;
                    case 1:
                        activity.addNewWeight();
                        break;
                    case 3:
                        //     activity.mAdapter.notifyDataSetChanged();
                        //    activity.number = 0;
                        activity.updateWeight();//提交失败后再次提交
                        break;
                    case 5:
                        activity.lightScreenCyclicity();
                        break;
                }
            }
        }
    }

    private void returnValue() {
        Intent intent = new Intent();
        intent.putExtra("actualWeight", String.valueOf(actualWeight));
        intent.putExtra("actualNumber", actualNumber);
        intent.putExtra("state", state); //将计算的值回传回去
        intent.putExtra("position", position);
        //通过intent对象返回结果，必须要调用一个setResult方法，
        setResult(2, intent);
    }

    private void updateWeight() {
        mLoadingDialog.showDialog();
        //  String numberStr = String.valueOf(number);
        if (isOL()) {
            showMessage("超出秤量程！");
            return;
        }
        float weightFt = Float.parseFloat(strWeight);
        //  final int n = Integer.parseInt(numberStr);
        if (weightFt < 0) {
            showMessage("重量不能为小于0");
            mLoadingDialog.dismissDialogs();
            return;
        }
        if (weightFt == 0 && state == 2) {
            showMessage("重量不能为小于0");
            mLoadingDialog.dismissDialogs();
            return;
        }
        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.setUpdateActualWeight(productId, batchNumber, state, submitData());
                if (api.Result) {
                    actualWeight = actualWeight.add(numUtil.getDecimalNet(strWeight));
                    //  PurchaseDetailManager instance = PurchaseDetailManager.getInstance();
                    //  instance.updatePurchaseDetail(batchNumber, productName, productId, weightFt, 0, state, 1);
                    if (state == 1) {
                        returnValue();
                        finish();
                    } else {
                        mHandler.sendEmptyMessage(0);
                    }
                    mLoadingDialog.dismissDialogs();
                    showMessage(api.ResultMessage);
                } else {
                    state = 2;
                    PurchaseDetailManager instance = PurchaseDetailManager.getInstance();
                    instance.updatePurchaseDetail(batchNumber, productName, productId, Float.parseFloat(strWeight), 0, state, 2);
                    sqlInsert();
                    mLoadingDialog.dismissDialogs();
                    showMessage(api.ResultMessage);
                }
            }
        });
    }

    private void sqlInsert() {
        //state 1 已回收 2 未回收     接口只担任出的任务时 goId 去向库id  置为空
        OperationLog log = new OperationLog("", "", goLibraryId, productName, plu, numUtil.getDecimalNet(strWeight), 0, "KG", NumFormatUtil.getDateDetail(), getCameraPic(), 2);
        logManager.insertOperationLog(log);
    }

    private String submitData() {
        JSONObject object = new JSONObject();
        String DataHelper = "";
        try {
            object.put("Weight", strWeight);
            object.put("Number", "0");
            object.put("ComelibraryId", "8E42A7B1-BC5C-4C97-A504-314892734410");//采购
            object.put("GolibraryId", goLibraryId);
            object.put("Picture", getCameraPic());
            DataHelper = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return DataHelper;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mMisc.beep();
        switch (keyCode) {
            case KeyEvent.KEYCODE_NUMPAD_DIVIDE:// 取消
                mLoadingDialog.dismissDialogs();
                return true;
            case KeyEvent.KEYCODE_F2:// 置零
                lastWeight = 0.0f;
                currWeight = 0.0f;
                if (!mScale.zero()) {
                    showMessage("置零失败");
                } else {
                    TvTareWeight.setText(R.string.base_weight);
                }
                return true;
            case KeyEvent.KEYCODE_F1:// 去皮
                lastWeight = 0.0f;
                currWeight = 0.0f;
                if (mScale.tare()) {
                    float curTare = mScale.getFloatTare();
                    TvTareWeight.setText(NumFormatUtil.df2.format(curTare));
                } else {
                    showMessage("去皮失败");
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                returnValue();
                finish();
                return true;

        }
        return super.onKeyDown(keyCode, event);
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


    private class ConfirmGoodsDetailAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private List<PurchaseDetail> mData;

        ConfirmGoodsDetailAdapter(Context context, List<PurchaseDetail> data) {
            this.mData = data;
            mLayoutInflater = LayoutInflater.from(context);
        }

        public void addData(@NonNull PurchaseDetail addData) {
            if (this.mData == null) {
                this.mData = new ArrayList<>();
            }
            this.mData.add(addData);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.confirm_goods_detail_item, null);
                holder.mNumber = (TextView) convertView.findViewById(R.id.confirm_goods_detail_item_number);
                holder.mName = (TextView) convertView.findViewById(R.id.confirm_goods_detail_item_name);
                holder.mUnit = (TextView) convertView.findViewById(R.id.confirm_goods_detail_item_unit);
                holder.mWeight = (TextView) convertView.findViewById(R.id.confirm_goods_detail_item_weight);
                holder.mConfirm = (TextView) convertView.findViewById(R.id.confirm_goods_detail_item_confirm);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final PurchaseDetail dItem = (PurchaseDetail) getItem(position);
            holder.mNumber.setText(dItem.getWeightId() + "");
            holder.mName.setText(dItem.getProductName());
            // holder.mUnit.setText("2");
            holder.mWeight.setText(String.valueOf(dItem.getDecimalQuantity()));
            if (dItem.getSubmit() == 1) {
                holder.mConfirm.setText("已确定");
                holder.mConfirm.setBackground(getResources().getDrawable(R.drawable.radio_gray));
            } else {
                holder.mConfirm.setText("确定");
                holder.mConfirm.setBackground(getResources().getDrawable(R.drawable.radio_selected));
                holder.mConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMisc.beep();
                        strWeight = String.valueOf(dItem.getDecimalQuantity());
                        state = 2;
                        mHandler.sendEmptyMessage(3);
                    }
                });
            }
            return convertView;
        }

        private class ViewHolder {
            TextView mNumber;
            TextView mName;
            TextView mUnit;
            TextView mWeight;
            TextView mConfirm;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        timeflag = false;
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
                    updateWeight();
                    dialog.dismiss();
                } else {
                    state = 2;
                    mMisc.beep();
                }
            }
        }).setTitle("提示").show();
    }
}
