package com.seray.pork;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.seray.adapter.OrderAdapter;
import com.seray.entity.ApiResult;
import com.seray.entity.OrderPick;
import com.seray.http.UploadDataHttp;
import com.seray.view.LoadingDialog;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends BaseActivity {
    private int mType = 1;// 1 待配货   2 已配货
    private Button mReturn,mUptata;
    private TextView lastSelected = null;
    private TextView waitConfigureTv,productsConfigureTv, finishConfigureTv;
    private ListView listView;
    private SparseArray<List<OrderPick>> mSparseArray = new SparseArray<>();
    private List<OrderPick> tableOrderList = new ArrayList<>();
    OrderAdapter orderAdapter;
    private OrderHandler mHandler = new OrderHandler(new WeakReference<>(this));
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();
        initListener();
        initAdapter();
    }

    private void initView() {
        loadingDialog = new LoadingDialog(this);
        waitConfigureTv = (TextView) findViewById(R.id.tv_order_wait_customer);
        productsConfigureTv = (TextView) findViewById(R.id.tv_order_wait_products);
        mReturn = (Button) findViewById(R.id.bt_order_return);
        mUptata = (Button) findViewById(R.id.bt_order_updata);
        lastSelected = waitConfigureTv;
        finishConfigureTv = (TextView) findViewById(R.id.tv_order_finish);
        listView = (ListView) findViewById(R.id.lv_order);
    }

    private void initAdapter() {
        orderAdapter = new OrderAdapter(this);
        listView.setAdapter(orderAdapter);
        getOrderList();
    }

    private void initListener() {
        waitConfigureTv.setOnClickListener(this);
        productsConfigureTv.setOnClickListener(this);
        finishConfigureTv.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                OrderPick orderPick = tableOrderList.get(position);
                Intent intent = new Intent(OrderActivity.this, OrderDetailActivity.class);
                intent.putExtra("OrderPick", orderPick);
                intent.putExtra("Type",mType);
                startActivity(intent);
            }
        });
        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                finish();
            }
        });
        mUptata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                getOrderList();
            }
        });
    }

    //获取订单列表
    private void getOrderList() {
        loadingDialog.showDialog();
        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getOrderList();
                if (api.Result) {
                    if (api.SparseArray.size() > 0) {
                        mSparseArray.clear();
                        mSparseArray =api.SparseArray;
                    }else {
                        showMessage("暂无数据");
                    }
//                    sqlInsert(1, "");
                    loadingDialog.dismissDialog();
                    mHandler.sendEmptyMessage(1);
                } else {
//                    sqlInsert(2, "");
                    loadingDialog.dismissDialog();
                    showMessage(api.ResultMessage);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getOrderList();
    }

    private void updateTableList() {
        List<OrderPick> result = mSparseArray.get(mType);
        if (result==null)
            result = new ArrayList<>();
        tableOrderList.clear();
        tableOrderList.addAll(result);
        orderAdapter.setNewData(tableOrderList);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_order_wait_customer:
                changedSelectedView(v);
                mType = 1;
                updateTableList();
                break;
            case R.id.tv_order_wait_products:
                changedSelectedView(v);
                mType = 3;
                updateTableList();
            case R.id.tv_order_finish:
                changedSelectedView(v);
                mType = 2;
                updateTableList();
                break;
        }
    }

    private static class OrderHandler extends Handler {

        WeakReference<OrderActivity> mWeakReference;

        OrderHandler(WeakReference<OrderActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            OrderActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.updateTableList();
                        break;
                }
            }
        }
    }

    private void changedSelectedView(View v) {
        if (lastSelected != null) {
            lastSelected.setBackground(getResources().getDrawable(R.drawable.shapeone));
            lastSelected.setTextColor(Color.BLACK);
        }
        lastSelected = (TextView) v;
        lastSelected.setBackground(getResources().getDrawable(R.drawable.radio_selected));
        lastSelected.setTextColor(Color.WHITE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
