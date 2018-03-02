package com.seray.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 以品名配订单
 */

public class OrderProduct  implements Serializable{
    public static final long serialVersionUID = 1L;

    private String commodityName;
    private int number;
    private float weight;
    private float actualWeight;
    private int actualNumber;
    // 明细
    private List<OrderProductDetail> detailList;

    public OrderProduct (){

    }
    public OrderProduct (String commodityName,int number,BigDecimal weight,BigDecimal actualWeight,
                         int actualNumber){
        this.commodityName = commodityName;
        this.number = number;
        this.weight = getFloatValue(weight);
        this.actualWeight = getFloatValue(actualWeight);
        this.actualNumber = actualNumber;
    }

    private float getFloatValue(BigDecimal value) {
        return value.floatValue();
    }

    private BigDecimal getDecimalValue(float value) {
        String val = Float.toString(value);
        return new BigDecimal(val);
    }

    private float getRoundFloat(int scale, float value) {
        BigDecimal b = new BigDecimal(String.valueOf(value));
        b = b.setScale(scale, BigDecimal.ROUND_HALF_UP);
        return b.floatValue();
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public float getWeight() {
        return weight;
    }
    public BigDecimal getDecimalWeight() {
        return getDecimalValue(weight).setScale(2, BigDecimal.ROUND_UNNECESSARY);
    }

    public void setWeight(BigDecimal weight) {
        this.weight = getFloatValue(weight);
    }
    public void setWeight(float weight) {
        this.weight = getRoundFloat(2, weight);
    }



    public float getActualWeight() {
        return actualWeight;
    }
    public BigDecimal getDecimalActualWeight() {
        return getDecimalValue(actualWeight).setScale(2, BigDecimal.ROUND_UNNECESSARY);
    }

    public void setActualWeight(BigDecimal actualWeight) {
        this.actualWeight = getFloatValue(actualWeight);
    }
    public void setActualWeight(float actualWeight) {
        this.actualWeight = getRoundFloat(2, actualWeight);
    }

    public int getActualNumber() {
        return actualNumber;
    }

    public void setActualNumber(int actualNumber) {
        this.actualNumber = actualNumber;
    }


    public List<OrderProductDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<OrderProductDetail> detailList) {
        this.detailList = detailList;
    }

    @Override
    public String toString() {
        return "OrderProduct{" +
                "commodityName='" + commodityName + '\'' +
                ", number=" + number +
                ", weight=" + weight +
                ", actualWeight=" + actualWeight +
                ", actualNumber=" + actualNumber +
                ", detailList=" + detailList +
                '}';
    }
}
