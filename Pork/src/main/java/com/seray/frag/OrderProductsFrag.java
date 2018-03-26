package com.seray.frag;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.entity.ApiResult;
import com.seray.entity.OrderProduct;
import com.seray.entity.OrderProductDetail;
import com.seray.http.UploadDataHttp;
import com.seray.pork.BaseActivity;
import com.seray.pork.OrderProductsDetailActivity;
import com.seray.pork.R;
import com.seray.utils.NumFormatUtil;
import com.seray.view.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 按品名配货
 */

public class OrderProductsFrag extends Fragment implements View.OnClickListener {
    private int mType = 2;// 1 待配货 2 按品名配货  3 已配货待上车 4 扫码分拣成订单形式  5 已上车
    private Button mReturn, mUptata;
    private ListView listView;
    //    private SparseArray<List<OrderPick>> mSparseArray = new SparseArray<>();
    private List<OrderProduct> productsList = new ArrayList<>();
    OrderProductsAdapter productsAdapter;
    LoadingDialog loadingDialog;
    private Misc mMisc;
    private int page = 1;
    private String returnMessage = "";
    NumFormatUtil mNumUtil;
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
                    productsAdapter.setNewData(productsList);
                    myToast("暂无数据");
                    loadingDialog.dismissDialogs();
                    break;
                case 0x222:
                    productsAdapter.setNewData(productsList);
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
         view = inflater.inflate(R.layout.fragment_order_products, container, false);
        initView();
        initAdapter();
        initListener();
        return view;
    }

    private void initView() {
        mNumUtil = NumFormatUtil.getInstance();
        mMisc = Misc.newInstance();
        loadingDialog = new LoadingDialog(getContext());
        mReturn = (Button) view.findViewById(R.id.bt_order_products_return);
        mUptata = (Button) view.findViewById(R.id.bt_order_products_updata);
        listView = (ListView) view.findViewById(R.id.lv_order_products);
    }

    private void initAdapter() {
        productsAdapter = new OrderProductsAdapter(getContext());
        listView.setAdapter(productsAdapter);
    }

    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                OrderProduct orderDetail = productsList.get(position);
                Intent intent = new Intent(getContext(), OrderProductsDetailActivity.class);
                intent.putExtra("OrderProduct", orderDetail);
                intent.putExtra("Type", mType);
                startActivity(intent);
            }
        });
        mReturn.setOnClickListener(this);
        mUptata.setOnClickListener(this);
    }

    //获取订单列表
    private void getOrderList() {
        loadingDialog.showDialog();
        BaseActivity.httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getOrderList(mType,"", page);
                returnMessage = api.ResultMessage;
                if (api.Result) {
                    jsonProducts(api.ResultJsonStr);
                    if (productsList.size() > 0) {
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

    private void jsonProducts(String json) {
        JSONObject obj;
        productsList.clear();
        try {
            obj = new JSONObject(json);
            JSONArray object = obj.getJSONArray("Result");
            for (int j = 0; j < object.length(); j++) {
                JSONObject jsonObject = object.getJSONObject(j);
                String commodityName = jsonObject.getString("CommodityName");
                double weight = jsonObject.getDouble("Weight");
                double actualWeight = jsonObject.getDouble("ActualWeight");
                int actualNumber = jsonObject.getInt("ActualNumber");
                int number = jsonObject.getInt("Number");
                JSONArray orderdetail = jsonObject.getJSONArray("orderdetail");
                List<OrderProductDetail> opDetail = new ArrayList<>();
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
                    opDetail.add(detail);
                }
                OrderProduct orderProduct = new OrderProduct(commodityName, number, mNumUtil.getDecimalNetWithOutHalfUp(weight),
                        mNumUtil.getDecimalNetWithOutHalfUp(actualWeight), actualNumber);
                orderProduct.setDetailList(opDetail);
                productsList.add(orderProduct);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (view.getVisibility()==View.VISIBLE)
            getOrderList();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        mMisc.beep();
        switch (v.getId()) {
            case R.id.bt_order_products_return:
                getActivity().finish();
                break;
            case R.id.bt_order_products_updata:
                getOrderList();
                break;
        }
    }

    public class OrderProductsAdapter extends BaseAdapter {
        private Context mContext;
        LayoutInflater mInflater;
        private List<OrderProduct> mData;

        public OrderProductsAdapter(Context context) {
            this.mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        public void setNewData(@NonNull List<OrderProduct> newData) {
            if (this.mData == null) {
                this.mData = new ArrayList<>();
            }
            this.mData.clear();
            this.mData.addAll(newData);
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

            OrderProduct itemDate = mData.get(position);
            int state = 2;
            ViewHolder mHolder;
            if (convertView == null) {
                mHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.order_detail_list_layout, null);
                mHolder.nameTv = (TextView) convertView.findViewById(R.id.order_detail_list_name);
                mHolder.quantityTv = (TextView) convertView.findViewById(R.id.order_detail_list_quantity);
                mHolder.actualQuantityTv = (TextView) convertView.findViewById(R.id.order_detail_list_actualQuantity);
                mHolder.state = (TextView) convertView.findViewById(R.id.order_detail_list_state);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            mHolder.nameTv.setText(itemDate.getCommodityName());
            if (itemDate.getNumber() > 0) {
                if (itemDate.getWeight()>0){
                    mHolder.quantityTv.setText(String.valueOf(itemDate.getNumber())+"*"+itemDate.getWeight()+"KG");
                }else {
                    mHolder.quantityTv.setText(String.valueOf(itemDate.getNumber()));
                }
                mHolder.actualQuantityTv.setText(String.valueOf(itemDate.getActualNumber()));
                if (itemDate.getNumber() <= itemDate.getActualNumber())
                    state = 1;
                else
                    state = 2;
            } else {
                mHolder.quantityTv.setText(String.valueOf(itemDate.getDecimalWeight())+"KG");
                mHolder.actualQuantityTv.setText(String.valueOf(itemDate.getDecimalActualWeight()));
                if (itemDate.getDecimalWeight().compareTo(itemDate.getDecimalActualWeight()) <= 0)
                    state = 1;
                else
                    state = 2;
            }
            if (state == 1) {
                mHolder.state.setText("足量");
                mHolder.state.setTextColor(getResources().getColor(R.color.white));
            } else {
                mHolder.state.setText("不足");
                mHolder.state.setTextColor(getResources().getColor(R.color.red));
            }
            return convertView;
        }

        class ViewHolder {
            TextView nameTv;
            TextView quantityTv;
            TextView actualQuantityTv;
            TextView state;
        }
    }

}
