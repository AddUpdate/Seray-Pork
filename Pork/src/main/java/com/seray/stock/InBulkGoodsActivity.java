package com.seray.stock;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.seray.entity.ApiResult;
import com.seray.entity.PurchaseSubtotal;
import com.seray.http.UploadDataHttp;
import com.seray.pork.BaseActivity;
import com.seray.pork.R;
import com.seray.view.LoadingDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class InBulkGoodsActivity extends BaseActivity {

    private ListView mListView;
    private Button mReturn,mUpdata;
    InBulkGoodsAdapter adapter = null;
    private List<PurchaseSubtotal> detailList;// 测试

    private InBulkGoodsHandler InBulkGoodsHandler = new InBulkGoodsHandler(new
            WeakReference<>(this));
    LoadingDialog loadingDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_bulk_goods);
        initView();
        initAdapter();
        initData();
        initListener();
        initAdapter();
    }
    private void initView() {
        mListView = (ListView) findViewById(R.id.lv_in_bulk_goods);
        mReturn = (Button) findViewById(R.id.in_bulk_goods_return);
        mUpdata = (Button) findViewById(R.id.in_bulk_goods_updata);
        loadingDialog = new LoadingDialog(this);
    }

    private void initListener() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                String number = detailList.get(position).getBatchNumber();
                Intent intent = new Intent(InBulkGoodsActivity.this, ConfirmInBulkActivity.class);
                intent.putExtra("number", number);
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
        mUpdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                initData();
            }
        });
    }

    private void initData() {
        loadingDialog.showDialog();
        detailList = new ArrayList<>();
        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getBatchNoListPost();
                if (api.Result) {
                    detailList = api.SubtotalList;
                    if (detailList.size() > 0) {
                        InBulkGoodsHandler.sendEmptyMessage(1);
                    } else {
                        InBulkGoodsHandler.sendEmptyMessage(1);
                        showMessage("暂无数据");
                    }
                    loadingDialog.dismissDialogs();
                } else {
                    loadingDialog.dismissDialogs();
                    showMessage(api.ResultMessage);
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    private void initAdapter() {
        if (adapter == null)
            adapter = new InBulkGoodsAdapter(this);
        mListView.setAdapter(adapter);
    }

    private void updateAdapter() {
        adapter.notifyDataSetChanged();
    }


    private static class InBulkGoodsHandler extends Handler {

        WeakReference<InBulkGoodsActivity> mWeakReference;

        InBulkGoodsHandler(WeakReference<InBulkGoodsActivity> weakReference) {
            mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            InBulkGoodsActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        activity.initAdapter();
                        break;
                    case 1:
                        activity.updateAdapter();
                        break;
                    case 3:
                        //     activity.mAdapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }


    private class InBulkGoodsAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;

        InBulkGoodsAdapter(Context context) {
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
                convertView = mLayoutInflater.inflate(R.layout.confirm_goods_item, null);
                holder.mBatchNumberTv = (TextView) convertView.findViewById(R.id.confirm_goods_item_batch_number);
                holder.mSuppliersTv = (TextView) convertView.findViewById(R.id.confirm_goods_item_suppliers);
                holder.mStateTv = (TextView) convertView.findViewById(R.id.confirm_goods_item_state);
                //  holder.mRemarkTv = (TextView) convertView.findViewById(R.id.confirm_goods_item_remark);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            PurchaseSubtotal dItem = (PurchaseSubtotal) getItem(position);
            holder.mBatchNumberTv.setText(dItem.getBatchNumber());
            holder.mSuppliersTv.setText(dItem.getSupplier());
            holder.mStateTv.setText(dItem.getRemark().equals("2") ? "未完成" : "完成");
            //   holder.mRemarkTv.setText(dItem.getRemark());
            return convertView;
        }

        private class ViewHolder {
            TextView mBatchNumberTv;
            TextView mSuppliersTv;
            TextView mStateTv;
            //    TextView mRemarkTv;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InBulkGoodsHandler.removeCallbacksAndMessages(null);
    }
    
    
}
