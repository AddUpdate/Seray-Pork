package com.seray.stock;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.entity.PurchaseDetail;
import com.seray.entity.PurchaseSubtotal;

/**
 * Created by pc on 2017/11/8.
 * 进货录入
 */

public abstract class BaseTwoFragment extends Fragment implements View.OnClickListener {

    Misc mMisc;
    Handler mHandler = new Handler(Looper.getMainLooper());

    static final String KEY_SUPPLIERS = "Suppliers"; //供应商
    static final String KEY_BATCH_NUMBER = "BatchNumber";//批次号
    static final String KEY_STOCK_DATE = "StockDate";// 进货日期
    static final String KEY_TEL = "Tel";//电话号码
    static final String KEY_DETAILS_NUMBER = "DetailsNumber";//明细个数
    static final String KEY_TOTAL_AMOUNT = "Total_Amount";//总金额
    static final String KEY_PRODUCT_NAME = "productName"; //商品名
    static final String KEY_PLU_CODE = "PluCode"; //plucode
    static final String KEY_QUANTITY = "Quantity"; //计重 重量
    static final String KEY_NUMBER = "Number"; //计件个数
    static final String KEY_UNIT_PRICE = "UnitPrice"; //单价
    // static final String KEY_PRICE = "Price"; //产品小计金额
    static final String KEY_INPUT = "Input"; //进货单
    static final String KEY_CER = "Cer"; // 合格证

    static final String KEY_PLACE = "StockPlace";

    static final String KEY_SOURCE = "Source";

    Toast mToast;

    ParameterInterface sInterface;

    abstract void clearEditFocus();

    abstract void pushParameter();

    abstract void clearViewContent();

    public void showMessage(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mToast == null)
                    mToast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
                else
                    mToast.setText(msg);
                mToast.show();
            }
        });
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMisc = Misc.newInstance();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sInterface = (ParameterInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        sInterface = null;
    }

    @Override
    public void onClick(View v) {
        mMisc.beep();
    }

    interface ParameterInterface {

        void pushSubtotal(@NonNull PurchaseSubtotal subtotal);

        void pushDetail(@NonNull PurchaseDetail detail);

        void restartStock(int beginIndex);// 0 or 1
    }
}
