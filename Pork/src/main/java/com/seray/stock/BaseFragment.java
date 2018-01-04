package com.seray.stock;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    static final String KEY_CER = "Cer";

    static final String KEY_INPUT = "Input";

    static final String KEY_NAME = "ProductName";

    static final String KEY_CODE = "PluCode";

    static final String KEY_UNIT = "Unit";

    static final String KEY_WEIGHT = "Weight";

    static final String KEY_PRICE = "Price";

    static final String KEY_ONLINE_PRICE = "OnlinePrice";

    static final String KEY_STOCK_DATE = "StockDate";

    static final String KEY_OPERATION_DATE = "OperationDate";

    static final String KEY_PLACE = "StockPlace";

    static final String KEY_SOURCE = "Source";

    ParameterInterface mInterface;

    Map<String, Object> parameter;

    Misc mMisc;

    Toast mToast;

    abstract void clearEditFocus();

    abstract void pushParameter();

    abstract void clearViewContent();

    public void showMessage(String msg) {
        if (mToast == null)
            mToast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
        else
            mToast.setText(msg);
        mToast.show();
    }

    public void addParameter(String key, Object value) {
        if (!TextUtils.isEmpty(key) && value != null) {
            if (parameter != null)
                parameter.put(key, value);
            else
                LogUtil.e("parameter is null");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ParameterInterface)
            mInterface = (ParameterInterface) context;
        else
            LogUtil.e("activity must implements FragmentInteraction");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parameter = new HashMap<>();
        mMisc = Misc.newInstance();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mInterface = null;
    }

    @Override
    public void onClick(View v) {
        mMisc.beep();
    }

    interface ParameterInterface {
        void pushParameter(Map<String, Object> parameter);

        void restartStock(boolean isStock, int count);

        void receiveSourceArea(String place, String source);
    }
}
