package com.seray.entity;

import com.seray.utils.LogUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.math.BigDecimal;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by pc on 2017/11/8.
 * 进货录入小计明细
 */
@Entity
public class PurchaseDetail implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private Long id;
    private long ownerId;//与父级id关联
    private int weightId;//钩子标识
    private String productId; // 确定重量商品id
    private String ParentsItemName;//  父级商品名
    private String productName;//商品名
    private String pluCode;//编码
    private float unitPrice;//单价
    private float quantity;//重量
    private int number;//计件下的数量
    private float ActualWeight;//实际重量
    private int actualNumber;//实际数量
    private String certificateImg;//合格证图片
    private float sellQuantity;//销售数量
    private String unit;//单位（kg,箱，袋）
    private String remark;//备注
    private int state; // 确认是否入库        1已核实   2未核实
    private int submit; //是否成功提交至后台  1 已提交  2未提交

    public PurchaseDetail() {

    }

    public PurchaseDetail(String productName, String pluCode,
                          String unit, String parentsItemName) {
        this.productName = productName;
        this.pluCode = pluCode;
        this.unit = unit;
        this.ParentsItemName = parentsItemName;
    }

    public PurchaseDetail(String productName, String pluCode, BigDecimal quantity,
                          BigDecimal unitPrice, String certificateImg,
                          BigDecimal sellQuantity, String unit, String remark) {
        this.productName = productName;
        this.pluCode = pluCode;
        this.certificateImg = certificateImg;
        this.quantity = getFloatValue(quantity);
        this.unitPrice = getFloatValue(unitPrice);
        this.sellQuantity = getFloatValue(sellQuantity);
        this.unit = unit;
        this.remark = remark;
    }

    @Generated(hash = 1676503986)
    public PurchaseDetail(Long id, long ownerId, int weightId, String productId, String ParentsItemName, String productName,
                          String pluCode, float unitPrice, float quantity, int number, float ActualWeight, int actualNumber,
                          String certificateImg, float sellQuantity, String unit, String remark, int state, int submit) {
        this.id = id;
        this.ownerId = ownerId;
        this.weightId = weightId;
        this.productId = productId;
        this.ParentsItemName = ParentsItemName;
        this.productName = productName;
        this.pluCode = pluCode;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.number = number;
        this.ActualWeight = ActualWeight;
        this.actualNumber = actualNumber;
        this.certificateImg = certificateImg;
        this.sellQuantity = sellQuantity;
        this.unit = unit;
        this.remark = remark;
        this.state = state;
        this.submit = submit;
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

    public int getWeightId() {
        return weightId;
    }

    public void setWeightId(int weightId) {
        this.weightId = weightId;
    }

    public float getQuantity() {
        return quantity;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public float getActualWeight() {
        return ActualWeight;
    }

    public float getSellQuantity() {
        return sellQuantity;
    }

    public BigDecimal getDecimalActualWeight() {
        return getDecimalValue(ActualWeight).setScale(3, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getDecimalQuantity() {
        return getDecimalValue(quantity).setScale(3, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getDecimalUnitPrice() {
        return getDecimalValue(unitPrice).setScale(3, BigDecimal.ROUND_UNNECESSARY);
    }

    public BigDecimal getDecimalSellQuantity() {
        return getDecimalValue(sellQuantity).setScale(3, BigDecimal.ROUND_HALF_UP);
    }

    public void setActualWeight(BigDecimal actualWeight) {
        ActualWeight = getFloatValue(actualWeight);
    }


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getParentsItemName() {
        return ParentsItemName;
    }

    public void setParentsItemName(String parentsItemName) {
        ParentsItemName = parentsItemName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getSubmit() {
        return submit;
    }

    public void setSubmit(int submit) {
        this.submit = submit;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPluCode() {
        return pluCode;
    }

    public void setPluCode(String pluCode) {
        this.pluCode = pluCode;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = getFloatValue(quantity);
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = getFloatValue(unitPrice);
    }

    public String getCertificateImg() {
        return certificateImg;
    }

    public void setCertificateImg(String certificateImg) {
        this.certificateImg = certificateImg;
    }

    public void setSellQuantity(BigDecimal sellQuantity) {
        this.sellQuantity = getFloatValue(sellQuantity);
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setQuantity(float quantity) {
        this.quantity = getRoundFloat(3, quantity);
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = getRoundFloat(2, unitPrice);
    }

    public void setActualWeight(float ActualWeight) {
        this.ActualWeight = getRoundFloat(3, ActualWeight);
    }

    public void setSellQuantity(float sellQuantity) {
        this.sellQuantity = getRoundFloat(3, sellQuantity);
    }

    public long getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public int getActualNumber() {
        return this.actualNumber;
    }

    public void setActualNumber(int actualNumber) {
        this.actualNumber = actualNumber;
    }

    @Override
    public String toString() {
        return "PurchaseDetail{" +
                "id=" + id +
                ", productId='" + productId + '\'' +
                ", ParentsItemName='" + ParentsItemName + '\'' +
                ", productName='" + productName + '\'' +
                ", pluCode='" + pluCode + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", number=" + number +
                ", ActualWeight=" + ActualWeight +
                ", certificateImg='" + certificateImg + '\'' +
                ", sellQuantity=" + sellQuantity +
                ", unit='" + unit + '\'' +
                ", remark='" + remark + '\'' +
                ", state=" + state +
                ", submit=" + submit +
                '}';
    }
}
