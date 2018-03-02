package com.seray.http;

import com.seray.pork.dao.ConfigManager;

public class API {

    private static String BASE_HTTP;

    static {
        ConfigManager configManager = ConfigManager.getInstance();
        String BASE_IP = configManager.query("ip");
        BASE_HTTP = "http://" + BASE_IP + "/Interface/";
    }

    public static void reset() {
        ConfigManager configManager = ConfigManager.getInstance();
        String BASE_IP = configManager.query("ip");
        BASE_HTTP = "http://" + BASE_IP + "/Interface/";
    }
    /**
     * 登录获取权限
     */
    public static String LOGIN_URL =
            BASE_HTTP + "Login.ashx";
    /**
     * 获取商品二维码信息
     */
    public static String GET_BAR_CODE_CONTENT_URL = BASE_HTTP + "GetSysLogByBarCode.ashx";
    /**
     * 获取录入列表
     */
    public static String GET_BATCH_NUMBER_SEACH_URL =
            BASE_HTTP + "GetPurchaseDetailsBySeach.ashx";

    /**
     * 提交采购单
     */
    public static String SET_UPLOAD_BATCH_NUMBER_URL =
            BASE_HTTP + "SavePurchase.ashx";
    /**
     * 获取 批次号列表
     */
    public static String GET_BATCH_NO_URL =
            BASE_HTTP + "GetPurchase.ashx";
    /**
     * 获取批次号下明细
     */
    public static String GET_PURCHASE_DETAIL_URL =
            BASE_HTTP + "GetPurchaseDetail.ashx";
    /**
     * 提交 商品重量确认
     */
    public static String SET_UPDATE_ACTUAL_WEIGHT_URL =
            BASE_HTTP + "UpdateActualWeight.ashx";
    /**
     * 入白条库
     */
    public static String SET_SAVE_LOULIBRARY_URL =
            BASE_HTTP + "SaveLoulibrary.ashx";
    /**
     * 出白条库到分割
     */
    public static String SET_OUT_LOULIBRARY_URL =
            BASE_HTTP + "OutLoulibrary.ashx";
    /**
     * 出白条库到鲜品
     */
    public static String SET_TAKE_LOULIBRARY_URL =
            BASE_HTTP + "TakeLoulibrary.ashx";

    /**
     * 进分割房
     */
    public static String SET_SAVE_DIVISION_URL =
            BASE_HTTP + "SaveDivision.ashx";
    /**
     * 出分割房到分拣
     */
    public static String SET_OUT_DIVISION_URL =
            BASE_HTTP + "OutDivision.ashx";

    /**
     * 出分割房到鲜品
     */
    public static String SET_TAKE_DIVISION_URL =
            BASE_HTTP + "TakeDivision.ashx";
    /**
     * 添加分拣
     */
    public static String SET_SAVE_SORTING_AREA_URL =
            BASE_HTTP + "SaveSortingArea.ashx";
    /**
     * 出分拣
     */
//    public static String SET_TAKE_SORTING_AREA_URL =
//            BASE_HTTP + "TakeSortingArea.ashx";

    public static String OUT_SORTING_AREA_URL =
            BASE_HTTP + "OutSortingArea.ashx";
    /**
     * 入速冻库 成品1 2 号库
     */
    public static String SET_SAVE_INVENTORY_BAR_CODE_URL =
            BASE_HTTP + "SaveInventoryByBarCode.ashx";

    /**
     * 入鲜品库
     */
    public static String SET_SAVE_INVENTORY_URL =
            BASE_HTTP + "SaveInventory.ashx";

    /**
     * 出鲜品
     */
    public static String SET_OUT_INVENTORY_URL =
            BASE_HTTP + "OutInventory.ashx";

    /**
     * 获取订单
     */
    public static String GET_ORDERS_URL =
            BASE_HTTP + "GetOrders.ashx";


    /**
     * 提交订单
     */
    public static String SAVE_ORDERS_URL =
            BASE_HTTP + "SaveOrder";
    /**
     * 订单配货
     */
    public static String UPDATET_ORDER_ACTUAL_WEIGHT =
            BASE_HTTP + "UpdatetOrderActualWeight.ashx";
    /**
     * 订单上车
     */
    public static String UPDATET_ORDER_VEHICLE =
            BASE_HTTP + "UpdateOrderVehicle.ashx";
}
