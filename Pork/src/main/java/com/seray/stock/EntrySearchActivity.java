package com.seray.stock;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.seray.entity.ApiResult;
import com.seray.entity.ProductsCategory;
import com.seray.entity.PurchaseDetail;
import com.seray.entity.PurchaseOrder;
import com.seray.entity.PurchaseSearch;
import com.seray.entity.PurchaseSubtotal;
import com.seray.entity.Supplier;
import com.seray.http.UploadDataHttp;
import com.seray.pork.BaseActivity;
import com.seray.pork.ProductsSelectActivity;
import com.seray.pork.R;
import com.seray.pork.SupplierSelectActivity;
import com.seray.utils.CustomEditInputListener;
import com.seray.utils.LogUtil;
import com.seray.view.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by pc on 2017/11/12.
 * 查询入库
 */

public class EntrySearchActivity extends BaseActivity {

    private static final int START_DATE = 0;
    private static final int END_DATE = 1;

    private ListView mListView;

    private TextView mBeginDateTv, mEndDateTv, mProNameTv, mSuppliersTv;

    private Button mOkButton, mResetButton;
    private SparseArray<Date> mDateMap = new SparseArray<>();
    private SparseArray<String> parameter = new SparseArray<>();
    private String suppliers = "", products = "";//供应商
    private MyDateSetListener mDateSetListener = null;
    private PurchaseSuppliersAdapter mSuppAdapter = null;
    //  private PurchaseOrderManager mPurchaseManager;

    private List<PurchaseSearch> mSearchList = null;
    private List<PurchaseOrder> result = null;
    private EntrySearchHandler mEntrySearchHandler = new EntrySearchHandler(new
            WeakReference<>(this));
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_search);
        EventBus.getDefault().register(this);
        initView();
        initAdapter();
        initListener();
    }

    private void initView() {
        loadingDialog = new LoadingDialog(this);
        mBeginDateTv = (TextView) findViewById(R.id.entry_search_begin);
        mEndDateTv = (TextView) findViewById(R.id.entry_search_end);
        mProNameTv = (TextView) findViewById(R.id.entry_search_plu);
        mSuppliersTv = (TextView) findViewById(R.id.entry_search_suppliers);

        mOkButton = (Button) findViewById(R.id.entry_search_ok);
        mResetButton = (Button) findViewById(R.id.entry_search_reset);

        mListView = (ListView) findViewById(R.id.entry_search_list_view);
    }

    private void initListener() {
        mResetButton.setOnClickListener(this);
        mOkButton.setOnClickListener(this);
        mBeginDateTv.setOnClickListener(this);
        mEndDateTv.setOnClickListener(this);
        mProNameTv.setOnClickListener(this);
        mSuppliersTv.setOnClickListener(this);
    }

    private void updateAdapter() {

        mSuppAdapter.setNewData(mSearchList);

    }

    private void initAdapter() {
        if (mSuppAdapter == null)
            mSuppAdapter = new PurchaseSuppliersAdapter(EntrySearchActivity.this, mSearchList);
        mListView.setAdapter(mSuppAdapter);
    }


    private void submitSearch(final String startData, final String endData) {
        loadingDialog.showDialog();
        httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.getbatchNumberSeach(suppliers, products, startData, endData);
                if (api.Result) {
                    mSearchList = api.SearchList;
                    mEntrySearchHandler.sendEmptyMessage(1);
                    loadingDialog.dismissDialog();
                    if (mSearchList.size() == 0) {
                        showMessage("查无信息");
                    }
                } else {
                    loadingDialog.dismissDialog();
                    showMessage(api.ResultMessage);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.entry_search_begin:
                createDateDialog(START_DATE);
                break;
            case R.id.entry_search_end:
                createDateDialog(END_DATE);
                break;
            case R.id.entry_search_plu:
                // TODO: 2017/11/28  
                Intent skipIntent = new Intent(EntrySearchActivity.this, ProductsSelectActivity.class);
                //      skipIntent.putExtra("FromWhere", 3);
                startActivity(skipIntent);
                break;
            case R.id.entry_search_suppliers:
                startActivity(new Intent(EntrySearchActivity.this, SupplierSelectActivity.class));
                break;
            case R.id.entry_search_ok:
                QueryConditions();
                break;
            case R.id.entry_search_reset:
                research();
                break;

        }
    }


    private static class EntrySearchHandler extends Handler {

        WeakReference<EntrySearchActivity> mWeakReference;

        EntrySearchHandler(WeakReference<EntrySearchActivity> weakReference) {
            mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            EntrySearchActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        activity.initAdapter();
                        break;
                    case 1:
                        activity.updateAdapter();
                        break;
                    case 3:
                        //     activity.mAdapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }


    public void research() {
        searchReset();
        //   queryAll();
    }

    private void searchReset() {
        mDateMap.clear();
        parameter.clear();
        suppliers = "";
        mBeginDateTv.setText("");
        mEndDateTv.setText("");
        mProNameTv.setText("");
        mSuppliersTv.setText("");
    }

    private void QueryConditions() {
        String startData = "";
        String endData = "";
        if (mBeginDateTv.getText().toString().length() > 0) {
            startData = getStringFormatDate(START_DATE);
        }
        if (mEndDateTv.getText().toString().length() > 0) {
            endData = getStringFormatDate(END_DATE);
        }
        if (startData.equals("") && endData.equals("")) {
            submitSearch(startData, endData);
        } else {
            if (isDateAlready()) {
                submitSearch(startData, endData);
            } else {
                showMessage("搜索日期不正确");
            }
        }
    }

    private int checkQueryType() {
        int queryType = -1;
        if (mDateMap.size() == 0) {
            if (parameter.size() > 0) {
                queryType = 0;//品名查询
            }
        } else if (mDateMap.size() > 0) {
            if (isDateAlready()) {
                if (parameter.size() == 0) {
                    queryType = 1;//时间查询
                } else if (parameter.size() > 0) {
                    queryType = 2;//时间 品名查询
                }
            } else {
                showMessage("搜索日期不正确");
            }
        }
        return queryType;
    }

    private boolean isDateAlready() {
        Date startDate = mDateMap.get(START_DATE);
        Date endDate = mDateMap.get(END_DATE);
        boolean isSelectBeginDate = startDate != null;
        boolean isSelectEndDate = endDate != null;
        if (!isSelectBeginDate)
            return false;
        if (!isSelectEndDate) {
            endDate = getToday();
            mDateMap.put(END_DATE, endDate);
        }
        return startDate.before(endDate);
    }

    private Date getToday() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    private class MyDateSetListener implements DatePickerDialog.OnDateSetListener {

        int dateType;

        MyDateSetListener() {
        }

        void setDateType(int dateType) {
            this.dateType = dateType;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Date data = getData(year, monthOfYear, dayOfMonth);
            mDateMap.put(dateType, data);
            String formatDate = getStringFormatDate(data);
            if (formatDate != null) {
                int beginIndex = formatDate.indexOf("-");
                if (beginIndex != -1)
                    formatDate = formatDate.substring(beginIndex + 1, formatDate.length());
            }
            if (dateType == START_DATE)
                mBeginDateTv.setText(formatDate);
            else
                mEndDateTv.setText(formatDate);
        }
    }

    private void query(int queryType) {
        switch (queryType) {
            case 0:
                sqlQueryThread.submit(new Runnable() {
                    @Override
                    public void run() {
                        //    result = mPurchaseManager.queryByMarketName("1111"); //供应商查询

//                        String beginDate = getStringFormatDate(START_DATE);
//                        String endDate = getStringFormatDate(END_DATE);
//                        result = mPurchaseManager.queryByMarketNameAndDate(beginDate,endDate,"1111");//供应商和日期查询

//                      List<PurchaseDetail> detailList = new ArrayList<>();
//                        detailList = mPurchaseManager.queryByProductName("123");//商品名查询

                        //   result = mPurchaseManager.queryByMarketNameAndProductName("1111","4444");// 商品名 和 供应商
//
                        //     result = mPurchaseManager.queryByMarketNameAndProductNameDate("2017-10-01","2017-11-16","1111","4444");//日期 和 供应商 商品名
                        //    result = mPurchaseManager.queryByDate("2017-10-01","2017-11-16");//时间查询
                        for (int i = 0; i < result.size(); i++) {
                            LogUtil.e("searySubtotal===one", result.get(i).getPurchaseSubtotal().toString());
                            for (int j = 0; j < result.get(i).getPurchaseDetails().size(); j++) {
                                LogUtil.e("detailList===two", result.get(i).getPurchaseDetails().get(j).toString());
                            }
                        }
                        //    mEntrySearchHandler.sendEmptyMessage(1);
                    }
                });
                break;
        }
    }

    private String getStringFormatDate(int key) {
        Date date = mDateMap.get(key);
        return getStringFormatDate(date);
    }

    private String getStringFormatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(date);
    }

    private Date getData(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year); // 年
        c.set(Calendar.MONTH, month); // 月
        c.set(Calendar.DAY_OF_MONTH, day); // 日
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    // from SupplierSelectActivity
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveSupplier(Supplier msg) {
        String name = msg.getSupplierName();
        mSuppliersTv.setText(name);
        suppliers = name;
    }

    // from ProductsSelectActivity
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveProducts(PurchaseDetail msg) {
        String proName = msg.getProductName();
        mProNameTv.setText(proName);
        products = proName;
    }

    private void createDateDialog(int dateType) {
        if (mDateSetListener == null)
            mDateSetListener = new MyDateSetListener();
        mDateSetListener.setDateType(dateType);
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, mDateSetListener, calendar.get
                (Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.reprint_ok), new
                DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dialog.show();
    }

    private class PurchaseSuppliersAdapter extends BaseAdapter {


        private Context mContext;
        LayoutInflater mInflater;
        private List<PurchaseSearch> mData;

        public PurchaseSuppliersAdapter(Context context, List<PurchaseSearch> data) {
            this.mContext = context;
            this.mData = data;
            mInflater = LayoutInflater.from(mContext);
        }

        public void setNewData(@NonNull List<PurchaseSearch> newData) {
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
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.entry_search_subtotal_item, null);
                holder.mSuppliers = (TextView) convertView.findViewById(R.id.entry_search_subtotal_suppliers);
                holder.mBatchNumber = (TextView) convertView.findViewById(R.id.entry_search_subtotal_batch_number);
                holder.mDate = (TextView) convertView.findViewById(R.id.entry_search_subtotal_date);
                holder.mName = (TextView) convertView.findViewById(R.id.entry_search_subtotal_name);
                holder.mWeight = (TextView) convertView.findViewById(R.id.entry_search_subtotal_weight);
                holder.mActualWeight = (TextView) convertView.findViewById(R.id.entry_search_subtotal_actual_weight);
                holder.mState = (TextView) convertView.findViewById(R.id.entry_search_subtotal_state);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            PurchaseSearch dItem = (PurchaseSearch) getItem(position);
            holder.mSuppliers.setText(dItem.getSupplier());
            holder.mBatchNumber.setText(dItem.getBatchNumber());
            holder.mDate.setText(dItem.getStockDate());
            holder.mName.setText(dItem.getProductName());
            holder.mWeight.setText(dItem.getQuantity());
            holder.mActualWeight.setText(dItem.getActualQuantity());
            holder.mState.setText(dItem.getState());
            return convertView;
        }

        private class ViewHolder {
            TextView mDate;
            TextView mSuppliers;
            TextView mBatchNumber;
            TextView mName;
            TextView mWeight;
            TextView mActualWeight;
            TextView mState;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDateMap != null) {
            mDateMap.clear();
            mDateMap = null;
        }
        if (parameter != null) {
            parameter.clear();
            parameter = null;
        }
        mEntrySearchHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
    }

}
