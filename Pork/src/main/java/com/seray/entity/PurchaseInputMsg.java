package com.seray.entity;

public class PurchaseInputMsg {

    private String proName;

    private String pluCode;

    private String place;

    private String source;

    private int unitType;

    public PurchaseInputMsg(String proName, String pluCode, String place, String source, int unitType) {
        this.proName = proName;
        this.pluCode = pluCode;
        this.place = place;
        this.source = source;
        this.unitType = unitType;
    }

    public String getProName() {
        return proName;
    }

    public String getPluCode() {
        return pluCode;
    }

    public String getPlace() {
        return place;
    }

    public int getUnitType() {
        return unitType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "PurchaseInputMsg{" +
                "proName='" + proName + '\'' +
                ", pluCode='" + pluCode + '\'' +
                ", place='" + place + '\'' +
                ", source='" + source + '\'' +
                ", unitType=" + unitType +
                '}';
    }
}
