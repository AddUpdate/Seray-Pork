package com.seray.entity;

import java.util.List;

/**
 * Created by pc on 2017/11/8.
 * 进货订单录入
 */

public class PurchaseOrder {

    private PurchaseSubtotal purchaseSubtotal;
    private List<PurchaseDetail> purchaseDetails;

    public PurchaseOrder(){}

    public PurchaseOrder(PurchaseSubtotal purchaseSubtotal, List<PurchaseDetail> purchaseDetails) {
        this.purchaseSubtotal = purchaseSubtotal;
        this.purchaseDetails = purchaseDetails;
    }

    public PurchaseSubtotal getPurchaseSubtotal() {
        return purchaseSubtotal;
    }

    public void setPurchaseSubtotal(PurchaseSubtotal purchaseSubtotal) {
        this.purchaseSubtotal = purchaseSubtotal;
    }

    public List<PurchaseDetail> getPurchaseDetails() {
        return purchaseDetails;
    }

    public void setPurchaseDetails(List<PurchaseDetail> purchaseDetails) {
        this.purchaseDetails = purchaseDetails;
    }

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "purchaseSubtotal=" + purchaseSubtotal +
                ", purchaseDetails=" + purchaseDetails +
                '}';
    }
}
