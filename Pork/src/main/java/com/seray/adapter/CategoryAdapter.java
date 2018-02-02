package com.seray.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.seray.entity.ProductsCategory;
import com.seray.pork.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2017/12/7.
 */

public class CategoryAdapter extends BaseAdapter{

    private Context mContext;
    LayoutInflater mInflater;
    private List<ProductsCategory> mData;

     public CategoryAdapter(Context context, List<ProductsCategory>data) {
        this.mContext = context;
        this.mData = data;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setNewData(@NonNull List<ProductsCategory> newData) {
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

        ProductsCategory itemDate = mData.get(position);

        ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.separate_group_item, null);
            mHolder.nameTv = (TextView) convertView.findViewById(R.id.separate_group_item_name);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.nameTv.setText(itemDate.getCategoryName());
        return convertView;
    }

    class ViewHolder {
        TextView nameTv;
    }
}
