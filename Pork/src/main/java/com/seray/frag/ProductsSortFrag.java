package com.seray.frag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.adapter.OrderProductsDetailAdapter;
import com.seray.entity.OrderProductDetail;
import com.seray.pork.R;
import com.seray.utils.NumFormatUtil;
import com.seray.view.CustomScanDialog;
import com.seray.view.LoadingDialog;

import java.util.List;

/**
 * 扫品名分拣成订单
 */

public class ProductsSortFrag extends Fragment {
    private Button btReturn, btScan;
    private TextView tvProduct;
    OrderProductsDetailAdapter detailAdapter;
    NumFormatUtil mNumUtil;
    private Misc mMisc;
    LoadingDialog loadingDialog;
    View view;
    Toast mToast = null;

    private void myToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_products_sort, container, false);
        initView();
        initListener();
        return view;
    }

    private void initView() {
        mNumUtil = NumFormatUtil.getInstance();
        mMisc = Misc.newInstance();
        loadingDialog = new LoadingDialog(getContext());
        ListView listView = (ListView) view.findViewById(R.id.lv_products_sort);
        detailAdapter = new OrderProductsDetailAdapter(getContext());
        btReturn = (Button) view.findViewById(R.id.bt_products_sort_return);
        btScan = (Button) view.findViewById(R.id.bt_products_sort_scan);
        tvProduct = (TextView) view.findViewById(R.id.tv_products_sort_name);
        listView.setAdapter(detailAdapter);
    }

    private void initListener() {
        btReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                getActivity().finish();
            }
        });
        btScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomScanDialog dialog = new CustomScanDialog(getContext());
                dialog.show();
                dialog.setOnLoadDataListener(new CustomScanDialog.OnLoadDataListener() {
                    @Override
                    public void onLoadSuccess(List<OrderProductDetail> result) {
                        if (result.isEmpty()){
                            tvProduct.setText("");
                            myToast("暂无数据");
                        }else {
                            tvProduct.setText(result.get(0).getCommodityName());
                        }
                        updateAdapter(result);
                        loadingDialog.dismissDialogs();
                    }

                    @Override
                    public void onLoadFailed(String error) {
                        loadingDialog.dismissDialogs();
                        myToast(error);
                    }
                });
            }
        });
    }

    private void updateAdapter(List<OrderProductDetail> data) {
        detailAdapter.setNewData(data);
    }
}
