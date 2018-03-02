package com.seray.entity;

import android.util.SparseArray;

import java.util.Arrays;
import java.util.List;

public class ApiResult {

    public boolean Result = false;

    public int ResultCode;

    public String ResultMessage;

    public String ResultJsonStr;

    public String [] sourceDetail;

    public List<PurchaseSubtotal> SubtotalList;
    public List<PurchaseDetail> DetailList;
    public List<PurchaseSearch> SearchList;
    public List<OrderPick> orderPickList;

    public ApiResult() {
    }

    public ApiResult(boolean result, int resultCode, String msg) {
        this.Result = result;
        this.ResultCode = resultCode;
        this.ResultMessage = msg;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "Result=" + Result +
                ", ResultCode=" + ResultCode +
                ", ResultMessage='" + ResultMessage + '\'' +
                ", ResultJsonStr='" + ResultJsonStr + '\'' +
                ", sourceDetail=" + Arrays.toString(sourceDetail) +
                ", SubtotalList=" + SubtotalList +
                ", DetailList=" + DetailList +
                ", SearchList=" + SearchList +
                ", orderPickList=" + orderPickList +
                '}';
    }
}
