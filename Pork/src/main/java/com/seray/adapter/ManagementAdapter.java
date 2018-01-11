package com.seray.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.seray.entity.ProductsCategory;
import com.seray.pork.R;
import java.util.List;

/**
 * Created by pc on 2018/1/8.
 */

public class ManagementAdapter extends BaseAdapter{
    private Context mContext;
    LayoutInflater mInflater;
    private List<String> mData;

    public ManagementAdapter(Context context, List<String>data) {
        this.mContext = context;
        this.mData = data;
        mInflater = LayoutInflater.from(mContext);
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

        String  itemDate = mData.get(position);

        ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.management_grid_item, null);
            mHolder.nameTv = (TextView) convertView.findViewById(R.id.btn_management_tare);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        if (itemDate.equals("返回")){
            mHolder.nameTv.setTextColor(mContext.getResources().getColor(R.color.white));
            mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.fntopagebutton_bottom));
        }else {
            mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.shapeone));
        }
        mHolder.nameTv.setText(itemDate);
        return convertView;
    }

    class ViewHolder {
        TextView nameTv;
    }
}