package com.seray.pork;

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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.seray.cache.ScanGunKeyEventHelper;
import com.seray.entity.ApiResult;
import com.seray.entity.OrderDetail;
import com.seray.entity.OrderPick;
import com.seray.entity.OrderProductDetail;
import com.seray.http.UploadDataHttp;
import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;
import com.seray.view.LoadingDialog;
import com.seray.view.PromptDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends BaseActivity implements ScanGunKeyEventHelper.OnScanSuccessListener {
    private ListView listView;
    private Button btReturn, btFinish;
    private TextView tvQuantity, tvActualQuantity;
    private List<OrderDetail> detailList = new ArrayList<>();
    private OrderPick orderPicks;
    OrderDetailAdapter detailAdapter;
    private int Type;
    private int REQUESTCODE = 1;
    private int state = 2;
    private String details = "";
    private String returnMessage;
    ScanGunKeyEventHelper scanGunKeyEventHelper = null;
    LoadingDialog loadingDialog;
    private OrderDetailHandler mHandler = new OrderDetailHandler(new WeakReference<>(this));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        loadingDialog = new LoadingDialog(this);
        listView = (ListView) findViewById(R.id.lv_order_detail);
        detailAdapter = new OrderDetailAdapter(this, detailList);
        btReturn = (Button) findViewById(R.id.bt_order_detail_return);
        btFinish = (Button) findViewById(R.id.bt_order_detail_finish);
        tvQuantity = (TextView) findViewById(R.id.tv_order_detail_quantity);
        tvActualQuantity = (TextView) findViewById(R.id.tv_order_detail_actualquantity);
        listView.setAdapter(detailAdapter);
        scanGunKeyEventHelper = new ScanGunKeyEventHelper(this);
    }

    private void initData() {
        Intent intent = getIntent();
        Type = intent.getIntExtra("Type", 1);
        if (Type == 3)
            btFinish.setVisibility(View.VISIBLE);
        if (Type == 2)
            return;
        orderPicks = (OrderPick) getIntent().getSerializableExtra("OrderPick");
        detailList = orderPicks.getDetailList();
        detailAdapter.setNewData(detailList);
        if (Type == 3 || Type == 5) {
            tvQuantity.setText("已配量");
            tvActualQuantity.setText("上车量");
        } else {
            tvQuantity.setText("需求量");
            tvActualQuantity.setText("已配量");
        }
    }

    private void updateAdapter() {
        detailAdapter.notifyDataSetChanged();
    }

    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                if (Type != 1)
                    return;
                OrderDetail orderDetail = detailList.get(position);
                Intent intent = new Intent(OrderDetailActivity.this, OrderWeightActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("OrderDetail", orderDetail);
                intent.putExtras(bundle);
                intent.putExtra("position", position);
                intent.putExtra("type", Type);
                startActivityForResult(intent, REQUESTCODE);
            }
        });
        btReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                finish();
            }
        });
        btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                showNormalDialog("此次为非常规操作 \t是否继续？");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            if (requestCode == REQUESTCODE) {
                if (data == null)
                    return;
                BigDecimal actualWeight = new BigDecimal(data.getStringExtra("actualWeight"));
                int actualNumber = data.getIntExtra("actualNumber", 0);
                int position = data.getIntExtra("position", 0);
                int state = data.getIntExtra("state", 2);
                if (actualWeight.compareTo(new BigDecimal(0)) <= 0 && actualNumber == 0) {
                    if (state != 1)
                        return;
                }
                BigDecimal weight = detailList.get(position).getBigDecimalActualWeight();

                if (weight.compareTo(actualWeight) == 0 && actualNumber == detailList.get(position).getActualNumber()) {
                    if (state != 1)
                        return;
                }
                for (int i = 0; i < detailList.size(); i++) {
                    if (position == i) {
                        detailList.get(i).setActualWeight(actualWeight);
                        detailList.get(i).setActualNumber(actualNumber);
                        detailList.get(i).setState(detailList.get(i).getState() == 1 ? 1 : state);
                        updateAdapter();
                        break;
                    }
                }
            }
        }
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
        if (Type == 3) {
            details = "";
            details = barcode;
            LogUtil.d("barcode", details);
            setOrderState();
        }
    }

    //订单上车
    private void setOrderState() {
        loadingDialog.showDialog();
        BaseActivity.httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.updatetOrderVehicle(orderPicks.getOrderNumber(), details, state);
                returnMessage = api.ResultMessage;
                state = 2;
                if (api.Result) {
                    jsonProducts(api.ResultJsonStr);
                    mHandler.sendEmptyMessage(1);
                } else {
                    // sqlInsert(2, "");
                    mHandler.sendEmptyMessage(2);
                }
            }
        });
    }

    private static class OrderDetailHandler extends Handler {

        WeakReference<OrderDetailActivity> mWeakReference;

        OrderDetailHandler(WeakReference<OrderDetailActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            OrderDetailActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.show();
                        break;
                    case 2:
                        activity.show();
                        break;
                }
            }
        }
    }

    private void show() {
        if (Type == 3 && returnMessage.equals("成功")) {
            detailAdapter.setNewData(detailList);
        }
        showMessage(returnMessage);
        loadingDialog.dismissDialogs();
    }

    public class OrderDetailAdapter extends BaseAdapter {

        private Context mContext;
        LayoutInflater mInflater;
        private List<OrderDetail> mData;

        public OrderDetailAdapter(Context context, List<OrderDetail> data) {
            this.mContext = context;
            this.mData = data;
            mInflater = LayoutInflater.from(mContext);
        }

        public void setNewData(@NonNull List<OrderDetail> newData) {
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

            OrderDetail itemDate = mData.get(position);

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
            mHolder.nameTv.setText(itemDate.getProductName());
            if (itemDate.getNumber() > 0) {
                if (Type == 3 || Type == 5) {
                    if (itemDate.getWeight() > 0) {
                        mHolder.quantityTv.setText(String.valueOf(itemDate.getActualNumber()) + "*" + itemDate.getWeight() + "KG");
                    } else {
                        mHolder.quantityTv.setText(String.valueOf(itemDate.getActualNumber()));
                    }
                    mHolder.actualQuantityTv.setText(String.valueOf(itemDate.getVehicleNumber()));
                } else {
                    if (itemDate.getWeight() > 0) {
                        mHolder.quantityTv.setText(String.valueOf(itemDate.getNumber()) + "*" + itemDate.getWeight() + "KG");
                    } else {
                        mHolder.quantityTv.setText(String.valueOf(itemDate.getNumber()));
                    }
                    mHolder.actualQuantityTv.setText(String.valueOf(itemDate.getActualNumber()));
                }
            } else {
                if (Type == 3 || Type == 5) {
                    mHolder.quantityTv.setText(String.valueOf(itemDate.getBigDecimalActualWeight()) + "KG");
                    mHolder.actualQuantityTv.setText(String.valueOf(itemDate.getBigDecimalVehicleWeight()));
                } else {
                    mHolder.quantityTv.setText(String.valueOf(itemDate.getBigDecimalWeight()) + "KG");
                    mHolder.actualQuantityTv.setText(String.valueOf(itemDate.getBigDecimalActualWeight()));
                }
            }
            if (Type == 3 || Type == 5) {
                mHolder.state.setText(itemDate.getIsVehicle());
                if (itemDate.getIsVehicle().equals("未上车")) {
                    mHolder.state.setTextColor(getResources().getColor(R.color.red));
                } else {
                    mHolder.state.setTextColor(getResources().getColor(R.color.white));
                }
            } else {
                if (itemDate.getState() == 1) {
                    mHolder.state.setText("已完成");
                    mHolder.state.setTextColor(getResources().getColor(R.color.white));
                } else {
                    mHolder.state.setText("未完成");
                    mHolder.state.setTextColor(getResources().getColor(R.color.red));
                }
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

    private void jsonProducts(String json) {
        NumFormatUtil mNumUtil = NumFormatUtil.getInstance();
        JSONObject obj;
        detailList.clear();
        try {
            obj = new JSONObject(json);
            JSONArray orderdetail = obj.getJSONArray("Result");
            for (int i = 0; i < orderdetail.length(); i++) {
                JSONObject detailObject = orderdetail.getJSONObject(i);

                String commodityName = detailObject.getString("CommodityName");
                double ActualWeight = detailObject.getDouble("ActualWeight");
                double VehicleWeight = detailObject.getDouble("VehicleWeight");
                double Weight = detailObject.getDouble("Weight");
                int ActualNumber = detailObject.getInt("ActualNumber");
                int Number = detailObject.getInt("Number");
                int VehicleNumber = detailObject.getInt("VehicleNumber");
                int IsVehicleInt = detailObject.getInt("IsVehicle");
                String isVehicle = "未上车";
                if (IsVehicleInt == 1) {
                    isVehicle = "已上车";
                } else {
                    isVehicle = "未上车";
                }
                OrderDetail detail = new OrderDetail();
                detail.setProductName(commodityName);
                detail.setActualNumber(ActualNumber);
                detail.setVehicleNumber(VehicleNumber);
                detail.setNumber(Number);
                detail.setIsVehicle(isVehicle);
                detail.setActualWeight(mNumUtil.getDecimalNetWithOutHalfUp(ActualWeight));
                detail.setVehicleWeight(mNumUtil.getDecimalNetWithOutHalfUp(VehicleWeight));
                detail.setWeight(mNumUtil.getDecimalNetWithOutHalfUp(Weight));
                detailList.add(detail);
            }
        } catch (JSONException e) {
            LogUtil.e(e.getMessage());
        }
    }

    /*
         *  信息确认提示
         */
    public void showNormalDialog(String weightContent) {

        new PromptDialog(this, R.style.Dialog, weightContent, new PromptDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    loadingDialog.showDialog();
                    mMisc.beep();
                    state = 1;
                    details = "";
                    setOrderState();
                    dialog.dismiss();
                } else {
                    mMisc.beep();
                    state = 2;
                }

            }
        }).setTitle("警告").show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanGunKeyEventHelper.onDestroy();
    }
}
