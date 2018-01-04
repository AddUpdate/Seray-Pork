package com.seray.entity;

/**
 * Created by pc on 2017/12/17.
 */

public class PurchaseSearch {
    private String supplier;//供应商
    private String batchNumber; //订单号   批次号
    private String stockDate;//进货日期
    private String productName;//商品名
    private String quantity;//数量
    private String actualQuantity;//实际数量
    private String state; // 确认是否入库        1已入库   2未入库

    public PurchaseSearch(String stockDate,String supplier,String batchNumber,String productName,String quantity,
                          String actualQuantity,String state){
        this.supplier = supplier;
        this.batchNumber = batchNumber;
        this.stockDate = stockDate;
        this.productName = productName;
        this.quantity = quantity;
        this.actualQuantity = actualQuantity;
        this.state = state;

    }


    public String getSupplier() {
        return supplier;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public String getStockDate() {
        return stockDate;
    }

    public String getProductName() {
        return productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getActualQuantity() {
        return actualQuantity;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return "PurchaseSearch{" +
                "supplier='" + supplier + '\'' +
                ", batchNumber='" + batchNumber + '\'' +
                ", stockDate='" + stockDate + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity='" + quantity + '\'' +
                ", actualQuantity='" + actualQuantity + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
