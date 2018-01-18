package com.seray.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.seray.entity.ProductsCategory;
import com.seray.pork.ExcessStockActivity;
import com.seray.pork.FinishProductActivity;
import com.seray.pork.FrozenLibraryActivity;
import com.seray.pork.OrderActivity;
import com.seray.pork.R;
import com.seray.pork.SeparateActivity;
import com.seray.pork.SortActivity;
import com.seray.pork.TemporaryLibraryActivity;
import com.seray.stock.PurchaseActivity;
import com.seray.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2018/1/10.
 */

public class ChooseFunctionAdapter extends BaseAdapter {
    private Context mContext;
    LayoutInflater mInflater;
    private List<String> mData;
    private List<Integer> mPosition;

    public ChooseFunctionAdapter(Context context) {
        this.mContext = context;
//        this.mData = data;
//        this.mPosition = mPosition;
        mInflater = LayoutInflater.from(mContext);
    }
    public void setNewData(@NonNull List<String> newData,List<Integer> nPosition) {
        if (this.mData == null) {
            this.mData = new ArrayList<>();
        }
        if (this.mPosition == null) {
            this.mPosition = new ArrayList<>();
        }
        this.mData.clear();
        this.mData.addAll(newData);
        this.mPosition.clear();
        this.mPosition.addAll(nPosition);
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
//        switch (position) {
//            case 0:
//                mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_icon_1_off));
//                break;
//            case 1:
//                mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_icon_2_off));
//                break;
//            case 2:
//                mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_icon_3_off));
//                break;
//            case 3:
//                mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_icon_4_off));
//                break;
//            case 4:
//                mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_icon_5_off));
//                break;
//            case 5:
//                mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_icon_6_off));
//                break;
//            case 6:
//                mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_icon_7_off));
//                break;
//            case 7:
//                mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_icon_8_off));
//                break;
//        }

                switch (mPosition.get(position)) {
                    case 0:
                        mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_icon_1_on));
                        break;
                    case 1:
                        mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_icon_2_on));
                        break;
                    case 2:
                        mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_icon_3_on));
                        break;
                    case 3:
                        mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_icon_4_on));
                        break;
                    case 4:
                        mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_icon_5_on));
                        break;
                    case 5:
                        mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_icon_6_on));
                        break;
                    case 6:
                        mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_icon_7_on));
                        break;
                    case 7:
                        mHolder.nameTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_icon_8_on));
                        break;
                }



        return convertView;
    }

    class ViewHolder {
        TextView nameTv;
    }
}
