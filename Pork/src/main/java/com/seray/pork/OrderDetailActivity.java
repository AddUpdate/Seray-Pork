package com.seray.pork;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.seray.entity.OrderDetail;
import com.seray.entity.OrderPick;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends BaseActivity {
    private ListView listView;
    private Button btReturn;
    private List<OrderDetail> detailList = new ArrayList<>();
    private OrderPick orderPicks;
    OrderDetailAdapter detailAdapter;
    private int Type;
    int REQUESTCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv_order_detail);
        detailAdapter = new OrderDetailAdapter(this, detailList);
        btReturn = (Button) findViewById(R.id.bt_order_detail_return);
        listView.setAdapter(detailAdapter);
    }

    private void initData() {
        Intent intent = getIntent();
        Type = intent.getIntExtra("Type", 1);
        orderPicks = (OrderPick) getIntent().getSerializableExtra("OrderPick");
        detailList = orderPicks.getDetailList();
        detailAdapter.setNewData(detailList);
    }

    private void updateAdapter() {
        detailAdapter.notifyDataSetChanged();
    }

    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                if (Type == 2)
                    return;
                OrderDetail orderDetail = detailList.get(position);
                Intent intent = new Intent(OrderDetailActivity.this, OrderWeightActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("OrderDetail", orderDetail);
                intent.putExtras(bundle);
                intent.putExtra("position", position);
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
                    return;
                }
                BigDecimal weight = detailList.get(position).getBigDecimalActualWeight();

                if (weight.compareTo(actualWeight) == 0 && actualNumber == detailList.get(position).getActualNumber()) {
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
                mHolder.quantityTv.setText(String.valueOf(itemDate.getNumber()));
                mHolder.actualQuantityTv.setText(String.valueOf(itemDate.getActualNumber()));

            } else {
                mHolder.quantityTv.setText(String.valueOf(itemDate.getBigDecimalWeight()));
                mHolder.actualQuantityTv.setText(String.valueOf(itemDate.getBigDecimalActualWeight()));
            }
             if (itemDate.getState() == 1) {
                mHolder.state.setText("已完成");
                mHolder.state.setTextColor(getResources().getColor(R.color.white));
            } else {
                mHolder.state.setText("未完成");
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
