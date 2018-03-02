package com.seray.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.widget.TextView;

import com.seray.cache.ScanGunKeyEventHelper;
import com.seray.entity.ApiResult;
import com.seray.entity.OrderProductDetail;
import com.seray.http.UploadDataHttp;
import com.seray.pork.R;
import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomScanDialog extends Dialog implements ScanGunKeyEventHelper.OnScanSuccessListener {

    private ScanGunKeyEventHelper mGunHelper;
    private List<OrderProductDetail> mScanData = null;
    private OnLoadDataListener loadDataListener;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            Object obj = msg.obj;
            if (what == 0) {//失败
                if (loadDataListener != null)
                    loadDataListener.onLoadFailed(obj.toString());
                delayDismiss("读取失败");
            } else if (what == 1) {//成功
                if (loadDataListener != null)
                    loadDataListener.onLoadSuccess(mScanData);
                delayDismiss("读取成功");
            }
        }
    };

    public CustomScanDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
        mGunHelper = new ScanGunKeyEventHelper(this);
    }

    public void setOnLoadDataListener(@NonNull OnLoadDataListener listener) {
        this.loadDataListener = listener;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mGunHelper.onDestroy();
        if (mScanData != null) {
            mScanData.clear();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        TextView msgView = (TextView) findViewById(R.id.dialog_message);
        msgView.setText("开始扫码");
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_NUMPAD_DIVIDE) {
                TextView msgView = (TextView) findViewById(R.id.dialog_message);
                msgView.setText("取消扫码");
                msgView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 500);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        int code = event.getKeyCode();
        if (code == KeyEvent.KEYCODE_BACK || code == KeyEvent.KEYCODE_NUMPAD_DIVIDE) {
            return super.dispatchKeyEvent(event);
        } else {
            mGunHelper.analysisKeyEvent(event);
        }
        return true;
    }

    @Override
    public void onScanSuccess(String barcode) {
        final TextView msgView = (TextView) findViewById(R.id.dialog_message);
        msgView.setText("扫码成功");
        final String batch = barcode;
        LogUtil.d("扫码读取",batch);
        msgView.postDelayed(new Runnable() {
            @Override
            public void run() {
                msgView.setText("读取数据");
                readQRContent(batch);
            }
        }, 500);
    }

    private void readQRContent(final String con) {
        if (con.isEmpty()) {
            if (loadDataListener != null)
                loadDataListener.onLoadFailed("空二维码");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getOrderList(4, con, 1);
                String resMsg = api.ResultMessage;
                Message msg = Message.obtain();
                if (api.Result) {
                    mScanData = jsonProducts(api.ResultJsonStr);
                    msg.what = 1;
                    msg.obj = resMsg;
                } else {
                    msg.what = 0;
                    msg.obj = resMsg;
                }
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    private void delayDismiss(@NonNull String tip) {
        TextView msgView = (TextView) findViewById(R.id.dialog_message);
        msgView.setText(tip);
        msgView.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 500);
    }

    private List<OrderProductDetail> jsonProducts(String json) {
        NumFormatUtil mNumUtil = NumFormatUtil.getInstance();
        JSONObject obj;
        List<OrderProductDetail> result = new ArrayList<>();
        try {
            obj = new JSONObject(json);
            JSONArray orderdetail = obj.getJSONArray("Result");
            for (int i = 0; i < orderdetail.length(); i++) {
                JSONObject detailObject = orderdetail.getJSONObject(i);
                String orderNumber = detailObject.getString("OrderNumber");
                String orderDetailId = detailObject.getString("OrderDetailId");
                String customerName = detailObject.getString("CustomerName");
                String commodityName1 = detailObject.getString("CommodityName");
                String orderDate = detailObject.getString("OrderDate");
                double weight1 = detailObject.getDouble("Weight");
                double actualWeight1 = detailObject.getDouble("ActualWeight");
                int number1 = detailObject.getInt("Number");
                int actualNumber1 = detailObject.getInt("ActualNumber");
                OrderProductDetail detail = new OrderProductDetail(orderDetailId, customerName,
                        commodityName1, orderNumber, number1, mNumUtil.getDecimalNetWithOutHalfUp(weight1),
                        actualNumber1, mNumUtil.getDecimalNetWithOutHalfUp(actualWeight1), orderDate);
                result.add(detail);
            }
        } catch (JSONException e) {
            LogUtil.e(e.getMessage());
        }
        return result;
    }

    public interface OnLoadDataListener {

        void onLoadSuccess(List<OrderProductDetail> result);

        void onLoadFailed(String error);
    }
}
