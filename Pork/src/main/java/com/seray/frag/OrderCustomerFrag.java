package com.seray.frag;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.adapter.OrderAdapter;
import com.seray.entity.ApiResult;
import com.seray.entity.OrderPick;
import com.seray.http.UploadDataHttp;
import com.seray.pork.BaseActivity;
import com.seray.pork.OrderDetailActivity;
import com.seray.pork.R;
import com.seray.view.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 以客户配订单
 */

public class OrderCustomerFrag extends Fragment implements View.OnClickListener {
    private int mType = 1;//1 待配货 2 按品名配货  3 已配货待上车 4 扫码分拣成订单形式  5 已上车
    private Button mReturn, mUptata, mLastPage, mNextPage;
    private ListView listView;
    private TextView tvPage;
    //    private SparseArray<List<OrderPick>> mSparseArray = new SparseArray<>();
    private List<OrderPick> tableOrderList = new ArrayList<>();
    OrderAdapter orderAdapter;
    LoadingDialog loadingDialog;
    private Misc mMisc;
    private int page = 1;
    private int totalPage;
    private String returnMessage = "";
    View view;
    Toast mToast = null;

    private void myToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x111:
                    orderAdapter.setNewData(tableOrderList);
                    myToast("暂无数据");
                    loadingDialog.dismissDialogs();
                    break;
                case 0x222:
                    orderAdapter.setNewData(tableOrderList);
                    tvPage.setText(page + "：" + totalPage);
                    loadingDialog.dismissDialogs();
                    break;
                case 0x333:
                    loadingDialog.dismissDialogs();
                    myToast(returnMessage);
                    break;
            }
        }

        ;
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_customer, container, false);
        initView();
        initAdapter();
        initListener();
        return view;
    }

    private void initView() {
        mMisc = Misc.newInstance();
        loadingDialog = new LoadingDialog(getContext());
        mReturn = (Button) view.findViewById(R.id.bt_order_customer_return);
        mUptata = (Button) view.findViewById(R.id.bt_order_customer_updata);
        mLastPage = (Button) view.findViewById(R.id.bt_order_customer_last_page);
        mNextPage = (Button) view.findViewById(R.id.bt_order_customer_next_page);
        tvPage = (TextView) view.findViewById(R.id.tv_order_customer_page);
        listView = (ListView) view.findViewById(R.id.lv_order_customer);
    }

    private void initAdapter() {
        orderAdapter = new OrderAdapter(getContext());
        listView.setAdapter(orderAdapter);
    }

    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                OrderPick orderPick = tableOrderList.get(position);
                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                intent.putExtra("OrderPick", orderPick);
                intent.putExtra("Type", mType);
                startActivity(intent);
            }
        });
        mLastPage.setOnClickListener(this);
        mNextPage.setOnClickListener(this);
        mReturn.setOnClickListener(this);
        mUptata.setOnClickListener(this);
    }

    //获取订单列表
    private void getOrderList() {
        loadingDialog.showDialog();
        BaseActivity.httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getOrderList(mType, "", page);
                returnMessage = api.ResultMessage;
                if (api.Result) {
                    tableOrderList.clear();
                    if (api.orderPickList.size() > 0) {
                        tableOrderList = api.orderPickList;
                        totalPage = Integer.valueOf(api.sourceDetail[0]);
                        mHandler.sendEmptyMessage(0x222);
                    } else {
                        mHandler.sendEmptyMessage(0x111);
                    }
                } else {
//                    sqlInsert(2, "");
                    mHandler.sendEmptyMessage(0x333);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (view.getVisibility() == View.VISIBLE)
            getOrderList();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        mMisc.beep();
        switch (v.getId()) {
            case R.id.bt_order_customer_return:
                getActivity().finish();
                break;
            case R.id.bt_order_customer_updata:
                getOrderList();
                break;
            case R.id.bt_order_customer_last_page:
                if (page > 1) {
                    page -= 1;
                    getOrderList();
                } else
                    myToast("已到首页！");
                break;
            case R.id.bt_order_customer_next_page:
                if (page < totalPage) {
                    page += 1;
                    getOrderList();
                } else
                    myToast("已到最后一页！");
                break;
        }
    }
}
