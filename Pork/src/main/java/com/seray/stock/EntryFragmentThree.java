package com.seray.stock;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.seray.entity.ApiResult;
import com.seray.entity.PurchaseDetail;
import com.seray.entity.PurchaseOrder;
import com.seray.entity.PurchaseSubtotal;
import com.seray.http.UploadDataHttp;
import com.seray.pork.BaseActivity;
import com.seray.pork.R;
import com.seray.pork.dao.PurchaseOrderManager;
import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;
import com.seray.view.LoadingDialog;
import com.seray.view.PromptDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

public class EntryFragmentThree extends BaseTwoFragment {

    private Button submitBt;
    private TextView suppliersTx, batchNumberTx, dateTx, telTx, numberTx, inputTx;

    private ListView mListView;

    private PurchaseOrder mOrder;
    private PurchaseSubtotal subtotal;
    private List<PurchaseDetail> detailList;

    private PurchaseDetailAdapter mAdapter = null;
    LoadingDialog loadingDialog;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receivePurchaseOrder(@NonNull PurchaseOrder order) {
        mOrder = order;
        subtotal = mOrder.getPurchaseSubtotal();
        detailList = mOrder.getPurchaseDetails();
        if (subtotal.getDetailsNumber() < detailList.size())
            subtotal.setDetailsNumber(detailList.size());
        updateViewDate();
        updateAdapter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        loadingDialog = new LoadingDialog(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entry_fragment_three, container, false);
        initViewAndListener(view);
        return view;
    }

    public void initViewAndListener(View view) {

        submitBt = (Button) view.findViewById(R.id.entry_frag_three_submit);
        mListView = (ListView) view.findViewById(R.id.entry_frag_three_list_view);

        suppliersTx = (TextView) view.findViewById(R.id.entry_frag_three_suppliers);
        batchNumberTx = (TextView) view.findViewById(R.id.entry_frag_three_batch_number);
        dateTx = (TextView) view.findViewById(R.id.entry_frag_three_date);
        telTx = (TextView) view.findViewById(R.id.entry_frag_three_tel);
        numberTx = (TextView) view.findViewById(R.id.entry_frag_three_number);
        inputTx = (TextView) view.findViewById(R.id.entry_frag_three_input);

        submitBt.setOnClickListener(this);
        initAdapter();
    }

    private void updateViewDate() {
        suppliersTx.setText(subtotal.getSupplier());
        batchNumberTx.setText(subtotal.getBatchNumber());
        String stockDate = subtotal.getStockDate();

        dateTx.setText(stockDate);
        telTx.setText(TextUtils.isEmpty(subtotal.getTel()) ? "X" : subtotal.getTel());
        numberTx.setText(String.valueOf(subtotal.getDetailsNumber()));
        if (TextUtils.isEmpty(subtotal.getPurOrderImg()))
            inputTx.setText("X");
        else
            inputTx.setText("√");
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.entry_frag_three_submit:
                if (detailList != null && !detailList.isEmpty() && subtotal != null) {
                    if (!TextUtils.isEmpty(subtotal.getSupplier()) &&
                            !TextUtils.isEmpty(subtotal.getBatchNumber()) &&
                            !TextUtils.isEmpty(subtotal.getStockDate())) {
                        showNormalDialog("确定提交吗？");
                    } else
                        showMessage("请将供应商信息填写完整");
                } else
                    showMessage("请将信息填写完整");
                break;
        }
    }


    private void updateBatch() {
        loadingDialog.showDialog();
        BaseActivity.httpQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ApiResult api = UploadDataHttp.uploadDataPost(changeArrayDateToJson());
                sqlInsert(api.sourceDetail);
                if (api.Result) {
                    submitBt.post(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.dismissDialog();
                            sInterface.restartStock(0);
                        }
                    });
                }
                loadingDialog.dismissDialog();
                showMessage(api.ResultMessage);
            }
        });
    }

    private String changeArrayDateToJson() {
        String jsonString;
        JSONArray jsonArray;
        JSONObject object;
        jsonArray = new JSONArray();
        for (int i = 0; i < detailList.size(); i++) {
            object = new JSONObject();
            try {
                object.put("SupplierName", subtotal.getSupplier());
                object.put("PurchaseNumber", subtotal.getBatchNumber());
                object.put("PurchaseDate", subtotal.getStockDate());
                object.put("SupplierPhone", subtotal.getTel());
                object.put("Certificate", subtotal.getPurOrderImg());
                object.put("ItemName", detailList.get(i).getProductName());
                object.put("PLU", detailList.get(i).getPluCode());
                object.put("ParentsItemName", detailList.get(i).getParentsItemName());// TODO: 2017/11/27
                object.put("Number", detailList.get(i).getNumber());
                object.put("Weight", detailList.get(i).getDecimalQuantity());
                object.put("WeightCompany", detailList.get(i).getUnit());
                object.put("UnitPrice", detailList.get(i).getDecimalUnitPrice());
                object.put("OrderPicture", detailList.get(i).getCertificateImg());
                object.put("ActualWeight", BigDecimal.ZERO);
                object.put("Remarks", detailList.get(i).getRemark());
                object.put("State", "2");
                jsonArray.put(object); //把JSONObject对象装入jsonArray数组里面
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        jsonString = jsonArray.toString(); //把JSONObject转换成json格式的字符串

        return jsonString;
    }

    private void sqlInsert(String[] index) {
        for (int j = 0; j < detailList.size(); j++) {
            detailList.get(j).setSubmit(1);
            detailList.get(j).setState(2);
            for (int i = 0; i < index.length; i++) {
                if (index[i].equals(j + "")) {
                    detailList.get(j).setSubmit(2);
                }
            }
        }
        PurchaseOrderManager manager = PurchaseOrderManager.getInstance();
        manager.insertOrder(mOrder);
    }

    private void updateAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    private void initAdapter() {
        if (mAdapter == null)
            mAdapter = new PurchaseDetailAdapter(getContext());
        mListView.setAdapter(mAdapter);
    }

    private class PurchaseDetailAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;

        PurchaseDetailAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return detailList == null ? 0 : detailList.size();
        }

        @Override
        public Object getItem(int position) {
            return detailList.get(position);
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
                convertView = mLayoutInflater.inflate(R.layout.entry_frag_three_item, null);
                holder.mProNameTv = (TextView) convertView.findViewById(R.id.entry_frag_three_item_name);
                holder.mPluCodeTv = (TextView) convertView.findViewById(R.id.entry_frag_three_item_plu);
                holder.mQuantityTv = (TextView) convertView.findViewById(R.id
                        .entry_frag_three_item_quantity);
                holder.mNumber = (TextView) convertView.findViewById(R.id.entry_frag_three_item_number);
                holder.mUnitPriceTv = (TextView) convertView.findViewById(R.id.entry_frag_three_item_unit_price);
                holder.mCertificateTv = (TextView) convertView.findViewById(R.id
                        .entry_frag_three_item_cer);
                holder.mDeleteTv = (TextView) convertView.findViewById(R.id.entry_frag_three_item_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            PurchaseDetail dItem = (PurchaseDetail) getItem(position);
            holder.mProNameTv.setText(dItem.getProductName());
            holder.mPluCodeTv.setText(dItem.getPluCode());
            if (dItem.getUnit().equals("KG")) {
                holder.mQuantityTv.setText(String.valueOf(dItem.getDecimalQuantity()) + dItem.getUnit());
                holder.mNumber.setText(dItem.getNumber() + "");
            } else {
                holder.mQuantityTv.setText(String.valueOf(dItem.getDecimalQuantity()));
                holder.mNumber.setText(dItem.getNumber() + dItem.getUnit());
            }
            holder.mUnitPriceTv.setText(String.valueOf(dItem.getDecimalUnitPrice()));
            holder.mCertificateTv.setText(TextUtils.isEmpty(dItem.getCertificateImg()) ? "X" : "√");
            holder.mDeleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PurchaseDetail remove = detailList.remove(position);
                    LogUtil.e("Fragment", "Detail HashCode = " + remove.hashCode());
                    remove = null;
                    mOrder.setPurchaseDetails(detailList);
                    updateAdapter();
                    showMessage("删除成功");
                }
            });
            return convertView;
        }

        private class ViewHolder {
            TextView mProNameTv;
            TextView mPluCodeTv;
            TextView mQuantityTv;
            TextView mUnitPriceTv;
            TextView mNumber;
            TextView mCertificateTv;
            TextView mDeleteTv;
        }
    }

    /*
     *  信息确认提示
     */
    private void showNormalDialog(String weightContent) {

        new PromptDialog(getContext(), R.style.Dialog, weightContent, new PromptDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    loadingDialog.showDialog();
                    mMisc.beep();
                    updateBatch();
                    dialog.dismiss();
                } else {
                    mMisc.beep();
                }
            }
        }).setTitle("提示").show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    void clearEditFocus() {

    }

    @Override
    void pushParameter() {
        LogUtil.e("Four", "PushParameter");
    }

    @Override
    void clearViewContent() {
        suppliersTx.setText("");
        batchNumberTx.setText("");
        dateTx.setText("");
        telTx.setText("");
        numberTx.setText("");
        inputTx.setText("");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
