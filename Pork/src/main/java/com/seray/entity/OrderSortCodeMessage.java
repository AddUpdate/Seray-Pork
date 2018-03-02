package com.seray.entity;

/**
 * Created by pc on 2018/2/26.
 */

public class OrderSortCodeMessage {
    String barCode;
    public OrderSortCodeMessage(String barCode){
        this.barCode = barCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    @Override
    public String toString() {
        return "OrderSortCodeMessage{" +
                "barCode='" + barCode + '\'' +
                '}';
    }
}
