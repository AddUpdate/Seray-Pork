package com.seray.inter;

public abstract class BackDisplayBase {

    /**
     * 是否打开后显
     */
    boolean isOpen;

    public abstract void showIsStable(boolean isStable);

    public abstract void showIsZero(boolean isZero);

    public abstract void showPrice(String price);

    public abstract void showTare(String tare);

    public abstract void showAmount(String amount);

    public abstract void showWeight(String weight);

    public abstract void showWeightType(int type);

    public abstract void showCustomerName(String name);

    public abstract void showProductName(String name);

    public abstract void showSubtotal(String subtotal);

    public abstract void showSubtotalAmount(String amount);

    public abstract void initBackDisplay();

    public abstract void showMemberPrice(String memberPrice);

    public void showPlace(String place) {

    }

    public void showQRImage(String content) {
    }
}
