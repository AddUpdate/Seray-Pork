package com.seray.utils;

import java.math.BigDecimal;

public class DecimalFormat {

    public static float getRoundFloat(int scale, float value) {
        BigDecimal b = new BigDecimal(String.valueOf(value));
        b = b.setScale(scale, BigDecimal.ROUND_HALF_UP);
        return b.floatValue();
    }
}
