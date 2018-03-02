package com.seray.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.seray.entity.OrderPick;
import com.seray.pork.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2017/12/21.
 */

public class OrderAdapter extends BaseAdapter {

    private Context mContext;
    LayoutInflater mInflater;
    private List<OrderPick> mData;

    public OrderAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setNewData(@NonNull List<OrderPick> newData) {
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

        OrderPick itemDate = mData.get(position);

        ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.order_listview_layout, null);
            mHolder.orderNumberTv = (TextView) convertView.findViewById(R.id.order_list_number);
            mHolder.dataTv = (TextView) convertView.findViewById(R.id.order_list_date);
            mHolder.nameTv = (TextView) convertView.findViewById(R.id.order_list_name);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.orderNumberTv.setText(itemDate.getOrderNumber());
        mHolder.dataTv.setText(itemDate.getOrderDate());
        mHolder.nameTv.setText(itemDate.getName());
        return convertView;
    }

    class ViewHolder {
        TextView orderNumberTv;
        TextView dataTv;
        TextView nameTv;
    }
}