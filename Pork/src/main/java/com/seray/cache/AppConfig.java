package com.seray.cache;

import com.seray.utils.LogUtil;

/**
 * 秤系统基本配置类
 */
public class AppConfig {

    /**
     * 是否使用佳博USB打印机
     */
    public static boolean isUseGpPrinter = false;
    /**
     * 是否是生产环境
     * 发布前注意修改 LEVEL = NOTHING{@link LogUtil }
     */
    public static final boolean isDeploy = true;

    /**
     * 挂单功能状态
     */
    public static boolean isOpenEntryOrder = false;

    /**
     * 是否开启激活验证
     */
    public static boolean isCheckAvailable = false;

    /**
     * 秤系统的类型
     */
    public static ScaleType SCALE_TYPE = getScaleTypeFromDb();

    /**
     * 是否是地秤
     */
    public static boolean isT200() {
        return SCALE_TYPE == ScaleType.T200;
    }

    /**
     * 是否是桌秤
     */
    public static boolean isSR200() {
        return SCALE_TYPE == ScaleType.SR200;
    }

    /**
     * 从数据库中取出秤类型
     *
     * @return 默认类型：T200
     */
    private static ScaleType getScaleTypeFromDb() {
        ScaleType st = ScaleType.SR200;
  //      String result = ConfigManager.getInstance().query("ScaleType");
//        if (TextUtils.isEmpty(result)) {
//            return st;
//        } else if (NumFormatUtil.isNumeric(result)) {
//            int t = Integer.parseInt(result);
//            if (t == 1) {
//                st = ScaleType.T200;
//            } else if (t == 2) {
//                st = ScaleType.SR200;
//            }
//        } else {
//            return st;
//        }
        return st;
    }

    /**
     * 将秤类型保存至数据库
     */
    public static void saveScaleTypeToDb() {
        String value;
        if (SCALE_TYPE == ScaleType.T200) {
            value = "1";
        } else if (SCALE_TYPE == ScaleType.SR200) {
            value = "2";
        } else {// 默认值T200
            value = "1";
        }
     //   ConfigManager.getInstance().insert("ScaleType", value);
        // TODO: 2017/11/3   
    }

    /**
     * T200地秤 SR200桌秤
     */
    public enum ScaleType {
        T200, SR200
    }
}
