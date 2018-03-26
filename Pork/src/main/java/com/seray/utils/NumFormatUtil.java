package com.seray.utils;

import android.text.TextUtils;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * 数字获取及格式化辅助类
 */
public class NumFormatUtil {

    public static final int PASSWORD_TO_OPERATION = 0;
    public static final int PASSWORD_TO_SETTING = 1;
    public static final int PASSWORD_TO_UNIT = 2;
    public static final int PASSWORD_TO_CONFIG = 3;

    public static DecimalFormat df2 = new DecimalFormat("######0.00");
    public static DecimalFormat df3 = new DecimalFormat("######0.000");
    private static NumFormatUtil mUtil = null;
    private static Pattern pattern = Pattern.compile("^(-?[0-9\\.]+)$");
    private static Pattern isIntPattern = Pattern.compile("^(-?[0-9]+)$");

    private NumFormatUtil() {
    }

    public static NumFormatUtil getInstance() {
        if (mUtil == null) {
            synchronized (NumFormatUtil.class) {
                if (mUtil == null) {
                    mUtil = new NumFormatUtil();
                }
            }
        }
        return mUtil;
    }

    /**
     * 生成交易流水号
     */
    // TODO: 2017/11/2
//    public static String createSerialNum() {
//        if (CacheHelper.DATE_ID > 998) {
//            CacheHelper.DATE_ID = 0;
//        }
//        CacheHelper.DATE_ID += 1;
//        return CacheHelper.DeviceCode + getDatePoint2() + getOneRandomNumber()
//                + String.format(Locale.CHINA, "%03d", CacheHelper.DATE_ID);
//    }

    /**
     * 生成交易流水号中的时间前缀
     */
    private static String getDatePoint2() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss", Locale.CHINA);
        return sdf.format(date);
    }

    /**
     * 生成交易流水号中的一位随机数
     */
    private static String getOneRandomNumber() {
        String rNum = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        int index = random.nextInt(rNum.length());
        return String.valueOf(rNum.charAt(index));
    }

    /**
     * 是否是数字
     */
    public static boolean isNumeric(String str) {
        return !TextUtils.isEmpty(str) && pattern.matcher(str).matches();
    }

    public static boolean isInt(String str) {
        return !TextUtils.isEmpty(str) && isIntPattern.matcher(str).matches();
    }

    /**
     * 生成页面显示
     */
    public static String getFormatDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        return sdf.format(new Date());
    }

    /**
     * 生成交易时间
     */
    public static String getDateDetail() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return format.format(date);
    }

    public static String getReportDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return format.format(date);
    }

    /**
     * 生成Log日志的CreateAt
     */
    public static String getLogCreateAt() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.CHINA);
        return format.format(date);
    }

    /**
     * 生成运维中心两道密码
     */
    public static String getRandomPassword(int flag) {
        String pwd = "";
        switch (flag) {

            case PASSWORD_TO_SETTING:
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                String yesterday = new SimpleDateFormat("MMdd", Locale.CHINA).format(cal.getTime());
                pwd = yesterday + "37";
                break;
            case PASSWORD_TO_OPERATION:
                pwd = "015";
                break;
            case PASSWORD_TO_CONFIG:
                pwd = "018";
                break;
        }
        return pwd;
    }

    public BigDecimal getDecimalNet(String w) {
        return this.getDecimalNet(w, 2);
    }

    public BigDecimal getDecimalPiece(String p) {
        return this.getDecimalNet(p, 0);
    }

    public BigDecimal getAvgDecimalPrice(Double ba, Double bb) {
        BigDecimal a = new BigDecimal(ba);
        BigDecimal b = new BigDecimal(bb);
        return a.divide(b, 2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getDecimalNetWithOutHalfUp(double net) {
        String s = Double.toString(net);
        return new BigDecimal(s).setScale(2, BigDecimal.ROUND_UNNECESSARY);
    }


    private BigDecimal getDecimalNet(String w, int s) {
        return this.decimalNet(w, s);
    }

    private BigDecimal decimalNet(String w, int s) {
        if (s < 0) {
            s = 3;
        }
        return new BigDecimal(w.trim()).setScale(s, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getDecimalSum(BigDecimal p, BigDecimal w) {
        return this.mul(p, w);
    }

    private BigDecimal mul(BigDecimal p, BigDecimal w) {
        BigDecimal b = p.multiply(w).setScale(1, BigDecimal.ROUND_HALF_UP);
        return new BigDecimal(b.toString() + "0");
    }

    public BigDecimal getDecimalTare(String t) {
        return this.getDecimalTare(t, 3);
    }

    private BigDecimal getDecimalTare(String t, int s) {
        if (s < 0) {
            s = 3;
        }
        return new BigDecimal(t).setScale(s, BigDecimal.ROUND_UNNECESSARY);
    }

    public BigDecimal getDecimalPrice(String str) {
        return new BigDecimal(str).setScale(2, BigDecimal.ROUND_UNNECESSARY);
    }

    public BigDecimal getDecimalPrice(double d) {
        String str = Double.toString(d);
        return this.getDecimalPrice(str);
    }

    public BigDecimal getDecimalPriceHalfUp(double str) {
        return new BigDecimal(String.valueOf(str)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getDecimalAmount(String str) {
        return new BigDecimal(str).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getDecimalAmount(Double d) {
        return new BigDecimal(Double.toString(d)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
