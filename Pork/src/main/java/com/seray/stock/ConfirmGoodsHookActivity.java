package com.seray.stock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.seray.entity.ApiResult;
import com.seray.entity.PurchaseDetail;
import com.seray.http.UploadDataHttp;
import com.seray.pork.BaseActivity;
import com.seray.pork.R;
import com.seray.view.LoadingDialog;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 吊秤详细商品列表
 */
public class ConfirmGoodsHookActivity extends BaseActivity {
    private TextView TvBatchNumber;
    private String batchNumber;
    private Button BtReturn;
    private ListView detailListView;
    private List<PurchaseDetail> detailList;

    HookAdapter adapter = null;
    LoadingDialog loadingDialog;
    private HookHandler hookHandler = new HookHandler(new
            WeakReference<>(this));
    int REQUESTCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_goods_hook);
        ininView();
        initData();
        initAdapter();
        initListener();
    }

    private void ininView() {
        TvBatchNumber = (TextView) findViewById(R.id.confirm_goods_hook_batch_number);
        detailListView = (ListView) findViewById(R.id.lv_confirm_goods_hook);
        BtReturn = (Button) findViewById(R.id.confirm_goods_hook_return);
    }

    private void initListener() {

        detailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                String productId = detailList.get(position).getProductId();
                String productName = detailList.get(position).getProductName();
                String actualWeight = String.valueOf(detailList.get(position).getDecimalActualWeight());
                int actualNumber = detailList.get(position).getActualNumber();
                String plu = detailList.get(position).getPluCode();
                String weight = String.valueOf(detailList.get(position).getDecimalQuantity());
                Intent intent = new Intent(ConfirmGoodsHookActivity.this, ConfirmGoodsDetailActivity.class);
                intent.putExtra("batchNumber", batchNumber);
                intent.putExtra("productId", productId);
                intent.putExtra("productName", productName);
                intent.putExtra("actualWeight", actualWeight);
                intent.putExtra("weight", weight);
                intent.putExtra("actualNumber", actualNumber);
                intent.putExtra("plu", plu);
                intent.putExtra("position", position);
                startActivityForResult(intent, REQUESTCODE);
            }
        });
        BtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                finish();
            }
        });
    }

    private void initData() {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.showDialog();
        Intent intent = getIntent();
        if (intent != null) {
            batchNumber = intent.getStringExtra("number");
            TvBatchNumber.setText("收据号:" + batchNumber);
        }
        detailList = new ArrayList<>();
        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getPurchaseDetail(batchNumber, "白条");
                if (api.Result) {
                    detailList = api.DetailList;
                    //    id = subtotalList.get(1).getRemark();
                    if (detailList.size() > 0) {
                        hookHandler.sendEmptyMessage(1);
                    } else {
                        showMessage("暂无数据");
                    }
                    loadingDialog.dismissDialog();
                } else {
                    loadingDialog.dismissDialog();
                    showMessage(api.ResultMessage);
                }
            }
        });
    }

    private static class HookHandler extends Handler {

        WeakReference<ConfirmGoodsHookActivity> mWeakReference;

        HookHandler(WeakReference<ConfirmGoodsHookActivity> weakReference) {
            mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ConfirmGoodsHookActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        //  activity.initAdapter();
                        break;
                    case 1:
                        activity.updateAdapter();
                        break;
                    case 3:

                        break;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            if (requestCode == REQUESTCODE) {
                if (data == null)
                    return;
                int state = data.getIntExtra("state", 2);
                BigDecimal actualWeight = new BigDecimal(data.getStringExtra("actualWeight"));
                int actualNumber = data.getIntExtra("actualNumber", 0);
                int position = data.getIntExtra("position", 0);
                if (actualWeight.compareTo(new BigDecimal(0)) <= 0 && actualNumber == 0) {
                    if (state != 1)
                    return;
                }
                BigDecimal weight = detailList.get(position).getDecimalActualWeight();

                if (weight.compareTo(actualWeight) == 0 && actualNumber == detailList.get(position).getActualNumber()) {
                    if (state != 1)
                    return;
                }
                for (int i = 0; i < detailList.size(); i++) {
                    if (position == i) {
                        detailList.get(i).setState(state);
                        detailList.get(i).setActualWeight(actualWeight);
                        detailList.get(i).setActualNumber(actualNumber);
                        updateAdapter();
                        break;
                    }
                }
            }
        }
    }

    private void initAdapter() {
        if (adapter == null)
            adapter = new HookAdapter(this);
        detailListView.setAdapter(adapter);
    }

    private void updateAdapter() {
        adapter.notifyDataSetChanged();
    }

    private class HookAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;

        HookAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return detailList == null ? 0 : detailList.size();
        }

        @Override
        public Object getItem(int position) {
            return detailList.get(position);
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
                convertView = mLayoutInflater.inflate(R.layout.confirm_goods_hook_item, null);
                holder.mNumber = (TextView) convertView.findViewById(R.id.confirm_goods_hook_item_number);
                holder.mName = (TextView) convertView.findViewById(R.id.confirm_goods_hook_item_name);
                holder.mWeight = (TextView) convertView.findViewById(R.id.confirm_goods_hook_item_weight);
                holder.mActualWeight = (TextView) convertView.findViewById(R.id.confirm_goods_hook_item_actual_weight);
                holder.mState = (TextView) convertView.findViewById(R.id.confirm_goods_hook_item_state);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final PurchaseDetail dItem = (PurchaseDetail) getItem(position);
            holder.mNumber.setText(position + 1 + "");
            holder.mName.setText(dItem.getProductName());
            if (dItem.getUnit().equals("KG")) {
                holder.mWeight.setText(String.valueOf(dItem.getDecimalQuantity()) + dItem.getUnit());
            } else {
                holder.mWeight.setText(String.valueOf(dItem.getDecimalQuantity()));
            }
            holder.mActualWeight.setText(String.valueOf(dItem.getDecimalActualWeight()));
            holder.mState.setText(dItem.getState() == 2 ? "未完成" : "已完成");
            return convertView;
        }

        private class ViewHolder {
            TextView mNumber;
            TextView mName;
            TextView mWeight;
            TextView mActualWeight;
            TextView mState;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hookHandler.removeCallbacksAndMessages(null);
    }
}
