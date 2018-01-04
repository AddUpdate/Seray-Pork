package com.seray.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Products  {
    @Id(autoincrement = true)
    private Long id;
    private String ProductId;
    private String ProductName;
    private String PluCode;
    private String CreatedAt;
    private int StatusCode;
    private String Remark;
    private String ParentId;
    private float UnitPrice;
    private int MeasurementMethod; //计算单位

    public Products() {
    }

    public Products(String productId, String productName, String pluCode, String createdAt,
                    int statusCode, String remark, String parentId, float unitPrice, int measurementMethod) {
        this.ProductId = productId;
        this.ProductName = productName;
        this.PluCode = pluCode;
        this.CreatedAt = createdAt;
        this.StatusCode = statusCode;
        this.Remark = remark;
        this.ParentId = parentId;
        this.UnitPrice = unitPrice;
        this.MeasurementMethod = measurementMethod;
    }

    @Generated(hash = 1110386182)
    public Products(Long id, String ProductId, String ProductName, String PluCode, String CreatedAt,
            int StatusCode, String Remark, String ParentId, float UnitPrice, int MeasurementMethod) {
        this.id = id;
        this.ProductId = ProductId;
        this.ProductName = ProductName;
        this.PluCode = PluCode;
        this.CreatedAt = CreatedAt;
        this.StatusCode = StatusCode;
        this.Remark = Remark;
        this.ParentId = ParentId;
        this.UnitPrice = UnitPrice;
        this.MeasurementMethod = MeasurementMethod;
    }

    public String getProductName() {
        return ProductName;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductId() {
        return this.ProductId;
    }

    public void setProductId(String ProductId) {
        this.ProductId = ProductId;
    }

    public void setProductName(String ProductName) {
        this.ProductName = ProductName;
    }

    public String getPluCode() {
        return this.PluCode;
    }

    public void setPluCode(String PluCode) {
        this.PluCode = PluCode;
    }

    public String getCreatedAt() {
        return this.CreatedAt;
    }

    public void setCreatedAt(String CreatedAt) {
        this.CreatedAt = CreatedAt;
    }

    public int getStatusCode() {
        return this.StatusCode;
    }

    public void setStatusCode(int StatusCode) {
        this.StatusCode = StatusCode;
    }

    public String getRemark() {
        return this.Remark;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public String getParentId() {
        return this.ParentId;
    }

    public void setParentId(String ParentId) {
        this.ParentId = ParentId;
    }

    public float getUnitPrice() {
        return this.UnitPrice;
    }

    public void setUnitPrice(float UnitPrice) {
        this.UnitPrice = UnitPrice;
    }

    public int getMeasurementMethod() {
        return this.MeasurementMethod;
    }

    public void setMeasurementMethod(int MeasurementMethod) {
        this.MeasurementMethod = MeasurementMethod;
    }

    @Override
    public String toString() {
        return "Products{" +
                "id=" + id +
                ", ProductId='" + ProductId + '\'' +
                ", ProductName='" + ProductName + '\'' +
                ", PluCode='" + PluCode + '\'' +
                ", CreatedAt='" + CreatedAt + '\'' +
                ", StatusCode=" + StatusCode +
                ", Remark='" + Remark + '\'' +
                ", ParentId='" + ParentId + '\'' +
                ", UnitPrice=" + UnitPrice +
                ", MeasurementMethod=" + MeasurementMethod +
                '}';
    }
}
