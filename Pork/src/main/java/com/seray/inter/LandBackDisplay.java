package com.seray.inter;

import com.lzscale.scalelib.misclib.BackDisplay;
import com.seray.utils.LogUtil;

/**
 * 地秤后显辅助类
 */
public class LandBackDisplay extends BackDisplayBase {

    private static BackDisplay mBackDisplay;

    private static LandBackDisplay mInstance = null;

    private LandBackDisplay() {

    }

    public static LandBackDisplay getInstance(boolean isOpen) {

        if (mInstance == null) {
            mInstance = new LandBackDisplay();
        }

        mInstance.isOpen = isOpen;

        if (isOpen) {
            try {
                mBackDisplay = new BackDisplay();
            } catch (Exception e) {
                LogUtil.e("BackDisplay后显初始化异常...");
            }
        }
        return mInstance;
    }

    /**
     * 平稳
     */
    @Override
    public void showIsStable(boolean isStable) {
        if (!this.isOpen)
            return;
        try {
            mBackDisplay.DispLed2(isStable);
        } catch (Exception e) {
            LogUtil.e("后显更新平稳显示出错..." + e.getMessage());
        }
    }

    /**
     * 归零
     */
    @Override
    public void showIsZero(boolean isZero) {
        if (!this.isOpen)
            return;
        try {
            mBackDisplay.DispLed1(isZero);
        } catch (Exception e) {
            LogUtil.e("后显更新归零显示出错..." + e.getMessage());
        }
    }

    /**
     * 单价
     */
    @Override
    public void showPrice(String price) {
        if (!this.isOpen)
            return;
        try {
            mBackDisplay.DispLine3(getBackDisplay(price));
        } catch (Exception e) {
            LogUtil.e("后显更新单价显示出错..." + e.getMessage());
        }
    }

    /**
     * 皮重
     */
    @Override
    public void showTare(String tare) {
        if (!this.isOpen)
            return;
        try {
            mBackDisplay.DispLine1(getBackDisplay(tare));
        } catch (Exception e) {
            LogUtil.e("后显更新皮重显示出错..." + e.getMessage());
        }
    }

    /**
     * 总金额
     */
    @Override
    public void showAmount(String amount) {
        if (!this.isOpen)
            return;
        try {
            mBackDisplay.DispLine4(getBackDisplay(amount));
        } catch (Exception e) {
            LogUtil.e("后显更新总金额显示出错..." + e.getMessage());
        }
    }

    /**
     * 重量
     */
    @Override
    public void showWeight(String weight) {
        if (!this.isOpen)
            return;
        try {
            mBackDisplay.DispLine2(getBackDisplay(weight));
        } catch (Exception e) {
            LogUtil.e("后显更新重量显示出错..." + e.getMessage());
        }
    }

    @Override
    public void showWeightType(int type) {

    }

    @Override
    public void showCustomerName(String name) {

    }

    @Override
    public void showProductName(String name) {

    }

    @Override
    public void showSubtotal(String subtotal) {

    }

    @Override
    public void showSubtotalAmount(String amount) {

    }

    /**
     * 初始化显示
     */
    @Override
    public void initBackDisplay() {
        if (!this.isOpen)
            return;
        mBackDisplay.DispLine1("000.000");
        mBackDisplay.DispLine1(getBackDisplay("0.000"));
        mBackDisplay.DispLine2(getBackDisplay("0.000"));
        mBackDisplay.DispLine3(getBackDisplay("0.00"));
        mBackDisplay.DispLine4(getBackDisplay("0.00"));
        mBackDisplay.DispLed1(true);
        mBackDisplay.DispLed2(false);
    }

    @Override
    public void showMemberPrice(String memberPrice) {

    }

    private String getBackDisplay(String num) {
        String num1 = num;
        if (num1 == null)
            num1 = "";
        int maxLen = num1.contains(".") ? 7 : 6, len = maxLen - num1.length();
        for (int i = 0; i < len; i++) {
            num1 = " " + num1;
        }
        return num1;
    }
}
