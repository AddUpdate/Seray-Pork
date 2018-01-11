package com.seray.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.math.BigDecimal;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 操作日志
 */
@Entity
public class OperationLog {
    @Id(autoincrement = true)
    private Long id;
    private String comeLibraryId;
    private String comeLibraryName;
    private String goLibraryId;
    private String productName;
    private String plu;
    private float weight;
    private int number;
    private String unit;//单位（kg,箱，袋）
    private String date;
    private String picture;
    private int state;  //1 已回收  2未回收

    public OperationLog(String comeLibraryId, String comeLibraryName,
                        String goLibraryId, String productName, String plu, BigDecimal weight,
                        int number, String unit, String date,String picture, int state){
        this.comeLibraryId = comeLibraryId;
        this.comeLibraryName = comeLibraryName;
        this.goLibraryId = goLibraryId;
        this.productName = productName;
        this.plu = plu;
        this.weight = getFloatValue(weight);
        this.number = number;
        this.unit = unit;
        this.date = date;
        this.picture = picture;
        this.state = state;

    }

    @Generated(hash = 1786373372)
    public OperationLog(Long id, String comeLibraryId, String comeLibraryName,
            String goLibraryId, String productName, String plu, float weight, int number,
            String unit, String date, String picture, int state) {
        this.id = id;
        this.comeLibraryId = comeLibraryId;
        this.comeLibraryName = comeLibraryName;
        this.goLibraryId = goLibraryId;
        this.productName = productName;
        this.plu = plu;
        this.weight = weight;
        this.number = number;
        this.unit = unit;
        this.date = date;
        this.picture = picture;
        this.state = state;
    }

    @Generated(hash = 2033303483)
    public OperationLog() {
    }
    private float getFloatValue(BigDecimal value) {
        return value.floatValue();
    }
    private BigDecimal getDecimalValue(float value){
        String val = Float.toString(value);
        BigDecimal decimal = new BigDecimal(val);
        decimal.setScale(3, BigDecimal.ROUND_HALF_UP);
        return decimal;
    }
    private float getRoundFloat(int scale, float value) {
        BigDecimal b = new BigDecimal(String.valueOf(value));
        b = b.setScale(scale, BigDecimal.ROUND_HALF_UP);
        return b.floatValue();
    }
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getWeight() {
        return weight;
    }

    public BigDecimal getDecimalWeight() {
        return getDecimalValue(weight);
    }

    public void setWeight(BigDecimal weight) {
        this.weight = getFloatValue(weight);
    }

    public void setWeight(float weight) {
        this.weight = getRoundFloat(3,weight);
    }

    public String getComeLibraryId() {
        return comeLibraryId;
    }

    public void setComeLibraryId(String comeLibraryId) {
        this.comeLibraryId = comeLibraryId;
    }

    public String getComeLibraryName() {
        return comeLibraryName;
    }

    public void setComeLibraryName(String comeLibraryName) {
        this.comeLibraryName = comeLibraryName;
    }

    public String getGoLibraryId() {
        return goLibraryId;
    }

    public void setGoLibraryId(String goLibraryId) {
        this.goLibraryId = goLibraryId;
    }

    public String getPlu() {
        return plu;
    }

    public void setPlu(String plu) {
        this.plu = plu;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUnit() {
        return this.unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public int getNumber() {
        return this.number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    public String getPicture() {
        return this.picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
