package com.seray.pork;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import com.seray.adapter.OrderProductsDetailAdapter;
import com.seray.entity.OrderDetail;
import com.seray.entity.OrderPick;
import com.seray.entity.OrderProduct;
import com.seray.entity.OrderProductDetail;
import com.seray.utils.LogUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderProductsDetailActivity extends BaseActivity {
    private ListView listView;
    private Button btReturn;
    private TextView tvProducts;
    private List<OrderProductDetail> detailList = new ArrayList<>();
    private OrderProduct orderProduct;
    OrderProductsDetailAdapter detailAdapter;
    private int Type;
    int REQUESTCODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_products_detail);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        tvProducts = (TextView) findViewById(R.id.tv_order_products_name);
        listView = (ListView) findViewById(R.id.lv_order_products_detail);
        detailAdapter = new OrderProductsDetailAdapter(this);
        btReturn = (Button) findViewById(R.id.bt_order_products_detail_return);
        listView.setAdapter(detailAdapter);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            Type = intent.getIntExtra("Type", 2);
            orderProduct = (OrderProduct) getIntent().getSerializableExtra("OrderProduct");
            tvProducts.setText(orderProduct.getCommodityName());
            detailList = orderProduct.getDetailList();
            detailAdapter.setNewData(detailList);
        }
    }

    private void updateAdapter() {
        detailAdapter.setNewData(detailList);
    }

    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMisc.beep();
                OrderProductDetail orderDetail = detailList.get(position);
                Intent intent = new Intent(OrderProductsDetailActivity.this, OrderWeightActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("ProductDetail", orderDetail);
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
                if (state == 1) {
                    detailList.remove(position);
                    updateAdapter();
                    return;
                }
                if (actualWeight.compareTo(new BigDecimal(0)) <= 0 && actualNumber == 0) {
                    return;
                }
                BigDecimal weight = detailList.get(position).getDecimalActualWeight();

                if (weight.compareTo(actualWeight) == 0 && actualNumber == detailList.get(position).getActualNumber()) {
                    return;
                }
                for (int i = 0; i < detailList.size(); i++) {
                    if (position == i) {
                        detailList.get(i).setActualWeight(actualWeight);
                        detailList.get(i).setActualNumber(actualNumber);
                        detailList.get(i).setState(state);
                        updateAdapter();
                        break;
                    }
                }
            }
        }
    }
}
