package com.seray.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;
/**
 * Created by pc on 2017/11/8.
 * 进货订单小计
 */
@Entity
public class PurchaseSubtotal {
    @Id(autoincrement = true)
    private Long id;
    private String supplier;//供应商
    @Unique
    private String batchNumber; //订单号   批次号
    private String tel;//电话
    private String stockDate;//进货日期
    private String purOrderImg;//进货单图片
    private int detailsNumber;//明细个数
    private String remark;//备注

    public PurchaseSubtotal() {
    }

    public PurchaseSubtotal( String batchNumber, String supplier,
                            String tel, String stockDate, String purOrderImg, int detailsNumber, String remark) {
        this.batchNumber = batchNumber;
        this.supplier = supplier;
        this.tel = tel;
        this.stockDate = stockDate;
        this.purOrderImg = purOrderImg;
        this.detailsNumber = detailsNumber;
        this.remark = remark;
    }

    @Generated(hash = 88438748)
    public PurchaseSubtotal(Long id, String supplier, String batchNumber, String tel, String stockDate,
            String purOrderImg, int detailsNumber, String remark) {
        this.id = id;
        this.supplier = supplier;
        this.batchNumber = batchNumber;
        this.tel = tel;
        this.stockDate = stockDate;
        this.purOrderImg = purOrderImg;
        this.detailsNumber = detailsNumber;
        this.remark = remark;
    }



    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getStockDate() {
        return stockDate;
    }

    public void setStockDate(String stockDate) {
        this.stockDate = stockDate;
    }

    public String getPurOrderImg() {
        return purOrderImg;
    }

    public void setPurOrderImg(String purOrderImg) {
        this.purOrderImg = purOrderImg;
    }

    public int getDetailsNumber() {
        return detailsNumber;
    }

    public void setDetailsNumber(int detailsNumber) {
        this.detailsNumber = detailsNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "PurchaseSubtotal{" +
                "id=" + id +
                ", supplier='" + supplier + '\'' +
                ", batchNumber='" + batchNumber + '\'' +
                ", tel='" + tel + '\'' +
                ", stockDate='" + stockDate + '\'' +
                ", purOrderImg='" + purOrderImg + '\'' +
                ", detailsNumber=" + detailsNumber +
                ", remark='" + remark + '\'' +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
