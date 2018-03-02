package com.seray.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.seray.entity.OrderProductDetail;
import com.seray.pork.R;
import com.seray.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2018/2/26.
 */

public class OrderProductsDetailAdapter extends BaseAdapter {

    private Context mContext;
    LayoutInflater mInflater;
    private List<OrderProductDetail> mData;

    public OrderProductsDetailAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setNewData(@NonNull List<OrderProductDetail> newData) {
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

        OrderProductDetail itemDate = mData.get(position);

        ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.order_products_detail_layout, null);
            mHolder.nameTv = (TextView) convertView.findViewById(R.id.order_products_detail_list_name);
            mHolder.orderNumberTv = (TextView) convertView.findViewById(R.id.order_products_detail_list_order_number);
            mHolder.quantityTv = (TextView) convertView.findViewById(R.id.order_products_detail_list_quantity);
            mHolder.actualQuantityTv = (TextView) convertView.findViewById(R.id.order_products_detail_list_actualQuantity);
            mHolder.state = (TextView) convertView.findViewById(R.id.order_products_detail_list_state);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.nameTv.setText(itemDate.getCustomerName());
        mHolder.orderNumberTv.setText(itemDate.getOrderNumber());
        int state = 2;
        if (itemDate.getNumber() > 0) {
            if (itemDate.getWeight()>0){
                mHolder.quantityTv.setText(String.valueOf(itemDate.getNumber()+"*"+itemDate.getWeight()+"KG"));
            }else {
                mHolder.quantityTv.setText(String.valueOf(itemDate.getNumber()));
            }
            mHolder.actualQuantityTv.setText(String.valueOf(itemDate.getActualNumber()));
            if (itemDate.getActualNumber() >= itemDate.getNumber())
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
            mHolder.state.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            mHolder.state.setText("不足");
            mHolder.state.setTextColor(mContext.getResources().getColor(R.color.red));
        }
        return convertView;
    }

    class ViewHolder {
        TextView nameTv;
        TextView orderNumberTv;
        TextView quantityTv;
        TextView actualQuantityTv;
        TextView state;
    }
}

