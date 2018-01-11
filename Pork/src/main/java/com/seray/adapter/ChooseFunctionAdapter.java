package com.seray.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.seray.pork.R;
import com.seray.utils.LogUtil;

import java.util.List;

/**
 * Created by pc on 2018/1/10.
 */

public class ChooseFunctionAdapter extends BaseAdapter {
    private Context mContext;
    LayoutInflater mInflater;
    private List<String> mData;
    private List<Integer> mPosition;

    public ChooseFunctionAdapter(Context context, List<String> data, List<Integer> mPosition) {
        this.mContext = context;
        this.mData = data;
        this.mPosition = mPosition;
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

        String itemDate = mData.get(position);

        ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.choose_function_item, null);
            mHolder.nameTv = (TextView) convertView.findViewById(R.id.bt_name);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.nameTv.setText(itemDate);
        for (int i = 0; i <mPosition.size() ; i++) {
            if (position == mPosition.get(i)){
                mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.radio_gray));
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView nameTv;
    }
}
