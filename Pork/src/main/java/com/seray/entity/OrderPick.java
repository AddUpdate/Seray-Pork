package com.seray.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

/**
 *订单小计
 */

public class OrderPick  implements Serializable{
    public static final long serialVersionUID = 1L;

    // 订单号
    private String orderNumber;
    // 订单id
    private String orderId;
    // 下单日期
    private String orderDate;
    // 状态码
    private int statusCode;
    // 总数量
    private int totalNumber;
    // 订单总额
    private float amount;
    // 支付方式
    private String paymentMethod;
    // 支付状态
    private String state;

    // 备注信息
    private String remark;
    // 配送客户
    private String name;

    private String tel;
    // 明细
    private List<OrderDetail> detailList;


    public OrderPick() {
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public float getAmount() {
        return amount;
    }

    public BigDecimal getDecimalAmount() {
        return getDecimalValue(amount).setScale(2, BigDecimal.ROUND_UNNECESSARY);
    }


    public void setAmount(float amount) {
        this.amount =  getRoundFloat(2, amount);
    }

    public void setAmount(BigDecimal amount) {
        this.amount = getFloatValue(amount);
    }


    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OrderDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<OrderDetail> detailList) {
        this.detailList = detailList;
    }

}
