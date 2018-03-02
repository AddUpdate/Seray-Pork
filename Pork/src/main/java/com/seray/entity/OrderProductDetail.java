package com.seray.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by pc on 2018/2/8.
 */

public class OrderProductDetail implements Serializable{
    public static final long serialVersionUID = 1L;

    private String OrderDetailId;
    private String CustomerName;
    private String CommodityName;
    private String OrderNumber;
    private int Number;
    private float Weight;
    private int ActualNumber;
    private float ActualWeight;
    private String OrderDate;
    private String UserTelephone;

    public OrderProductDetail(String orderDetailId,String customerName,String commodityName,String orderNumber,
                              int number,BigDecimal weight,int actualNumber,BigDecimal actualWeight,
                              String orderDate) {
        this.OrderDetailId = orderDetailId;
        this.CustomerName = customerName;
        this.CommodityName = commodityName;
        this.OrderNumber = orderNumber;
        this.Number = number;
        this.Weight = getFloatValue(weight);
        this.ActualNumber = actualNumber;
        this.ActualWeight = getFloatValue(actualWeight);
        this.OrderDate = orderDate;
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

    public String getOrderDetailId() {
        return OrderDetailId;
    }

    public void setOrderDetailId(String orderDetailId) {
        OrderDetailId = orderDetailId;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getCommodityName() {
        return CommodityName;
    }

    public void setCommodityName(String commodityName) {
        CommodityName = commodityName;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public int getNumber() {
        return Number;
    }

    public void setNumber(int number) {
        Number = number;
    }

    public float getWeight() {
        return Weight;
    }
    public BigDecimal getDecimalWeight() {
        return getDecimalValue(Weight).setScale(2, BigDecimal.ROUND_UNNECESSARY);
    }

    public void setWeight(BigDecimal weight) {
        this.Weight = getFloatValue(weight);
    }
    public void setWeight(float weight) {
        this.Weight = getRoundFloat(2, weight);
    }



    public int getActualNumber() {
        return ActualNumber;
    }

    public void setActualNumber(int actualNumber) {
        ActualNumber = actualNumber;
    }

    public float getActualWeight() {
        return ActualWeight;
    }
    public BigDecimal getDecimalActualWeight() {
        return getDecimalValue(ActualWeight).setScale(2, BigDecimal.ROUND_UNNECESSARY);
    }

    public void setActualWeight(BigDecimal actualWeight) {
        this.ActualWeight = getFloatValue(actualWeight);
    }
    public void setActualWeight(float actualWeight) {
        this.ActualWeight = getRoundFloat(2, actualWeight);
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    @Override
    public String toString() {
        return "OrderProductDetail{" +
                "OrderDetailId='" + OrderDetailId + '\'' +
                ", CustomerName='" + CustomerName + '\'' +
                ", CommodityName='" + CommodityName + '\'' +
                ", OrderNumber='" + OrderNumber + '\'' +
                ", Number=" + Number +
                ", Weight=" + Weight +
                ", ActualNumber=" + ActualNumber +
                ", ActualWeight=" + ActualWeight +
                ", OrderDate='" + OrderDate + '\'' +
                ", UserTelephone='" + UserTelephone + '\'' +
                '}';
    }
}
