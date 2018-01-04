package com.seray.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.entity.TareItem;
import com.seray.pork.R;
import com.seray.pork.dao.ConfigManager;

import java.util.ArrayList;
import java.util.List;


/**
 * 扣重
 */

public class DeductTareDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private Misc mMisc;
    private TextView titleView;
    private ListView mListView;
    private List<TareItem> mData = new ArrayList<>();
    private DeductTareDialog mDialog;
    private Button posBtn, negBtn;
    ConfigManager configManager = ConfigManager.getInstance();
    private OnPositiveClickListener positiveClickListener;

    private OnNegativeClickListener negativeClickListener;

    public DeductTareDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
        this.mContext = context;
        this.mMisc = Misc.newInstance();
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                String txt;
                if (mLastSelectedTextView == null)
                    return true;
                if (mData.get(lastPosition).getCount() == 0)
                    txt = "";
                else
                    txt = String.valueOf(mData.get(lastPosition).getCount());
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mMisc.beep();
                    if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent.KEYCODE_NUMPAD_9) {
                        txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;
                    } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                        txt += keyCode - KeyEvent.KEYCODE_0;
                    } else if (keyCode == KeyEvent.KEYCODE_NUM_LOCK) {
                        if (!txt.isEmpty())
                            txt = txt.substring(0, txt.length() - 1);
                    } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
                        txt = "0";
                    } else {
                        return false;
                    }
                    mLastSelectedTextView.setText(txt);
                    if (txt.length() > 2)
                        txt = txt.substring(0, 2);
                    TareItem myItemDate = mData.get(lastPosition);
                    mData.set(lastPosition, myItemDate).setCount(Integer.valueOf(txt));
                    return true;
                }
                return true;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deduct_tare_dialog);
        mDialog = this;
        initView();
        initListener();

        mData.add(new TareItem(0, "车重", configManager.query("tareCar").equals("")?"0":configManager.query("tareCar")));
        mData.add(new TareItem(1, "小筐", configManager.query("tareSmall").equals("")?"0":configManager.query("tareSmall")));
        mData.add(new TareItem(2, "中筐", configManager.query("tareMedium").equals("")?"0":configManager.query("tareMedium")));
        mData.add(new TareItem(3, "大筐", configManager.query("tareBig").equals("")?"0":configManager.query("tareBig")));
        DeductTareAdapter mAdapter = new DeductTareAdapter(mData);
        mListView.setAdapter(mAdapter);
    }

    private TextView mLastSelectedTextView;

    private int lastPosition;

    private class DeductTareAdapter extends BaseAdapter {

        private Button  mInputSelected;

        LayoutInflater mInflater;

        List<TareItem> mData;

        DeductTareAdapter(List<TareItem> data) {
            mData = data;
            mInflater = LayoutInflater.from(getContext());
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

            final TareItem itemDate = mData.get(position);

            final ViewHolder mHolder;
            if (convertView == null) {
                mHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.deduct_tare_item_layout, null);
                mHolder.btnType = (TextView) convertView.findViewById(R.id.deduct_tare_item_type);
                mHolder.divideTv = (TextView) convertView.findViewById(R.id.deduct_tare_item_reduce);
                mHolder.contentTv = (TextView) convertView.findViewById(R.id.deduct_tare_item_data);
                mHolder.addTv = (TextView) convertView.findViewById(R.id.deduct_tare_item_increase);
                mHolder.btnRest = (Button) convertView.findViewById(R.id.deduct_tare_item_input);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }

            mHolder.btnType.setText(itemDate.getName()+"("+itemDate.getNetStr()+")");

            mHolder.divideTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMisc.beep();
                    int count = itemDate.getCount();
                    count--;
                    if (count < 0)
                        count = 0;
                    itemDate.setCount(count);
                    mHolder.contentTv.setText(String.valueOf(count));
                }
            });

            mHolder.addTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMisc.beep();
                    int count = itemDate.getCount();
                    count++;
                    if (count > 99)
                        count = 99;
                    itemDate.setCount(count);
                    mHolder.contentTv.setText(String.valueOf(count));
                }
            });

            mHolder.btnRest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMisc.beep();
                    if (mInputSelected != null) {
                        mInputSelected.setBackgroundResource(R.drawable.radio_gray);
                    }
                    lastPosition = position;
                    mLastSelectedTextView = mHolder.contentTv;
                    mInputSelected = mHolder.btnRest;
                    mInputSelected.setBackgroundResource(R.drawable.radio_selected);

                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        TextView btnType,divideTv, contentTv, addTv;
        Button btnRest;
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.deduct_tare_dialog_list);
        titleView = (TextView) findViewById(R.id.deduct_tare_dialog_title);
        posBtn = (Button) findViewById(R.id.bt_deduct_tare_dialog_positive);
        negBtn = (Button) findViewById(R.id.bt_deduct_tare_dialog_negative);
    }

    private void initListener() {
        posBtn.setOnClickListener(this);
        negBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mMisc.beep();
        switch (v.getId()) {
            case R.id.bt_deduct_tare_dialog_positive:
                if (positiveClickListener != null) {
                    for (int i = 0; i < mData.size(); i++) {
                        TareItem date = mData.get(i);
                        boolean isUse = date.getCount() == 0;
                        if (isUse)
                            mData.remove(i);
                    }
                    positiveClickListener.onPositiveClick(mDialog, mData);
                }
                break;
            case R.id.bt_deduct_tare_dialog_negative:
                if (negativeClickListener != null)
                    negativeClickListener.onNegativeClick(mDialog);
                break;
        }
    }

    public void setTitle(@StringRes int id) {
        if (titleView != null)
            this.titleView.setText(getString(id));
    }

    public void setTitle(@NonNull String title) {
        if (titleView != null)
            this.titleView.setText(title);
    }


    public void setOnPositiveClickListener(@StringRes int str, OnPositiveClickListener positiveClickListener) {
        Button posBtn = (Button) findViewById(R.id.bt_deduct_tare_dialog_positive);
        posBtn.setText(getString(str));
        this.positiveClickListener = positiveClickListener;
    }

    public void setOnNegativeClickListener(@StringRes int str, OnNegativeClickListener negativeClickListener) {
        Button negBtn = (Button) findViewById(R.id.bt_deduct_tare_dialog_negative);
        negBtn.setText(getString(str));
        this.negativeClickListener = negativeClickListener;
    }

    public interface OnPositiveClickListener {
        /**
         * 筛选过后的count ！= 0
         */
        void onPositiveClick(DeductTareDialog dialog, List<TareItem> data);
    }

    public interface OnNegativeClickListener {
        void onNegativeClick(DeductTareDialog dialog);
    }

    private String getString(@StringRes int id) {
        return mContext.getResources().getString(id);
    }

}
