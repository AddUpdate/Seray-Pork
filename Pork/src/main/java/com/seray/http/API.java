package com.seray.http;

public class API {

    /**
     * 获取商品二维码信息
     */
    public static final String GET_BAR_CODE_CONTENT_URL =
            "http://192.168.1.65:8020/Interface/GetSysLogByBarCode.ashx";
    /**
     * 获取录入列表
     */
    public static final String GET_BATCH_NUMBER_SEACH_URL =
            "http://192.168.1.65:8020/Interface/GetPurchaseDetailsBySeach.ashx";

    /**
     * 提交采购单
     */
    public static final String SET_UPLOAD_BATCH_NUMBER_URL =
            "http://192.168.1.65:8020/Interface/SavePurchase.ashx";
    /**
     * 获取 批次号列表
     */
    public static final String GET_BATCH_NO_URL =
            "http://192.168.1.65:8020/Interface/GetPurchase.ashx";
    /**
     * 获取批次号下明细
     */
    public static final String GET_PURCHASE_DETAIL_URL =
            "http://192.168.1.65:8020/Interface/GetPurchaseDetail.ashx";
    /**
     * 提交 商品重量确认
     */
    public static final String SET_UPDATE_ACTUAL_WEIGHT_URL =
            "http://192.168.1.65:8020/Interface/UpdateActualWeight.ashx";
    /**
     * 入白条库
     */
    public static final String SET_SAVE_LOULIBRARY_URL =
            "http://192.168.1.65:8020/Interface/SaveLoulibrary.ashx";
    /**
     * 出白条库到分割
     */
    public static final String SET_OUT_LOULIBRARY_URL =
            "http://192.168.1.65:8020/Interface/OutLoulibrary.ashx";
    /**
     * 出白条库到鲜品
     */
    public static final String SET_TAKE_LOULIBRARY_URL =
            "http://192.168.1.65:8020/Interface/TakeLoulibrary.ashx";

    /**
     * 进分割房
     */
    public static final String SET_SAVE_DIVISION_URL =
            "http://192.168.1.65:8020/Interface/SaveDivision.ashx";
    /**
     * 出分割房到分拣
     */
    public static final String SET_OUT_DIVISION_URL =
            "http://192.168.1.65:8020/Interface/OutDivision.ashx";

    /**
     * 出分割房到鲜品
     */
    public static final String SET_TAKE_DIVISION_URL =
            "http://192.168.1.65:8020/Interface/TakeDivision.ashx";
    /**
     * 添加分拣
     */
    public static final String SET_SAVE_SORTING_AREA_URL =
            "http://192.168.1.65:8020/Interface/SaveSortingArea.ashx";
    /**
     * 出分拣
     */
    public static final String SET_TAKE_SORTING_AREA_URL =
            "http://192.168.1.65:8020/Interface/TakeSortingArea.ashx";
    /**
     * 入速冻库 成品1 2 号库
     */
    public static final String SET_SAVE_INVENTORY_BAR_CODE_URL =
            "http://192.168.1.65:8020/Interface/SaveInventoryByBarCode.ashx";

    /**
     * 入鲜品库
     */
    public static final String SET_SAVE_INVENTORY_URL =
            "http://192.168.1.65:8020/Interface/SaveInventory.ashx";

    /**
     * 出鲜品
     */
    public static final String SET_OUT_INVENTORY_URL =
            "http://192.168.1.65:8020/Interface/OutInventory.ashx";

    /**
     * 获取订单
     */
    public static final String GET_ORDERS_URL =
            "http://192.168.1.65:8020/Interface/GetOrders.ashx";


    /**
     * 提交订单
     */
    public static final String SAVE_ORDERS_URL =
            "http://192.168.1.65:8020/Interface/SaveOrder";
    /**
     * 订单配货
     */
    public static final String UPDATET_ORDER_ACTUAL_WEIGHT =
            "http://192.168.1.65:8020/Interface/UpdatetOrderActualWeight.ashx";

}
