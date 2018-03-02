package com.seray.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.math.BigDecimal;

import org.greenrobot.greendao.annotation.Generated;
/**
 * 订单明细
 */
@Entity
public class OrderDetail implements Serializable {
    @Transient
    public static final long serialVersionUID = 1L;
    @Id(autoincrement = true)
    private Long id;
    private String orderDetailId; //商品id
    private int actualNumber;//实际件数
    private float actualWeight;// 实际配货量
    private int vehicleNumber;//上车数量
    private float vehicleWeight;//上车重量
    private int state;//是否完成配货  2 未完成  1 完成
    private String isVehicle;//是否上车
    private String orderNumber;// 订单号
    private String productName;// 商品名
    private String alibraryName;//位于库

    private float weight;//重量
    private int number;//件数
    private String orderDate;//订单日期
    private String barCode;//二维码

    private float price;// 下单时价格
    private float discountPrice;// 折扣价
    private float amount;// 金额
    private String picture;

    public OrderDetail(String orderDetailId, int actualNumber, int state,String isVehicle, String orderNumber, String productName,
                       String alibraryName, float weight, int number,
                       String orderDate, String barCode,
                       float price, float discountPrice, float amount,String picture) {
        this.orderDetailId = orderDetailId;
        this.actualNumber = actualNumber;
        this.state = state;
        this.isVehicle = isVehicle;
        this.orderNumber = orderNumber;
        this.productName = productName;
        this.alibraryName = alibraryName;
        this.weight = weight;
        this.number = number;
        this.orderDate = orderDate;
        this.barCode = barCode;
        this.price = price;
        this.discountPrice = discountPrice;
        this.amount = amount;
        this.picture = picture;
    }

    @Generated(hash = 268085433)
    public OrderDetail() {
    }

    @Generated(hash = 2140653985)
    public OrderDetail(Long id, String orderDetailId, int actualNumber, float actualWeight, int vehicleNumber, float vehicleWeight,
            int state, String isVehicle, String orderNumber, String productName, String alibraryName, float weight, int number,
            String orderDate, String barCode, float price, float discountPrice, float amount, String picture) {
        this.id = id;
        this.orderDetailId = orderDetailId;
        this.actualNumber = actualNumber;
        this.actualWeight = actualWeight;
        this.vehicleNumber = vehicleNumber;
        this.vehicleWeight = vehicleWeight;
        this.state = state;
        this.isVehicle = isVehicle;
        this.orderNumber = orderNumber;
        this.productName = productName;
        this.alibraryName = alibraryName;
        this.weight = weight;
        this.number = number;
        this.orderDate = orderDate;
        this.barCode = barCode;
        this.price = price;
        this.discountPrice = discountPrice;
        this.amount = amount;
        this.picture = picture;
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getIsVehicle() {
        return this.isVehicle;
    }

    public void setIsVehicle(String isVehicle) {
        this.isVehicle = isVehicle;
    }
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public float getActualWeight() {
        return actualWeight;
    }

    public float getPrice() {
        return price;
    }

    public float getDiscountPrice() {
        return discountPrice;
    }

    public float getAmount() {
        return amount;
    }


    public BigDecimal getBigDecimalActualWeight() {
        return getDecimalValue(actualWeight).setScale(2, BigDecimal.ROUND_HALF_UP);
    }


    public BigDecimal getDecimalPrice() {
        return getDecimalValue(price).setScale(2, BigDecimal.ROUND_UNNECESSARY);
    }

    public BigDecimal getDecimalDiscountPrice() {
        return getDecimalValue(discountPrice).setScale(2, BigDecimal.ROUND_UNNECESSARY);
    }

    public BigDecimal getDecimalAmount() {
        return getDecimalValue(amount).setScale(2, BigDecimal.ROUND_UNNECESSARY);
    }

    public int getActualNumber() {
        return actualNumber;
    }

    public void setActualNumber(int actualNumber) {
        this.actualNumber = actualNumber;
    }

    public void setActualWeight(float actualWeight) {
        this.actualWeight = getRoundFloat(2, actualWeight);
    }

    public void setPrice(float price) {
        this.price = getRoundFloat(2, price);
    }

    public void setDiscountPrice(float discountPrice) {
        this.discountPrice = getRoundFloat(2, discountPrice);
    }

    public void setAmount(float amount) {
        this.amount = getRoundFloat(2, amount);
    }


    public void setActualWeight(BigDecimal actualWeight) {
        this.actualWeight = getFloatValue(actualWeight);
    }

    public void setPrice(BigDecimal price) {
        this.price = getFloatValue(price);
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = getFloatValue(discountPrice);
    }

    public void setAmount(BigDecimal amount) {
        this.amount = getFloatValue(amount);
    }

    public String getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(String orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getAlibraryName() {
        return alibraryName;
    }

    public void setAlibraryName(String alibraryName) {
        this.alibraryName = alibraryName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public float getWeight() {
        return weight;
    }

    public BigDecimal getBigDecimalWeight() {
        return getDecimalValue(weight).setScale(2, BigDecimal.ROUND_UNNECESSARY);
    }


    public void setWeight(float weight) {
        this.weight = getRoundFloat(2, weight);
    }


    public void setWeight(BigDecimal weight) {
        this.weight = getFloatValue(weight);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(int vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public float getVehicleWeight() {
        return vehicleWeight;
    }


    public BigDecimal getBigDecimalVehicleWeight() {
        return getDecimalValue(vehicleWeight).setScale(2, BigDecimal.ROUND_UNNECESSARY);
    }


    public void setVehicleWeight(float vehicleWeight) {
        this.vehicleWeight = getRoundFloat(2, vehicleWeight);
    }


    public void setVehicleWeight(BigDecimal vehicleWeight) {
        this.vehicleWeight = getFloatValue(vehicleWeight);
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "id=" + id +
                ", orderDetailId='" + orderDetailId + '\'' +
                ", actualNumber=" + actualNumber +
                ", actualWeight=" + actualWeight +
                ", vehicleNumber=" + vehicleNumber +
                ", vehicleWeight=" + vehicleWeight +
                ", state=" + state +
                ", isVehicle='" + isVehicle + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", productName='" + productName + '\'' +
                ", alibraryName='" + alibraryName + '\'' +
                ", weight=" + weight +
                ", number=" + number +
                ", orderDate='" + orderDate + '\'' +
                ", barCode='" + barCode + '\'' +
                ", price=" + price +
                ", discountPrice=" + discountPrice +
                ", amount=" + amount +
                ", picture='" + picture + '\'' +
                '}';
    }
}
