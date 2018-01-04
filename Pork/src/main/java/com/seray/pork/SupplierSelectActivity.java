package com.seray.pork;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.seray.entity.MonitorSupplierMsg;
import com.seray.entity.Supplier;
import com.seray.pork.dao.SupplierManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 供应商选择
 */

public class SupplierSelectActivity extends BaseActivity {
    private ListView listView;
    SupplierSelectAdapter selectAdapter;
    private List<Supplier> supplierList = new ArrayList<>();
    SupplierManager manager = SupplierManager.getInstance();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveSupplier(MonitorSupplierMsg msg) {
        supplierList.clear();
        supplierList = msg.getList();
        selectAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_select);
        EventBus.getDefault().register(this);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv_supplier_select);
        selectAdapter = new SupplierSelectAdapter(this);
        listView.setAdapter(selectAdapter);
    }
    private void initData(){
        supplierList = manager.queryAllSupplier();

    }
    private void initListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              mMisc.beep();
               Supplier supplier = supplierList.get(position);
                EventBus.getDefault().post(new Supplier(supplier.getSupplierId(),supplier.getSupplierName(),
                        supplier.getSupplierAddress(),supplier.getSupplierPhone())); //to entryFragmentOne
                    finish();
            }
        });
    }

    private class SupplierSelectAdapter extends BaseAdapter {

        private Context mContext;

        LayoutInflater mInflater;

        SupplierSelectAdapter(Context context) {
            this.mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return supplierList == null ? 0 : supplierList.size();
        }

        @Override
        public Object getItem(int position) {
            return supplierList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final Supplier itemDate = supplierList.get(position);

            final ViewHolder mHolder;
            if (convertView == null) {
                mHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.supplier_select_listview, null);
                mHolder.nameTv = (TextView) convertView.findViewById(R.id.supplier_select_item_name);
                mHolder.telTv = (TextView) convertView.findViewById(R.id.supplier_select_item_tel);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }

            mHolder.nameTv.setText(itemDate.getSupplierName());
            mHolder.telTv.setText(itemDate.getSupplierPhone());

            return convertView;
        }
    }

    static class ViewHolder {
        TextView nameTv, telTv;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
