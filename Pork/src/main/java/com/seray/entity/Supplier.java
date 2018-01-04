package com.seray.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 供应商
 */
@Entity
public class Supplier {
    @Id(autoincrement = true)
    private Long id;
    private String SupplierId;
    private String SupplierName;
    private String SupplierAddress;
    private String SupplierPhone;

    public Supplier() {
    }

    public Supplier(String supplierId, String supplierName, String supplierAddress, String supplierPhone) {
        this.SupplierId = supplierId;
        this.SupplierName = supplierName;
        this.SupplierAddress = supplierAddress;
        this.SupplierPhone = supplierPhone;
    }

    @Generated(hash = 1068345812)
    public Supplier(Long id, String SupplierId, String SupplierName, String SupplierAddress,
            String SupplierPhone) {
        this.id = id;
        this.SupplierId = SupplierId;
        this.SupplierName = SupplierName;
        this.SupplierAddress = SupplierAddress;
        this.SupplierPhone = SupplierPhone;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSupplierId() {
        return this.SupplierId;
    }

    public void setSupplierId(String SupplierId) {
        this.SupplierId = SupplierId;
    }

    public String getSupplierName() {
        return this.SupplierName;
    }

    public void setSupplierName(String SupplierName) {
        this.SupplierName = SupplierName;
    }

    public String getSupplierAddress() {
        return this.SupplierAddress;
    }

    public void setSupplierAddress(String SupplierAddress) {
        this.SupplierAddress = SupplierAddress;
    }

    public String getSupplierPhone() {
        return this.SupplierPhone;
    }

    public void setSupplierPhone(String SupplierPhone) {
        this.SupplierPhone = SupplierPhone;
    }
}
