package com.seray.stock;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;
import com.seray.view.LoadingDialog;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/*
 * 地磅 待入库列表
 */
public class ConfirmInBulkActivity extends BaseActivity {
    private TextView TvBatchNumber;
    private String batchNumber;
    private Button BtReturn;
    private ListView detailListView;
    private List<PurchaseDetail> detailList;

    ConfirmInBulkAdapter adapter = null;
    LoadingDialog loadingDialog;
    private ConfirmInBulkHandler confirmInBulkHandler = new ConfirmInBulkHandler(new
            WeakReference<>(this));
    private int REQUESTCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_in_bulk);
        ininView();
        initData();
        initAdapter();
        initListener();
    }

    private void ininView() {
        TvBatchNumber = (TextView) findViewById(R.id.confirm_in_bulk_batch_number);
        detailListView = (ListView) findViewById(R.id.lv_confirm_in_bulk);
        BtReturn = (Button) findViewById(R.id.confirm_in_bulk_return);
    }

    private void initListener() {

        detailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();

                PurchaseDetail detail = detailList.get(position);
                Intent intent = new Intent(ConfirmInBulkActivity.this, InBulkQuantityActivity.class);
                intent.putExtra("batchNumber", batchNumber);
                intent.putExtra("position", position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("PurchaseDetail", detail);
                intent.putExtras(bundle);
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
                ApiResult api = UploadDataHttp.getPurchaseDetail(batchNumber, "");
                if (api.Result) {
                    detailList = api.DetailList;
                    if (detailList.size() > 0) {
                        confirmInBulkHandler.sendEmptyMessage(1);
                    } else {
                        confirmInBulkHandler.sendEmptyMessage(1);
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

    private static class ConfirmInBulkHandler extends Handler {

        WeakReference<ConfirmInBulkActivity> mWeakReference;

        ConfirmInBulkHandler(WeakReference<ConfirmInBulkActivity> weakReference) {
            mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ConfirmInBulkActivity activity = mWeakReference.get();
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            mMisc.beep();
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_NUMPAD_DIVIDE:// 取消
                loadingDialog.dismissDialog();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 3) {
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
            adapter = new ConfirmInBulkAdapter(this);
        detailListView.setAdapter(adapter);
    }

    private void updateAdapter() {
        adapter.notifyDataSetChanged();
    }

    private class ConfirmInBulkAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;

        ConfirmInBulkAdapter(Context context) {
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
                convertView = mLayoutInflater.inflate(R.layout.confirm_in_bulk_item, null);
                // holder.mNumber = (TextView) convertView.findViewById(R.id.confirm_in_bulk_item_number);
                holder.mName = (TextView) convertView.findViewById(R.id.confirm_in_bulk_item_name);
                holder.mNumberUnit = (TextView) convertView.findViewById(R.id.confirm_in_bulk_item_number_unit);
                holder.mActualNumber = (TextView) convertView.findViewById(R.id.confirm_in_bulk_item_actual_number);
                holder.mWeight = (TextView) convertView.findViewById(R.id.confirm_in_bulk_item_weight);
                holder.mActualWeight = (TextView) convertView.findViewById(R.id.confirm_in_bulk_item_actual_weight);
                holder.mState = (TextView) convertView.findViewById(R.id.confirm_in_bulk_item_state);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final PurchaseDetail dItem = (PurchaseDetail) getItem(position);
            //   holder.mNumber.setText(position + 1 + "");
            holder.mName.setText(dItem.getProductName());
            if (dItem.getUnit().equals("KG")) {
                holder.mWeight.setText(String.valueOf(dItem.getDecimalQuantity()) + dItem.getUnit());
                holder.mWeight.setTextColor(getResources().getColor(R.color.red));
                holder.mNumberUnit.setText(String.valueOf(dItem.getNumber()));

            } else {
                holder.mWeight.setText(String.valueOf(dItem.getDecimalQuantity()));
                holder.mNumberUnit.setText(dItem.getNumber() + dItem.getUnit());
                holder.mNumberUnit.setTextColor(getResources().getColor(R.color.red));
            }
            holder.mActualNumber.setText(String.valueOf(dItem.getActualNumber()));
            holder.mActualWeight.setText(String.valueOf(dItem.getDecimalActualWeight()));
            holder.mState.setText(dItem.getState() == 2 ? "未完成" : "已完成");
            return convertView;
        }

        private class ViewHolder {
            //    TextView mNumber;
            TextView mName;
            TextView mNumberUnit;
            TextView mWeight;
            TextView mActualWeight;
            TextView mActualNumber;
            TextView mState;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        confirmInBulkHandler.removeCallbacksAndMessages(null);
    }
}
