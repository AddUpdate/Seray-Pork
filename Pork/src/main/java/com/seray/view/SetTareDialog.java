package com.seray.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.entity.TareItem;
import com.seray.pork.R;
import com.seray.pork.dao.ConfigManager;
import com.seray.utils.LogUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * 设置扣重值
 */

public class SetTareDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private Misc mMisc;
    private TextView titleView;
    private ListView mListView;
    private List<TareItem> mData = new ArrayList<>();
    private SetTareDialog mDialog;
    private RelativeLayout layout;
    SetTareAdapter mAdapter;
    private OnPositiveClickListener positiveClickListener;

    private OnNegativeClickListener negativeClickListener;
    private Button posBtn, negBtn;
    ConfigManager configManager = ConfigManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_tare_dialog);
        mDialog = this;
        initView();
        initListener();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.set_tare_dialog_list);
        titleView = (TextView) findViewById(R.id.set_tare_dialog_title);
        posBtn = (Button) findViewById(R.id.bt_set_tare_dialog_positive);
        negBtn = (Button) findViewById(R.id.bt_set_tare_dialog_negative);
        layout = (RelativeLayout) findViewById(R.id.rl_set_tare_dialog);
    }

    private void initListener() {
        posBtn.setOnClickListener(this);
        negBtn.setOnClickListener(this);
        mAdapter = new SetTareAdapter();
        mListView.setAdapter(mAdapter);
    }

    public SetTareDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
        this.mContext = context;
        this.mMisc = Misc.newInstance();
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                String txt;
                if (mLastSelectedTextView == null)
                    return true;
                if (mData.get(lastPosition).getNetStr() == null) {
                    txt = "";
                } else {
                    String data = mData.get(lastPosition).getNetStr();
                    if (data.contains(".")) {
                        String str = data.substring(data.indexOf(".") + 1, data.length());
                        if (Integer.valueOf(str) > 0) {
                            txt = data;
                            LogUtil.e("2", txt);
                        } else {
                            txt = data.substring(0, data.indexOf(".") + 1);
                            LogUtil.e("3", txt);
                        }
                    } else {
                        txt = data;
                    }
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mMisc.beep();
                    LogUtil.e("ACTION_DOWN", txt);
                    if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent
                            .KEYCODE_NUMPAD_9) {
                        int i = txt.indexOf(".");
                        if (i < 0 || (i > -1 && i > txt.length() - 4))
                            txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;
                    } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent
                            .KEYCODE_9) {
                        int i = txt.indexOf(".");
                        if (i < 0 || (i > -1 && i > txt.length() - 4))
                            txt += keyCode - KeyEvent.KEYCODE_0;
                    } else if (keyCode == KeyEvent.KEYCODE_E) {
                        if (!txt.contains("."))
                            txt += ".";
                    } else if (keyCode == KeyEvent.KEYCODE_NUM_LOCK) {
                        if (!txt.isEmpty())
                            txt = txt.substring(0, txt.length() - 1);
                    } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
                        txt = "";
                    } else {
                        return false;
                    }
                    mLastSelectedTextView.setText(txt);
                    int length = txt.length();
                    int index = 0;
                    if (txt.contains(".")) {
                        index = txt.lastIndexOf(".");
                        if (index == length - 1) {
                            if (index == 0)
                                txt = "";
                            else
                                txt += "0";
                        }
                    }
                    if (length > 7) {
                        if (index >= 6)
                            txt = txt.substring(0, 6);
                        else
                            txt = txt.substring(0, 7);
                    }
                    TareItem myItemDate = mData.get(lastPosition);
                    mData.set(lastPosition, myItemDate).setNetStr(txt);
                    return true;
                }
                return true;
            }
        });
    }

    private TextView mLastSelectedTextView;

    private int lastPosition;

    private class SetTareAdapter extends BaseAdapter {

        private Button mInputSelected;

        LayoutInflater mInflater;

        List<TareItem> mData;

        SetTareAdapter() {
            mInflater = LayoutInflater.from(getContext());
        }

        public void setNewData(@NonNull List<TareItem> newData) {
            if (this.mData == null) {
                this.mData = new ArrayList<>();
            }
            //   this.mData.clear();
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

            final TareItem itemDate = mData.get(position);

            final ViewHolder mHolder;
            if (convertView == null) {
                mHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.set_tare_item_layout, null);
                mHolder.btnType = (TextView) convertView.findViewById(R.id.set_tare_item_type);
                mHolder.weightTv = (TextView) convertView.findViewById(R.id.set_tare_item_data);
                mHolder.btnRest = (Button) convertView.findViewById(R.id.set_tare_item_input);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }

            mHolder.btnType.setText(itemDate.getName());
            mHolder.weightTv.setText(itemDate.getNetStr());
            mHolder.btnRest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMisc.beep();
                    if (mInputSelected != null) {
                        mInputSelected.setBackgroundResource(R.drawable.radio_gray);
                    }
                    lastPosition = position;
                    mLastSelectedTextView = mHolder.weightTv;
                    mInputSelected = mHolder.btnRest;
                    mInputSelected.setBackgroundResource(R.drawable.radio_selected);
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        TextView btnType, weightTv;
        Button btnRest;
    }


    public void setTitle(@StringRes int id) {
        if (titleView != null)
            this.titleView.setText(getString(id));
    }

    public void setTitle(@NonNull String title) {
        if (titleView != null) {
            this.titleView.setText(title);
            updater(title);
        }
    }

    public void updater(String type) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int height = dm.heightPixels;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        if (type.equals("设置配货浮动率")) {
            mData.add(new TareItem(4, "浮动率(0~1)", configManager.query("floatRange")));
            params.height=height/2;
        } else {
            mData.add(new TareItem(0, "车重", configManager.query("tareCar")));
            mData.add(new TareItem(1, "小筐", configManager.query("tareSmall")));
            mData.add(new TareItem(2, "中筐", configManager.query("tareMedium")));
            mData.add(new TareItem(3, "大筐", configManager.query("tareBig")));
            params.height=height-40;
        }
        layout.setLayoutParams(params);
        mAdapter.setNewData(mData);
    }

    @Override
    public void onClick(View v) {
        mMisc.beep();
        switch (v.getId()) {
            case R.id.bt_set_tare_dialog_positive:
                if (positiveClickListener != null) {
                    for (int i = 0; i < mData.size(); i++) {
                        TareItem date = mData.get(i);
                        boolean isUse = date.getNetStr() == null;
                        if (isUse)
                            mData.remove(i);
//                        Config config = new Config();
//                        switch (date.getType()) {
//                            case 0:
//                                config.setKey("tareCar");
//                                break;
//                            case 1:
//                                config.setKey("tareSmall");
//                                break;
//                            case 2:
//                                config.setKey("tareMedium");
//                                break;
//                            case 3:
//                                config.setKey("tareBig");
//                                break;
//                        }
//                        config.setValue(date.getNetStr());
//                        list.add(config);
                    }
                    positiveClickListener.onPositiveClick(mDialog, mData);
                }
                break;
            case R.id.bt_set_tare_dialog_negative:
                if (negativeClickListener != null)
                    negativeClickListener.onNegativeClick(mDialog);
                break;
        }
    }

    public void setOnPositiveClickListener(@StringRes int str, OnPositiveClickListener positiveClickListener) {
        Button posBtn = (Button) findViewById(R.id.bt_set_tare_dialog_positive);
        posBtn.setText(getString(str));
        this.positiveClickListener = positiveClickListener;
    }

    public void setOnNegativeClickListener(@StringRes int str, OnNegativeClickListener negativeClickListener) {
        Button negBtn = (Button) findViewById(R.id.bt_set_tare_dialog_negative);
        negBtn.setText(getString(str));
        this.negativeClickListener = negativeClickListener;
    }

    public interface OnPositiveClickListener {
        /**
         * 筛选过后的count ！= 0
         */
        void onPositiveClick(SetTareDialog dialog, List<TareItem> data);
    }

    public interface OnNegativeClickListener {
        void onNegativeClick(SetTareDialog dialog);
    }

    private String getString(@StringRes int id) {
        return mContext.getResources().getString(id);
    }

}
