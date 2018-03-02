package com.seray.entity;

/**
 * Created by pc on 2018/2/5.
 */

public class ApiParameter {
    String division;
    String dataHelper;
    String id;
    String purchaseNumber;
    String state;

    public ApiParameter() {

    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getDataHelper() {
        return dataHelper;
    }

    public void setDataHelper(String dataHelper) {
        this.dataHelper = dataHelper;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPurchaseNumber() {
        return purchaseNumber;
    }

    public void setPurchaseNumber(String purchaseNumber) {
        this.purchaseNumber = purchaseNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "ApiParameter{" +
                "division='" + division + '\'' +
                ", dataHelper='" + dataHelper + '\'' +
                ", id='" + id + '\'' +
                ", purchaseNumber='" + purchaseNumber + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
